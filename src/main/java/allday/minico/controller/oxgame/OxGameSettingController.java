package allday.minico.controller.oxgame;

import allday.minico.dto.member.Member;
import allday.minico.dto.oxgame.ProblemTypeDTO;
import allday.minico.service.oxgame.OxGameSettingService;
import allday.minico.utils.member.SceneManager;
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
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class OxGameSettingController {
    private static final OxGameSettingService settingService = OxGameSettingService.getInstance();

    // === 버튼 그룹 - 난도, 타이머, 문제 횟수 설정 ===
    @FXML private Button btnLvLow, btnLvMid, btnLvHigh, btnLvRandom;
    @FXML private Button btnTM10, btnTM15, btnTM20;
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


    @FXML
    public void initialize() {
        // 폰트 설정
        Font.loadFont(getClass().getResourceAsStream("/allday/minico/fonts/TmoneyRoundWindExtraBold.ttf"), 14);
        Font.loadFont(getClass().getResourceAsStream("/allday/minico/fonts/TmoneyRoundWindRegular.ttf"), 14);

        // 버튼 그룹 초기화
        lvButtons = List.of(btnLvLow, btnLvMid, btnLvHigh, btnLvRandom);
        tmButtons = List.of(btnTM10, btnTM15, btnTM20);
        ctButtons = List.of(btnCT5, btnCT10, btnCT15);

        // Hover 이미지 전환 처리
        hoverContainer.hoverProperty().addListener((obs, wasHover, isNowHover) -> {
            imageNormal.setVisible(!isNowHover);
            imageNormal.setCursor(Cursor.HAND); // 손 모양 커서
            imageHover.setVisible(isNowHover);
            imageHover.setCursor(Cursor.HAND); // 손 모양 커서
        });

        // 테스트용 마우스 핸들러는 개발 모드에서만 작동
        if (DEBUG_MOUSE_BORDER) {
            Platform.runLater(() -> {
                rootPane.requestLayout();
                rootPane.layout();
                addMouseTestHandler(rootPane);
            });
        }

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

        setupComboBox();
    }

    // ======= 로직 처리 =======
    // 콤보박스(주제 선택) 내용 불러오기
    private void setupComboBox() {
        List<ProblemTypeDTO> typeList = settingService.getProblemType();
        System.out.println(typeList.isEmpty());
        selectProblemType.setItems(FXCollections.observableArrayList(typeList));
    }

    // ====== FXML 이벤트 처리 ======
    // 클릭 이벤트 - 버튼 클릭 시 스타일 변경
    @FXML
    private void handleBackToMiniroom(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/miniroom.fxml")); // 실제 경로로 수정
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            SceneManager.getPrimaryStage().setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSettingSelect(ActionEvent event) {
        Button clickedBtn = (Button) event.getSource();

        if (lvButtons.contains(clickedBtn)) {
            updateSelectedStyle(clickedBtn, lvButtons);
        } else if (tmButtons.contains(clickedBtn)) {
            updateSelectedStyle(clickedBtn, tmButtons);
        } else if (ctButtons.contains(clickedBtn)) {
            updateSelectedStyle(clickedBtn, ctButtons);
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
}
