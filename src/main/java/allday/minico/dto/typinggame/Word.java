package allday.minico.dto.typinggame;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Word {
    private int word_id;
    private String text;
    private String difficulty;
    private int type_id;
}
