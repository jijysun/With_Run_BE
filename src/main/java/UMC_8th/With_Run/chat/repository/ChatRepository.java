package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.entity.Chat;
import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;


public interface ChatRepository extends JpaRepository<Chat, Long>{

    List<Chat> findAllByUserChatListContains(List<UserChat> userChatList);

    List<Chat> findAllByUserChatListIn(List<UserChat> userChatList);
}
