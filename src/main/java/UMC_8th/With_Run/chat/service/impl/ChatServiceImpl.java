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

import java.util.*;
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
    private final SimpMessagingTemplate template;
    private final FollowRepository followRepository;

    /// followee = 내가 팔로우
    /// follower = 나를 팔로우!


    /*
     * COMMON
     * - x
     *
     * CHAT
     * 1. EnterChat -> 메세지 조회 페이징 도입
     * 2. Chatting -> 읽지 않은 메세지 수 최적화
     */

    @Transactional(readOnly = true)
    public List<ChatResponseDTO.GetChatListDTO> getChatList(User user) {
        List<ChatResponseDTO.GetChatListSQLDTO> chatList = userChatRepository.getChatList(user.getId());

        if (chatList.isEmpty()) throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST); // 성공 코드로 전환?

        return chatList.stream()
                .map(result -> {
                    List<String> usernameList = Arrays.asList(result.getUsernames().split(","));
                    List<String> profileList = Arrays.asList(result.getProfileImages().split(","));

                    return ChatConverter.toGetChatListDTO(result, usernameList, profileList);
                })
                .collect(Collectors.toList());
    }

    // 채팅 첫 생성 메소드
    @Transactional
    public ChatResponseDTO.CreateChatDTO createChat(Long targetId, User user) {
        User targetUser = userRepository.findByIdWithProfile(targetId).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));

        Optional<List<UserChat>> privateChat = userChatRepository.findByTwoUserId(user.getId(), targetUser.getId());

        if (privateChat.isPresent()) { // 이미 갠톡 존재하는 경우 해당 채팅 입장.
            UserChat userChat = privateChat.get().get(0);
            userChat.setToChatting();
            Long chatId = userChat.getChat().getId();
            List<ChatResponseDTO.BroadcastMsgDTO> chatHistoryDTO = MessageConverter.toChatHistoryDTO(messageRepository.findByChat_Id(chatId), chatId);
            return ChatConverter.toCreateChatDTO(chatId, chatHistoryDTO); // join fetch!
        }


        Chat chat = ChatConverter.toNewChatConverter();

        List<UserChat> userChats = new ArrayList<>();
        UserChat newUserChat = UserChatConverter.toNewUserChat(user, targetUser, null, chat);
        newUserChat.setToChatting();
        userChats.add(newUserChat);
        userChats.add(UserChatConverter.toNewUserChat(targetUser, user, null,  chat));

        chat.addUserChat(userChats.get(0));
        chat.addUserChat(userChats.get(1));

        Chat saveChat = chatRepository.save(chat);
        Long chatId = saveChat.getId();
        userChatRepository.saveAll(userChats);

        ///  메세지 보내기!!
        // redis 처리 전용 dto 변환,

        messageRepository.save(MessageConverter.toFirstChatMessage(user, chat));
        return ChatConverter.toCreateChatDTO(chatId, MessageConverter.toChatHistoryDTO(messageRepository.findByChat_Id(chatId), chatId));
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

        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        List<ChatRequestDTO.InviteDTO> inviteUserList = reqDTO.getInviteUserList();

        if (chat.getParticipants() + inviteUserList.size() > 4) {
            throw new ChatHandler(ErrorCode.CANT_INVITE_MORE_FOUR);
        }

        List<Long> invitedUserIdList = inviteUserList.stream()
                .map(ChatRequestDTO.InviteDTO::getUserId)
                .toList();

        if (userChatRepository.findAlreadyInvited(chatId, invitedUserIdList)) {
            throw new UserHandler(ErrorCode.ALREADY_PARTICIPATING);
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
            newUserChat.add(UserChatConverter.toNewUserChat(user, null, joinChatName, chat));
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
    public List<ChatResponseDTO.BroadcastMsgDTO> enterChat(Long chatId, User user) { // 메세지에 대한 대량의 입출력, MySQL 로는 무겁지 않을까요...
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        // 사용자의 읽지 않은 메세지 수 0 + isChatting = true -> redis
        userChat.setToChatting();

        return MessageConverter.toChatHistoryDTO(messageRepository.findByChat_Id(chatId), chatId); // join fetch!
    }


    @Transactional
    public void leaveChat(Long chatId, User user) {
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
        userChat.setToNotChatting(); // -> redis
    }

    @Transactional
    public void deleteChat(Long chatId, User user) {
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));
        UserChat userChat = userChatRepository.findByUser_IdAndChat_Id(user.getId(), chatId).orElseThrow(() -> new ChatHandler(ErrorCode.WRONG_CHAT));

        
        // redis 삭제 로직 추가
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