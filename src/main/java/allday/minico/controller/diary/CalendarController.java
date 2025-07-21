package allday.minico.controller.diary;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.geometry.Pos;
import lombok.Setter;

import java.time.LocalDate;
import java.time.YearMonth;

public class CalendarController {

    @FXML
    private Text monthYearText;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Button nextMonthButton;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Button closeButton;

    private YearMonth currentYearMonth;
    private LocalDate today = LocalDate.now();
    private LocalDate selectedDate = today;
    private DiaryController diaryController;

    @Setter
    private Runnable closeCallback;

    public void setDiaryController(DiaryController diaryController) {
        this.diaryController = diaryController;
        this.selectedDate = diaryController.getSelectedDate(); // 초기 선택값
        this.currentYearMonth = YearMonth.from(selectedDate);
        renderCalendar();
    }

    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now(); // fallback
        renderCalendar();

        prevMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            renderCalendar();
        });

        nextMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            renderCalendar();
        });

        closeButton.setOnAction(e -> {
            if (closeCallback != null) {
                closeCallback.run();  // 이게 calendarModalContainer.setVisible(false); 를 호출함
            }
        });
    }

    private void renderCalendar() {
        calendarGrid.getChildren().clear();
        monthYearText.setText(String.format("%d년 %d월", currentYearMonth.getYear(), currentYearMonth.getMonthValue()));

        // 요일 표시
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = createDayLabel(days[i]);
            calendarGrid.add(dayLabel, i, 0);
        }

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7; // 일요일 시작

        int daysInMonth = currentYearMonth.lengthOfMonth();
        int row = 1, col = dayOfWeek;

        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            Button dayButton = createDateButton(date);
            calendarGrid.add(dayButton, col, row);

            col++;
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    private Label createDayLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(14));
        label.setTextFill(Color.web("#e27d92"));
        label.setAlignment(Pos.CENTER);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return label;
    }

    private Button createDateButton(LocalDate date) {
        Button btn = new Button(String.valueOf(date.getDayOfMonth()));
        btn.setPrefSize(40, 40);
        btn.setStyle("-fx-background-radius: 20; -fx-background-color: transparent;");

        if (date.equals(today)) {
            btn.setStyle("-fx-background-color: #fcd5ce; -fx-border-color: #e27d92; -fx-border-radius: 20; -fx-font-weight: bold;");
        }
        if (date.equals(selectedDate)) {
            btn.setStyle("-fx-background-color: #f4c2c2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20;");
        }

        btn.setOnAction(e -> {
            selectedDate = date;
            renderCalendar();
            if (diaryController != null) {
                diaryController.onDateChanged(selectedDate);
            }
            if (closeCallback != null) {
                closeCallback.run(); // 모달 닫기 대신 콜백 실행
            }
        });


        return btn;
    }
}
