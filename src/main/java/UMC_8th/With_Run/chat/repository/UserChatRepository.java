package UMC_8th.With_Run.chat.repository;

import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserChatRepository extends JpaRepository<UserChat, Long> {

    List<UserChat> findAllByUser(User user);

    List<UserChat> findAllByUserId(Long userId); // 채팅방 목록 조회
}
