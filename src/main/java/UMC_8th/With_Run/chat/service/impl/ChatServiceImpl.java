package UMC_8th.With_Run.chat.service.impl;

import UMC_8th.With_Run.chat.converter.ChatConverter;
import UMC_8th.With_Run.chat.converter.MessageConverter;
import UMC_8th.With_Run.chat.converter.UserChatConverter;
import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.chat.repository.ChatRepository;
import UMC_8th.With_Run.chat.repository.MessageRepository;
import UMC_8th.With_Run.chat.repository.UserChatRepository;
import UMC_8th.With_Run.chat.service.ChatService;
import UMC_8th.With_Run.common.apiResponse.status.ErrorCode;
import UMC_8th.With_Run.common.exception.handler.ChatHandler;
import UMC_8th.With_Run.common.exception.handler.UserHandler;
import UMC_8th.With_Run.common.redis.pub_sub.RedisPublisher;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.FollowRepository;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final UserChatRepository userChatRepository;
    private final ProfileRepository profileRepository;
    private final MessageRepository messageRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final SimpMessagingTemplate template;
    private final FollowRepository followRepository;
    private final RedisPublisher redisPublisher;

    /// followee = 내가 팔로우
    /// follower = 나를 팔로우!


    /*
    * COMMON
    * 1. ChatService 분리 (ChatService, MessageService, ~~~)
    * 2. Code Refactoring
    *
    * CHAT
    * 1. EnterChat -> 메세지 조회 페이징 도입
    * 2. Chatting -> 읽지 않은 메세지 수 최적화
    * 3. GetChatList -> 너무 많고 이상한 Stream 최적화. DTO Projection?
    * 4. CreateChat -> 갠톡 있는 경우 개인톡 바로 입장.
    * 5. ShareCourse -> 바로 입장.
    */


    public List<ChatResponseDTO.GetChatListDTO> getChatList(User user) {

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
    public List<ChatResponseDTO.BroadcastMsgDTO> createChat(Long targetId, User user) {
        User targetUser = userRepository.findByIdWithProfile(targetId).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));

        Optional<List<UserChat>> privateChat = userChatRepository.findByTwoUserId(user.getId(), targetUser.getId());

        if (privateChat.isPresent()){ // 이미 갠톡 존재하는 경우 해당 채팅 입장.
            UserChat userChat = privateChat.get().get(0);
            userChat.setToChatting();
            Long chatId = userChat.getChat().getId();
            return MessageConverter.toChatHistoryDTO(messageRepository.findByChat_Id(chatId), chatId); // join fetch!
        }


        Chat chat = ChatConverter.toNewChatConverter();

        List<UserChat> userChats = new ArrayList<>();
        userChats.add(UserChatConverter.toNewUserChat(user, targetUser, chat));
        userChats.add(UserChatConverter.toNewUserChat(targetUser, user, chat));

        chat.addUserChat(userChats.get(0));
        chat.addUserChat(userChats.get(1));

        Chat saveChat = chatRepository.save(chat);
        Long chatId = saveChat.getId();
        userChatRepository.saveAll(userChats);

        ///  메세지 보내기!!
        // redis 처리 전용 dto 변환,

        messageRepository.save(MessageConverter.toFirstChatMessage(user, chat));

        return MessageConverter.toChatHistoryDTO(messageRepository.findByChat_Id(chatId), chatId); // join fetch!
    }

    // 채팅방 이름 변경 메소드, 전체 공통 변경
    @Transactional
    public ChatResponseDTO.RenameChatDTO renameChat(Long chatId, String newName, User user) {
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
    public List<ChatResponseDTO.GetInviteUserDTO> getInviteUser(Long chatId, User user) {

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        // 이미 채팅방이 자신 포함 4명이라면 초대 목록 조회도 거절
        if (chat.getParticipants() >= 4) {
            throw new ChatHandler(ErrorCode.CHAT_IS_FULL);
        }

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
    public void inviteUser(Long chatId, ChatRequestDTO.InviteUserReqDTO reqDTO, User user) {

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
        for (ChatRequestDTO.InviteDTO dto : inviteUserList) {

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
            if (!userChat.getIsDefaultChatName()) { // 각 유저에 대해 커스텀 ChatName 이 아닌 경우 Update
                continue;
            }

            Long currentUserId = userChat.getUser().getId();

            String collect = newUserChat.stream()
                    .filter(other -> !other.getUser().getId().equals(currentUserId))
                    .map(otherUserChat -> otherUserChat.getUser().getProfile().getName())
                    .collect(Collectors.joining(", "));
            String otherName = userChat.getChatName() + ", " + collect;

            log.info("{}'s name = {}", userChat.getUser().getProfile().getName(), otherName);

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
        messageRepository.save(MessageConverter.toInviteMessage(user, chat, inviteMsg));
        template.convertAndSend("/sub/" + chatId + "/msg", inviteMsg);
    }

    @Transactional
    public List<ChatResponseDTO.BroadcastMsgDTO> enterChat(Long chatId, User user) { // 메세지에 대한 대량의 입출력, MySQL 로는 무겁지 않을까요...?
        ///  TODO 사용자에 대한 읽지 않은 메세지 수 0으로 세팅
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        // 사용자의 읽지 않은 메세지 수 0 + isChatting = true
        userChat.setToChatting();

        return MessageConverter.toChatHistoryDTO(messageRepository.findByChat_Id(chatId), chatId); // join fetch!
    }


    @Transactional
    public void leaveChat(Long chatId,User user) {
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
        userChat.setToNotChatting();
    }

    @Transactional
    public void deleteChat(Long chatId, User user) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        userChatRepository.delete(userChat);

        Integer participants = chat.getParticipants();

        if (participants - 1 == 0) {
            chatRepository.deleteById(chatId);
        } else {
            chat.updateParticipants(participants - 1);
        }
    }


    /*public User getUserByJWT(HttpServletRequest request, String method) { // join fetch 를 통한 조회
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();

        log.info("ChatService.getUserByJWT() - {} -> found User!", method);
        return userRepository.findByEmailJoinFetch(email).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));
    }*/
}