package sungaron.foodiespot.dto;

import lombok.Builder;
import lombok.Data;
import sungaron.foodiespot.entity.Place;

@Data
@Builder
public class PlaceDto {
    private String place_name;
    private String address;
    private String posX; //위도
    private String posY; //경도
    private boolean checkLike;
    public static PlaceDto of(Place place){
        return PlaceDto.builder()
                .place_name(place.getPlace_name())
                .address(place.getAddress())
                .posX(place.getPosX())
                .posY(place.getPosY())
                .checkLike(false)
                .build();
    }
}
