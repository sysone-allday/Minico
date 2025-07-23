package allday.minico.controller.diary;

import allday.minico.dto.diary.Todolist;
import allday.minico.service.diary.DiaryService;
import allday.minico.service.diary.TodolistService;
import allday.minico.session.AppSession;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.skin.DatePickerSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class MyRoomController {

    @FXML private Button backButton;

    @FXML private ImageView weatherImageView;
    @FXML private ImageView minimiImageView;
    private final TodolistService todoService = new TodolistService();
    private final DiaryService diaryService = new DiaryService();
    // 일력
    @FXML private Label monthLabel;
    @FXML private Label dayLabel;

    // 잡초 ImageView 7개 주입
    @FXML private ImageView weed1; @FXML private ImageView weed2; @FXML private ImageView weed3;
    @FXML private ImageView weed4; @FXML private ImageView weed5; @FXML private ImageView weed6;
    @FXML private ImageView weed7; @FXML private ImageView weed8; @FXML private ImageView weed9;
    @FXML private ImageView weed10; @FXML private ImageView weed11;

    private List<ImageView> weeds;   // 편하게 리스트로 묶기
    private String memberId;

    // 날씨 api 키
    private static final String API_KEY = "c1b35f20fb45fd683ea1a60795b70f0d";

    @FXML
    public void initialize() {
        memberId = AppSession.getLoginMember().getMemberId();
        weeds = List.of(weed1, weed2, weed3, weed4, weed5, weed6, weed7, weed8, weed9, weed10, weed11);
        linkTodoController();      // Todo 컨트롤러 연결(화면엔 안 붙임)

        updateWeatherImage("Seoul"); // 날씨 이미지 도시 설정

        updateWeedDensity(loadTodayProgress());      // 초기값(0% 달성 → 잡초 전체 노출)

        /* 미니미 이미지 변경 */
        // skin DB에서 image_path 조회
        String imagePath = diaryService.getImagePathFor(memberId);

        // 경로 유형에 따라 이미지 로드
        Image img;
        if (imagePath.startsWith("/") || imagePath.startsWith("@")) {      // 클래스패스 자원
            URL res = getClass().getResource(imagePath.startsWith("@")
                    ? imagePath.substring(1)      // "@../" → "../"
                    : imagePath);
            img = new Image(res.toExternalForm());
        } else if (imagePath.startsWith("http")) {                         // URL
            img = new Image(imagePath, true);
        } else {                                                           // 로컬 파일 시스템
            img = new Image(new File(imagePath).toURI().toString());
        }

        // 4) ImageView에 세팅
        minimiImageView.setImage(img);

        /* 일력 삽입 */
        LocalDate today = LocalDate.now();
        monthLabel.setText(today.getMonthValue() + "월");
        dayLabel.setText(String.valueOf(today.getDayOfMonth()));
    }

    // 달성률 로드
    private double loadTodayProgress() {
        List<Todolist> list = todoService.getTodos(memberId, LocalDate.now());
        long done = list.stream().filter(Todolist::isDone).count();
        return list.isEmpty() ? 0 : (double) done / list.size();
    }

    // todo 리스트 불러오기
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


    // 창문 날씨별 삽입
    private void updateWeatherImage(String city) {
        try {
            String apiURL = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            JsonObject json = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonObject();
            String weather = json.getAsJsonArray("weather").get(0).getAsJsonObject().get("main").getAsString();

            // ★ 1) 전체 응답 찍기
            System.out.println("[RAW] " + json);

// ★ 2) 데이터 타임스탬프 → 현지 시각으로 변환
            long dt   = json.get("dt").getAsLong();        // UTC epoch
            int tzSec = json.get("timezone").getAsInt();   // 예: +32400 = KST
            ZonedDateTime obsTime = Instant.ofEpochSecond(dt)
                    .atZone(ZoneOffset.ofTotalSeconds(tzSec));
            System.out.println("[TIME] 관측시각 = " + obsTime);

            // 밤·낮 판별 (00~05, 19~23시는 night)
            boolean isNight = java.time.LocalTime.now().getHour() >= 19
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
        /* 방어 코드: Scene 이 없으면 바로 반환 */
        Scene currentScene =
                (event == null || event.getSource() == null)
                        ? null
                        : ((Node) event.getSource()).getScene();
        if (currentScene == null) {
            System.err.println("🚫 Scene 이 연결되지 않았습니다.");
            return;
        }
        Stage stage = (Stage) currentScene.getWindow();

        try {
            Parent diaryRoot = FXMLLoader.load(
                    getClass().getResource("/allday/minico/view/diary/diary.fxml"));

            /* 새 Scene 을 만들어 교체 → 루트 교체 시점에 따른 이벤트 충돌 없음 */
            Scene diaryScene =  new Scene(diaryRoot, 1280, 800);
            diaryScene.getStylesheets().add(
                    getClass().getResource(
                            "/allday/minico/css/diary.css").toExternalForm());

            /* (선택) 최소·최대 또는 고정 크기 */
            stage.setMinWidth(1280);
            stage.setMinHeight(800);
            stage.setScene(diaryScene);
            stage.setResizable(false);
            stage.sizeToScene();        // ⭐ 새 씬 크기에 맞게 Stage 사이즈 재조정
            stage.show();

        } catch (IOException e) {
            System.err.println("🚫 diary.fxml 로드 실패");
            e.printStackTrace();
        }
    }



    // 메인 화면으로 이동
    @FXML
    private void goToMain() {
        try {
            // 메인 화면 FXML 로드
            Parent mainRoot = FXMLLoader.load(getClass().getResource("/allday/minico/view/Miniroom.fxml"));

            // 현재 Stage 얻기
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Scene 변경
            stage.getScene().setRoot(mainRoot);
        } catch (IOException e) {
            System.err.println("🚫 [화면 전환 실패] Miniroom.fxml 로드 중 오류 발생");
            System.err.println("경로 확인: /allday/minico/view/Miniroom.fxml");
            e.printStackTrace();
        }
    }

}
