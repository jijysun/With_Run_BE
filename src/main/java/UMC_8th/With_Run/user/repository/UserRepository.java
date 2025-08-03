package UMC_8th.With_Run.user.repository;

import UMC_8th.With_Run.chat.entity.mapping.UserChat;
import UMC_8th.With_Run.user.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);


    List<User> findAllByIdIn(Collection<Long> ids);

    List<User> findAllByFollowerAndUserChatListNotInOrderById(User follower, Collection<List<UserChat>> userChatLists);


    @Query("Select u From User u join fetch u.profile where u.email = :email ")
    User findByEmailJoinFetch(@Param("email") String email);

}
