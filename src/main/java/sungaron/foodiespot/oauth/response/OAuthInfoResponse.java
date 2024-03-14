package sungaron.foodiespot.oauth.response;

import sungaron.foodiespot.oauth.OAuthProvider;

public interface OAuthInfoResponse {
    String getEmail();
    String getNickname();
    OAuthProvider getOAuthProvider();
}
