package allday.minico.dto.typinggame;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlankGame {
    private int blank_id;
    private String question_text;
    private int type_id;
    private int word_id;
}
