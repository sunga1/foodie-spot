package sungaron.foodiespot.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sungaron.foodiespot.entity.Member;
import sungaron.foodiespot.jwt.AuthTokens;
import sungaron.foodiespot.jwt.AuthTokensGenerator;
import sungaron.foodiespot.oauth.loginParams.OAuthLoginParams;
import sungaron.foodiespot.oauth.response.OAuthInfoResponse;
import sungaron.foodiespot.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class OAuthLoginService {
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public AuthTokens login(OAuthLoginParams params){
        OAuthInfoResponse oAuthInfoResponse = requestOAuthInfoService.request(params);
        Long memberId = findOrCreateMember(oAuthInfoResponse);
        return authTokensGenerator.generate(memberId);
    }

    private Long findOrCreateMember(OAuthInfoResponse oAuthInfoResponse) {
        return memberRepository.findByEmail(oAuthInfoResponse.getEmail())
                .map(Member::getId)
                .orElseGet(()->newMember(oAuthInfoResponse));
    }

    private Long newMember(OAuthInfoResponse oAuthInfoResponse) {
        Member member = Member.builder()
                .email(oAuthInfoResponse.getEmail())
                .nickname(oAuthInfoResponse.getNickname())
                .oAuthProvider(oAuthInfoResponse.getOAuthProvider())
                .build();
        return memberRepository.save(member).getId();
    }


}
