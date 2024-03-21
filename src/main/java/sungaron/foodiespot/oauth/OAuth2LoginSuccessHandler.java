package sungaron.foodiespot.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.repository.MemberRepository;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@AllArgsConstructor
@Slf4j
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    //로그인 성공시 발생하는 핸들러
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //성공 시 메세지 출력 후 홈 화면으로 redirect
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter pw = response.getWriter();
        String prevPage = (String) request.getSession().getAttribute("prevPage");
        if(prevPage!=null){
            pw.println("<script>alert('로그인에 성공하였습니다!'); location.href='" + prevPage + "';</script>");
        } else{
            pw.println("<script>alert('로그인에 성공하였습니다!'); location.href='/';</script>");
        }

        pw.flush();
    }
}