package allday.minico.dao.diary;

import allday.minico.dto.diary.Todolist;
import allday.minico.sql.diary.TodolistSQL;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static allday.minico.utils.DBUtil.getConnection;

public class TodolistDAO {
    // 날짜별 투두 조회
    public List<Todolist> getTodosByDate(String memberId, LocalDate date) {
        List<Todolist> list = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(TodolistSQL.SELECT_BY_MEMBER_AND_DATE)) {
            ps.setString(1, memberId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Todolist(
                            rs.getLong("TODO_ID"),
                            rs.getString("CONTENT"),
                            "Y".equalsIgnoreCase(rs.getString("IS_DONE"))
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    // 투두 추가
    public long insertTodo(String content, LocalDate date, String memberId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(TodolistSQL.INSERT, new String[]{"TODO_ID"})) {
            ps.setString(1, content);
            ps.setDate(2, java.sql.Date.valueOf(date));
            ps.setString(3, memberId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // 완료 수정
    public void updateDone(long id, boolean done) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(TodolistSQL.UPDATE_DONE)) {
            ps.setString(1, done ? "Y" : "N");
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 투두 내용 수정
    public void updateContent(long id, String content) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(TodolistSQL.UPDATE_CONTENT)) {
            ps.setString(1, content);
            ps.setLong(2, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    // 삭제
    public void delete(long id) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(TodolistSQL.DELETE)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public double getWeedRatio(String memberId, LocalDate date) {
        double ratio = 0;
        try (Connection conn = getConnection();
             CallableStatement cs = conn.prepareCall(TodolistSQL.CALL_GET_WEED_RATIO)) {

            cs.registerOutParameter(1, Types.DOUBLE);
            cs.setString(2, memberId);
            cs.setDate(3, java.sql.Date.valueOf(date));
            cs.execute();

            ratio = cs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ratio;
    }


}