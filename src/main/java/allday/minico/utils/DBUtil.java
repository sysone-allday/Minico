package allday.minico.utils;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DBUtil {
    private static String DB_URL;
    private static String DB_USER;
    private static String DB_PASS;

    // 정적 초기화 블록: 클래스 로딩 시 한 번 실행됨
    static {
        try (InputStream input = DBUtil.class.getClassLoader().getResourceAsStream("database.properties")) {
            Properties prop = new Properties();
            prop.load(input);

            DB_URL = prop.getProperty("db.url");
            DB_USER = prop.getProperty("db.username");
            DB_PASS = prop.getProperty("db.password");

        } catch (Exception e) {
            System.err.println("DB 설정 파일 로딩 실패");
            e.printStackTrace();
        }
    }

    // DB 연결 반환
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    // DB 리소스 정리
    public static void close(Connection conn, Statement stmt, ResultSet rs) {
        try { if (rs != null) rs.close(); } catch (Exception ignored) {}
        try { if (stmt != null) stmt.close(); } catch (Exception ignored) {}
        try { if (conn != null) conn.close(); } catch (Exception ignored) {}
    }

    public static void close(Connection conn, Statement stmt) {
        close(conn, stmt, null);
    }
}
