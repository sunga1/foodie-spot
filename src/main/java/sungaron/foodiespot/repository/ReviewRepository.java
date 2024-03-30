package sungaron.foodiespot.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.entity.Place;
import sungaron.foodiespot.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review,Long> {

    // 멤버의 email을 통해 특정 멤버가 작성한 모든 리뷰를 반환하기 위한 메서드
    @Query("select r from Review r " +
            "join fetch r.member m "+
            "join fetch r.place p "+
            "left join fetch r.uploadImages u "+
            "where r.member.email=:email")
    List<Review> findByMemberEmail(@Param("email")String memberEmail);

    // 특정 멤버가 작성한 리뷰의 개수를 알기 위한 메서드
    @Query("select count(r) from Review r "+
            "where r.member.email = :email")
    Long countReviewByMemberEmail(@Param("email") String email);

    // 특정 멤버가 특정 장소에 대해 이미 리뷰를 작성했는지 확인하는 메서드
    boolean existsByMemberAndPlace_PosXAndPlace_PosY(Member member, String posX, String posY);

    List<Review> findByPlace(Place place);
}
