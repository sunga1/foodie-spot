package sungaron.foodiespot.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import sungaron.foodiespot.entity.Review;
import sungaron.foodiespot.entity.UploadImage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ReviewDto {
    private Long id;
    private String place_name;
    private String address;
    private String posX;
    private String posY;
    private String memberNickname;
    private List<String> eatFoods;
    private List<String> uploadImgPath;
    private List<MultipartFile> multipartFiles;
    private String rating;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static ReviewDto of(Review review,List<String>imgPath) {

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.id = review.getId();
        reviewDto.place_name = review.getPlace().getPlace_name();
        reviewDto.memberNickname=review.getMember().getNickname();
        reviewDto.address = review.getPlace().getAddress();
        reviewDto.posX = review.getPlace().getPosX();
        reviewDto.posY = review.getPlace().getPosY();
        reviewDto.eatFoods = review.getEatFoods();
        reviewDto.rating= review.getRating();
        reviewDto.title= review.getTitle();
        reviewDto.content = review.getContent();
        reviewDto.uploadImgPath = imgPath;
        reviewDto.createdAt = review.getCreatedAt();
        return reviewDto;
    }
}
