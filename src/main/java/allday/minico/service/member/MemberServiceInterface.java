/*
MemberServiceInterface 인터페이스는 회원 서비스에서
ID 중복 확인, 회원가입, 로그인 기능의 기본 메서드 시그니처를 정의합니다.
구현 클래스는 이 인터페이스를 통해 핵심 회원 로직을 구현하게 됩니다.
 */
package allday.minico.service.member;

import allday.minico.dto.member.Member;

public interface MemberServiceInterface {
    boolean isMemberIdAvailable(String memberId);
    boolean signUp(Member member);
    Member login(String memberId, String memberPw);
}
