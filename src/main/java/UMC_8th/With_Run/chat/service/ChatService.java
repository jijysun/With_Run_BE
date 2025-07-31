package UMC_8th.With_Run.chat.service;

import UMC_8th.With_Run.common.redis.dto.PayloadDTO;
import UMC_8th.With_Run.common.redis.pub_sub.RedisPublisher;
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
import UMC_8th.With_Run.common.apiResponse.status.ErrorStatus;
import UMC_8th.With_Run.common.exception.GeneralException;
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
import java.util.stream.Collector;
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

    // 채팅 첫 생성 메소드
    @Transactional
    public void createChat(Long targetId, HttpServletRequest request) {
        User user = getUserByJWT(request, "createChat"); // jwt
        Profile userProfile = profileRepository.findByUserId(user.getId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_PROFILE));

        User targetUser = userRepository.findById(targetId).orElseThrow(()-> new UserHandler(ErrorCode.WRONG_USER)); // targetUser는 비영속 상태이다, targetUser에 대한 update, save는 필요
        Profile targetProfile = profileRepository.findByUserId(targetUser.getId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_PROFILE));

        Chat chat = ChatConverter.toNewChatConverter(userProfile, targetProfile);

        List<UserChat> userChats = new  ArrayList<>();
        userChats.add(UserChatConverter.toNewUserChat(user, chat));
        userChats.add(UserChatConverter.toNewUserChat(targetUser, chat));

        chat.addUserChat(userChats.get(0));
        chat.addUserChat(userChats.get(1));

        chatRepository.save(chat);
        userChatRepository.saveAll(userChats);
    }

    // 채팅 초대 목록 조회 리스트
    public List<ChatResponseDTO.GetInviteUserDTO> getInviteUser(Long chatId, HttpServletRequest request) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        // 이미 채팅방이 자신 포함 4명이라면 초대 목록 조회도 거절
        if (chat.getParticipants() >= 4 ){
            throw new ChatHandler(ErrorCode.CHAT_IS_FULL);
        }

        User user = getUserByJWT(request, "getInviteUser");

        /// 쿼리를 2번 날리자, JPQL 에서는 서브쿼리가 제한적이다.
        // 사용자가 팔로우 하는 다른 사용자, targetUser.id
        List<User> followList = followRepository.findAllByUserId(user.getId()).stream()
                .map(Follow-> Follow.getTargetUser()).toList();

        String idList = "";
        for (User user1 : followList) {
            idList += user1.getId() + ", ";  // loging
        }

        // 채팅방에 참여하고 있지 않은 사용자,
        List<Long> userChatList = userChatRepository.findAllByChat_Id(chatId).stream()
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
    public void inviteUser(Long chatId, ChatRequestDTO.InviteUserReqDTO reqDTO) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        if (chat.getParticipants() + reqDTO.getInviteUserList().size() > 4){
            throw new ChatHandler(ErrorCode.CANT_INVITE);
        }

        List<Long> userIdList = reqDTO.getInviteUserList().stream()
                .map(ChatRequestDTO.InviteDTO::getUserId).toList();

        List<User> userList = userRepository.findAllByIdIn(userIdList);

        List<String> nameList = reqDTO.getInviteUserList().stream()
                .map(ChatRequestDTO.InviteDTO::getUsername).toList();

        String name="";
        for (String s : nameList) {
            name += s + "님 ";
        }
        name = name.substring(0, name.length()-1);

        // userChat 여러 개 저장
        List<UserChat> newUserChatList = new ArrayList<>();
        for (User user : userList) {
            newUserChatList.add(UserChat.builder()
                    .chat(chat)
                    .user(user)
                    .build());
        }

        // chat : participants 증가 시키기, 이름 변경
        chat.updateParticipants(chat.getParticipants() + newUserChatList.size());
        userChatRepository.saveAll(newUserChatList);

        // 채팅방에 초대 메세지 뿌리기 + save
        String inviteMsg = reqDTO.getUsername() + "님이 " + name + "을 초대하였습니다.";
        messageRepository.save(MessageConverter.toInviteMessage(chat, inviteMsg));
        template.convertAndSend("/sub/" + chatId + "/msg", inviteMsg);
    }

    // 채팅방 이름 변경 메소드, 전체 공통 변경
    @Transactional
    public void renameChat(Long roomId, String newName) {
        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
        chat.renameChat(newName);
    }

    public List<Message> enterChat (Long roomId) {
        // 메세지에 대한 대량의 입출력, MySQL 로는 무겁지 않을까요...?
        return messageRepository.findByChat(chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT)));
    }

    @Transactional
    public void leaveChat(Long chatId, HttpServletRequest request) {
        User user = getUserByJWT(request, "leaveChat");
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        userChatRepository.deleteUserChatByUserAndChat(user, chat);

        chat.updateParticipants(chat.getParticipants()-1);
    }


    public List<ChatResponseDTO.GetChatListDTO> getChatList(HttpServletRequest request) {
        User user = getUserByJWT(request, "getChatList");  // jwt
        List<UserChat> userChats = userChatRepository.findAllByUser_Id(user.getId());

        log.info("userchat: " + userChats.size());
        if (userChats.isEmpty()) throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST);

        List<Chat> allByUserChatListIn = chatRepository.findAllByUserChatListIn(userChats);
        log.info("allByUserChatListIn: " + allByUserChatListIn.size());

        return ChatConverter.toGetChatListDTO(allByUserChatListIn);
    }

    public ChatResponseDTO.BroadcastMsgDTO chatting (Long chatId, ChatRequestDTO.ChattingReqDTO reqDTO) {
        User user = userRepository.findById(reqDTO.getUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
        Profile profile = profileRepository.findByUserId(user.getId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_PROFILE));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));
        Message msg = MessageConverter.toMessage(user, chat, reqDTO);

        // 메세지 저장
        messageRepository.save(msg);
        return MessageConverter.toBroadCastMsgDTO(user.getId(), chatId ,profile, msg);
    }

    public void chattingWithRedis (Long chatId, ChatRequestDTO.ChattingReqDTO reqDTO) {
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


    public void shareCourse (ChatRequestDTO.ShareReqDTO reqDTO){
        User user = userRepository.findById(reqDTO.getUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
        Course course = courseRepository.findById(reqDTO.getCourseId()).orElseThrow(() -> new CourseHandler(ErrorCode.WRONG_COURSE)); // 에러 코드 바꾸기

        Message courseMsg;

        if (reqDTO.getIsChat()){ // 채팅방 공유 시 채팅방 ID 이용
            Chat chat = chatRepository.findById(reqDTO.getChatId()).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
            courseMsg= MessageConverter.toShareMessage(user, chat, course);

            messageRepository.save(courseMsg);

            ChatResponseDTO.BroadcastCourseDTO courseDTO = MessageConverter.toBroadCastCourseDTO(user.getId(), chat.getId(), course);

            // 메세지 BroadCast
            template.convertAndSend("/sub/" + chat.getId() + "/msg", courseDTO);
        }
        else{
            // 친구를 통한 공유, 채팅이 없는 경우 추가
            User targetUser = userRepository.findById(reqDTO.getTargetUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
            Chat privateChat = chatRepository.findPrivateChat(user.getId(), targetUser.getId());
            if (privateChat == null){
                log.info("privateChat is Null!");
                privateChat = ChatConverter.toNewChatConverter(user.getProfile(), targetUser.getProfile());

                List<UserChat> ucList = new ArrayList<>();
                ucList.add(UserChatConverter.toNewUserChat(user, privateChat));
                ucList.add(UserChatConverter.toNewUserChat(targetUser, privateChat));

                Chat saveChat = chatRepository.save(privateChat);
                userChatRepository.saveAll(ucList);

                courseMsg= MessageConverter.toShareMessage(user, saveChat, course);

                // Save And Broadcast
                messageRepository.save(courseMsg);
                ChatResponseDTO.BroadcastCourseDTO courseDTO = MessageConverter.toBroadCastCourseDTO(user.getId(), saveChat.getId(),course);

                // 메세지 BroadCast
                template.convertAndSend("/sub/" + saveChat.getId() + "/msg", courseDTO);
            }
            else{
                log.info("privateChat is Not Null! id = {}", privateChat.getId());
                courseMsg= MessageConverter.toShareMessage(user, privateChat, course);

                // Save And Broadcast
                messageRepository.save(courseMsg);
                ChatResponseDTO.BroadcastCourseDTO courseDTO = MessageConverter.toBroadCastCourseDTO(user.getId(), privateChat.getId(),course);

                // 메세지 BroadCast
                template.convertAndSend("/sub/" + privateChat.getId() + "/msg", courseDTO);
            }
        }
    }

    public void shareCourseWithRedis (ChatRequestDTO.ShareReqDTO reqDTO){
        /// 여려 명 공유 시 채팅방 공유 로직, 카카오톡 공유 화면 참고!

        User user = userRepository.findById(reqDTO.getUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
        Course course = courseRepository.findById(reqDTO.getCourseId()).orElseThrow(() -> new CourseHandler(ErrorCode.WRONG_COURSE)); // 에러 코드 바꾸기

        if (reqDTO.getIsChat()){ // 채팅방 공유 시 채팅방 ID 이용
            Chat chat = chatRepository.findById(reqDTO.getChatId()).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

            messageRepository.save(MessageConverter.toShareMessage(user, chat, course));

            PayloadDTO<Object> payloadDTO = PayloadDTO.builder()
                    .type("share")
                    .payload(MessageConverter.toBroadCastCourseDTO(user.getId(), chat.getId(), course))
                    .build();

            // 메세지 BroadCast
//            redisPublisher.publishMsg("redis.chat."+reqDTO.getChatId(), payloadDTO);
        }
        else{
            // 친구를 통한 공유, 채팅이 없는 경우 추가
            User targetUser = userRepository.findById(reqDTO.getTargetUserId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
            Chat privateChat = chatRepository.findPrivateChat(user.getId(), targetUser.getId());
            if (privateChat == null){
                log.info("privateChat is Null!");
                privateChat = ChatConverter.toNewChatConverter(user.getProfile(), targetUser.getProfile());

                List<UserChat> ucList = new ArrayList<>();
                ucList.add(UserChatConverter.toNewUserChat(user, privateChat));
                ucList.add(UserChatConverter.toNewUserChat(targetUser, privateChat));

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
            }
            else{
                log.info("privateChat is Not Null! id = {}", privateChat.getId());

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


    public User getUserByJWT(HttpServletRequest request, String method) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        log.info("{} -> found User!", method);
        return userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));
    }
}