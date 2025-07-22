package allday.minico.controller.typinggame;

import allday.minico.dto.typinggame.Word;
import allday.minico.service.typinggame.TypingGameService;
import allday.minico.service.typinggame.TypingGameServiceImpl;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class TypingGameController {

    private TypingGameService typingGameService;

    @FXML private TextField inputField;
    @FXML private Pane gamePane;
    @FXML private Label timerLabel;
    @FXML private Label successCount;
    @FXML private Label failCount;
    @FXML private Button startButton;
    @FXML private Button backButton;
    @FXML private StackPane resultPane;
    @FXML private Label resultSuccessLabel;
    @FXML private Label resultFailLabel;


    private List<Word> wordBuffer = new ArrayList<>();
    private List<Word> successWordList = new ArrayList<>();
    private final int bufferThreshold = 10;
    private int timeRemaining = 60;
    private int success = 0;
    private int fail = 0;

    private Timeline gameTimer;
    private Timeline wordFallTimer;
    private Timeline wordDropTimer;

    private final List<Label> activeLabels = new CopyOnWriteArrayList<>();

    @FXML
    public void initialize() {
        typingGameService = new TypingGameServiceImpl();
    }

    @FXML
    private void startGame() {
        startButton.setVisible(false);
        timeRemaining = 60;
        success = 0;
        fail = 0;
        successCount.setText("0개");
        failCount.setText("0개");
        inputField.clear();
        gamePane.getChildren().clear();
        activeLabels.clear();
        wordBuffer.clear();
        wordBuffer.addAll(typingGameService.getRandomWord());

        startTimer();
        startWordDropper();
        startWordFaller();
    }

    private void startTimer() {
        timerLabel.setText("60:00");
        gameTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            timeRemaining--;
            timerLabel.setText(String.format("%02d:00", timeRemaining));
            if (timeRemaining <= 0) {
                stopAllTimers();
                System.out.println("게임 종료");
            }
        }));
        gameTimer.setCycleCount(Timeline.INDEFINITE);
        gameTimer.play();
    }


    private void startWordDropper() {
        wordDropTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> dropWord()));
        wordDropTimer.setCycleCount(Timeline.INDEFINITE);
        wordDropTimer.play();
    }



    private void startWordFaller() {
        wordFallTimer = new Timeline(new KeyFrame(Duration.millis(50), e -> {
            for (Label label : activeLabels) {
                double newY = label.getLayoutY() + 3;
                label.setLayoutY(newY);

                if (newY > gamePane.getHeight() - 30) {
                    gamePane.getChildren().remove(label);
                    activeLabels.remove(label);
                    fail++;
                    failCount.setText(fail + "개");
                }
            }
        }));
        wordFallTimer.setCycleCount(Timeline.INDEFINITE);
        wordFallTimer.play();
    }


    // 게임 끝나면 다 멈추기
    private void stopAllTimers() {
        if (gameTimer != null) gameTimer.stop();
        if (wordDropTimer != null) wordDropTimer.stop();
        if (wordFallTimer != null) wordFallTimer.stop();
        showResult();
    }

    
    // 랜덤으로 단어 떨어지기 기능
    private void dropWord() {
        if (wordBuffer.size() <= bufferThreshold) {
            wordBuffer.addAll(typingGameService.getRandomWord());
        }

        if (wordBuffer.isEmpty()) return;
        Word word = wordBuffer.remove(0);


        // 라벨 생성
        // 폰트 로드
        Font customFont = Font.loadFont(getClass().getResourceAsStream("/allday/minico/fonts/NEODGM.ttf"), 24);
        Label label = new Label(word.getText());
        label.setFont(customFont);
        label.setStyle("-fx-text-fill: black;");


        // Word 객체를 Label에 연결
        // 성공한 단어 list에서 사용하기 위함
        label.setUserData(word);
        
        // 단어 랜덤으로 위치 생성하는 로직 (겹치지 않게 구현)
        double x;
        boolean valid;
        int attempts = 0;

        do {
            x = Math.random() * (gamePane.getWidth() - 100);
            valid = true;

            for (Label existing : activeLabels) {
                double ex = existing.getLayoutX();
                double ew = existing.getWidth();

                // 일정 거리 이상 떨어져 있어야 통과
                if (Math.abs(x - ex) < ew + 20) {
                    valid = false;
                    break;
                }
            }
            attempts++;
        } while (!valid && attempts < 30); // 최대 30번 시도

        label.setLayoutX(x);
        label.setLayoutY(0);

        gamePane.getChildren().add(label);
        activeLabels.add(label);
    }

    
    // 입력한 답 확인 메서드
    @FXML
    public void checkAnswer() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;

        for (Label label : activeLabels) {
            if (label.getText().equalsIgnoreCase(input)) {
                success++;
                successCount.setText(success + "개");
                gamePane.getChildren().remove(label);
                activeLabels.remove(label);
                inputField.clear();

                // 정확하게 입력한 단어 list에 담아두기
                // 연결했던 Word 객체 꺼내서 저장 => label.getUserData() : label에 정보 붙여두는 방법
                Word correctWord = (Word) label.getUserData();
                successWordList.add(correctWord);

                return;
            }
        }
        System.out.println("틀렸습니다.");
        inputField.clear();
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

    // 게임 종료 후 결과창 띄우기
    private void showResult() {
        resultSuccessLabel.setText(successCount.getText()); // 기존 성공 label 값을 그대로 복사
        resultFailLabel.setText(failCount.getText());
        resultPane.setVisible(true); // 결과창 보여줌
    }
    
    // 빈칸 게임으로
    @FXML
    private void goToBlankGame() {
        try {

            // FXMLoader 객체 생성
            // FXMLLoader.load() 컨트롤러 접근 불가능
            // controller 통해서 데이터를 blank 쪽으로 보내줘야함
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/typinggame/blank_game.fxml"));


            // Parent root 로드
            Parent blankGameRoot = loader.load();

            // 컨트롤러 인스턴스 얻기
            BlankGameController controller = loader.getController();

            // 랜덤 10개 WordDTO 리스트 전달
            List<Word> randomWordList = new ArrayList<>(successWordList); // 네가 모아둔 성공 단어 리스트
            Collections.shuffle(randomWordList);
            List<Word> random10 = randomWordList.subList(0, Math.min(10, randomWordList.size()));
            controller.setSuccessWords(random10);


            // 화면 전환
            Stage stage = (Stage) resultPane.getScene().getWindow();
            stage.getScene().setRoot(blankGameRoot);


        } catch (IOException e) {
            System.err.println("🚫 [화면 전환 실패] Miniroom.fxml 로드 중 오류 발생");
            System.err.println("경로 확인: /allday/minico/view/typinggame/blank_game.fxml");
            e.printStackTrace();
        }
    }


}
