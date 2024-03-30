package sungaron.foodiespot.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sungaron.foodiespot.dto.PlaceDto;
import sungaron.foodiespot.dto.SessionUser;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.repository.MemberRepository;
import sungaron.foodiespot.service.PlaceService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final HttpSession httpSession;
    private final MemberRepository memberRepository;
    private final PlaceService placeService;
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
