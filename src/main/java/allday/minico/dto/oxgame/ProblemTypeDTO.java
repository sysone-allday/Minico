package allday.minico.dto.oxgame;

import lombok.Getter;
import lombok.Setter;

/**
 * ProblemTypeDTO
 *
 * 문제 유형 정보를 담는 DTO 클래스입니다.
 * 각 문제는 특정 주제에 속하며, 그 주제의 ID와 이름을 이 클래스를 통해 전달합니다.
 *
 * 주요 필드:
 * - typeId   : 문제 유형 ID
 * - typeName : 문제 유형 이름 (예: 과학, 역사 등)
 *
 * 주로 게임 설정화면(OxGameSettingController)이나 문제 조회 서비스에서 사용됩니다.
 * toString() 메서드는 typeName을 반환하여 콤보박스 표시 시 활용됩니다.
 *
 * @author 김슬기
 * @version 1.0
 */


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
