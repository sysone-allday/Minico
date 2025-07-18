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
