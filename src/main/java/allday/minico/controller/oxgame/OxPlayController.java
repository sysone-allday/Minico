package allday.minico.controller.oxgame;

import allday.minico.dto.oxgame.OxGameResult;
import allday.minico.dto.oxgame.OxQuestion;
import allday.minico.dto.oxgame.OxUserSetting;
import allday.minico.service.oxgame.OxPlayService;
import allday.minico.utils.member.SceneManager;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OxPlayController {

    private static final OxPlayService oxPlayService = OxPlayService.getInstance();
    @FXML private ImageView correctEffect;
    @FXML private ImageView wrongBackground;
    @FXML private ImageView wrongEffect;

    // === hover 버튼들(back, skip) ===
    @FXML private StackPane handlerBtnBackHover;
    @FXML private ImageView imageBackHover;
    @FXML private ImageView imageBackNormal;
    @FXML private StackPane handlerBtnSkipHover;
    @FXML private ImageView imageSkipHover;
    @FXML private ImageView imageSkipNomal;

    // === 문제 횟수 카운트 ===
    @FXML private Text cntText;

    // === 문제 출제 및 해설 텍스트 ===
    @FXML private Text infoText;
    @FXML private Text questionText;
    @FXML private Text explanationText;

    // === O와 X버튼 ===
    @FXML private StackPane stackPaneO;
    @FXML private ImageView oPopupSign;
    @FXML private ImageView imgBtnO;
    @FXML private StackPane stackPaneX;
    @FXML private ImageView xPopupSign;
    @FXML private ImageView imgBtnX;

    // === 미니미 관련 ====
    @FXML private ImageView minimiEffectFeel;
    @FXML private ImageView minimi;

    // === 타이머(초) ===
    @FXML private Label timerLabel;




    @FXML
    private void initialize() {
        // back, skip 버튼 hover 처리
        handlerBtnBackHover.hoverProperty().addListener((obs, wasHover, isNowHover) -> {
            imageBackNormal.setVisible(!isNowHover);
            imageBackNormal.setCursor(Cursor.HAND); // 손 모양 커서
            imageBackHover.setVisible(isNowHover);
            imageBackHover.setCursor(Cursor.HAND); // 손 모양 커서
        });
        handlerBtnSkipHover.hoverProperty().addListener((obs, wasHover, isNowHover) -> {
            imageSkipNomal.setVisible(!isNowHover);
            imageSkipNomal.setCursor(Cursor.HAND); // 손 모양 커서
            imageSkipHover.setVisible(isNowHover);
            imageSkipHover.setCursor(Cursor.HAND); // 손 모양 커서
        });

        // o, x 버튼 처리
        imgBtnO.setCursor(Cursor.HAND);
        imgBtnX.setCursor(Cursor.HAND);

        Rectangle clip = new Rectangle(correctEffect.getFitWidth(), correctEffect.getFitHeight());
        clip.setArcWidth(50);
        clip.setArcHeight(50);
        correctEffect.setClip(clip);

        Rectangle clip2 = new Rectangle(wrongEffect.getFitWidth(), wrongEffect.getFitHeight());
        clip2.setArcWidth(50);
        clip2.setArcHeight(50);
        wrongEffect.setClip(clip2);

        handlerBtnSkipHover.setOnMouseClicked(e -> skipGame());
        handlerBtnSkipHover.setOnMouseClicked(this::handleSkipButtonClick);



    }
    @FXML
    private void handleSkipButtonClick(MouseEvent e) {
        // 1) 확인용 Alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("게임 중단");
        alert.setHeaderText(null);                          // 헤더 숨김
        alert.setContentText("게임을 중단하시겠습니까?");

        // 아이콘·버튼 순서 커스터마이즈 (선택)
        ButtonType yes = new ButtonType("네", ButtonBar.ButtonData.YES);
        ButtonType no  = new ButtonType("아니오", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yes, no);

        // 2) 모달(=현재 창을 잠그고)로 보여주기
        Optional<ButtonType> result = alert.showAndWait();

        // 3) 사용자 선택에 따른 분기
        if (result.isPresent() && result.get() == yes) {
            skipGame();       // → 실제 스킵 로직
        }
    }

    // 스킵 버튼(또는 긴급 종료)에 공통 사용 가능
    private void skipGame() {

        // 1) 진행 중인 타이머 정지
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
        if (timerLabel.textProperty().isBound()) {
            timerLabel.textProperty().unbind();
        }

        // 2) 실행 대기 중인 PauseTransition 정지
        //    → 보여주기/해설용 Transition이 있을 수 있음
        //    → 전역 리스트로 관리해 두었다면 loop 돌면서 stop()
        activeTransitions.forEach(PauseTransition::stop);
        activeTransitions.clear();

        // 3) 문제 인덱스를 끝으로 이동
        currentIndex = questionList.size();

        // 4) 즉시 결과 화면
        saveGameResult();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/oxgame/ox-result.fxml"));
            Parent root = loader.load();

            // 컨트롤러 주입
            OxResultController controller = loader.getController();
            controller.setResultData(oxGameResult);  // oxGameResult 전달

            // 화면 전환
            Scene scene = new Scene(root, 1280, 800);
            SceneManager.getPrimaryStage().setScene(scene);

        } catch (Exception e) {
            System.err.println("OX 결과 화면으로 전환 실패: " + e.getMessage());
        }


    }

    // 예시: Transition을 등록/해제해 두는 전역 리스트
    private final List<PauseTransition> activeTransitions = new ArrayList<>();


    // 유저 선택 팻말 처리
    private void showPopupSign(ImageView popupSign) {
        oPopupSign.setVisible(false);
        xPopupSign.setVisible(false);

        popupSign.setVisible(true);
    }

    // ====== 미니미 방향 처리 ======
    @FXML
    private void handleOButtonClick(MouseEvent e) {
        if (!canAnswer) return;
        selectedAnswer = "O";
        answered = true;
        showPopupSign(oPopupSign);
        minimi.setScaleX(1);
    }

    @FXML
    private void handleXButtonClick(MouseEvent e) {
        if (!canAnswer) return;
        selectedAnswer = "X";
        answered = true;
        showPopupSign(xPopupSign);
        minimi.setScaleX(-1);
    }



    @FXML
    private void handleBackToSetting(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/oxgame/ox-setting.fxml")); // 실제 경로로 수정
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            SceneManager.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            System.err.println("게임 세팅화면으로 전환 실패 " + e.getMessage());

        }
    }



    public void initData(OxUserSetting oxUserSetting) {
        this.setting = oxUserSetting;
        this.currentIndex = 0;

        // 🔥 여기에서 문제 리스트 미리 받아둠
        this.questionList = oxPlayService.getQuestionText(
                setting.getProblemType().getTypeId(),
                setting.getDifficulty(),
                setting.getCount()
        );

        startGameIntro();
    }

    private OxGameResult oxGameResult;
    private List<OxQuestion> questionList;
    private OxUserSetting setting;
    private int currentIndex = 0;       // 문제 회차
    private Timeline timer;             // 타이머
    private int correctCount = 0;       // 사용자 정답 맞춘 갯수
    private boolean answered = false;       // 이미 정답 눌렀는지
    private boolean canAnswer = false;      // 지금 정답 선택 가능한 상태인지
    private String selectedAnswer = null;   // 사용자가 선택한 답 ("O"/"X")

    private void setAnswerBtnEnabled(boolean enabled) {
        stackPaneO.setDisable(!enabled);
        stackPaneX.setDisable(!enabled);
    }

    private void clearAllTexts() {
        infoText.setVisible(false);
        questionText.setVisible(false);
        explanationText.setVisible(false);
    }


    private void startGameIntro() {
        infoText.setVisible(true);
        infoText.setText("OX 게임에 오신 것을 환영합니다!");

        PauseTransition pause1 = new PauseTransition(Duration.seconds(3));
        pause1.setOnFinished(e -> {
            infoText.setText(String.format("선택한 주제는 %s / 문제 수: %d / 난도: %s / 제한 시간: %d초 입니다. ",
                    setting.getProblemType().getTypeName(),
                    setting.getCount(),
                    setting.getDifficulty(),
                    setting.getTimer()));
            PauseTransition pause2 = new PauseTransition(Duration.seconds(3));
            pause2.setOnFinished(e2 -> {
                infoText.setText("문제 나갑니다!");

                PauseTransition pause3 = new PauseTransition(Duration.seconds(1));
                pause3.setOnFinished(e3 -> runGameLoop());
                pause3.play();
            });
            pause2.play();
        });
        pause1.play();
    }


    private void runGameLoop() {
        if (currentIndex >= questionList.size()) {
            handleGameEnd();
            return;
        }

        OxQuestion q = questionList.get(currentIndex);
        cntText.setText((currentIndex + 1) + " / " + questionList.size());

        // 초기화
        answered = false;
        canAnswer = true;
        selectedAnswer = null;

        oPopupSign.setVisible(false);
        xPopupSign.setVisible(false);

        clearAllTexts();
        questionText.setText("[ 문제 ]\n" + q.getQuestionText());   // ← 실제 getter 사용
        questionText.setVisible(true);
        setAnswerBtnEnabled(true);

    /* ✅ “타임아웃 이후 처리”는 startCountdown 안에서 전부 해결하므로
       ✅ Runnable 인자를 없애고 딱 한 줄로 호출 */
        startCountdown(setting.getTimer());
    }


    private void handleAnswer(String userInput) {
        OxQuestion q = questionList.get(currentIndex);
        String correct = q.getAnswer();
        boolean isCorrect = userInput != null && userInput.equalsIgnoreCase(correct);

        if (isCorrect) correctCount++;

        infoText.setVisible(true);
        if (userInput == null) {
            infoText.setText("시간이 종료되었습니다.");
            wrongBackground.setVisible(true);
            wrongEffect.setVisible(true);
        } else {
            if(isCorrect) {
                infoText.setText("정답입니다!");
                correctEffect.setVisible(true);
            }else {
                infoText.setText("오답입니다.");
                wrongBackground.setVisible(true);
                wrongEffect.setVisible(true);
            }
        }

        showAnswerAndExplanation(q);
    }



    private void showAnswerAndExplanation(OxQuestion question) {
        clearAllTexts();
        infoText.setVisible(true);

        // 1초 후 → 정답 공개
        PauseTransition pause1 = new PauseTransition(Duration.seconds(1));
        activeTransitions.add(pause1);
        pause1.setOnFinished(e -> {
            infoText.setText(String.format("정답은 [ %s ] 입니다.", question.getAnswer()));

            // 2초 후 → 해설 공개
            PauseTransition pause2 = new PauseTransition(Duration.seconds(2));
            pause2.setOnFinished(e2 -> {
                clearAllTexts();
                explanationText.setVisible(true);
                explanationText.setText("[ 해설 ]\n" + question.getExplanation());

                // 3초 후 → 다음 문제로 이동
                PauseTransition pause3 = new PauseTransition(Duration.seconds(3));
                pause3.setOnFinished(e3 -> {
                    currentIndex++;
                    clearAllTexts();
                    clearAllBackgroundAndEffect();
                    
                    // 마지막 문제 판별
                    if (currentIndex < questionList.size()) {
                        infoText.setVisible(true);
                        infoText.setText("다음 문제입니다.");

                        PauseTransition pause4 = new PauseTransition(Duration.seconds(1));
                        pause4.setOnFinished(e4 -> runGameLoop());
                        pause4.play();
                    } else {
                        runGameLoop();
                    }
                });
                pause3.play();
            });
            pause2.play();
        });
        pause1.play();
    }

    private void clearAllBackgroundAndEffect() {
        wrongBackground.setVisible(false);
        wrongEffect.setVisible(false);
        correctEffect.setVisible(false);
    }


    private void handleGameEnd() {
        infoText.setVisible(true);
        infoText.setText("게임 종료! 🎉");
        questionText.setText("");
        explanationText.setText("");
        cntText.setText("");
        timerLabel.setText("00:00");

        // 게임 결과 저장
        saveGameResult();
    }

    private void saveGameResult() {
        if (oxGameResult == null) {
            oxGameResult = new OxGameResult();
        }
        oxGameResult.setCorrectCount(correctCount);
        oxGameResult.setTotalCount(setting.getCount());
        oxGameResult.setDifficulty(setting.getDifficulty());
        oxGameResult.setTypeName(setting.getProblemType().getTypeName());
        System.out.println("정답률 : " + oxGameResult.getAccuracy());
    }


    private Timeline countdown;
    // 표시 타이머: 0~10초까지만 보여줌
    // 선택 차단: 10초에 canAnswer = false;
    // 채점: 11초에 handleAnswer(selectedAnswer);
