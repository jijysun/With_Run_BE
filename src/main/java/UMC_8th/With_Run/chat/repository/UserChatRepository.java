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

    @Query("Select uc from UserChat uc join fetch uc.chat Where uc.user.id = :userId")
    List<UserChat> findAllByUser_IdWithChat(@Param("userId") Long userId);

    @Query("Select uc From UserChat uc JOIN FETCH uc.chat JOIN FETCH uc.user where uc.user.id = :userId")
    List<UserChat> findAllByUserIdWithChatAndParticipants(@Param("userId") Long userId);


    void deleteUserChatByUserAndChat(User user, Chat chat);


    /**
     * join fetch!
     * @param chatId
     * @return
     */
    @Query ("Select uc From UserChat uc join fetch uc.user u join fetch u.profile where uc.chat.id = :chatId ")
    List<UserChat> findAllByChat_Id(@Param("chatId") Long chatId);

    Optional<UserChat> findByUser_IdAndChat_Id(Long userId, Long chatId);

    List<UserChat> findAllByChat_IdAndIsChattingFalse(Long chatId);

    List<UserChat> findAllByChat_IdIn(Collection<Long> chatIds);

}
