/*
@author 최온유
LoginLog 클래스는 로그인 로그 정보를 담는 DTO로,
로그 ID, 회원 ID, 로그인 시간, 로그아웃 시간을 포함합니다.
로그 기록의 조회 및 저장에 사용됩니다.
 */
package allday.minico.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class LoginLog {
    private long logId;
    private String memberId;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
}
