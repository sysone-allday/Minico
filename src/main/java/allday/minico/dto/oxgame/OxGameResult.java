package allday.minico.dto.oxgame;

import lombok.Getter;
import lombok.Setter;

/**
 * OxGameResult
 *
 * OX 게임의 결과 데이터를 담는 DTO 클래스입니다.
 * 총 문제 수, 정답 수, 정답률, 난이도, 문제 유형명을 포함하여 결과 화면에 출력할 정보를 제공합니다.
 *
 * 주요 필드:
 * - correctCount : 맞춘 문제 수
 * - totalCount   : 전체 문제 수
 * - difficulty   : 선택한 난이도
 * - typeName     : 문제 유형명
 *
 * 주요 메서드:
 * - getAccuracy() : 정답률(%) 계산 메서드
 *
 * Lombok의 @Getter, @Setter 어노테이션으로 필드 접근자 및 설정자가 자동 생성됩니다.
 *
 * @author 김슬기
 * @version 1.0
 */


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
