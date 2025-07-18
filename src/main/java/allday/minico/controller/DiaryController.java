package allday.minico.controller;

import allday.minico.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DiaryController implements Initializable {
    @FXML
    private Button dateBackButton; // 전 날  버튼
    @FXML
    private Button dateNextButton; // 다음 날 버튼
    @FXML
    private Button diaryRegisterButton; // 일기 등록 버튼
    @FXML
    private Button diaryEditDoneButton; // 일기 수정 완료 버튼
    @FXML
    private Button diaryEditButton; // 일기 수정 버튼
    @FXML
    private TextArea diaryTextArea; // 일기 작성 구역
    @FXML
    private Text diaryContentText; // 작성된 일기 보여주는 구역
    @FXML
    private Text dateText; // 날짜 고르기
    @FXML
    private DatePicker datePicker; // 날짜 선택
    @FXML
    private TodolistController todolistController;

    // 유저아이디 - 나중에 값으로 넣음
    private final String memberId = "user01";

    // 현재 날짜로 초기화
    private LocalDate selectedDate = LocalDate.now();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        diaryContentText.setFont(Font.font("Neo둥근모", 24));

        // 날짜 텍스트 초기화
        updateDateText();

        diaryRegisterButton.setOnAction(event -> diaryRegisterButtonAction());
        diaryEditButton.setOnAction(event -> diaryEditButtonAction());
        diaryEditDoneButton.setOnAction(event -> diaryEditDoneButtonAction());

        // DatePicker가 있다면
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
            datePicker.setOnAction(e -> {
                LocalDate picked = datePicker.getValue();
                onDateChanged(picked);
                selectedDate = picked;
                loadDiaryForDate(selectedDate);
            });
        }

        // 버튼 이벤트 연결
        dateBackButton.setOnAction(event -> {
            onDateChanged(selectedDate.minusDays(1));  // 투두리스트 날짜 변경
            selectedDate = selectedDate.minusDays(1);   // 일기 날짜 변경
            updateDateText();
            loadDiaryForDate(selectedDate); // DB에서 일기 조회
        });

        dateNextButton.setOnAction(event -> {
            onDateChanged(selectedDate.plusDays(1));    // 투두리스트 날짜 변경
            selectedDate = selectedDate.plusDays(1);    // 일기 날짜 변경
            updateDateText();
            loadDiaryForDate(selectedDate); // DB에서 일기 조회
        });

        // 앱 시작시 오늘 날짜로 diary 내용 조회
        loadDiaryForDate(selectedDate);

        // 오늘 날짜를 원하는 포맷으로 넣기
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일"); // 원하는 포맷
        dateText.setText(today.format(formatter));
    }

    private void updateDateText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        dateText.setText(selectedDate.format(formatter));
    }

    // 일기 등록 버튼 시 내용 db 저장
    public void diaryRegisterButtonAction() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDb = connection.getConnection();

        String content = diaryTextArea.getText();

        if (content == null || content.trim().isEmpty()) {
            System.out.println("내용을 입력해주세요.");
            return;
        }

        String insertSql = "INSERT INTO DIARY (CONTENT, WRITTEN_AT, VISIBILITY, MEMBER_ID, EMOTION_ID) " +
                "VALUES (?, ?, ?, ?, ?)";

        try {
            PreparedStatement preparedStatement = connectDb.prepareStatement(insertSql);
            preparedStatement.setString(1, content);
            preparedStatement.setString(2, String.valueOf(selectedDate));
            preparedStatement.setString(3, "public");
            preparedStatement.setString(4, memberId);
            preparedStatement.setInt(5, 1);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("일기 등록 성공!");
                diaryEditButton.setVisible(true);
                diaryEditDoneButton.setVisible(false);
                diaryRegisterButton.setVisible(false);
                loadDiaryForDate(selectedDate);
            } else {
                System.out.println("일기 등록 실패.");
            }

            preparedStatement.close();
            connectDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 일기 수정 버튼 클릭 시 일기 내용 업데이트
    public void diaryEditButtonAction() {
        // 수정완료 버튼만 보이게
        diaryTextArea.setText(diaryContentText.getText());
        diaryTextArea.setVisible(true);
        diaryContentText.setVisible(false);
        diaryEditButton.setVisible(false);
        diaryEditDoneButton.setVisible(true);
        diaryRegisterButton.setVisible(false);
        // textarea.setEditable(true); // 편집 활성화(필요시)
    }

    public void diaryEditDoneButtonAction() {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDb = connection.getConnection();

        String content = diaryTextArea.getText();

        String editSql = "UPDATE diary SET content = ? WHERE TO_CHAR(written_at, 'YYYY-MM-DD') = ? AND MEMBER_ID = ?";
        try {
            PreparedStatement preparedStatement = connectDb.prepareStatement(editSql);
            preparedStatement.setString(1, content);
            preparedStatement.setString(2, selectedDate.toString());
            preparedStatement.setString(3, memberId);

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("일기 수정 완료!");
                // “수정” 버튼만 보이게 (DB에서 일기 다시 불러오기!)
                loadDiaryForDate(selectedDate);
            } else {
                System.out.println("일기 수정 실패.");
            }
            preparedStatement.close();
            connectDb.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 날짜 별로 일기 가져오기
    private void loadDiaryForDate(LocalDate date) {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDb = connection.getConnection();

        String selectSql = "SELECT CONTENT FROM DIARY WHERE TO_CHAR(WRITTEN_AT, 'YYYY-MM-DD') = ? AND MEMBER_ID=?";
        try {
            PreparedStatement preparedStatement = connectDb.prepareStatement(selectSql);
            preparedStatement.setString(1, selectedDate.toString());
            preparedStatement.setString(2, memberId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                String content = rs.getString("CONTENT");
                diaryContentText.setText(content);      // ★ 이 한 줄!
                diaryContentText.setVisible(true);      // 텍스트 창 보여주기
                diaryTextArea.setVisible(false);
                diaryEditButton.setVisible(true);
                diaryRegisterButton.setVisible(false);
                diaryEditDoneButton.setVisible(false);
            } else {
                diaryContentText.setVisible(false);
                diaryTextArea.setVisible(true);
                diaryTextArea.clear(); // 내용 없으면 빈칸
                diaryEditButton.setVisible(false);
                diaryRegisterButton.setVisible(true);
                diaryEditDoneButton.setVisible(false);
            }
            rs.close();
            preparedStatement.close();
            connectDb.close();
        } catch (SQLException e) {
            e.printStackTrace();
            diaryTextArea.clear();
        }
    }

    public void onDateChanged(LocalDate newDate) {
        selectedDate = newDate;
        updateDateText();
        loadDiaryForDate(selectedDate);
        todolistController.setDateAndMember(newDate, memberId);
    }


}