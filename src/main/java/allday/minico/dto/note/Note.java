package allday.minico.dto.note;

import lombok.Getter;
import lombok.Setter;
/**
 * Note
 *
 * 오답노트 정보를 담는 DTO 클래스입니다.
 * 틀린 문제의 텍스트, 정답, 메모, 사용자 ID 등의 데이터를 포함하여 저장/조회에 활용됩니다.
 *
 * 주요 필드:
 * - wrongId       : 오답 고유 ID
 * - questionText  : 문제 내용
 * - answerText    : 정답 내용
 * - memo          : 사용자가 작성한 메모
 * - memberId      : 사용자 ID
 *
 * Lombok의 @Getter, @Setter를 사용하여 접근자 메서드를 자동 생성합니다.
 *
 * @author 정소영
 * @version 1.0
 */

@Getter
@Setter
public class Note {
    private int wrongId;
    private String questionText;
    private String answerText;
    private String memo;
    private String memberId;
}
