package UMC_8th.With_Run.user.repository;

import UMC_8th.With_Run.user.entity.Scraps;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScrapsRepository extends JpaRepository<Scraps, Long> {
    List<Scraps> findAllByUserId(Long userId);
}
