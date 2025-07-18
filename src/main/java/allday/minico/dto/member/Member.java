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