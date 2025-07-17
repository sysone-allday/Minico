package allday.minico;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private Connection databaseLink;

    public Connection getConnection() {
        String url      = "jdbc:oracle:thin:@//localhost:1521/xepdb1";  // Oracle URL (XE 인스턴스)
        String dbUser   = "kosa";                                // Oracle 계정
        String dbPass   = "1234";                                // Oracle 비밀번호

        try {
            // Oracle JDBC 드라이버 로딩
            Class.forName("oracle.jdbc.OracleDriver");

            // Oracle DB에 연결
            databaseLink = DriverManager.getConnection(url, dbUser, dbPass);
        } catch (ClassNotFoundException e) {
            System.err.println("Oracle JDBC 드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Oracle DB 연결 실패:");
            e.printStackTrace();
        }

        return databaseLink;
    }
}
