package allday.minico.controller;

import allday.minico.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TodolistController implements Initializable {

    @FXML
    private VBox todoListBox;

    private List<Todo> todos = new ArrayList<>();

    private String memberId = "user01";
    private LocalDate selectedDate = LocalDate.now();

    // Todo 데이터 클래스
    public static class Todo {
        public String content;
        public boolean isDone;

        public Todo(String content, boolean isDone) {
            this.content = content;
            this.isDone = isDone;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // DB에서 투두 목록 불러오기
        loadTodosFromDB();
        // 화면에 체크박스 렌더링
        loadTodoList();
    }

    // DB에서 투두 데이터 읽어와서 todos 리스트에 저장
    private void loadTodosFromDB() {
        todos.clear();
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDb = connection.getConnection();

        System.out.println(">>> selectedDate: " + selectedDate);
        System.out.println(">>> memberId: " + memberId);

        String selectSql = "SELECT CONTENT, IS_DONE FROM TODO WHERE TO_CHAR(CREATE_AT, 'YYYY-MM-DD') = ? AND MEMBER_ID = ?";

        try (PreparedStatement preparedStatement = connectDb.prepareStatement(selectSql)) {
            preparedStatement.setString(1, selectedDate.toString());
            preparedStatement.setString(2, memberId);
            System.out.println(">>> 쿼리 파라미터: " + selectedDate.toString() + " / " + memberId);

            ResultSet rs = preparedStatement.executeQuery();
            int count = 0;
            while (rs.next()) {
                String content = rs.getString("CONTENT");
                boolean isDone = "Y".equalsIgnoreCase(rs.getString("IS_DONE"));
                todos.add(new Todo(content, isDone));
                count++;
            }
            System.out.println(">>> DB에서 읽은 투두 개수: " + count);
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try { connectDb.close(); } catch (Exception ignore) {}
    }


    // todos 리스트를 기반으로 체크박스들을 그려주는 함수
    public void loadTodoList() {
        todoListBox.getChildren().clear();
        for (Todo todo : todos) {
            CheckBox cb = new CheckBox(todo.content);
            cb.setSelected(todo.isDone);
            cb.setPrefWidth(290.0);
            cb.setPrefHeight(39.0); // 원하는 높이로
            cb.setStyle("-fx-font-size: 18px;");
            // 체크박스 상태 변경 → DB에 반영
            cb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                todo.isDone = newVal;
                updateTodoDoneInDB(todo, newVal);
            });
            todoListBox.getChildren().add(cb);
        }
    }

    // 체크박스 클릭 시 DB에도 상태 반영
    private void updateTodoDoneInDB(Todo todo, boolean isDone) {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDb = connection.getConnection();
        String updateSql = "UPDATE TODO SET IS_DONE = ? WHERE CONTENT = ? AND MEMBER_ID = ? AND TO_CHAR(CREATE_AT, 'YYYY-MM-DD') = ?";
        try (PreparedStatement pstmt = connectDb.prepareStatement(updateSql)) {
            pstmt.setString(1, isDone ? "Y" : "N");
            pstmt.setString(2, todo.content);
            pstmt.setString(3, memberId);
            pstmt.setString(4, selectedDate.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try { connectDb.close(); } catch (Exception ignore) {}
    }

    // 외부에서 투두리스트(날짜, 아이디 등) 갱신하고 싶을 때 사용
    public void setTodos(List<Todo> newTodos) {
        this.todos = newTodos;
        loadTodoList();
    }
    public void setDateAndMember(LocalDate date, String memberId) {
        this.selectedDate = date;
        this.memberId = memberId;
        loadTodosFromDB();
        loadTodoList();
    }
}