package UMC_8th.With_Run.user.repository;

import UMC_8th.With_Run.user.entity.Follow;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    List<Follow> findAllByUserId(Long userId);

    List<Follow> findAllByTargetUserId(Long targetUserId);

    Optional<Follow> findByUserIdAndTargetUserId(Long userId, Long targetUserId);
}

