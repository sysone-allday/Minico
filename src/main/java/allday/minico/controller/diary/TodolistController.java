package allday.minico.controller.diary;

import allday.minico.dto.diary.Todolist;
import allday.minico.service.diary.TodolistService;
import allday.minico.session.AppSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.ResourceBundle;

public class TodolistController implements Initializable {

    @FXML private TextField inputField;
    @FXML private Button addBtn;
    @FXML private ListView<Todolist> listView;
    @FXML private ProgressBar progress;

    private final ObservableList<Todolist> todos = FXCollections.observableArrayList();
    private final TodolistService todoService = new TodolistService();
    @Setter
    private MyRoomController myRoomController; // 주입받는 참조

    private String memberId;
    private LocalDate selectedDate = LocalDate.now();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        memberId = AppSession.getLoginMember().getMemberId();
        listView.setItems(todos); // ListView 데이터 연결
        listView.setCellFactory(lv -> new ListCell<>() {
            private final CheckBox cb = new CheckBox();
            private final Label label = new Label();
            private final Button btnEdit = new Button("✎");
            private final Button btnOk = new Button("✔");
            private final Button btnDel = new Button("✖");
            private final TextField tf = new TextField();

            private boolean editing = false;

            {
                // CSS 클래스 연결
                cb.getStyleClass().add("todo-check");
                label.getStyleClass().add("todo-label");
                tf.getStyleClass().add("todo-edit");
                btnEdit.getStyleClass().addAll("todo-btn","edit-btn");
                btnOk  .getStyleClass().addAll("todo-btn","ok-btn");
                btnDel .getStyleClass().addAll("todo-btn","del-btn");

                cb.setOnAction(e -> {
                    Todolist item = getItem();
                    if (item != null) {
                        item.setDone(cb.isSelected());          // Setter
                        todoService.setDone(item.getId(), item.isDone()); // Getter
                        refreshProgress();
                    }
                });

                // 투두 삭제
                btnDel.setOnAction(e -> {
                    Todolist item = getItem();
                    if (item != null) {
                        todoService.remove(item.getId());
                        todos.remove(item);
                        refreshProgress();
                    }
                });

                // 투두 수정
                btnEdit.setOnAction(e -> {
                    Todolist item = getItem();
                    if (item != null) {
                        tf.setText(item.getContent());
                        editing = true;
                        updateItem(item, false);
                        tf.requestFocus();
                    }
                });

                btnOk.setOnAction(e -> commitEdit());
                tf.setOnAction(e -> commitEdit());
            }

            // 텍스트 수정 저장
            private void commitEdit() {
                Todolist item = getItem();
                if (item != null && editing) {
                    String newText = tf.getText().trim();
                    if (!newText.isEmpty() && !newText.equals(item.getContent())) {
                        todoService.setContent(item.getId(), newText);
                        item.setContent(newText);
                    }
                    editing = false;
                    updateItem(item, false);
                }
            }

            // 셀 렌더링
            @Override
            protected void updateItem(Todolist item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else if (editing && getItem() == item) {
                    cb.setSelected(item.isDone());
                    tf.setText(item.getContent());
                    tf.setPrefWidth(140);
                    setGraphic(new HBox(10, cb, tf, btnOk, btnDel));
                } else {
                    cb.setSelected(item.isDone());
                    label.setText(item.getContent());
                    label.setStyle(item.isDone() ? "-fx-strikethrough:true;-fx-opacity:.5" : "-fx-strikethrough:false;");
                    setGraphic(new HBox(10, cb, label, btnEdit, btnDel));
                }
            }
        });

        loadTodosFromDB(); // DB에서 오늘 날짜 todo 로드
        refreshProgress(); // 달성률

        addBtn.setOnAction(e -> addTodo());
        inputField.setOnAction(e -> addTodo());
    }

    // db에서 todo 로드
    private void loadTodosFromDB() {
        System.out.println("selectedDate = " + selectedDate);  // ★ 찍어 보면 오늘 날짜
        todos.clear();
        todos.addAll(todoService.getTodos(memberId, selectedDate));
        System.out.println("조회된 Todo 수 = " + todos.size());
    }

    // todo 추가
    private void addTodo() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        long newId = todoService.addTodo(text, selectedDate, memberId);
        if (newId != 0) {
            todos.add(new Todolist(newId, text, false));
            inputField.clear();
            refreshProgress();
        }
    }

    // 달성률 가져오기
    public void refreshProgress() {
        long done = todos.stream().filter(Todolist::isDone).count();
        double prog = todos.isEmpty() ? 0 : (double) done / todos.size();
        System.out.println("[Todo] done=" + done + ", total=" + todos.size() + ", prog=" + prog);

        progress.setProgress(prog);

        // MyRoom 쪽에도 전달
        if (myRoomController != null) {
            myRoomController.updateWeedDensity(prog);
        }
    }

    // 날짜/멤버 변경 시 외부에서 호출
    public void setDateAndMember(LocalDate date, String memberId) {
        this.selectedDate = date;
        this.memberId = memberId;
        loadTodosFromDB();
        refreshProgress();
    }

    public void setMyRoomController(MyRoomController mc) {
        this.myRoomController = mc;
        refreshProgress();          // ★ 연결된 순간 바로 sync
    }

}