// 1. 카운트다운 시작
    private void startCountdown(int seconds) {

        // 이전 타이머 정지
        if (countdown != null) countdown.stop();

        // ⚠️ 라벨이 이미 바인딩돼 있으면 해제
        if (timerLabel.textProperty().isBound()) timerLabel.textProperty().unbind();

        IntegerProperty remainMs = new SimpleIntegerProperty(seconds * 1_000);

        timerLabel.textProperty().bind(Bindings.createStringBinding(
                () -> formatTime(remainMs.get()),
                remainMs
        ));

        countdown = new Timeline(
                new KeyFrame(Duration.millis(50), e -> {
                    int next = remainMs.get() - 50;
                    remainMs.set(Math.max(next, 0));

                    if (next <= 0) {
                        // 입력 차단
                        setAnswerBtnEnabled(false);
                        canAnswer = false;

                        countdown.stop();
                        timerLabel.textProperty().unbind();          // 다음 라운드용 준비
                        timerLabel.setText(formatTime(0));

                        handleAnswer(selectedAnswer);                // 마지막 선택 채점
                    }
                })
        );
        countdown.setCycleCount(Animation.INDEFINITE);

        // 버튼 활성
        canAnswer = true;
        setAnswerBtnEnabled(true);
        countdown.playFromStart();
    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int ms = millis % 1000;
        return String.format("%02d:%03d", seconds, ms);
    }

}
