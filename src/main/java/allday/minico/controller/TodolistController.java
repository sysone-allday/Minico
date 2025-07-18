package allday.minico.controller;

import allday.minico.DatabaseConnection;
import javafx.scene.control.ProgressBar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
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

    @FXML private TextField inputField;
    @FXML private Button addBtn;
    @FXML private ListView<Todo> listView;
    @FXML private ProgressBar progress;

    private final ObservableList<Todo> todos = FXCollections.observableArrayList();

    private String memberId = "USER01";
    private LocalDate selectedDate = LocalDate.now();

    // Todo 데이터
    public static class Todo {
        public long id;
        public String content;
        public boolean isDone;

        public Todo(long id, String content, boolean isDone) {
            this.id = id;
            this.content = content;
            this.isDone = isDone;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        todos.clear();
        listView.setItems(todos);

        listView.setCellFactory(lv -> new ListCell<Todo>() {
            private final CheckBox cb = new CheckBox();
            private final Label label = new Label();
            private final Button btnEdit = new Button("✎");
            private final Button btnOk = new Button("✔"); // ✔ 수정완료 버튼
            private final Button btnDel = new Button("✖");

            private final TextField tf = new TextField();

            // 현재 편집중인 Todo 객체인지 확인
            private boolean editing = false;

            {
                // 체크박스: 완료 여부 DB 반영
                cb.setOnAction(e -> {
                    Todo item = getItem();
                    if (item != null) {
                        item.isDone = cb.isSelected();
                        updateDoneDB(item.id, item.isDone);
                        refreshProgress();
                    }
                });

                // 삭제
                btnDel.setOnAction(e -> {
                    Todo item = getItem();
                    if (item != null) {
                        deleteTodoFromDB(item.id);
                        todos.remove(item);
                        refreshProgress();
                    }
                });

                // 수정 진입: ✎ → ✔
                btnEdit.setOnAction(e -> {
                    Todo item = getItem();
                    if (item != null) {
                        tf.setText(item.content);
                        editing = true;
                        updateItem(item, false);
                        tf.requestFocus();
                    }
                });

                // ✔버튼: 편집 완료
                btnOk.setOnAction(e -> commitEdit());
                // 텍스트필드 엔터: 편집 완료
                tf.setOnAction(e -> commitEdit());
                // 포커스 잃어도 자동완료 하고 싶다면 아래 주석 해제
                // tf.focusedProperty().addListener((obs, oldV, newV) -> { if (!newV) commitEdit(); });
            }

            private void commitEdit() {
                Todo item = getItem();
                if (item != null && editing) {
                    String newText = tf.getText().trim();
                    if (!newText.isEmpty() && !newText.equals(item.content)) {
                        updateContentDB(item.id, newText);
                        item.content = newText;
                    }
                    editing = false;
                    updateItem(item, false);
                }
            }

            @Override
            protected void updateItem(Todo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else if (editing && getItem() == item) {
                    cb.setSelected(item.isDone);
                    tf.setText(item.content);
                    tf.setPrefWidth(140);
                    setGraphic(new HBox(10, cb, tf, btnOk, btnDel)); // ✔ 버튼 표시!
                } else {
                    cb.setSelected(item.isDone);
                    label.setText(item.content);
                    label.setStyle(item.isDone ? "-fx-strikethrough:true;-fx-opacity:.5" : "-fx-strikethrough:false;");
                    setGraphic(new HBox(10, cb, label, btnEdit, btnDel));
                }
            }
        });
        loadTodosFromDB();
        refreshProgress();

        addBtn.setOnAction(e -> addTodo());
        inputField.setOnAction(e -> addTodo());
    }

    private void addTodo() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        long newId = insertTodoToDB(text);     // DB에 INSERT 후 PK 받기
        if (newId != 0) {
            todos.add(new Todo(newId, text, false));
            inputField.clear();
            refreshProgress();
        }
    }

        // 달성률
    private void refreshProgress() {
        long done = todos.stream().filter(t -> t.isDone).count();
        progress.setProgress(todos.isEmpty()? 0 : (double)done / todos.size());
    }


    /** INSERT 후 생성된 PK 리턴 */
    private long insertTodoToDB(String text) {
        String sql = "INSERT INTO TODO (CONTENT, IS_DONE, CREATE_AT, MEMBER_ID)"
                + " VALUES (?, 'N', ?, ?)";
        try (Connection c=new DatabaseConnection().getConnection();
             PreparedStatement ps=c.prepareStatement(sql, new String[]{"TODO_ID"})) {
            ps.setString(1, text);
            ps.setDate  (2, java.sql.Date.valueOf(selectedDate));
            ps.setString(3, memberId.toUpperCase());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                return rs.next()? rs.getLong(1) : 0;
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    private void updateDoneDB(long id, boolean done){
        String sql="UPDATE TODO SET IS_DONE=? WHERE TODO_ID=?";
        try(Connection c=new DatabaseConnection().getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1, done? "Y":"N");
            ps.setLong(2, id);
            ps.executeUpdate();
        }catch(SQLException e){e.printStackTrace();}
    }

    private void updateContentDB(long id, String text){
        String sql="UPDATE TODO SET CONTENT=? WHERE TODO_ID=?";
        try(Connection c=new DatabaseConnection().getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setString(1, text);
            ps.setLong(2, id);
            ps.executeUpdate();
        }catch(SQLException e){e.printStackTrace();
        }
    }

    private void deleteTodoFromDB(long id){
        String sql="DELETE FROM TODO WHERE TODO_ID=?";
        try(Connection c=new DatabaseConnection().getConnection();
            PreparedStatement ps=c.prepareStatement(sql)){
            ps.setLong(1, id);
            ps.executeUpdate();
        }catch(SQLException e){e.printStackTrace();}
    }


    // DB에서 투두 데이터 읽어와서 todos 리스트에 저장
    private void loadTodosFromDB() {
        todos.clear();
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDb = connection.getConnection();

        System.out.println(">>> selectedDate: " + selectedDate);
        System.out.println(">>> memberId: " + memberId);

        String selectSql =
                "SELECT TODO_ID, CONTENT, IS_DONE " +
                        "FROM   TODO " +
                        "WHERE  MEMBER_ID = UPPER(?) " +
                        "  AND  TRUNC(CREATE_AT) = ?";
        try (PreparedStatement preparedStatement = connectDb.prepareStatement(selectSql)) {
            preparedStatement.setString(1, memberId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(selectedDate));
            System.out.println(">>> 쿼리 파라미터: " + selectedDate.toString() + " / " + memberId);

            ResultSet rs = preparedStatement.executeQuery();
            int count = 0;
            while (rs.next()) {
                long id =  rs.getLong("TODO_ID");
                String content = rs.getString("CONTENT");
                boolean isDone = "Y".equalsIgnoreCase(rs.getString("IS_DONE"));
                todos.add(new Todo(id, content, isDone));
                count++;
            }
            System.out.println(">>> DB에서 읽은 투두 개수: " + count);
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try { connectDb.close(); } catch (Exception ignore) {}
    }


    public void setDateAndMember(LocalDate date, String memberId) {
        this.selectedDate = date;
        this.memberId = memberId;
        loadTodosFromDB();
        refreshProgress();
    }
}