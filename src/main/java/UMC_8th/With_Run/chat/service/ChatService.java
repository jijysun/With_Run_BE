package UMC_8th.With_Run.chat.service;

import UMC_8th.With_Run.common.redis.dto.PayloadDTO;
import UMC_8th.With_Run.chat.converter.ChatConverter;
import UMC_8th.With_Run.chat.converter.MessageConverter;
import UMC_8th.With_Run.chat.converter.UserChatConverter;
import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.Message;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.chat.repository.ChatRepository;
import UMC_8th.With_Run.chat.repository.MessageRepository;
import UMC_8th.With_Run.chat.repository.UserChatRepository;
import UMC_8th.With_Run.common.apiResponse.status.ErrorCode;
import UMC_8th.With_Run.common.exception.handler.ChatHandler;
import UMC_8th.With_Run.common.exception.handler.CourseHandler;
import UMC_8th.With_Run.common.exception.handler.UserHandler;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.course.repository.CourseRepository;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.FollowRepository;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final ProfileRepository profileRepository;
    private final MessageRepository messageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SimpMessagingTemplate template;
    private final FollowRepository followRepository;
    private final CourseRepository courseRepository;
//    private final RedisPublisher redisPublisher;

    /// followee = 내가 팔로우
    /// follower = 나를 팔로우!

    public List<ChatResponseDTO.GetChatListDTO> getChatList(HttpServletRequest request) {

        User user = getUserByJWT(request, "getChatList");  // jwt

        List<UserChat> userChatList = userChatRepository.findAllByUserIdJoinFetchChatUserAndProfile(user.getId());

        if (userChatList.isEmpty()) throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST); // 성공 코드로 전환?

        List<Long> chatIdList = userChatList.stream()
                .map(userChat -> userChat.getChat().getId())
                .toList();

        // 해당 채팅방에 참여하고 있는 user_chat 파싱
        List<UserChat> otherUserChatList = userChatRepository.findAllByChat_IdInJoinFetchUserAndProfile(chatIdList);

        // chatIdList 순서에 맞게 userChat 파싱
        Map<Long, List<UserChat>> participantsMap = otherUserChatList.stream()
                .filter(userChat -> !userChat.getUser().getId().equals(user.getId()))
                .collect(Collectors.groupingBy(userchat -> userchat.getChat().getId()));

        List<Integer> unReadMsgCountList = userChatList.stream()
                .map(UserChat::getUnReadMsg)
                .toList();


        log.info("'getChatList' - Chat.count that user is participating in : " + userChatList.size());
        
        return ChatConverter.toGetChatListDTO(userChatList, unReadMsgCountList, chatIdList, participantsMap);
    }

    // 채팅 첫 생성 메소드
    @Transactional
    public void createChat(Long targetId, HttpServletRequest request) {
        User user = getUserByJWT(request, "createChat"); // jwt
        User targetUser = userRepository.findByIdWithProfile(targetId).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER)); // targetUser는 비영속 상태이다, targetUser에 대한 update, save는 필요

        Chat chat = ChatConverter.toNewChatConverter();

        List<UserChat> userChats = new ArrayList<>();
        userChats.add(UserChatConverter.toNewUserChat(user, targetUser, chat));
        userChats.add(UserChatConverter.toNewUserChat(targetUser, user,chat));

        chat.addUserChat(userChats.get(0));
        chat.addUserChat(userChats.get(1));

        chatRepository.save(chat);
        userChatRepository.saveAll(userChats);

        ///  메세지 보내기!!
    }

    // 채팅방 이름 변경 메소드, 전체 공통 변경
    @Transactional
    public ChatResponseDTO.RenameChatDTO renameChat(Long chatId, String newName, HttpServletRequest request) {
        User user = getUserByJWT(request, "renameChat");
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        UserChat userchat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chat.getId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));

        log.info("newName : " + newName);

        userchat.renameChat(newName);

        return ChatResponseDTO.RenameChatDTO.builder()
                .chatId(chatId)
                .chatName(newName)
                .build();
    }

    // 채팅 초대 목록 조회 리스트
    public List<ChatResponseDTO.GetInviteUserDTO> getInviteUser(Long chatId, HttpServletRequest request) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        // 이미 채팅방이 자신 포함 4명이라면 초대 목록 조회도 거절
        if (chat.getParticipants() >= 4) {
            throw new ChatHandler(ErrorCode.CHAT_IS_FULL);
        }

        User user = getUserByJWT(request, "getInviteUser");

        /// 쿼리를 2번 날리자, JPQL 에서는 서브쿼리가 제한적이다.
        // 사용자가 팔로우 하는 다른 사용자, targetUser.id
        List<User> followList = followRepository.findAllByUserId(user.getId()).stream()
                .map(Follow -> Follow.getTargetUser()).toList();

        String idList = "";
        for (User user1 : followList) {
            idList += user1.getId() + ", ";  // logging
        }

        // 채팅방에 참여하고 있지 않은 사용자,
        List<Long> userChatList = userChatRepository.findAllByChat_IdJoinFetchUserAndProfile(chatId).stream()
                .map(UserChat -> UserChat.getUser().getId()).toList();

        String userChatIdList = userChatList.toString();

        // 팔로잉 리스트에서 채팅방 참여자 제외 추출
        List<Long> canInviteUserIdList = followList.stream()
                .filter(u -> !userChatList.contains(u.getId()))
                .map(u -> u.getId())
                .toList();

        String canInviteList = "";
        for (Long userId : canInviteUserIdList) { // logging
            canInviteList += userId.toString() + ", ";
        }

        List<Profile> canInviteUserProfileList = profileRepository.findAllByUser_IdIn(canInviteUserIdList);

        log.info("follow List : {}", idList);
        log.info("in Chat, userIdList : {}", userChatIdList);
        log.info("canInviteList : {}", canInviteList);
        return ChatConverter.toGetInviteUserDTO(canInviteUserIdList, canInviteUserProfileList);
    }

    // 위 메소드에서 조회한 사용자 초대 메소드
    @Transactional
    public void inviteUser(Long chatId, ChatRequestDTO.InviteUserReqDTO reqDTO, HttpServletRequest request) {
        
        ///  초대한 사람이 초대자의 팔로우 리스트에 있는 지 검사 로직 추가
        
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        List<ChatRequestDTO.InviteDTO> inviteUserList = reqDTO.getInviteUserList();

        if (chat.getParticipants() + inviteUserList.size() > 4) {
            throw new ChatHandler(ErrorCode.CANT_INVITE);
        }
        chat.updateParticipants(chat.getParticipants() + inviteUserList.size());

        // 기존 사용자
        List<UserChat> userChatList = userChatRepository.findAllByChat_IdJoinFetchUserAndProfile(chatId);


        // 신규 사용자 update
        String preUserNameList = userChatList.stream()
                .map(userChat -> userChat.getUser().getProfile().getName())
                .collect(Collectors.joining(", "));

        log.info("preUserName : {}", preUserNameList);

        List<UserChat> newUserChat = new ArrayList<>();
        for (ChatRequestDTO.InviteDTO dto :  inviteUserList){

            User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));

            String collect = inviteUserList.stream()
                    .filter(userInfo -> !userInfo.getUserId().equals(dto.getUserId()))
                    .map(ChatRequestDTO.InviteDTO::getUsername)
                    .collect(Collectors.joining(", "));

            String joinChatName = preUserNameList + ", " + collect;

            log.info("joinChatName : {}", joinChatName);
            newUserChat.add(UserChatConverter.toNewUserChatInInvite(user, joinChatName, chat));
        }
        userChatRepository.saveAll(newUserChat);

        // 기존 사용자 update
        log.info("pre User Update");
        for (UserChat userChat : userChatList) { // 기존 사용자의 user_chat 에 대해
            if (!userChat.getIsDefaultChatName()){ // 각 유저에 대해 커스텀 ChatName 이 아닌 경우 Update
                continue;
            }

            Long currentUserId = userChat.getUser().getId();

            String collect = newUserChat.stream()
                    .filter(other -> !other.getUser().getId().equals(currentUserId))
                    .map(otherUserChat -> otherUserChat.getUser().getProfile().getName())
                    .collect(Collectors.joining(", "));
            String otherName = userChat.getChatName() + ", " + collect;

            log.info("{}'s name = {}", userChat.getUser().getProfile().getName(),  otherName);

            userChat.renameDefaultChatName(otherName);
        }

        // 이거 보단 다시 만드는 게...
        List<String> nameList = inviteUserList.stream()
                .map(ChatRequestDTO.InviteDTO::getUsername).toList();

        String name = "";
        for (String s : nameList) {
            name += s + "님 ";
        }
        name = name.substring(0, name.length() - 1);

        // 채팅방에 초대 메세지 뿌리기 + save
        String inviteMsg = reqDTO.getUsername() + "님이 " + name + "을 초대하였습니다.";
        messageRepository.save(MessageConverter.toInviteMessage(getUserByJWT(request, "InviteUser"), chat, inviteMsg));
        template.convertAndSend("/sub/" + chatId + "/msg", inviteMsg);
    }

    @Transactional
    public List<ChatResponseDTO.BroadcastMsgDTO> enterChat(Long chatId, HttpServletRequest request) { // 메세지에 대한 대량의 입출력, MySQL 로는 무겁지 않을까요...?
        ///  TODO 사용자에 대한 읽지 않은 메세지 수 0으로 세팅
        User user = getUserByJWT(request, "enterChat");
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() ->  new ChatHandler(ErrorCode.WRONG_CHAT));

        // 사용자의 읽지 않은 메세지 수 0 + isChatting = true
        userChat.setToChatting();

        return MessageConverter.toChatHistoryDTO(messageRepository.findByChat_Id(chatId), chatId); // join fetch!
    }

    @Transactional
    public ChatResponseDTO.BroadcastMsgDTO chatting(Long chatId, ChatRequestDTO.ChattingReqDTO reqDTO) {
        // 1. 메세지가 수신된다
        User user = userRepository.findById(reqDTO.getUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
        Profile profile = profileRepository.findByUserId(user.getId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_PROFILE));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));
        Message msg = MessageConverter.toMessage(user, chat, reqDTO);

        // 2. 채팅방에 참여하고 있지 않은 사용자의 안읽은 메세지 수 증가
        // select * From user_chat uc where chat_id = ? and isChatting = false;
        List<UserChat> userChatList = userChatRepository.findAllByChat_IdAndIsChattingFalse(chatId);
        userChatList.forEach(UserChat::updateUnReadMsg);

        // 메세지 저장
        messageRepository.save(msg);
        return MessageConverter.toBroadCastMsgDTO(user.getId(), chatId, profile, msg);
    }

    public void chattingWithRedis(Long chatId, ChatRequestDTO.ChattingReqDTO reqDTO) {
        User user = userRepository.findById(reqDTO.getUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
        Profile profile = profileRepository.findByUserId(user.getId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_PROFILE));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));
        Message msg = MessageConverter.toMessage(user, chat, reqDTO);

        // 메세지 저장, Redis...?
        messageRepository.save(msg);

        // redis 처리 전용 dto 변환,
        PayloadDTO<Object> payloadDTO = PayloadDTO.builder()
                .type("chat")
                .payload(MessageConverter.toBroadCastMsgDTO(user.getId(), chatId, profile, msg))
                .build();

