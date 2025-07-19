package UMC_8th.With_Run.user.repository;

import UMC_8th.With_Run.user.entity.Profile;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import UMC_8th.With_Run.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(Long userId);

    List<Profile> findAllByUserInOrderByUser_Id(Collection<User> users);
}
