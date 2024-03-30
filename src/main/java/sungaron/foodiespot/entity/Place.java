package sungaron.foodiespot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Place {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String place_name;
    private String address;
    private String posX; //위도
    private String posY; //경도

    @OneToMany(mappedBy = "place", orphanRemoval = true)
    private List<Review> reviews;

    public static Place toEntity(String place_name,String address,String posX,String posY){
        return Place.builder()
                .place_name(place_name)
                .address(address)
                .posX(posX)
                .posY(posY)
                .reviews(new ArrayList<>())
                .build();
    }
}
