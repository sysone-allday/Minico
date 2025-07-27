/*
@author 최온유
Member 클래스는 회원 정보를 담는 DTO로,
아이디, 닉네임, 비밀번호, 이메일, 가입일, 레벨, 경험치,
보유 코인, 미니미 타입, 방문 횟수 등의 속성을 포함합니다.
회원가입, 로그인, 정보 수정 등 다양한 기능에서 데이터 전달용으로 사용됩니다.
 */

package allday.minico.dto.member;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Member {
    private String memberId;
    private String nickname;
    private String password;
    private String passwordHint;
    private String email;
    private LocalDateTime joinDate;
    private Integer level;
    private Integer experience;
    private Integer coin;
    private String minimi;
    private Integer visitCount;
}