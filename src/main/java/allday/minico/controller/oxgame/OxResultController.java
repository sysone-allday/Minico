package allday.minico.controller.oxgame;

import allday.minico.dto.oxgame.OxGameResult;
import allday.minico.session.AppSession;
import allday.minico.utils.member.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class OxResultController {
    public ImageView minimi;
    // === 다시하기, 그만하기 버튼 ===
    @FXML private Button btnRetry;
    @FXML private Button btnStopGame;

    // === back 버튼 ===
//    @FXML private StackPane hoverContainer;
//    @FXML private ImageView imageHover;
//    @FXML private ImageView imageNormal;

    // === 결과 text 제어 fx:id ===
    @FXML private Text difficulty;
    @FXML private Text typeName;
    @FXML private Text totalQuestion;
    @FXML private Text correctCount;
    @FXML private Text accuracy;

    @FXML
    public void initialize() {
        // 폰트 설정
        Font.loadFont(getClass().getResourceAsStream("/allday/minico/fonts/NEODGM.ttf"), 14);

        // Hover 이미지 전환 처리
//        hoverContainer.hoverProperty().addListener((obs, wasHover, isNowHover) -> {
//            imageNormal.setVisible(!isNowHover);
//            imageNormal.setCursor(Cursor.HAND); // 손 모양 커서
//            imageHover.setVisible(isNowHover);
//            imageHover.setCursor(Cursor.HAND); // 손 모양 커서
//        });

        // stop 및 retry 버튼 이미지 크기 변화
        btnStopGame.setOnMouseEntered(e -> {
            btnStopGame.setScaleX(1.05);
            btnStopGame.setScaleY(1.05);
        });
        btnStopGame.setOnMouseExited(e -> {
            btnStopGame.setScaleX(1.0);
            btnStopGame.setScaleY(1.0);
        });
        btnRetry.setOnMouseEntered(e -> {
            btnRetry.setScaleX(1.05);
            btnRetry.setScaleY(1.05);
        });
        btnRetry.setOnMouseExited(e -> {
            btnRetry.setScaleX(1.0);
            btnRetry.setScaleY(1.0);
        });

        // 다시하기, 그만하기 버튼 클릭 시 화면 이동
        btnRetry.setOnMouseClicked(this::retryGame);
        btnStopGame.setOnMouseClicked(this::handleBackToMiniroom);

        String url = AppSession.getOxCharacterImageUrl();
        Platform.runLater(() -> {
            if (url != null && minimi != null) {
                minimi.setImage(new Image(url));
            }
        });
    }

    @FXML
    private void retryGame(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/oxgame/ox-setting.fxml")); // 실제 경로로 수정
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            SceneManager.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            System.err.println("게임 세팅 화면 전환 실패 " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToMiniroom(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/miniroom.fxml")); // 실제 경로로 수정
            Parent root = loader.load();
            // 화면 전환
            Scene scene = new Scene(root, 1280, 800);
            Stage stage = SceneManager.getPrimaryStage();
            stage.setScene(scene);
            stage.show();

        } catch (Exception e) {
            System.err.println("미니룸 이동 화면 전환 실패 " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 이전 게임 플레이 결과를 가져와서 text 업데이트
    public void setResultData(OxGameResult oxGameResult) {
        difficulty.setText(oxGameResult.getDifficulty());
        typeName.setText(oxGameResult.getTypeName());
        totalQuestion.setText(String.valueOf(oxGameResult.getTotalCount()));
        correctCount.setText(String.valueOf(oxGameResult.getCorrectCount()));
        accuracy.setText(String.valueOf(oxGameResult.getAccuracy()));
    }


}
