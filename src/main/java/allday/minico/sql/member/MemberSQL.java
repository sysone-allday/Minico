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
}