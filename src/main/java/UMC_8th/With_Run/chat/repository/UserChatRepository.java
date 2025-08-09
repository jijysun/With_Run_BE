package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.dto.ChatResponseDTO;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

    @Query(value = "SELECT " +
            "c.id AS chatId, uc.chat_name AS chatName, c.last_received_msg AS lastReceivedMsg, c.participants AS participants, uc.un_read_msg AS unReadMsg, " +
            " GROUP_CONCAT(p.name ORDER BY other_u.id) AS usernames, " +
            " GROUP_CONCAT(p.profile_image ORDER BY other_u.id) AS profileImages " +
            "FROM user_chat uc " +
            "JOIN chat c ON uc.chat_id = c.id " +
            "LEFT JOIN user_chat other_uc ON other_uc.chat_id = c.id AND other_uc.user_id != uc.user_id " +
            "LEFT JOIN user other_u ON other_uc.user_id = other_u.id " +
            "LEFT JOIN profile p ON other_u.id = p.user_id " +
            "WHERE uc.user_id = :userId " +
            "GROUP BY c.id, uc.chat_name, c.last_received_msg, c.participants",
            nativeQuery = true)
    List<ChatResponseDTO.GetChatListSQLDTO> getChatList(@Param("userId") Long userId);

    @Query ("select uc From UserChat uc join fetch uc.chat where uc.chat.id IN (" +
            "Select c.id From Chat c JOIN c.userChatList uc2 Where c.participants = 2 AND uc2.user.id in (:id1, :id2)" +
              " GROUP BY c.id having count (uc2.chat.id) = 2)")
    Optional<List<UserChat>> findByTwoUserId(@Param("id1")Long userId1, @Param("id2") Long userId2);

    @Query ("Select uc From UserChat uc join fetch uc.user u join fetch u.profile where uc.chat.id = :chatId ")
    List<UserChat> findAllByChat_IdJoinFetchUserAndProfile(@Param("chatId") Long chatId);

    Optional<UserChat> findByUser_IdAndChat_Id(Long userId, Long chatId);

    List<UserChat> findAllByChat_IdAndIsChattingFalse(Long chatId);
    
}
