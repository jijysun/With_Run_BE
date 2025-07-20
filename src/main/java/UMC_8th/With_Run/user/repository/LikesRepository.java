package UMC_8th.With_Run.user.repository;

import UMC_8th.With_Run.user.entity.Likes;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    List<Likes> findAllByUserId(Long userId);
}

