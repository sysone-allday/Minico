package allday.minico.dto.oxgame;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OxGameResult {
    private int correctCount;
    private int totalCount;
    private String difficulty;
    private String typeName;

    public double getAccuracy() {
        if (totalCount == 0) return 0.0;
        return (correctCount * 100.0) / totalCount;
    }
}
