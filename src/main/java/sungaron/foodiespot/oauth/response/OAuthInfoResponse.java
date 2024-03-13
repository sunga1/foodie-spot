package sungaron.foodiespot.oauth.response;

import sungaron.foodiespot.entity.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    OAuthProvider getOAuthProvider();
}
