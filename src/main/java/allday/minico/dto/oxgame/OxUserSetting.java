package allday.minico.dto.oxgame;

import lombok.Data;

/**
 * OxUserSetting
 *
 * OX 퀴즈 게임에서 사용자가 선택한 설정 정보를 담는 DTO 클래스입니다.
 * 게임 시작 전 설정한 난이도, 제한 시간, 문제 수, 문제 유형 정보를 저장하고 전달하는 역할을 합니다.
 *
 * 주요 필드:
 * - difficulty     : 난이도 ("하", "중", "상", "무작위")
 * - timer          : 제한 시간 (초 단위)
 * - count          : 문제 개수
 * - problemType    : 문제 유형 정보 (ProblemTypeDTO)
 *
 * Lombok의 @Data 어노테이션을 통해 getter/setter, toString, equals 등의 메서드가 자동 생성됩니다.
 *
 * @author 김슬기
 * @version 1.0
 */


@Data
public class OxUserSetting {
    private String difficulty;
    private int timer;
    private int count;
    private ProblemTypeDTO problemType;
}
