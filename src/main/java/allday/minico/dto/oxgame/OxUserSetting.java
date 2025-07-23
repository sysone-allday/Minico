package allday.minico.dto.oxgame;

import lombok.Data;

@Data
public class OxUserSetting {
    private String difficulty;
    private int timer;
    private int count;
    private ProblemTypeDTO problemType;
}
