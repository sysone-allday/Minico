package allday.minico.dto.oxgame;

import lombok.Getter;
import lombok.Setter;
/**
 * OxQuestion
 *
 * OX 퀴즈에서 사용하는 문제 데이터를 담는 DTO 클래스입니다.
 * 문제 텍스트, 정답(O 또는 X), 해설 정보를 포함하며, DB에서 불러온 정보를 담아 컨트롤러 및 서비스에서 활용됩니다.
 *
 * 주요 필드:
 * - questionText : 문제 문장
 * - answer       : 정답 ("O" 또는 "X")
 * - explanation  : 정답 해설
 *
 * Lombok의 @Getter와 @Setter를 사용하여 필드에 대한 접근자/설정자가 자동 생성됩니다.
 *
 * @author 김슬기
 * @version 1.0
 */


@Setter
@Getter
public class OxQuestion {
    String questionText; 	// 문제내용
    String answer; 			// 'O' or 'X'
    String explanation;		// 문제 해설
}
