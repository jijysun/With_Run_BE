package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("Select m From Message m JOIN fetch m.user u JOIN fetch u.profile where m.chat.id = :chatId") // Join Fetch!
    List<Message> findByChat_Id(@Param("chatId") Long chatId);

    long countMessageByChat_Id(Long chatId);
}
