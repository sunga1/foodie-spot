package sungaron.foodiespot.oauth;

import lombok.Builder;
import lombok.Getter;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.entity.Role;

import java.util.Map;

@Getter
@Builder
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String email;
    private String nickname;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String,Object> attributes){
        // naver 와 kakao 로부터 사용자 정보를 받아올 때 json 내에 이메일과 닉네임이 들어있는 키값이 일치하지 않기 때문에 함수를 각각 따로 만들었다.
        if("naver".equals(registrationId)){
            return ofNaver("id",attributes);
        } else{
            return ofKakao("id",attributes);
        }
    }

    private static OAuthAttributes ofNaver(String userNameAttributeName,Map<String,Object>attributes){
        // attributes 를 콘솔에 찍어보면 response내에 email과 nickname 정보가 들어있음을 확인할 수 있다.
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .email((String)response.get("email"))
                .nickname((String) response.get("nickname"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    private static OAuthAttributes ofKakao(String userNameAttributeName,Map<String,Object>attributes){
        Map<String, Object> response = (Map<String, Object>) attributes.get("kakao_account");
        // 카카오는 profile 내에 nickname 정보가 있어 한번 response.get 을 진행한다.
        Map<String, Object> account = (Map<String, Object>) response.get("profile");
        return OAuthAttributes.builder()
                .email((String) response.get("email"))
                .nickname((String) account.get("nickname"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }
    public Member toEntity(){
        return Member.builder()
                .nickname(nickname)
                .email(email)
                .role(Role.USER)
                .build();
    }
}
