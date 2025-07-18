package allday.minico.service.member;

import allday.minico.dao.member.LoginLogDAO;

import java.sql.SQLException;

public class LoginLogService {

    private static final LoginLogService instance = new LoginLogService();

    private LoginLogService() {}

    public static LoginLogService getInstance() {
        return instance;
    }

    private final LoginLogDAO loginLogDAO = LoginLogDAO.getInstance();

    public long recordLoginLog(String memberId){
        try{
            long logId = loginLogDAO.insertLoginLog(memberId);

            System.out.println(memberId + "로그인 시 로그 ID :" +  logId);//////////////////////////////////////////////////////////////////////////// 지워

            if(logId > 0){ return logId;}
            else {System.out.println("LoginLogService : 비정상적인 로그 ID 반환"); return -1L;} // 로그 ID가 비정상적인 값일 때 (0 이하)
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
}
