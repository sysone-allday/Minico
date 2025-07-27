/*
@author 최온유
LoginLogService 클래스는 로그인, 로그아웃, 회원가입 시
로그 기록을 DB에 저장하고 관련 ID를 반환하거나 갱신하는 기능을 담당합니다.
LoginLogDAO를 통해 DB와 통신하며, 각 로그 기록의 성공 여부를 처리합니다.
 */
package allday.minico.service.member;

import allday.minico.dao.member.LoginLogDAO;

import java.sql.SQLException;

public class LoginLogService {

    private final LoginLogDAO loginLogDAO = LoginLogDAO.getInstance();
    private static final LoginLogService instance = new LoginLogService();
    private LoginLogService() {}
    public static LoginLogService getInstance() {
        return instance;
    }

    public long recordLoginLog(String memberId){ // 로그인 시 로그 기록 및 로그 ID 반환
        try{
            long logId = loginLogDAO.insertLoginLog(memberId);
            System.out.println(memberId + "로그인 시 로그 ID :" +  logId);
            if(logId > 0){ return logId;}
            else {System.out.println("비정상적인 로그 ID 반환"); return -1L;} // 로그 ID가 비정상적인 값일 때 (0 이하)
        } catch (SQLException e){
                e.printStackTrace();
                return -1L;
        }
    }

    public boolean recordLogoutLog(long logId) {
        try {
            boolean result = loginLogDAO.updateLogoutLog(logId);
            return result;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }


    public boolean recordLogForSignUp(String memberId) {
        try{
            boolean recordSuccess = loginLogDAO.insertLogForSignUp(memberId);
            return recordSuccess;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
