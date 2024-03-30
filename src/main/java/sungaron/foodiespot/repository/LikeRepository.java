package sungaron.foodiespot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sungaron.foodiespot.entity.Like;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.entity.Place;

import java.util.List;

@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {
    void deleteByMemberAndPlace(Member loginMember, Place place);

    Boolean existsByMemberEmailAndPlace(String loginMemberEmail, Place place);

    @Query("select l from Like l " +
            "join fetch l.member m "+
            "join fetch l.place p "+
            "where l.member=:member")
    List<Like> findByMember(@Param("member") Member member);
}
