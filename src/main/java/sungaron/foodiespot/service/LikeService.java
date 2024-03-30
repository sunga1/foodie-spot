package sungaron.foodiespot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sungaron.foodiespot.entity.Like;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.entity.Place;
import sungaron.foodiespot.repository.LikeRepository;
import sungaron.foodiespot.repository.MemberRepository;
import sungaron.foodiespot.repository.PlaceRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;
    private final PlaceRepository placeRepository;

    // Like 생성
    @Transactional
    public void addLike(Member loginMember, Place place){

        likeRepository.save(Like.builder()
                .member(loginMember)
                .place(place)
                .build()
        );
    }

    // Like 삭제
    @Transactional
    public void deleteLike(Member loginMember, Place place){

        likeRepository.deleteByMemberAndPlace(loginMember,place);
    }

    // 특정 회원이 특정 장소에 좋아요 눌렀는지 확인하기 위해
    public Boolean checkLike(String loginMemberEmail, Place place){
        return likeRepository.existsByMemberEmailAndPlace(loginMemberEmail,place);
    }


}

