package sungaron.foodiespot.oauth.ApiClient;

import sungaron.foodiespot.entity.OAuthProvider;
import sungaron.foodiespot.oauth.loginParams.OAuthLoginParams;
import sungaron.foodiespot.oauth.response.OAuthInfoResponse;

//OAuth 요청을 위한 Client 클래스
public interface OAuthApiClient {
    OAuthProvider oAuthProvider(); //Client 의 타입 반환
    String requestAccessToken(OAuthLoginParams params); //Authorization Code를 기반으로 인증 API를 요청해서 Access Token 획득
    OAuthInfoResponse requestOauthInfo(String accessToken); //Access Token 을 기반을 Email, Nickname 을 가지는 response 객체 획득

}
