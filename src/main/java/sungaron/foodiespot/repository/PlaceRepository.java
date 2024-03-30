package sungaron.foodiespot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sungaron.foodiespot.entity.Place;

import java.util.List;
import java.util.Optional;

public interface PlaceRepository extends JpaRepository<Place,Long> {
    // pos_x와 pos_y값이 모두 일치하는 장소가 있는지 확인
    boolean existsByPosXAndPosY(String posX, String posY);

    // pos_x와 pos_y값이 모두 일치하는 장소 반환
    Optional<Place> findByPosXAndPosY(String posX, String posY);
}
