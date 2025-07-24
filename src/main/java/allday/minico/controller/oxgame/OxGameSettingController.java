package allday.minico.controller.oxgame;

import allday.minico.dto.oxgame.OxUserSetting;
import allday.minico.dto.oxgame.ProblemTypeDTO;
import allday.minico.service.oxgame.OxGameSettingService;
import allday.minico.session.AppSession;
import allday.minico.utils.member.SceneManager;
import allday.minico.utils.audio.BackgroundMusicManager;
import allday.minico.utils.audio.BackgroundMusicManager;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class OxGameSettingController {
    private static final OxGameSettingService settingService = OxGameSettingService.getInstance();
    private final OxUserSetting oxUserSetting = new OxUserSetting();

    // === minimi ===
    @FXML private ImageView minimi;
    private String characterInfo;

    // === 버튼 그룹 - 난도, 타이머, 문제 횟수 설정 ===
    @FXML private Button btnLvLow, btnLvMid, btnLvHigh, btnLvRandom;
    @FXML private Button btnTM5, btnTM10, btnTM15;
    @FXML private Button btnCT5, btnCT10, btnCT15;

    // 버튼을 List에 담아, 중복 선택 방지
    private List<Button> lvButtons;
    private List<Button> tmButtons;
    private List<Button> ctButtons;

    // === Hover 버튼 ===
    @FXML private ImageView imageNormal;
    @FXML private ImageView imageHover;
    @FXML private StackPane hoverContainer;

    // == rootPane 설정 ===
    @FXML private AnchorPane rootPane;

    // === 개발자 테스트 모드용 flag ===
    private static final boolean DEBUG_MOUSE_BORDER = false;

    // === 콤보박스 - 주제선택 ===
    @FXML private ComboBox<ProblemTypeDTO> selectProblemType;

    // === start 이미지 크기 변화 ===
    @FXML private ImageView btnStartImage;
    private String characterImageUrl;


    @FXML
    public void initialize() {
        // 배경음악 연속 재생 (이미 재생 중이면 유지)
        Platform.runLater(() -> {
            if (rootPane.getScene() != null) {
                BackgroundMusicManager.ensureMainMusicPlaying(rootPane.getScene());
            }
        });
        
        // 폰트 설정
        Font.loadFont(getClass().getResourceAsStream("/allday/minico/fonts/NEODGM.ttf"), 14);

        // 버튼 그룹 초기화
        lvButtons = List.of(btnLvLow, btnLvMid, btnLvHigh, btnLvRandom);
        tmButtons = List.of(btnTM5, btnTM10, btnTM15);
        ctButtons = List.of(btnCT5, btnCT10, btnCT15);

        // Hover 이미지 전환 처리
        hoverContainer.hoverProperty().addListener((obs, wasHover, isNowHover) -> {
            imageNormal.setVisible(!isNowHover);
            imageNormal.setCursor(Cursor.HAND); // 손 모양 커서
            imageHover.setVisible(isNowHover);
            imageHover.setCursor(Cursor.HAND); // 손 모양 커서
        });
        
        // start 이미지 크기 변화
        btnStartImage.setOnMouseEntered(e -> {
            btnStartImage.setScaleX(1.05);
            btnStartImage.setScaleY(1.05);
            btnStartImage.setCursor(Cursor.HAND); // 손 모양 커서
        });

        btnStartImage.setOnMouseExited(e -> {
            btnStartImage.setScaleX(1.0);
            btnStartImage.setScaleY(1.0);
            btnStartImage.setCursor(Cursor.DEFAULT);
        });
        String url = AppSession.getOxCharacterImageUrl();
        Platform.runLater(() -> {
            if (characterImageUrl != null && minimi != null) {
                minimi.setImage(new Image(characterImageUrl));
            } else if (characterImageUrl == null) {
                minimi.setImage(new Image(url));
            }
        });

        setupComboBox();
    }


    public void setCharacterImageUrl(String url) {
        this.characterImageUrl = url;

        // 이미 minimi가 초기화되었다면 바로 설정
        if (minimi != null) {
            minimi.setImage(new Image(characterImageUrl));
            AppSession.setOxCharacterImageUrl(characterImageUrl);
            System.out.println("setCharacterImageUrl에서 직접 설정 완료");
        }
    }

    // 콤보박스(주제 선택) 내용 불러오기
    private void setupComboBox() {
        List<ProblemTypeDTO> typeList = settingService.getProblemType();
        System.out.println(typeList.isEmpty());
        selectProblemType.setItems(FXCollections.observableArrayList(typeList));
        selectProblemType.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            oxUserSetting.setProblemType(newVal);
        });
    }

    // 클릭 이벤트 - 버튼 클릭 시 스타일 변경
    @FXML
    private void handleBackToMiniroom(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/miniroom.fxml")); // 실제 경로로 수정
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            SceneManager.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            System.err.println("미니룸 이동 화면 전환 실패 " + e.getMessage());
        }
    }

    // 사용자가 선택한 값 저장 및 css 업데이트
    @FXML
    public void handleSettingSelect(ActionEvent event) {
        Button clickedBtn = (Button) event.getSource();

        if (lvButtons.contains(clickedBtn)) {
            updateSelectedStyle(clickedBtn, lvButtons);
            oxUserSetting.setDifficulty(clickedBtn.getText());
        } else if (tmButtons.contains(clickedBtn)) {
            updateSelectedStyle(clickedBtn, tmButtons);
            oxUserSetting.setTimer(Integer.parseInt(clickedBtn.getText()));
        } else if (ctButtons.contains(clickedBtn)) {
            updateSelectedStyle(clickedBtn, ctButtons);
            oxUserSetting.setCount(Integer.parseInt(clickedBtn.getText()));
        }
    }

    // 클릭 이벤트 - 난이도, 타이머 등 선택한 버튼만 selected(스타일) 유지
    private void updateSelectedStyle(Button selected, List<Button> group) {
        for (Button btn : group) {
            btn.getStyleClass().remove("selected");
        }
        selected.getStyleClass().add("selected");
    }

     //  디버그 확인(각 요소마다의 경계 확인)
    private void addMouseTestHandler(Node node) {
        node.setOnMouseClicked(e -> {
            System.out.println("Clicked: " + node.getClass().getSimpleName());
        });

        node.setOnMouseEntered(e -> node.setStyle("-fx-border-color: red; -fx-border-width: 2;"));
        node.setOnMouseExited(e -> node.setStyle(""));

        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                addMouseTestHandler(child);
            }
        }

    }

    // 문제 시작 버튼을 클릭했을 때
    @FXML
    public void startGame(MouseEvent mouseEvent) {
        // 검증 - 추후 모달같은 창이 있으면 띄우면 좋을 것 같음
        if (oxUserSetting.getDifficulty() == null
                || oxUserSetting.getTimer() == 0
                || oxUserSetting.getCount() == 0
                || oxUserSetting.getProblemType() == null) {
            System.out.println("모든 설정을 선택해주세요.");
            return;
        }

        // 게임 화면으로 전환
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/oxgame/ox-play.fxml"));
            Parent root = loader.load();

            // OxViewController 컨트롤러에 데이터(사용자가 선택한 게임 세팅 값) 넘김
            OxPlayController controller = loader.getController();
            controller.initData(oxUserSetting);

            SceneManager.getPrimaryStage().setScene(new Scene(root, 1280, 800));
        } catch (Exception e) {
            System.err.println("게임 시작 화면 전환 실패 " + e.getMessage());
        }
    }


}
