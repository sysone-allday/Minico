package allday.minico.service.member;


import allday.minico.dao.member.MemberDAO;
import allday.minico.dto.member.Member;

import java.sql.SQLException;

public class MemberService implements MemberServiceInterface {

    public MemberDAO memberDAO;

    public MemberService() {
        this.memberDAO = MemberDAO.getInstance();
    }

    @Override // ID 중복 검사 메서드
    public boolean isMemberIdAvailable(String memberId) {
        try {
            boolean isDuplicate = memberDAO.isIdExists(memberId);
            return !isDuplicate;
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("ID 중복여부 확인 중 예외 발생");
            return false;
        }
    }

    @Override
    public boolean signUp(Member member) { // INSERT 성공 시 true 반환
        try {
            memberDAO.signUp(member); return true;
        } catch (SQLException e){
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean login(String memberId, String memberPw) {
        return false;
    }

    public String findPwHint(String memberId) { // 아이디와 일치하는 힌트 가져오기
        try {
            String hintText =  memberDAO.getPwHint(memberId);
            return hintText;
        } catch (SQLException e){
            return null;
        }
    }
}
