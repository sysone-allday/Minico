package allday.minico.controller.diary;

import allday.minico.dto.diary.Diary;
import allday.minico.dto.member.Member;
import allday.minico.service.diary.DiaryService;
import allday.minico.session.AppSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

public class DiaryController implements Initializable {
    @FXML private Button dateBackButton;
    @FXML private Button dateNextButton;
    @FXML private Button diaryRegisterButton;
    @FXML private Button diaryEditDoneButton;
    @FXML private Button diaryEditButton;
    @FXML private TextArea diaryTextArea;
    @FXML private Text diaryContentText;
    @FXML private Text dateText;
    @FXML private DatePicker datePicker;
    @FXML private StackPane calendarModalContainer;
    @FXML private VBox todolist;
    @FXML private Button backButton;

    private TodolistController todolistController;
    private final DiaryService diaryService = new DiaryService();

    private String memberId;
    // 현재 보고 있는 날짜를 반환
    @Getter
    private LocalDate selectedDate = LocalDate.now();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        memberId = AppSession.getLoginMember().getMemberId();
        diaryContentText.setFont(Font.font("Neo둥근모", 24));
        updateDateText();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/diary/todolist.fxml"));
            VBox loaded = loader.load();  // VBox 루트 노드
            todolistController = loader.getController(); // 진짜 컨트롤러 얻기

            // calendarModalContainer처럼 화면에 추가
            todolist.getChildren().setAll(loaded);  // 현재 todolist에 컨텐츠 교체

            // 날짜 적용
            todolistController.setDateAndMember(selectedDate, memberId);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 다이어리 crud 버튼
        diaryRegisterButton.setOnAction(event -> diaryRegisterButtonAction());
        diaryEditButton.setOnAction(event -> diaryEditButtonAction());
        diaryEditDoneButton.setOnAction(event -> diaryEditDoneButtonAction());

        //
        if (datePicker != null) {
            datePicker.setValue(LocalDate.now()); // 화면 초기 오늘 날짜 설정
            datePicker.setOnAction(e -> { // 날짜 변경 이벤트
                LocalDate newDate = datePicker.getValue();
                onDateChanged(newDate);
            });
        }

        // 날짜 전날, 다음 날 변경
        dateBackButton.setOnAction(event -> onDateChanged(selectedDate.minusDays(1)));
        dateNextButton.setOnAction(event -> onDateChanged(selectedDate.plusDays(1)));

        // 앱 시작시 오늘 날짜로 diary 내용 조회
        onDateChanged(selectedDate);
    }

    // 날짜 변경
    private void updateDateText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        dateText.setText(selectedDate.format(formatter));
    }

    // 일기 등록
    public void diaryRegisterButtonAction() {
        String content = diaryTextArea.getText();
        if (content == null || content.trim().isEmpty()) {
            System.out.println("내용을 입력해주세요.");
            return;
        }
        Diary diary = new Diary(null, content, selectedDate, memberId, 1);
        boolean result = diaryService.registerDiary(diary);
        if (result) {
            System.out.println("일기 등록 성공!");
            loadDiaryForDate(selectedDate);
        } else {
            System.out.println("일기 등록 실패.");
        }
    }

    //일기 수정
    public void diaryEditButtonAction() {
        diaryTextArea.setText(diaryContentText.getText());
        diaryTextArea.setVisible(true);
        diaryContentText.setVisible(false);
        diaryEditButton.setVisible(false);
        diaryEditDoneButton.setVisible(true);
        diaryRegisterButton.setVisible(false);
    }

    // 일기 수정 완료
    public void diaryEditDoneButtonAction() {
        String content = diaryTextArea.getText();
        Diary diary = new Diary(null, content, selectedDate, memberId, 1);
        boolean result = diaryService.editDiary(diary);
        if (result) {
            System.out.println("일기 수정 완료!");
            loadDiaryForDate(selectedDate);
        } else {
            System.out.println("일기 수정 실패.");
        }
    }

    // 날짜에 따른 일기 로드
    private void loadDiaryForDate(LocalDate date) {
        Diary diary = diaryService.getDiary(memberId, date);
        // 일기 내용 유무에 따라 textfield 변경
        if (diary != null) {
            diaryContentText.setText(diary.getContent());
            diaryContentText.setVisible(true); // 읽기 모드로 전환
            diaryTextArea.setVisible(false); // 수정 모드 숨김
            diaryEditButton.setVisible(true);
            diaryRegisterButton.setVisible(false);
            diaryEditDoneButton.setVisible(false);
        } else {
            diaryContentText.setVisible(false);
            diaryTextArea.setVisible(true);
            diaryTextArea.clear();
            diaryEditButton.setVisible(false);
            diaryRegisterButton.setVisible(true);
            diaryEditDoneButton.setVisible(false);
        }
        updateDateText();
    }

    // 날짜 변경에 따른 일기 화면 갱신
    public void onDateChanged(LocalDate newDate) {
        selectedDate = newDate;
        loadDiaryForDate(selectedDate);

        // todolist 컨트롤러도 바뀜
        if (todolistController != null) {
            todolistController.setDateAndMember(selectedDate, memberId);  // 컨트롤러에 호출
        }
    }

    //달력 클릭하면 모달 띄우기
    @FXML
    private void openCalendarModal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/diary/calendar.fxml"));
            Parent calendarPane = loader.load();

            CalendarController calendarController = loader.getController();
            calendarController.setDiaryController(this);

            calendarController.setCloseCallback(() -> calendarModalContainer.setVisible(false)); // 닫기 콜백
            calendarModalContainer.getChildren().setAll(calendarPane);
            calendarModalContainer.setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // back 버튼 클릭 시 마이룸으로 이동
    @FXML
    private void goToMyRoom() {
        try {
            // 메인 화면 FXML 로드
            Parent mainRoot = FXMLLoader.load(getClass().getResource("/allday/minico/view/diary/myroom.fxml"));

            // 현재 Stage와 Scene 가져오기
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = stage.getScene();

            // Root 교체
            stage.getScene().setRoot(mainRoot);

            scene.getStylesheets().add(getClass().getResource("/allday/minico/css/diary.css").toExternalForm());

        } catch (IOException e) {
            System.err.println("🚫 [화면 전환 실패] myroom.fxml 로드 중 오류 발생");
            System.err.println("경로 확인: /allday/minico/view/diary/myroom.fxml");
            e.printStackTrace();
        }
    }

}
