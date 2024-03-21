package sungaron.foodiespot.oauth;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import net.minidev.json.JSONUtil;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import sungaron.foodiespot.dto.SessionUser;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.repository.MemberRepository;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final MemberRepository memberRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        //DefaultOAuth2UserService는 OAuth2UserService 구현체 중 하나로 OAuth2 사용자 정보를 가져오는데 사용됨
        OAuth2UserService<OAuth2UserRequest,OAuth2User> delegate = new DefaultOAuth2UserService();
        // 소셜 로그인 제공업체로부터 받은 사용자 정보를 OAuth2User 객체에 저장
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        // registrationId: naver 또는 kakao
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        // 사용자의 소셜로그인에 대한 세부 정보를 attributes 객체에 저장
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        // attributes 객체의 email 속성을 사용하여 멤버객체를 생성하거나 업데이트
        Member member = saveOrUpdate(attributes);

        //세션에 사용자 정보를 저장
        httpSession.setAttribute("user",new SessionUser(member));
        //세션 유지시간을 60분으로 제한
        httpSession.setMaxInactiveInterval(3600);

        //DefaultOAuth2User 객체를 생성하여 인증된 사용자를 나타냄 -> 인증 및 권한을 부여할 때 사용
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey()
        );
    }

    // 이메일을 통해 회원인지 확인 후 회원이 아니면 memberRepository 에 저장
    private Member saveOrUpdate(OAuthAttributes attributes) {
        Member member = memberRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getNickname()))
                .orElse(attributes.toEntity());
        return memberRepository.save(member);
    }
}
