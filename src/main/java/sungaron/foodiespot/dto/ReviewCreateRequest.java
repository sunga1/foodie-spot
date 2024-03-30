package sungaron.foodiespot.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
import sungaron.foodiespot.entity.Review;

import java.util.List;

@Data
@NoArgsConstructor
public class ReviewCreateRequest {
    private String place_name;
    private String address;
    private String posX;
    private String posY;
    private List<String> eatFoods;
    private List<MultipartFile> multipartFiles;
    private String rating;
    private String title;
    private String content;


}
