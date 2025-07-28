/*
@author 최온유
MemberService 클래스는 회원가입, 로그인, ID 중복 검사,
회원정보 수정 및 삭제, 비밀번호 힌트 조회, 중복 로그인 방지 등
회원 관련 비즈니스 로직을 처리하는 서비스 계층입니다.
MemberDAO를 통해 DB와 통신하며, 각 기능의 성공 여부와 예외를 관리합니다.
 */
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
    public Member login(String memberId, String memberPw) {
        try {
            return memberDAO.tryLogin(memberId, memberPw);
            } catch(SQLException e) {
            System.out.println("회원정보 가져오는 중 SQL 예외 발생");
            e.printStackTrace();
            return null;
        }
    }

    public String findPwHint(String memberId) { // 아이디와 일치하는 힌트 가져오기
        try {
            String hintText =  memberDAO.getPwHint(memberId);
            return hintText;
        } catch (SQLException e){
            return null;
        }
    }
    public boolean modifyMemberInfo(String modifyInfoMemberId, String nickname, String email, String password, String passwordHint) {
        try {
            boolean isModifyComplete = memberDAO.updateMemberInfo(modifyInfoMemberId, nickname, email,password,passwordHint);
            return  isModifyComplete;
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("회원정보 수정 중 예외 발생");
            return false;
        }
    }

    public boolean deleteAccount(String deleteId) {
        try {
            boolean deleteAccountResult = memberDAO.deleteMember(deleteId);
            if(deleteAccountResult) {
                System.out.println("회원 탈퇴 성공");
                return deleteAccountResult;
            } else {
                System.out.println("회원 탈퇴 실패");
                return deleteAccountResult;
            }
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("회원 탈퇴 중 예외 발생");
            return false;
        }
    }

    public Boolean preventMultipleLogins(String checkId) {
        try {
            Boolean isMultiLogin = memberDAO.checkMultipleLogin(checkId);
            return isMultiLogin;
        } catch(SQLException e){
            e.printStackTrace();
            System.out.println("멀티 로그인 방지 중 예외 발생");
            return false;
        }
    }
    
    public Member getMemberByNickname(String nickname) {
        try {
            return memberDAO.getMemberByNickname(nickname);
        } catch (SQLException e) {
            System.out.println("닉네임으로 회원정보 가져오는 중 SQL 예외 발생");
            e.printStackTrace();
            return null;
        }
    }
}
