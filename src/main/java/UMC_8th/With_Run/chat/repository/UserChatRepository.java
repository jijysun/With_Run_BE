package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

    @Query(" SELECT uc.chat " +
            "FROM UserChat uc " +
            "where uc.user.id IN (:user1Id, :user2Id)" +
            "GROUP BY uc.chat.id " +
            "HAVING count (distinct uc.user.id) = 2 AND count (uc.chat.id) = 2")
    Optional<Chat> findPrivateChat(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    List<UserChat> findAllByUser(User user);

    void deleteUserChatByUserAndChat(User user, Chat chat);

    List<UserChat> findAllByChat_Id(Long chatId);

    List<UserChat> findAllByUser_Id(Long userId);
}
