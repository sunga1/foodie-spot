package sungaron.foodiespot.oauth;

import org.springframework.stereotype.Component;
import sungaron.foodiespot.entity.OAuthProvider;
import sungaron.foodiespot.oauth.ApiClient.OAuthApiClient;
import sungaron.foodiespot.oauth.loginParams.OAuthLoginParams;
import sungaron.foodiespot.oauth.response.OAuthInfoResponse;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class RequestOAuthInfoService {
    private final Map<OAuthProvider, OAuthApiClient> clients;

    public RequestOAuthInfoService(List<OAuthApiClient> clients){
        this.clients=clients.stream().collect(
                Collectors.toUnmodifiableMap(OAuthApiClient::oAuthProvider, Function.identity())
        );
    }

    public OAuthInfoResponse request(OAuthLoginParams params){
        OAuthApiClient client = clients.get(params.oAuthProvider());
        String accessToken = client.requestAccessToken(params);
        return client.requestOauthInfo(accessToken);
    }
}
