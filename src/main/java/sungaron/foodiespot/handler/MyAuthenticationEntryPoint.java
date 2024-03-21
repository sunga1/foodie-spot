package sungaron.foodiespot.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.io.PrintWriter;

public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    //인증 예외처리
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        //메세지 출력 후 홈으로 redirect
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter pw = response.getWriter();
        pw.println("<script>alert('로그인한 멤버만 가능합니다!'); location.href='/members/loginPage';</script>");
        pw.flush();
    }
}
