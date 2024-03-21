package sungaron.foodiespot.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Member {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;


    @Builder
    public Member(String email, String nickname, Role role){
        this.email=email;
        this.nickname=nickname;
        this.role=role;
    }

    public Member update(String nickname){
        this.nickname=nickname;
        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }
}
