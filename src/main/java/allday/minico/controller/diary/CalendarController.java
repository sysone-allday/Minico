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

// 김민서 파트
public class CalendarController {

    // 현재 달/년 표시 텍스트
    @FXML
    private Text monthYearText;

    // 이전/다음 달로 이동하는 버튼
    @FXML
    private Button prevMonthButton;
    @FXML
    private Button nextMonthButton;

    // 달력 격자(GridPane)
    @FXML
    private GridPane calendarGrid;

    // 모달 닫기 버튼
    @FXML
    private Button closeButton;

    // 현재 표시 중인 달 (예: 2025-07)
    private YearMonth currentYearMonth;

    // 오늘 날짜
    private LocalDate today = LocalDate.now();

    // 선택된 날짜 (기본: 오늘)
    private LocalDate selectedDate = today;

    // 다이어리 컨트롤러 참조
    private DiaryController diaryController;

    // 모달 닫기 콜백 함수
    @Setter
    private Runnable closeCallback;

    // 다이어리 컨트롤러 설정 및 초기 선택 날짜 세팅
    public void setDiaryController(DiaryController diaryController) {
        this.diaryController = diaryController;
        this.selectedDate = diaryController.getSelectedDate(); // 초기 선택 날짜
        this.currentYearMonth = YearMonth.from(selectedDate);
        renderCalendar(); // 달력 생성
    }

    // FXML 초기화 메서드
    @FXML
    public void initialize() {
        currentYearMonth = YearMonth.now(); // 기본값 설정
        renderCalendar(); // 달력 초기 렌더링

        // 이전 달 버튼 이벤트
        prevMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            renderCalendar();
        });

        // 다음 달 버튼 이벤트
        nextMonthButton.setOnAction(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            renderCalendar();
        });

        // 닫기 버튼 이벤트 (콜백 실행)
        closeButton.setOnAction(e -> {
            if (closeCallback != null) {
                closeCallback.run();  // calendarModalContainer.setVisible(false); 호출됨
            }
        });
    }

    // 달력 그리기 함수
    private void renderCalendar() {
        calendarGrid.getChildren().clear(); // 기존 달력 내용 삭제
        monthYearText.setText(String.format("%d년 %d월", currentYearMonth.getYear(), currentYearMonth.getMonthValue()));

        // 요일 라벨 추가
        String[] days = {"일", "월", "화", "수", "목", "금", "토"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = createDayLabel(days[i]);
            calendarGrid.add(dayLabel, i, 0);
        }

        // 현재 달의 첫 날짜와 시작 요일 계산
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7; // 일요일 시작 기준

        int daysInMonth = currentYearMonth.lengthOfMonth(); // 현재 달의 총 일 수
        int row = 1, col = dayOfWeek;

        // 날짜 버튼 생성
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

    // 요일 라벨 생성 함수
    private Label createDayLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font(14));
        label.setTextFill(Color.web("#e27d92"));
        label.setAlignment(Pos.CENTER);
        label.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); // 그리드 내에서 꽉 차게
        return label;
    }

    // 날짜 버튼 생성 함수
    private Button createDateButton(LocalDate date) {
        Button btn = new Button(String.valueOf(date.getDayOfMonth()));
        btn.setPrefSize(40, 40); // 버튼 크기 설정
        btn.setStyle("-fx-background-radius: 20; -fx-background-color: transparent;"); // 기본 스타일

        // 오늘 날짜일 경우 스타일 강조
        if (date.equals(today)) {
            btn.setStyle("-fx-background-color: #fcd5ce; -fx-border-color: #e27d92; -fx-border-radius: 20; -fx-font-weight: bold;");
        }

        // 선택된 날짜일 경우 스타일 강조
        if (date.equals(selectedDate)) {
            btn.setStyle("-fx-background-color: #f4c2c2; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20;");
        }

        // 날짜 클릭 이벤트
        btn.setOnAction(e -> {
            selectedDate = date;       // 선택 날짜 변경
            renderCalendar();          // 달력 다시 그림 (선택 강조 반영)
            if (diaryController != null) {
                diaryController.onDateChanged(selectedDate); // 다이어리 내용 로딩
            }
            if (closeCallback != null) {
                closeCallback.run();   // 모달 닫기 콜백 실행
            }
        });

        return btn;
    }
}
