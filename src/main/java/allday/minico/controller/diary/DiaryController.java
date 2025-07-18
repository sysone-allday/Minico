package allday.minico.controller.diary;

import allday.minico.dto.diary.Diary;
import allday.minico.service.diary.DiaryService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    private final DiaryService diaryService = new DiaryService();

    private final String memberId = "USER01";
    private LocalDate selectedDate = LocalDate.now();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        diaryContentText.setFont(Font.font("Neo둥근모", 24));
        updateDateText();

        diaryRegisterButton.setOnAction(event -> diaryRegisterButtonAction());
        diaryEditButton.setOnAction(event -> diaryEditButtonAction());
        diaryEditDoneButton.setOnAction(event -> diaryEditDoneButtonAction());

        if (datePicker != null) {
            datePicker.setValue(LocalDate.now());
            datePicker.setOnAction(e -> {
                LocalDate newDate = datePicker.getValue();
                onDateChanged(newDate);
            });
        }

        dateBackButton.setOnAction(event -> onDateChanged(selectedDate.minusDays(1)));
        dateNextButton.setOnAction(event -> onDateChanged(selectedDate.plusDays(1)));

        // 앱 시작시 오늘 날짜로 diary 내용 조회
        onDateChanged(selectedDate);
    }

    private void updateDateText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 M월 d일");
        dateText.setText(selectedDate.format(formatter));
    }

    public void diaryRegisterButtonAction() {
        String content = diaryTextArea.getText();
        if (content == null || content.trim().isEmpty()) {
            System.out.println("내용을 입력해주세요.");
            return;
        }
        Diary diary = new Diary(null, content, selectedDate, "public", memberId, 1);
        boolean result = diaryService.registerDiary(diary);
        if (result) {
            System.out.println("일기 등록 성공!");
            loadDiaryForDate(selectedDate);
        } else {
            System.out.println("일기 등록 실패.");
        }
    }

    public void diaryEditButtonAction() {
        diaryTextArea.setText(diaryContentText.getText());
        diaryTextArea.setVisible(true);
        diaryContentText.setVisible(false);
        diaryEditButton.setVisible(false);
        diaryEditDoneButton.setVisible(true);
        diaryRegisterButton.setVisible(false);
    }

    public void diaryEditDoneButtonAction() {
        String content = diaryTextArea.getText();
        Diary diary = new Diary(null, content, selectedDate, "public", memberId, 1);
        boolean result = diaryService.editDiary(diary);
        if (result) {
            System.out.println("일기 수정 완료!");
            loadDiaryForDate(selectedDate);
        } else {
            System.out.println("일기 수정 실패.");
        }
    }

    private void loadDiaryForDate(LocalDate date) {
        Diary diary = diaryService.getDiary(memberId, date);
        if (diary != null) {
            diaryContentText.setText(diary.getContent());
            diaryContentText.setVisible(true);
            diaryTextArea.setVisible(false);
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

    public void onDateChanged(LocalDate newDate) {
        selectedDate = newDate;
        loadDiaryForDate(selectedDate);
    }
}
