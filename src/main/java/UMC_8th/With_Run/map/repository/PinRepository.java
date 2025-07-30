package UMC_8th.With_Run.map.repository;

import UMC_8th.With_Run.course.entity.Course;
import UMC_8th.With_Run.map.entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PinRepository extends JpaRepository<Pin, Long> {
    List<Pin> findByCourseOrderByPinOrderAsc(Course course);
}
