package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

//    List<UserChat> findAllByUser(User user);

    List<UserChat> findAllByUser_Id(Long userId);

    void deleteUserChatByUserAndChat(User user, Chat chat);

    List<UserChat> findAllByChat_Id(Long chatId);

    UserChat findByUser_IdAndChat_Id(Long userId, Long chatId);

    List<UserChat> findAllByChat_IdAndIsChattingFalse(Long chatId);
}
