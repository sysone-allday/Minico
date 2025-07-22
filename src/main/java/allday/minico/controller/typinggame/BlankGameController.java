package allday.minico.controller.typinggame;

import allday.minico.dto.typinggame.Word;
import allday.minico.service.typinggame.TypingGameService;
import allday.minico.service.typinggame.TypingGameServiceImpl;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class BlankGameController {

    @FXML private Pane wordListPane;
    @FXML private Button backButton;

    private TypingGameService typingGameService;
    private List<Word> successWords;


    @FXML
    public void initialize() {
        typingGameService = new TypingGameServiceImpl();
    }



    @FXML
    public void checkAnswer() {

    }

    private List<String> successWordList;




    public void setSuccessWords(List<Word> words) {
        this.successWords = words;

        for (Word w : words) {
            System.out.println("넘어온 단어: " + w.getWord_id() + ", 뜻: " + w.getText());
        }

        initBlankGame();
    }

    private void initBlankGame() {
        // 보기용 단어 표시
        showWordList();
        // 문제 조회
        loadBlankQuestions();
    }

    // 보기에 들어갈 단어 10개 표시
    private void loadBlankQuestions() {

        wordListPane.getChildren().clear(); // 기존 라벨 제거

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(30); // 수평 간격
        flowPane.setVgap(30); // 수직 간격 (필요시)
        flowPane.setLayoutX(80); // 원하는 위치 조정
        flowPane.setLayoutY(50);
        flowPane.setPrefWrapLength(800); // 줄바꿈 기준 너비 (적절히 조정)
        flowPane.setStyle("-fx-background-color: transparent;"); // 배경 투명

        for (Word word : successWords) {
            String wordText = word.getText();

            // ✅ 텍스트 객체로 정확한 너비 측정
            Text tempText = new Text(wordText);
            Font font = Font.font("NeoDunggeunmo", 20); // 원하는 폰트와 크기 지정
            tempText.setFont(font);
            double textWidth = tempText.getLayoutBounds().getWidth();

            // ✅ 라벨 생성 및 설정
            Label label = new Label(wordText);
            label.setFont(font); // 폰트도 똑같이 적용
            label.setStyle("-fx-border-color: #ccc; -fx-padding: 5 10;");
            label.setAlignment(Pos.CENTER);
            label.setWrapText(false);
            label.setTextOverrun(OverrunStyle.CLIP);

            // ✅ 정확한 너비 적용 (+ padding 고려해서 여유 있게)
            double padding = 30; // 좌우 15px씩
            double finalWidth = textWidth + padding;
            label.setPrefWidth(finalWidth);
            label.setMinWidth(Region.USE_PREF_SIZE);
            label.setMaxWidth(Region.USE_PREF_SIZE);

            flowPane.getChildren().add(label);
        }

        wordListPane.getChildren().add(flowPane);
    }

    // 문제 5개 조회 (10개중 5개만 조회)
    private void showWordList() {

    }


    @FXML
    public void goToTypingGame() {
        try {
            // 타이핑게임 화면 FXML 로드
            Parent TypingGameRoot = FXMLLoader.load(getClass().getResource("/allday/minico/view/typinggame/typing_game.fxml"));

            // 현재 Stage 얻기
            Stage stage = (Stage) backButton.getScene().getWindow();

            // Scene 변경
            stage.getScene().setRoot(TypingGameRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
