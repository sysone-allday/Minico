package allday.minico.dao.member;

import allday.minico.dto.member.Member;
import allday.minico.sql.member.MemberSQL;
import allday.minico.utils.DBUtil;

import java.sql.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static allday.minico.utils.DBUtil.getConnection;

public class MemberDAO {

    private static MemberDAO instance;

    private MemberDAO(){}
    public static MemberDAO getInstance() {
        if (instance == null) {
            instance = new MemberDAO();
        }
        return instance;
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

    public boolean updateMemberInfo(String modifyInfoMemberId, String nickname, String email, String password, String passwordHint) throws SQLException {
        String updateMemberInfoSQL = MemberSQL.updateMemberInfoSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateMemberInfoSQL)) {

            // 닉네임, 이메일, 비밀번호, 힌트, ID 순
            pstmt.setString(1, nickname);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, passwordHint);
            pstmt.setString(5, modifyInfoMemberId);

            if (pstmt.executeUpdate() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean deleteMember(String deleteId) throws SQLException {
        String deleteMemberSQL = MemberSQL.deleteMemberSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteMemberSQL)) {
            pstmt.setString(1, deleteId);

            if(pstmt.executeUpdate() > 0){
                return true;
            }
        }
        return false;
    }

    public Boolean checkMultipleLogin(String checkId) throws SQLException {
        String checkMultipleLoginSQL = MemberSQL.checkMultipleLoginSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkMultipleLoginSQL)) {

            pstmt.setString(1, checkId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) { // 결과가 존재할때(해당 MEMBER_ID가 존재할때) 실행
                String result = rs.getString("LOGOUT_TIME");
                if(result != null){
                    System.out.println("로그인 중 아님, 로그인 가능");
                    return true; // LOGOUT_TIME 컬럼이 null 아니면 로그아웃 한 것이므로 로그인 가능
                } else{
                    System.out.println("다른 곳에서 로그인 중이므로 로그인 불가");
                    return false; // LOGOUT_TIME 컬럼이 null 이면 로그아웃을 하지 않은 것이므로 로그인 불가
                }
            }
        }
        System.out.println("없는 멤버거나 로그아웃기록이 없는 ID");
        return null; // 조회된 행이 아예 없으면 없는 MEMBER_ID
    }
}
