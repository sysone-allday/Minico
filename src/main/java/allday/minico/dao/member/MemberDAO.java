package allday.minico.dao.member;

import allday.minico.dto.member.Member;
import allday.minico.sql.member.MemberSQL;
import java.sql.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MemberDAO {

    private static MemberDAO instance;
    
    // DB 접속 정보를 Properties에서 로드
    private static final Properties dbProps = new Properties();
    private static String url;
    private static String dbUsername;
    private static String dbPassword;
    
    static {
        try (InputStream input = MemberDAO.class.getResourceAsStream("/database.properties")) {
            if (input != null) {
                dbProps.load(input);
                url = dbProps.getProperty("db.url");
                dbUsername = dbProps.getProperty("db.username");
                dbPassword = dbProps.getProperty("db.password");
                // System.out.println("✅ database.properties 로드 성공");
            } else {
                url = "jdbc:oracle:thin:@//localhost:1521/xepdb1";
                dbUsername = "ace";
                dbPassword = "ace";
            }
        } catch (IOException e) {
            // Fallback to hardcoded values
            url = "jdbc:oracle:thin:@//localhost:1521/xepdb1";
            dbUsername = "ace";
            dbPassword = "ace";
            System.out.println("⚠️ database.properties 로드 실패: " + e.getMessage());
        }
    }

    private MemberDAO(){}
    public static MemberDAO getInstance() {
        if (instance == null) {
            instance = new MemberDAO();
        }
        return instance;
    }

    // DB 에 접근하기 위해 필요한 통로역할을 하는 Connection 객체
    private Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
            return conn;
        } catch (SQLException e) {
            System.err.println("❌ DB 연결 실패!");
            System.err.println("URL: " + url);
            System.err.println("사용자명: " + dbUsername);
            System.err.println("오류 코드: " + e.getErrorCode());
            System.err.println("오류 메시지: " + e.getMessage());
            throw e;
        }
    }


    public boolean isIdExists(String memberId) throws SQLException { // 중복 ID 인지 조회
        String idCheckSQL = MemberSQL.idCheakSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(idCheckSQL)) {
            pstmt.setString(1, memberId); // WHERE 로 memberid 조회
            ResultSet rs = pstmt.executeQuery(); // SELECT 결과 저장
            if (rs.next()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean signUp(Member member) throws SQLException { // 회원가입 INSERT
        String signUpSQL = MemberSQL.signUpSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(signUpSQL)) {

            pstmt.setString(1, member.getMemberId());
            pstmt.setString(2, member.getPassword());
            pstmt.setString(3, member.getNickname());
            pstmt.setString(4, member.getEmail());
            pstmt.setTimestamp(5, Timestamp.valueOf(member.getJoinDate()));
            pstmt.setString(6, member.getPasswordHint());
            pstmt.setInt(7, member.getVisitCount());
            pstmt.setInt(8, member.getLevel());
            pstmt.setInt(9, member.getExperience());
            pstmt.setInt(10, member.getCoin());
            pstmt.setString(11, member.getMinimi());

            if (pstmt.executeUpdate() > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public String getPwHint(String memberId) throws SQLException { // 비번 찾기
        String findHintSQL = MemberSQL.findHintSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(findHintSQL)) {
            pstmt.setString(1,memberId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("PW_HINT"); // 컬럼명
            } else {
                return null; // 해당 ID가 없는 경우
            }
        }

    }

    public Member tryLogin(String memberId, String memberPw) throws SQLException { // 로그인 시도
        String getAllMemberInfoSQL = MemberSQL.getAllMemberInfoSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(getAllMemberInfoSQL)) {
            pstmt.setString(1, memberId);
            pstmt.setString(2, memberPw);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("MEMBER_ID"));
                member.setPassword(rs.getString("MEMBER_PASSWORD"));
                member.setMinimi(rs.getString("MINIMI_TYPE"));
                member.setCoin(rs.getInt("COIN"));
                member.setEmail(rs.getString("EMAIL"));
                member.setExperience(rs.getInt("EXPERIENCE"));
                member.setLevel(rs.getInt("LV"));
                member.setNickname(rs.getString("NICKNAME"));
                member.setJoinDate(rs.getTimestamp("JOIN_DATE").toLocalDateTime());
                member.setPasswordHint(rs.getString("PW_HINT"));
                member.setVisitCount(rs.getInt("VISIT_COUNT"));
                return member;
            } else {
                return null;
            }
        }
    }
}
