/*
Friend 클래스는 친구의 ID, 닉네임, 레벨, 마지막 로그아웃 시간,
친구 상태(friendStatus), 친구 요청 시간 등의 정보를 담는 DTO입니다.
친구 조회 및 요청 목록 등의 기능에서 데이터 전달용으로 사용됩니다.
 */

package allday.minico.dto.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Friend {
    private String friendId;
    private String friendNickname;
    private Integer friendLevel;
    private LocalDateTime lastLogoutTime = null;
    private String friendStatus;
    private LocalDateTime requestedAt;

    public Friend(String friendId, String friendNickname, int friendLevel, LocalDateTime requestedAt, LocalDateTime lastLogoutTime) {
        this.friendId = friendId;
        this.friendNickname = friendNickname;
        this.friendLevel = friendLevel;
        this.requestedAt = requestedAt;
        this.lastLogoutTime =  lastLogoutTime;
    }
}