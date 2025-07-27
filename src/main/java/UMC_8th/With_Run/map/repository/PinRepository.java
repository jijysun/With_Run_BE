package UMC_8th.With_Run.map.repository;

import UMC_8th.With_Run.map.entity.Pin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PinRepository extends JpaRepository<Pin, Long> {
    List<Pin> findAllByIdIn(List<Long> ids); // ID 목록으로 여러 핀을 가져오기 위함
}
