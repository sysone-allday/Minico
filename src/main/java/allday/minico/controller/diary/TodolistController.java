package allday.minico.controller.diary;

import allday.minico.dto.diary.Todolist;
import allday.minico.service.diary.TodolistService;
import allday.minico.session.AppSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TodolistController implements Initializable {

    @FXML private TextField inputField;
    @FXML private Button addBtn;
    @FXML private ListView<Todolist> listView;
    @FXML private ProgressBar progress;

    private final ObservableList<Todolist> todos = FXCollections.observableArrayList();

    private final TodolistService todoService = new TodolistService();

    private String memberId;
    private LocalDate selectedDate = LocalDate.now();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        memberId = AppSession.getLoginMember().getMemberId();
        listView.setItems(todos);
        listView.setCellFactory(lv -> new ListCell<Todolist>() {
            private final CheckBox cb = new CheckBox();
            private final Label label = new Label();
            private final Button btnEdit = new Button("✎");
            private final Button btnOk = new Button("✔");
            private final Button btnDel = new Button("✖");
            private final TextField tf = new TextField();

            private boolean editing = false;

            {
                cb.setOnAction(e -> {
                    Todolist item = getItem();
                    if (item != null) {
                        item.setDone(cb.isSelected());          // Setter
                        todoService.setDone(item.getId(), item.isDone()); // Getter
                        refreshProgress();
                    }
                });

                btnDel.setOnAction(e -> {
                    Todolist item = getItem();
                    if (item != null) {
                        todoService.remove(item.getId());
                        todos.remove(item);
                        refreshProgress();
                    }
                });

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

        loadTodosFromDB();
        refreshProgress();

        addBtn.setOnAction(e -> addTodo());
        inputField.setOnAction(e -> addTodo());
    }

    private void loadTodosFromDB() {
        todos.clear();
        todos.addAll(todoService.getTodos(memberId, selectedDate));
    }

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

    private void refreshProgress() {
        long done = todos.stream().filter(Todolist::isDone).count();
        progress.setProgress(todos.isEmpty() ? 0 : (double) done / todos.size());
    }

    // 날짜/멤버 변경 시 외부에서 호출
    public void setDateAndMember(LocalDate date, String memberId) {
        this.selectedDate = date;
        this.memberId = memberId;
        loadTodosFromDB();
        refreshProgress();
    }
}