package sungaron.foodiespot.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sungaron.foodiespot.dto.PlaceDto;
import sungaron.foodiespot.dto.ReviewCreateRequest;
import sungaron.foodiespot.dto.ReviewDto;
import sungaron.foodiespot.dto.SessionUser;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.entity.Review;
import sungaron.foodiespot.repository.MemberRepository;
import sungaron.foodiespot.repository.ReviewRepository;
import sungaron.foodiespot.service.LikeService;
import sungaron.foodiespot.service.PlaceService;
import sungaron.foodiespot.service.ReviewService;
import sungaron.foodiespot.service.UploadImageService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final HttpSession httpSession;
    private final ReviewService reviewService;
    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final UploadImageService uploadImageService;
    private final PlaceService placeService;
    private final LikeService likeService;
    @GetMapping("/create")
    public String reviewPage(Model model) {
        model.addAttribute("reviewCreateRequest",new ReviewCreateRequest());
        return "/reviews/create";
    }
    @PostMapping("/create")
    public String createReview(@Valid  @ModelAttribute ReviewCreateRequest req, Model model,BindingResult bindingResult) throws IOException {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        Member member = memberRepository.findByEmail(user.getEmail()).get();
        if(reviewService.validReview(member,req,bindingResult).hasErrors()){
            return "/reviews/create";
        }
        else{
            reviewService.saveReview(req,member);
            model.addAttribute("message","리뷰가 등록되었습니다!");
            model.addAttribute("nextUrl","/members/myPage");
            return "printMessage";
        }
    }
    @GetMapping("/search")
    public String searchPlace(Model model){
        return "reviews/search";
    }

    @PostMapping("/delete/{reviewId}")
    public String reviewDelete(@PathVariable Long reviewId, Model model){
        reviewService.deleteReview(reviewId);
        model.addAttribute("message","리뷰가 삭제되었습니다!");
        model.addAttribute("nextUrl","/members/myPage");

        return "printMessage";
    }

    @GetMapping("/myReview")
    public String myReview(Model model,@RequestParam(required = false,defaultValue = "1") int page){
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        List<ReviewDto> reviewDtoList = reviewService.getMyReviews(user.getEmail());
        PageRequest pageRequest = PageRequest.of(page - 1, 15, Sort.by("id").descending());
        Long myReviewCnt = reviewRepository.countReviewByMemberEmail(user.getEmail());
        Page<ReviewDto> reviewDtos = new PageImpl<>(reviewDtoList, pageRequest, myReviewCnt);
        model.addAttribute("reviewDtos",reviewDtos);
        model.addAttribute("reviewDto",new ReviewDto());
        return "reviews/myReview";
    }

    @PostMapping("/modify")
    public String modifyReview(Model model,@ModelAttribute ReviewDto reviewDto)throws IOException{
        reviewService.modifyReview(reviewDto);
        model.addAttribute("message","리뷰가 수정되었습니다!");
        model.addAttribute("nextUrl","/members/myPage");
        return "printMessage";
    }

    @ResponseBody
    @GetMapping("/images/{filename}")
    public Resource showImage(@PathVariable String filename) throws MalformedURLException {
        return new UrlResource("file:"+uploadImageService.getFullPath(filename));
    }

    @GetMapping("/byPlace")
    public ResponseEntity<List<ReviewDto>> getReviewByPlace(@RequestParam("posX") String posX,
                                                            @RequestParam("posY") String posY){
        List<ReviewDto> reviewDtos = reviewService.getReviewByPlace(posX, posY);
        return ResponseEntity.ok(reviewDtos);
    }

    @GetMapping(value={"","/"})
    public String getAllReviews(Model model){
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        List<PlaceDto> placeDtoList;
        if(user!=null){
            Member member = memberRepository.findByEmail(user.getEmail()).get();
            placeDtoList = placeService.getAllPlace(member);
        }else{
            placeDtoList = placeService.getAllPlace();
        }
        model.addAttribute("placeDtoList",placeDtoList);
        return "reviews/reviewList";
    }



}
