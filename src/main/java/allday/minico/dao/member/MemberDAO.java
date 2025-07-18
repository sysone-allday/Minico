package allday.minico.dao.member;


import allday.minico.dto.member.Member;
import allday.minico.sesstion.AppSession;
import allday.minico.sql.member.MemberSQL;
import java.sql.*;

public class MemberDAO {

    private static MemberDAO instance;
    // DB 접속에 필요한 필드 (일단 아이디 하드코딩)
    private static final String url = "jdbc:oracle:thin:@//localhost:1521/xepdb1";
    private static final String dbMember = "onyu";
    private static final String dbPassword = "onyu";

    private MemberDAO(){}
    public static MemberDAO getInstance() {
        if (instance == null) {
            instance = new MemberDAO();
        }
        return instance;
    }

    // DB 에 접근하기 위해 필요한 통로역할을 하는 Connection 객체
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, dbMember, dbPassword);
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
