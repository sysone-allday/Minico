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
