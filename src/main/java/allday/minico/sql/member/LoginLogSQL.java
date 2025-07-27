/*
@author 최온유
LoginLogSQL 클래스는 로그인/로그아웃 및 회원가입 로그를
기록하거나 갱신하기 위한 SQL 쿼리문을 정적 문자열로 정의한 클래스입니다.
로그인 시 로그 ID 반환, 로그아웃 시 시간 업데이트,
회원가입 시 초기 로그 기록을 위한 쿼리를 포함합니다.
 */

package allday.minico.sql.member;

public class LoginLogSQL {

    public static String loginLogRecordSQL = """
        BEGIN
            INSERT INTO LOGIN_LOG 
                (LOGIN_TIME, LOGOUT_TIME, MEMBER_ID)
                VALUES(?, NULL, ?)
            RETURNING LOG_ID INTO ?;
        END;
        """;
    public static String updateLogoutLogSQL = """
            UPDATE LOGIN_LOG
            SET LOGOUT_TIME = ?
            WHERE LOG_ID = ?
            """;
    public static String insertLogForSignupSQL = """
            INSERT INTO LOGIN_LOG
            (LOGIN_TIME, LOGOUT_TIME, MEMBER_ID)
            VALUES(SYSDATE, SYSDATE + INTERVAL '1' SECOND, ?)
            """;
}
