package sungaron.foodiespot.oauth.loginParams;

import org.springframework.util.MultiValueMap;
import sungaron.foodiespot.oauth.OAuthProvider;

//OAuth 요청을 위한 파라미터 값들을 갖고 있는 인터페이스
//RequestBody 로도 사용할 예정이므로 get함수 x
public interface OAuthLoginParams {
    OAuthProvider oAuthProvider();
    MultiValueMap<String, String> makeBody();
}
