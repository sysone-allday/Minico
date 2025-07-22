package allday.minico.controller.diary;

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
import javafx.stage.Stage;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;

public class MyRoomController {

    @FXML private ImageView weatherImageView;
    @FXML private AnchorPane calendarContainer;

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
        updateWeatherImage("Seoul");
        embedCalendar(); // 달력 넣기
    }

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


    private void updateWeatherImage(String city) {
        try {
            String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            String weather = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            // 밤·낮 판별 (00~05, 20~23시는 night)
            boolean isNight = java.time.LocalTime.now().getHour() >= 20
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
