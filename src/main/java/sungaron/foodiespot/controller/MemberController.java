package sungaron.foodiespot.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sungaron.foodiespot.dto.PlaceDto;
import sungaron.foodiespot.dto.ReviewDto;
import sungaron.foodiespot.dto.SessionUser;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.entity.Place;
import sungaron.foodiespot.entity.Review;
import sungaron.foodiespot.repository.MemberRepository;
import sungaron.foodiespot.repository.PlaceRepository;
import sungaron.foodiespot.repository.ReviewRepository;
import sungaron.foodiespot.service.LikeService;
import sungaron.foodiespot.service.PlaceService;
import sungaron.foodiespot.service.ReviewService;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
    private final HttpSession httpSession;
    private final MemberRepository memberRepository;
    private final ReviewService reviewService;
    private final LikeService likeService;
    private final PlaceRepository placeRepository;
    private final PlaceService placeService;
    @GetMapping("/loginPage")
    public String loginPage(Model model, HttpServletRequest request) {
        // 로그인 성공 시 이전 페이지로 redirect 되게 하기 위해 세션에 저장
        String uri = request.getHeader("Referer");
        if (uri != null && !uri.contains("/login") && !uri.contains("/join")) {
            request.getSession().setAttribute("prevPage", uri);
        }
        return "/members/loginPage";
    }
    @GetMapping("/myPage")
    public String myPage(@RequestParam(required = false,defaultValue = "1") int page, Model model){
        PageRequest pageRequest = PageRequest.of(page - 1, 15, Sort.by("id").descending());

        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        List<ReviewDto> myReviewDtoList = reviewService.getMyReviews(user.getEmail());
        Page<ReviewDto> myReviewDtos =new PageImpl<>(myReviewDtoList,pageRequest,myReviewDtoList.size());
        model.addAttribute("myReviewDtos",myReviewDtos);
        return "/members/myPage";
    }

    // 특정 장소에 좋아요 추가함으로써 찜한 장소에 추가됨
    @PostMapping("/addLike")
    public String addLike(@RequestParam("posX") String posX,
                                                            @RequestParam("posY") String posY, Model model) {
        //System.out.println(posX+"  "+posY);
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        Member member= memberRepository.findByEmail(user.getEmail()).get();
        Place place = placeRepository.findByPosXAndPosY(posX, posY).get();
        likeService.addLike(member, place);
        return "reviews/reviewList";
    }

    // 찜한 장소에 like 취소하기 위해
    @PostMapping("/delLike")
    public String deleteLike(@RequestParam("posX") String posX,
                                 @RequestParam("posY") String posY, Model model) {
        //System.out.println(posX+"  "+posY);
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        Member member= memberRepository.findByEmail(user.getEmail()).get();
        Place place = placeRepository.findByPosXAndPosY(posX, posY).get();
        likeService.deleteLike(member,place);
        return "reviews/reviewList";
    }

    // 특정 회원이 찜한 장소 리스트 볼 수 있는 uri
    @GetMapping("/likeList")
    public String getLikeList(Model model){
        SessionUser user = (SessionUser) httpSession.getAttribute("user");
        List<PlaceDto> placeDtoList;
        Member member = memberRepository.findByEmail(user.getEmail()).get();
        placeDtoList = placeService.getLikePlace(member);

        model.addAttribute("placeDtoList",placeDtoList);
        return "reviews/likeList";
    }
}
