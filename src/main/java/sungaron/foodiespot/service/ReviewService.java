package sungaron.foodiespot.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;
import sungaron.foodiespot.dto.ReviewCreateRequest;
import sungaron.foodiespot.dto.ReviewDto;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.entity.Place;
import sungaron.foodiespot.entity.Review;
import sungaron.foodiespot.entity.UploadImage;
import sungaron.foodiespot.repository.PlaceRepository;
import sungaron.foodiespot.repository.ReviewRepository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UploadImageService uploadImageService;
    private final PlaceService placeService;
    private final PlaceRepository placeRepository;

    // 리뷰 등록 시 검증하기 위한 메서드
    public BindingResult validReview(Member member, ReviewCreateRequest req,BindingResult bindingResult){
        // 음식점 상호명이 비어있을 경우
        if(req.getPlace_name().isEmpty()){
            bindingResult.addError(new FieldError("req","place_name","음식점을 선택해주세요."));
        }
        // 특정 회원이 해당 음식점의 리뷰를 이미 작성했는지 확인하기 위해
        else if(reviewRepository.existsByMemberAndPlace_PosXAndPlace_PosY(member, req.getPosX(), req.getPosY())) {
            bindingResult.addError(new FieldError("req","place_name","리뷰를 이미 작성한 장소입니다."));
        }
        // 먹음 음식을 입력하지 않은 경우
        if(req.getEatFoods().isEmpty()||req.getEatFoods().size()==0){
            bindingResult.addError(new FieldError("req","eatFoods","먹은 음식을 작성해 주세요."));
        }
        // 제목이 비어있을 경우
        if(req.getTitle().isEmpty()){
            bindingResult.addError(new FieldError("req","title","제목이 비어있습니다."));
        }
        if(req.getRating().isEmpty()){
            bindingResult.addError(new FieldError("req","rating","평점이 비어있습니다."));
        }
        return bindingResult;
    }

    // 리뷰를 저장하기 위한 메서드
    @Transactional
    public Review saveReview(ReviewCreateRequest req, Member member) throws IOException {

        Place place = placeService.findPlace(req.getPlace_name(), req.getAddress(), req.getPosX(), req.getPosY());
        Review review = Review.toEntity(req, place, member);

        // 리뷰 저장
        Review saveReview = reviewRepository.save(review);

        // 업로드된 이미지들을 저장
        for (MultipartFile file : req.getMultipartFiles()) {
            UploadImage uploadImage = this.uploadImageService.saveReviewImage(file, saveReview);
            saveReview.getUploadImages().add(uploadImage);
        }
        // member 에도 review 추가
        member.getReviews().add(saveReview);
        // place 에도 review 추가
        place.getReviews().add(saveReview);

        return saveReview;
    }

    // 리뷰를 삭제하기 위한 메서드
    @Transactional
    public void deleteReview(Long reviewId) {
        // 리뷰 id로 리뷰를 찾아서 반환
        Review review = reviewRepository.findById(reviewId).get();
        Place place = review.getPlace();
        reviewRepository.delete(review);
        place.getReviews().remove(review);
        // 해당 음식점의 리뷰가 없으면 음식점의 정보도 함께 사라짐
        if(place.getReviews().size()==0){
            placeRepository.delete(place);
        }
    }

    // 특정 멤버가 쓴 리뷰를 반환하는 메서드
    public List<ReviewDto> getMyReviews(String memberEmail){
        List<Review> myReviews = reviewRepository.findByMemberEmail(memberEmail);
        // 리뷰를 dto로 반환하기 위해 만든 객체
        List<ReviewDto> myReviewDtos = new ArrayList<>();
        for(Review myReview:myReviews){
            List<UploadImage> uploadImages = myReview.getUploadImages();
            // 이미지의 파일 경로만 저장하기 위해 만든 리스트
            List<String> imagePath=getFilePathList(uploadImages);
            // 리뷰 객체와 이미지 경로를 저장한 리스트를 파라미터 값으로 전달
            ReviewDto reviewDto = ReviewDto.of(myReview,imagePath);

            myReviewDtos.add(reviewDto);
        }
        return myReviewDtos;
    }

    // 리뷰 내 저장된 이미지들의 경로를 모두 리스트에 저장 후 반환하기 위한 함수
    private List<String> getFilePathList(List<UploadImage> uploadImages){
        // 이미지의 파일 경로만 저장하기 위해 만든 리스트
        List<String> imagePath=new ArrayList<>();
        for(UploadImage uploadImage:uploadImages){
            String filePath = uploadImage.getSavedFilename();
            imagePath.add(filePath);
        }
        return imagePath;
    }
    // 리뷰를 수정하기 위한 메서드
    @Transactional
    public void modifyReview(ReviewDto reviewDto) throws IOException{
        Review review = reviewRepository.findById(reviewDto.getId()).get();
        Review modifyReview = review.modify(reviewDto.getEatFoods(), reviewDto.getTitle(),reviewDto.getContent(), reviewDto.getRating());
        // 업로드한 이미지가 존재할 경우
        if(reviewDto.getMultipartFiles().size()>0){
            // 해당 리뷰에 저장되어 있는 이미지들을 삭제 후 업로드한 이미지 저장
            List<UploadImage> uploadImages = modifyReview.getUploadImages();
            modifyReview.uploadImageListClear();
            for(UploadImage uploadImage:uploadImages){
                uploadImageService.deleteImage(uploadImage);
            }
            // 업로드된 이미지들을 저장
            for (MultipartFile file : reviewDto.getMultipartFiles()) {
                UploadImage uploadImage = this.uploadImageService.saveReviewImage(file, modifyReview);
                modifyReview.getUploadImages().add(uploadImage);
            }
        }
    }

    //특정 장소에 작성된 리뷰 리스트 반환
    public List<ReviewDto> getReviewByPlace(String posX, String posY) {
        // 장소 찾은 후
        Place place = placeRepository.findByPosXAndPosY(posX,posY).get();
        // 해당 장소에 해당하는 리뷰들 조회
        List<Review> reviewByPlace = reviewRepository.findByPlace(place);
        List<ReviewDto> reviewDtos =new ArrayList<>();
        for(Review review:reviewByPlace){
            // 리뷰에 저장된 이미지들의 파일 경로 리스트에 모두 저장 후
            List<String> filePathList = getFilePathList(review.getUploadImages());
            // dto로 반환
            ReviewDto reviewDto = ReviewDto.of(review, filePathList);
            reviewDtos.add(reviewDto);
        }
        return reviewDtos;
    }
}
