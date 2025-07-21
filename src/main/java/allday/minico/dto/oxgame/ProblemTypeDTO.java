package allday.minico.dto.oxgame;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// 주제 번호 및 주제 명
public class ProblemTypeDTO {
    private int typeId;
    private String typeName;

    @Override
    public String toString() {
        return typeName;
    }
}
