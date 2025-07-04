package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.entity.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
}
