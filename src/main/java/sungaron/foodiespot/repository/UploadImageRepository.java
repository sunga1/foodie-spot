package sungaron.foodiespot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sungaron.foodiespot.entity.UploadImage;

public interface UploadImageRepository extends JpaRepository<UploadImage,Long> {
}
