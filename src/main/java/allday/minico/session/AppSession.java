package allday.minico.session;

import allday.minico.dto.member.Member;
import allday.minico.service.member.LoginLogService;

public class AppSession {
    private static Member loginMember;
    private static long loginlogId = -1;
    private static String playerNickname; // 플레이어 닉네임 저장

    public static void setLoginMember(Member member) {
        loginMember = member;
        // 로그인 시 닉네임도 함께 저장
        if (member != null) {
            playerNickname = member.getNickname();
        }
    }

    public static Member getLoginMember() {
        return loginMember;
    }

    public static String getPlayerNickname() {
        return playerNickname;
    }

    public static void setPlayerNickname(String nickname) {
        playerNickname = nickname;
    }

    public static boolean logout() { // 로그아웃

        if (loginlogId > 0) { // 로그인 로그 ID가 존재하는 경우에 실행
            boolean logoutComplete = LoginLogService.getInstance().recordLogoutLog(loginlogId); // 로그아웃 로그 UPDATE

            if (logoutComplete) { // 로그아웃 로그 UPDATE 성공 시 실행
                System.out.println(loginMember.getMemberId() +  " 유저의 로그아웃 성공, 로그아웃 로그 UPDATE");
                clear();
                return true;
            } else {
                System.out.println("로그아웃 로그 저장 실패");
                clear();
                return false;
            }
        } else {
            System.out.println("로그인 로그가 존재하지 않음");
            return false;
        }
    }

    public static void clear() { // 로그인 중 오류 발생 시
        // 로그인 정보 초기화
        loginMember = null;
        loginlogId = -1L;
        playerNickname = null;
    }

    public static void setLoginLog(long logId) { // 로그인시 로그 ID 저장해둠
        loginlogId = logId;
    }

    public static long getLoginLog() {
        return loginlogId;
    }
}