package allday.minico.dto.note;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Note {
    private int wrongId;
    private String questionText;
    private String answerText;
    private String memo;
    private String memberId;
}
