package sungaron.foodiespot.dto;

import lombok.Getter;
import sungaron.foodiespot.entity.Member;

import java.io.Serializable;

//세션에 사용자 정보를 저장하기 위한 dto 클래스
@Getter
public class SessionUser implements Serializable {
    private String nickname;
    private String email;

    public SessionUser(Member member){
        this.email=member.getEmail();
        this.nickname=member.getNickname();
    }
}
