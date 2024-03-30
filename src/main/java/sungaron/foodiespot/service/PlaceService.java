package sungaron.foodiespot.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sungaron.foodiespot.dto.PlaceDto;
import sungaron.foodiespot.entity.Like;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.entity.Place;
import sungaron.foodiespot.repository.PlaceRepository;
import sungaron.foodiespot.repository.LikeRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    private final LikeService likeService;
    private final LikeRepository likeRepository;

    // 해당 음식점의 정보를 찾고 없으면 새로운 Place 객체 생성 후 저장
    public Place findPlace(String place_name,String address, String posX, String posY){
        Place place;
        // 해당 음식점이 존재할 경우
        if(placeRepository.existsByPosXAndPosY(posX,posY)){
            place = placeRepository.findByPosXAndPosY(posX, posY).get();
        }
        // 해당 음식점이 존재하지 않을 경우
        else{
            place=savePlace(place_name,address, posX,posY);
        }
        return place;
    }

    // 음식점을 저장하기 위한 메서드
    @Transactional
    public Place savePlace(String place_name, String address,String pos_x,String pos_y){
        //System.out.println(pos_x+"    "+pos_y);
        // 해당 음식점에 관한 정보로 엔티티 생성 후 저장
        Place place = placeRepository.save(Place.toEntity(place_name, address, pos_x, pos_y));
        return place;
    }

    // 특정 회원이 좋아요를 누른 장소의 마커 색깔을 다르게 하기 위해
    public List<PlaceDto> getAllPlace(Member member){
        List<Place> places = placeRepository.findAll();
        //System.out.println(places.size());
        List<PlaceDto> placeDtoList = new ArrayList<>();
        for(Place place:places){
            PlaceDto placeDto = PlaceDto.of(place);
            if(likeService.checkLike(member.getEmail(), place)){
                placeDto.setCheckLike(true);
            }
            placeDtoList.add(placeDto);
        }
        return placeDtoList;
    }

    // 리뷰가 작성된 모든 장소 반환
    public List<PlaceDto> getAllPlace(){
        List<Place> places = placeRepository.findAll();
        List<PlaceDto> placeDtoList = new ArrayList<>();
        for(Place place:places){
            PlaceDto placeDto = PlaceDto.of(place);
            placeDtoList.add(placeDto);
        }
        return placeDtoList;
    }

    // 특정 회원이 찜한 장소 리스트 반환
    public List<PlaceDto> getLikePlace(Member member) {
        // 특정 회원이 누른 좋아요 리스트 반환
        List<Like> likeList=likeRepository.findByMember(member);
        List<PlaceDto> placeDtoList = new ArrayList<>();
        for(Like like:likeList){
            // 해당 likeList 에 해당되는 장소들 리스트로 반환
            PlaceDto placeDto = PlaceDto.of(like.getPlace());
            placeDto.setCheckLike(true);
            placeDtoList.add(placeDto);
        }
        return placeDtoList;
    }
}
