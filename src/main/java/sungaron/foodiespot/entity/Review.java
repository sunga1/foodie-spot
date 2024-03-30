package sungaron.foodiespot.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import sungaron.foodiespot.dto.ReviewCreateRequest;

import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Review extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="place_id")
    private Place place;

    private List<String> eatFoods;

    private String rating;
    private String title;
    private String content;

    @OneToMany(mappedBy = "review")
    private List<UploadImage> uploadImages;

    public static Review toEntity(ReviewCreateRequest reviewCreateRequest,Place place, Member member){
        return Review.builder()
                .member(member)
                .place(place)
                .eatFoods(reviewCreateRequest.getEatFoods())
                .rating(reviewCreateRequest.getRating())
                .title(reviewCreateRequest.getTitle())
                .content(reviewCreateRequest.getContent())
                .uploadImages(new ArrayList<UploadImage>())
                .build();

    }

    public Review modify(List<String> eatFoods,String title,String content,String rating){
        if(eatFoods.size()>0){
            this.eatFoods=eatFoods;
        }
        if(!title.isEmpty()){
            this.title=title;
        }
        if(!rating.isEmpty()){
            this.rating=rating;
        }
        if(!content.isEmpty()) {
            this.content = content;
        }
        return this;
    }

    public void uploadImageListClear() {
        this.uploadImages=new ArrayList<>();
    }
}
