package UMC_8th.With_Run.chat.service;

import UMC_8th.With_Run.chat.converter.ChatConverter;
import UMC_8th.With_Run.chat.converter.UserChatConverter;
import UMC_8th.With_Run.chat.dto.ChatRequestDTO;
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
import UMC_8th.With_Run.common.exception.handler.UserHandler;
import UMC_8th.With_Run.common.security.jwt.JwtTokenProvider;
import UMC_8th.With_Run.user.entity.Profile;
import UMC_8th.With_Run.user.entity.User;
import UMC_8th.With_Run.user.repository.ProfileRepository;
import UMC_8th.With_Run.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional
    public void createChat(Long targetId, HttpServletRequest request) {
        User user = getUserByJWT(request); // jwt
        Profile userProfile = profileRepository.findByUserId(user.getId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));

        User targetUser = userRepository.findById(3L).orElseThrow(()-> new UserHandler(ErrorCode.WRONG_USER)); // targetUser는 비영속 상태이다, targetUser에 대한 update, save는 필요
        Profile targetProfile = profileRepository.findByUserId(targetUser.getId()).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER));

        Chat chat = ChatConverter.toNewChatConverter(userProfile, targetProfile);

        List<UserChat> userChats = new  ArrayList<>();
        userChats.add(UserChatConverter.toUserChat(user, chat));
        userChats.add(UserChatConverter.toUserChat(targetUser, chat));

        chat.addUserChat(userChats.get(0));
        chat.addUserChat(userChats.get(1));

        chatRepository.save(chat);
        userChatRepository.saveAll(userChats);
    }

    public void getInviteUser(Long chatId) {

        // 사용자와 친구 관계이며, 채팅방에 참여하고 있지 않은 사용자 반환
    }

    @Transactional
    public void inviteUser(Long roomId, ChatRequestDTO.InviteUserReqDTO reqDTO) {
        List<User> users = userRepository.findAllByIdIn(reqDTO.getUserIds()); // 받은 id에 대한 여러 user 조회, JWT

//        users.add(userRepository.findById(3L).orElseThrow(() -> new UserHandler(ErrorCode.WRONG_USER))); // test code

//        Chat chat = chatRepository.findById(1L).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST)); // test
        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));

        // userChat 여러 개 저장
        List<UserChat> newUserList = new ArrayList<>();
        for (User user : users) {
            newUserList.add(UserChat.builder()
                    .chat(chat)
                    .user(user)
                    .build());
        }

        // chat : participants 증가 시키기, 이름 변경
        chat.updateParticipants(chat.getParticipants() + users.size());
        userChatRepository.saveAll(newUserList);
    }

    @Transactional
    public void renameChat(Long roomId, String newName) {
        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));
        chat.renameChat(newName);
    }

    public List<Message> enterChat (Long roomId) {
        Chat chat = chatRepository.findById(roomId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));
        return messageRepository.findByChat(chat);
    }

    @Transactional
    public void leaveChat(Long chatId, HttpServletRequest request) {
        // User user = userRepository.findById(3L).orElseThrow(()-> new ChatHandler(ErrorCode.WRONG_USER)); // test Code
        User user = getUserByJWT(request);
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new ChatHandler(ErrorCode.EMPTY_CHAT_LIST));

        userChatRepository.deleteUserChatByUserAndChat(user, chat);

        chat.updateParticipants(chat.getParticipants()-1);
    }


    public List<Chat> getChatList(HttpServletRequest request) {
        User user = getUserByJWT(request);  // jwt
        List<UserChat> userChats = userChatRepository.findAllByUser(user);

        log.info("userchat: " + userChats.size());
        if (userChats.isEmpty()) throw new ChatHandler(ErrorCode.EMPTY_CHAT_LIST);

        List<Chat> allByUserChatListIn = chatRepository.findAllByUserChatListIn(userChats);
        log.info("allByUserChatListIn: " + allByUserChatListIn.size());
        return allByUserChatListIn;
    }

    public void chatting (){

    }


    public void shareCourse (){

    }


    public User getUserByJWT(HttpServletRequest request) {
        Authentication authentication = jwtTokenProvider.extractAuthentication(request);
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElseThrow(() -> new GeneralException(ErrorStatus.WRONG_USER));
    }


}