//        redisPublisher.publishMsg("redis.chat."+chatId, payloadDTO);
    }


    public void shareCourse(ChatRequestDTO.ShareReqDTO reqDTO) {
        User user = userRepository.findById(reqDTO.getUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
        Course course = courseRepository.findById(reqDTO.getCourseId()).orElseThrow(() -> new CourseHandler(ErrorCode.WRONG_COURSE)); // 에러 코드 바꾸기

        Message courseMsg;

        if (reqDTO.getIsChat()) { // 채팅방 공유 시 채팅방 ID 이용
            Chat chat = chatRepository.findById(reqDTO.getChatId()).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
            courseMsg = MessageConverter.toShareMessage(user, chat, course);

            messageRepository.save(courseMsg);

            ChatResponseDTO.BroadcastCourseDTO courseDTO = MessageConverter.toBroadCastCourseDTO(user.getId(), chat.getId(), course);

            // 메세지 BroadCast
            template.convertAndSend("/sub/" + chat.getId() + "/msg", courseDTO);
        } else {
            // 친구를 통한 공유, 채팅이 없는 경우 추가
            User targetUser = userRepository.findById(reqDTO.getTargetUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
            Chat privateChat = chatRepository.findPrivateChat(user.getId(), targetUser.getId());
            if (privateChat == null) {
                log.info("'shareCourse'/toFriend - privateChat is Null!");
                privateChat = ChatConverter.toNewChatConverter();

                List<UserChat> ucList = new ArrayList<>();
                ucList.add(UserChatConverter.toNewUserChat(user,targetUser, privateChat));
                ucList.add(UserChatConverter.toNewUserChat(targetUser, user,privateChat));

                Chat saveChat = chatRepository.save(privateChat);
                userChatRepository.saveAll(ucList);

                courseMsg = MessageConverter.toShareMessage(user, saveChat, course);

                // Save And Broadcast
                messageRepository.save(courseMsg);
                ChatResponseDTO.BroadcastCourseDTO courseDTO = MessageConverter.toBroadCastCourseDTO(user.getId(), saveChat.getId(), course);

                // 메세지 BroadCast
                template.convertAndSend("/sub/" + saveChat.getId() + "/msg", courseDTO);
            } else {
                log.info("'shareCourse'/toFriend - privateChat is Not Null! id = {}", privateChat.getId());
                courseMsg = MessageConverter.toShareMessage(user, privateChat, course);

                // Save And Broadcast
                messageRepository.save(courseMsg);
                ChatResponseDTO.BroadcastCourseDTO courseDTO = MessageConverter.toBroadCastCourseDTO(user.getId(), privateChat.getId(), course);

                // 메세지 BroadCast
                template.convertAndSend("/sub/" + privateChat.getId() + "/msg", courseDTO);
            }
        }
    }

    public void shareCourseWithRedis(ChatRequestDTO.ShareReqDTO reqDTO) {
        /// 여려 명 공유 시 채팅방 공유 로직, 카카오톡 공유 화면 참고!

        User user = userRepository.findById(reqDTO.getUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
        Course course = courseRepository.findById(reqDTO.getCourseId()).orElseThrow(() -> new CourseHandler(ErrorCode.WRONG_COURSE)); // 에러 코드 바꾸기

        if (reqDTO.getIsChat()) { // 채팅방 공유 시 채팅방 ID 이용
            Chat chat = chatRepository.findById(reqDTO.getChatId()).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

            messageRepository.save(MessageConverter.toShareMessage(user, chat, course));

            PayloadDTO<Object> payloadDTO = PayloadDTO.builder()
                    .type("share")
                    .payload(MessageConverter.toBroadCastCourseDTO(user.getId(), chat.getId(), course))
                    .build();

            // 메세지 BroadCast
//            redisPublisher.publishMsg("redis.chat."+reqDTO.getChatId(), payloadDTO);
        } else {
            // 친구를 통한 공유, 채팅이 없는 경우 추가
            User targetUser = userRepository.findById(reqDTO.getTargetUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
            Chat privateChat = chatRepository.findPrivateChat(user.getId(), targetUser.getId());
            if (privateChat == null) {
                log.info("'shareCourse'/toFriend - privateChat is Null!");
                privateChat = ChatConverter.toNewChatConverter();

                List<UserChat> ucList = new ArrayList<>();
                ucList.add(UserChatConverter.toNewUserChat(user,targetUser, privateChat));
                ucList.add(UserChatConverter.toNewUserChat(targetUser, user,privateChat));

                Chat savedChat = chatRepository.save(privateChat);
                userChatRepository.saveAll(ucList);

                // Save And Broadcast
                messageRepository.save(MessageConverter.toShareMessage(user, savedChat, course));

                PayloadDTO<Object> payloadDTO = PayloadDTO.builder()
                        .type("share")
                        .payload(MessageConverter.toBroadCastCourseDTO(user.getId(), savedChat.getId(), course))
                        .build();

                // 메세지 BroadCast
//                redisPublisher.publishMsg("redis.chat."+reqDTO.getChatId(), payloadDTO);
            } else {
                log.info("'shareCourse'/toFriend - privateChat is Not Null! id = {}", privateChat.getId());

                // Save And Broadcast
                messageRepository.save(MessageConverter.toShareMessage(user, privateChat, course));

                PayloadDTO<Object> payloadDTO = PayloadDTO.builder()
                        .type("share")
                        .payload(MessageConverter.toBroadCastCourseDTO(user.getId(), privateChat.getId(), course))
                        .build();

                // 메세지 BroadCast
//                redisPublisher.publishMsg("redis.chat."+reqDTO.getChatId(), payloadDTO);
            }
        }
    }


    @Transactional
    public void leaveChat (Long chatId, HttpServletRequest request){
        User user = getUserByJWT(request, "leaveChat");
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
        userChat.setToNotChatting();
    }

    @Transactional
    public void deleteChat(Long chatId, HttpServletRequest request) {
        User user = getUserByJWT(request, "leaveChat");
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        userChatRepository.delete(userChat);

        Integer participants = chat.getParticipants();

        if (participants -1 == 0) {
            chatRepository.deleteById(chatId);
        }
        else{
            chat.updateParticipants(participants - 1);
        }
    }


    public User getUserByJWT(HttpServletRequest request, String method) { // join fetch 를 통한 조회
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        log.info("ChatService.getUserByJWT() - {} -> found User!", method);
        return userRepository.findByEmailJoinFetch(email).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
    }
}