package UMC_8th.With_Run.friend.repository;

import UMC_8th.With_Run.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AllFriendsRepository extends JpaRepository<User, Long>, AllFriendsRepositoryCustom {

}

