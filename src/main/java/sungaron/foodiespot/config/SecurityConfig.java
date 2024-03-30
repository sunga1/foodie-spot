package sungaron.foodiespot.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;
import sungaron.foodiespot.handler.MyAuthenticationEntryPoint;
import sungaron.foodiespot.oauth.CustomOAuth2UserService;
import sungaron.foodiespot.oauth.OAuth2LoginSuccessHandler;
import sungaron.foodiespot.oauth.OAuth2LogoutSuccessHandler;
import sungaron.foodiespot.repository.MemberRepository;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final MemberRepository memberRepository;

    @Bean
    public BCryptPasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    //로그인하지 않은 유저들만 접근 가능한 URL
    private static final String[] anonymousMemberUrl = {"/members/loginPage"};
    //로그인한 유저들만 접근 가능한 URL
    private static final String[] authenticatedMemberUrl = {"/members/myPage/**","/members/likeList","/reviews/create","/reviews/myReview"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity, HandlerMappingIntrospector introspector) throws  Exception{
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeRequests)->
                        authorizeRequests
                                //anonymousMemberUrl에 대해서는 익명의 사용자만 접근 가능
                                .requestMatchers(anonymousMemberUrl).anonymous()
                                //authenticatedUserUrl에 대해서는 로그인을 요구
                                .requestMatchers(authenticatedMemberUrl).authenticated()
                                .anyRequest().permitAll()
                )
                .exceptionHandling((exceptionConfig)->
                        exceptionConfig
                                .authenticationEntryPoint(new MyAuthenticationEntryPoint()) //인증 실패

                ) //로그인한 멤버만 접근할 수 있는 url에 로그인하지 않은 사용자가 접근할 경우 로그인 페이지로 이동시킴
                .oauth2Login((oauth2Login)->
                        oauth2Login
                                .loginPage("/loginPage") //로그인 페이지 url
                                //
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.userService(customOAuth2UserService))
                                .successHandler(new OAuth2LoginSuccessHandler())


                ) //로그아웃 시 세션 무효화 및 쿠키삭제
                .logout((logoutConfig)->
                        logoutConfig
                                .logoutUrl("/logout")
                                .invalidateHttpSession(true).deleteCookies("JSESSIONID")
                                .logoutSuccessHandler(new OAuth2LogoutSuccessHandler())
                )
                .build();

    }

}
