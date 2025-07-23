package allday.minico.dto.oxgame;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OxQuestion {
    String questionText; 	// 문제내용
    String answer; 			// 'O' or 'X'
    String explanation;		// 문제 해설
}
