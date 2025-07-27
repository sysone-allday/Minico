/*
MemberSQL 클래스는 회원 기능에 필요한 SQL 쿼리문들을
정적 문자열로 정의한 클래스입니다.
ID 중복 확인, 회원가입, 로그인, 비밀번호 힌트 조회,
회원정보 수정/삭제, 중복 로그인 검사, 닉네임 기반 조회 등을
처리하는 SQL 구문을 포함합니다.
 */

package allday.minico.sql.member;

public class MemberSQL {

    public static String idCheakSQL = """
			SELECT MEMBER_ID
			FROM MEMBER
			WHERE MEMBER_ID = ?
			""";
    public static String signUpSQL = """
			INSERT INTO MEMBER
			(MEMBER_ID, MEMBER_PASSWORD, NICKNAME, EMAIL, JOIN_DATE, PW_HINT, VISIT_COUNT, LV, EXPERIENCE, COIN, MINIMI_TYPE)
			VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
			""";

    public static String findHintSQL = """
			SELECT PW_HINT
			FROM MEMBER
			WHERE MEMBER_ID = ?
			""";

	public static String getAllMemberInfoSQL = """
			SELECT *
			FROM MEMBER
			WHERE MEMBER_ID = ? AND MEMBER_PASSWORD = ?
			""";
	public static String updateMemberInfoSQL = """
			UPDATE MEMBER
			SET
				NICKNAME = ?,
				EMAIL = ?,
				MEMBER_PASSWORD = ?,
				PW_HINT = ?
			WHERE MEMBER_ID = ?
			""";
	public static String deleteMemberSQL = """
			DELETE FROM MEMBER
			WHERE MEMBER_ID = ?
			""";
	public static String checkMultipleLoginSQL = """
			SELECT LOGOUT_TIME FROM LOGIN_LOG
			WHERE MEMBER_ID = ?
			ORDER BY LOG_ID DESC
			FETCH FIRST 1 ROWS ONLY
			""";
	
	public static String getMemberByNicknameSQL = """
			SELECT *
			FROM MEMBER
			WHERE NICKNAME = ?
			""";
}