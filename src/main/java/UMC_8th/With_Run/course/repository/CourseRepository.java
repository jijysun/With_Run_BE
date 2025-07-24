package UMC_8th.With_Run.course.repository;

import UMC_8th.With_Run.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
