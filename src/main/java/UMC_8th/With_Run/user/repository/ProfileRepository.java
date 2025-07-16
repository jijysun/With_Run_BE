package UMC_8th.With_Run.user.repository;

import UMC_8th.With_Run.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
