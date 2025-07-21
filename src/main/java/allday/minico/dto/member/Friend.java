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