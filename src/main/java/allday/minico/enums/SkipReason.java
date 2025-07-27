package allday.minico.enums;

/**
 * SkipReason
 *
 * OX 퀴즈 게임에서 문제 풀이를 중단하는 사유를 나타내는 열거형(Enum)입니다.
 * 사용자가 스킵 버튼을 눌렀는지, 설정 화면으로 돌아가기를 선택했는지 등을 구분할 때 사용됩니다.
 *
 * 열거형 상수:
 * - USER_SKIP     : 사용자가 문제 풀이 중 직접 스킵을 선택한 경우
 * - GO_TO_SETTING : 설정 화면으로 돌아가는 경우
 *
 * OxPlayController 클래스 등에서 게임 흐름 제어 시 활용됩니다.
 *
 * @author 김슬기
 * @version 1.0
 */

public enum SkipReason {
    USER_SKIP,
    GO_TO_SETTING,
}
