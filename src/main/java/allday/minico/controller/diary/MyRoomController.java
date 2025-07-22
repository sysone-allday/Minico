package allday.minico.controller.diary;

import allday.minico.dto.diary.Todolist;
import allday.minico.service.diary.TodolistService;
import allday.minico.session.AppSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MyRoomController {

    @FXML private ImageView weatherImageView;
    @FXML private Pane calendarContainer;
    private final TodolistService todoService = new TodolistService();


    // 잡초 ImageView 7개 주입
    @FXML private ImageView weed1;
    @FXML private ImageView weed2;
    @FXML private ImageView weed3;
    @FXML private ImageView weed4;
    @FXML private ImageView weed5;
    @FXML private ImageView weed6;
    @FXML private ImageView weed7;
    @FXML private ImageView weed8;
    @FXML private ImageView weed9;
    @FXML private ImageView weed10;
    @FXML private ImageView weed11;

    private List<ImageView> weeds;   // 편하게 리스트로 묶기
    private String memberId;

    private static final String API_KEY = getApiKey();

    private static String getApiKey() {
        String key = System.getenv("WEATHER_API_KEY");
        if (key == null || key.isBlank()) {
            throw new IllegalStateException("환경 변수 WEATHER_API_KEY 가 설정되지 않았습니다.");
        }
        return key;
    }
    @FXML
    public void initialize() {
        memberId = AppSession.getLoginMember().getMemberId();
        weeds = List.of(weed1, weed2, weed3, weed4, weed5, weed6, weed7, weed8, weed9, weed10, weed11);
        linkTodoController();      // Todo 컨트롤러 연결(화면엔 안 붙임)

        updateWeatherImage("Seoul");
        embedCalendar(); // 달력 넣기

        updateWeedDensity(loadTodayProgress());      // 초기값(0% 달성 → 잡초 전체 노출)
    }

    private double loadTodayProgress() {
        List<Todolist> list = todoService.getTodos(memberId, LocalDate.now());
        long done = list.stream().filter(Todolist::isDone).count();
        return list.isEmpty() ? 0 : (double) done / list.size();
    }

    private void linkTodoController() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass()
                    .getResource("/allday/minico/view/diary/todolist.fxml"));
            loader.load();
            TodolistController todoCtrl = loader.getController();
            todoCtrl.setMyRoomController(this);   // 여기서 refreshProgress() 호출됨
        } catch (IOException e) { e.printStackTrace(); }
    }

    // 잡초 제거
    public void updateWeedDensity(double progress) {
        // 달성률이 높을수록 남길 잡초 수가 줄어듦
        int maxWeed = weeds.size();                    // 10
        int weedToShow = (int) Math.round(maxWeed * (1 - progress));

        for (int i = 0; i < maxWeed; i++) {
            weeds.get(i).setVisible(i < weedToShow);   // 앞에서부터 숨김 처리
        }
    }


    // 달력 삽입
    private void embedCalendar() {
        DatePicker picker = new DatePicker(LocalDate.now());
        picker.setShowWeekNumbers(false);

        // 오늘 날짜만 빨간 배경/테두리
        picker.setDayCellFactory(dp -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (!empty && item.equals(LocalDate.now())) {
                    setStyle("-fx-background-color:#FFCCCC; -fx-border-color:red;");
                }
            }
        });

        // DatePickerSkin 으로 달력 Node 추출
        DatePickerSkin skin = new DatePickerSkin(picker);
        Node calendarGrid = skin.getPopupContent();     // VBox 타입

        // 달력 클릭 → diary 페이지
        calendarGrid.setOnMouseClicked(this::goToDiaryPage);

        calendarContainer.getChildren().add(calendarGrid);
    }


    // 창문 날씨별 삽입
    private void updateWeatherImage(String city) {
        try {
            String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            String weather = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            // 밤·낮 판별 (00~05, 20~23시는 night)
            boolean isNight = java.time.LocalTime.now().getHour() >= 18
                    || java.time.LocalTime.now().getHour() < 6;

            // 날씨별 접두어 결정
            String base = switch (weather.toLowerCase()) {
                case "snow"   -> "snow";
                case "clouds" -> "cloudy";
                case "rain"   -> "rainy";
                default       -> "sunny";
            };

            // 최종 파일 이름
            String imageFileName = base + (isNight ? "_night" : "") + ".png";
            String path = "/allday/minico/images/diary/" + imageFileName;

            InputStream stream = getClass().getResourceAsStream(path);
            System.out.println("TRY LOAD: " + path + " -> " + (stream == null)); // false 가 돼야 정상
            if (stream == null) return;           // 예외 처리
            weatherImageView.setImage(new Image(stream));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 달력 누르면 다이어리 페이지로 이동
    @FXML
    private void goToDiaryPage(MouseEvent event) {
        try {
            // 클릭된 노드에서 Stage 확보
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            // diary.fxml 로 전환
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(
                            "/allday/minico/view/diary/diary.fxml")));

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 뒤로 가기 누르면 미니룸 페이지로 이동
    @FXML
    private void goToMiniroomPage(MouseEvent event) {
        try {
            // 클릭된 노드에서 Stage 확보
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene().getWindow();

            // diary.fxml 로 전환
            Parent root = FXMLLoader.load(
                    Objects.requireNonNull(getClass().getResource(
                            "/allday/minico/view/Miniroom.fxml")));

            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
