package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

    List<UserChat> findAllByUser(User user);

    List<UserChat> findAllByUserId(Long userId); // 채팅방 목록 조회

    List<UserChat> findAllByUserIn(Collection<User> users);

    List<UserChat> findAllByIdIn(Collection<Long> ids);

    UserChat findByUserAndChat(User user, Chat chat);

    void deleteUserChatByUserAndChat(User user, Chat chat);
}
