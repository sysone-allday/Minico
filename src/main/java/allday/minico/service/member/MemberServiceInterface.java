package allday.minico.service.member;

import allday.minico.dto.member.Member;

public interface MemberServiceInterface {
    boolean isMemberIdAvailable(String memberId);
    boolean signUp(Member member);
    boolean login(String memberId, String memberPw);
}
