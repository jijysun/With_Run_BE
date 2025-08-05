package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

    @Query("Select uc From UserChat uc JOIN FETCH uc.chat JOIN FETCH uc.user u LEFT JOIN FETCH u.profile where uc.user.id = :userId")
    List<UserChat> findAllByUserIdJoinFetchChatUserAndProfile(@Param("userId") Long userId);

    @Query ("Select uc From UserChat uc join fetch uc.user u join fetch u.profile where uc.chat.id = :chatId ")
    List<UserChat> findAllByChat_IdJoinFetchUserAndProfile(@Param("chatId") Long chatId);

    Optional<UserChat> findByUser_IdAndChat_Id(Long userId, Long chatId);

    List<UserChat> findAllByChat_IdAndIsChattingFalse(Long chatId);


    @Query("select uc From UserChat uc JOIN FETCH uc.user u JOIN FETCH u.profile where uc.chat.id IN :chatIdList")
    List<UserChat> findAllByChat_IdInJoinFetchUserAndProfile(@Param("chatIdList") Collection<Long> chatIdList);

}
