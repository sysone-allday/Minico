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
