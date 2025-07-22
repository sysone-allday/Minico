package allday.minico.dao.member;

import allday.minico.sql.member.LoginLogSQL;
//import allday.minico.sql.member.MemberSQL;

import java.sql.*;

import static allday.minico.utils.DBUtil.getConnection;

public class LoginLogDAO {

    // 싱글 톤
    private static LoginLogDAO instance;
    private LoginLogDAO() {}
    public static LoginLogDAO getInstance() {
        if (instance == null) instance = new LoginLogDAO();
        return instance;
    }


    // 로그인 시 로그 INSERT 후 자동 생성된 LOG_ID 반환
    public long insertLoginLog(String memberId) throws SQLException {
        String sql = LoginLogSQL.loginLogRecordSQL;
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {
            stmt.setString(1, memberId);
            stmt.registerOutParameter(2, Types.BIGINT); // OUT 파라미터 (LOG_ID)

            stmt.execute(); // 쿼리 실행

            return stmt.getLong(2); // 반환된 로그ID 얻기
        }
    }

    public boolean updateLogoutLog(long logId) throws SQLException {
        String updateLogoutLogSQL = LoginLogSQL.updateLogoutLogSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateLogoutLogSQL)) {

            pstmt.setLong(1, logId);

            if (pstmt.executeUpdate() > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean insertLogForSignUp(String memberId) throws SQLException {
        String insertLogForSignupSQL = LoginLogSQL.insertLogForSignupSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertLogForSignupSQL)) {
            pstmt.setString(1, memberId);

            if (pstmt.executeUpdate() > 0) {
                return true;
            }
        }
        return false;
    }
}
