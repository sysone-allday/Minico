package allday.minico.controller.typinggame;

import allday.minico.dto.note.Note;
import allday.minico.dto.typinggame.BlankGame;
import allday.minico.dto.typinggame.Word;
import allday.minico.service.note.NoteService;
import allday.minico.service.note.NoteServiceImpl;
import allday.minico.service.typinggame.BlankGameService;
import allday.minico.service.typinggame.BlankGameServiceImpl;
import allday.minico.session.AppSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BlankGameController {

    @FXML private StackPane introPane;
    @FXML private Pane wordListPane;
    @FXML private Button backButton;
    @FXML private Label questionLabel;
    @FXML private Label successCount;
    @FXML private Label failCount;
    @FXML private TextField inputField;
    @FXML private StackPane resultPane;
    @FXML private Label resultSuccessLabel;
    @FXML private Label resultFailLabel;
    @FXML private Label timerLabel;
    @FXML private ImageView catImage;

    private BlankGameService blankGameService;
    private NoteService noteService;
    private List<Word> successWords;
    private List<BlankGame> problemList;
    private final List<Note> wrongList = new ArrayList<>();
    private int currentProblemIndex = 0;
    private int success = 0;
    private int fail = 0;

    private Image neutralCat;
    private Image smileCat;
    private Image sadCat;
    private String currentCatStatus = "neutral";  // "smile", "sad", "neutral"

    @FXML
    public void initialize() {

        blankGameService = new BlankGameServiceImpl();
        noteService = new NoteServiceImpl();

        // 이미지 한 번만 로딩
        neutralCat = new Image(getClass().getResource("/allday/minico/images/typinggame/cat-Photoroom1.png").toExternalForm());
        smileCat = new Image(getClass().getResource("/allday/minico/images/typinggame/happy-cat1.png").toExternalForm());
        sadCat = new Image(getClass().getResource("/allday/minico/images/typinggame/cry-cat1.png").toExternalForm());



        // 초기 이미지 설정
        catImage.setImage(neutralCat);
    }

    public void setSuccessWords(List<Word> words) {
        this.successWords = words;
        for (Word word : words) {
            System.out.println("넘어온 단어: " + word.getWord_id() + ", 뜻: " + word.getText());
        }
        initBlankGame();
    }

    private void initBlankGame() {
        showWordList();   // 보기 단어 표시
        getBlankProblems(); // 문제 조회 및 시작
        showIntro();
    }

    // 게임 시작전 설명 보여주기
    private void showIntro() {
        introPane.setVisible(true);
    }

    // 게임 시작 버튼
    @FXML
    private void startBlankGame() {
        introPane.setVisible(false);    // 설명창 숨기기

        questionLabel.setText("문제를 준비 중이야 ! \n 힌트에서 맞는 키워드를 입력해줘 ~ ");

        // 3초 후 문제 보여주기
        new Thread(() -> {
            try {
                Thread.sleep(3000); // 1초 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            javafx.application.Platform.runLater(this::showCurrentProblem);
        }).start();
    }

    // 보기 목록 보여주기
    private void showWordList() {
        wordListPane.getChildren().clear();
        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(20);
        flowPane.setVgap(30);
        flowPane.setLayoutX(80);
        flowPane.setLayoutY(70);
        flowPane.setPrefWrapLength(700);
        flowPane.setStyle("-fx-background-color: transparent;");

        for (Word word : successWords) {
            String wordText = word.getText();

            Text tempText = new Text(wordText);
            Font font = Font.font("NeoDunggeunmo", 20);
            tempText.setFont(font);
            double textWidth = tempText.getLayoutBounds().getWidth();

            Label label = new Label(wordText);
            label.setFont(font);
//            label.setStyle("-fx-border-color: #ccc; -fx-padding: 5 10;");
            label.setAlignment(Pos.CENTER);
            label.setWrapText(false);
            label.setTextOverrun(OverrunStyle.CLIP);

            double padding = 30;
            double finalWidth = textWidth + padding;
            label.setPrefWidth(finalWidth);
            label.setMinWidth(Region.USE_PREF_SIZE);
            label.setMaxWidth(Region.USE_PREF_SIZE);

            flowPane.getChildren().add(label);
        }
        wordListPane.getChildren().add(flowPane);
    }

    // 문제 5개 가져오기
    private void getBlankProblems() {
        List<BlankGame> blankGameList = new ArrayList<>();
        for (Word word : successWords) {
            BlankGame blankGame = new BlankGame();
            blankGame.setWordId(word.getWord_id());
            blankGameList.add(blankGame);
        }

        this.problemList = blankGameService.getBlankProblems(blankGameList, successWords);

        currentProblemIndex = 0;
        success = 0;
        fail = 0;
        successCount.setText("0개");
        failCount.setText("0개");

        showCurrentProblem();
    }

    // 현재 문제 표시
    private void showCurrentProblem() {
        // 고양이 neutral로 전환
        catImage.setImage(neutralCat);
        currentCatStatus = "neutral";

        // 입력창 잠시 비활성화
//        inputField.setDisable(true);

        // 2초 후 문제 표시
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            javafx.application.Platform.runLater(() -> {
                inputField.setDisable(false); // 입력창 다시 활성화

                if (currentProblemIndex < problemList.size()) {
                    BlankGame currentProblem = problemList.get(currentProblemIndex);
                    timerLabel.setText((currentProblemIndex + 1) + "/" + problemList.size() + " 문제");
                    questionLabel.setText(currentProblem.getQuestionText());
                } else {
                    questionLabel.setText("문제를 모두 풀었습니다!");
                    timerLabel.setText(problemList.size() + "/" + problemList.size() + " 문제");
                    inputField.setDisable(true);
                    showResult();
                }
            });
        }).start();
    }

    // 입력한 단어 정답 확인
    @FXML
    public void checkAnswer() {
        String input = inputField.getText().trim();
        if (input.isEmpty()) return;

        if (currentProblemIndex >= problemList.size()) return;

        BlankGame currentProblem = problemList.get(currentProblemIndex);
        String correctAnswer = getAnswerFromProblem(currentProblem);

        if (input.equalsIgnoreCase(correctAnswer)) {
            success++;
            successCount.setText(success + "개");
            updateCatFace("smile");
            questionLabel.setText("와 ~ 정답이야 o(〃＾▽＾〃)o");
        } else {
            fail++;
            failCount.setText(fail + "개");
            updateCatFace("sad");
            questionLabel.setText("오답이야 (⊙_⊙;) 단어장에서 복습해봐");

            // 틀린문제 저장 -> 단어장에서 보여주기 위해
            for (Word word : successWords) {
                if (word.getWord_id() == currentProblem.getWordId()) {
                    Note wrongNote = new Note();
                    wrongNote.setQuestionText(currentProblem.getQuestionText());        // 문제 내용 저장
                    wrongNote.setAnswerText(word.getText());                            // 정답 단어 저장
                    wrongNote.setMemberId(AppSession.getLoginMember().getMemberId());   // 로그인 유저 저장
                    wrongNote.setMemo(""); // 초기 메모는 빈값으로

                    wrongList.add(wrongNote);
                    break;
                }
            }
            // 단어장에 저장
            noteService.saveWrongNote(wrongList);
        }

        currentProblemIndex++;
        inputField.clear();
        showCurrentProblem();
    }

    // 정답 확인 로직
    private String getAnswerFromProblem(BlankGame problem) {
        // 현재는 정답을 추정할 수 있는 구조가 없으므로 word_id 기준으로 Word에서 찾아야 함
        for (Word word : successWords) {
            if (word.getWord_id() == problem.getWordId()) {
                return word.getText(); // 정답
            }
        }
        return ""; // 예외 방지용
    }

    // cat 이미지 변경
    private void updateCatFace(String newStatus) {
        if (!newStatus.equals(currentCatStatus)) {

            System.out.println("🐱 고양이 상태 바꿈: " + currentCatStatus + " → " + newStatus);
            switch (newStatus) {
                case "smile" -> catImage.setImage(smileCat);
                case "sad" -> catImage.setImage(sadCat);
                case "neutral" -> catImage.setImage(neutralCat);
            }
            currentCatStatus = newStatus;
        }
    }



    // 게임 종료 후 결과창 띄우기
    private void showResult() {
        resultSuccessLabel.setText(successCount.getText()); // 기존 성공 label 값을 그대로 복사
        resultFailLabel.setText(failCount.getText());
        resultPane.setVisible(true); // 결과창 보여줌
    }


    // 타이핑 게임으로 이동
    @FXML
    public void goToTypingGame() {
        try {
            Parent TypingGameRoot = FXMLLoader.load(getClass().getResource("/allday/minico/view/typinggame/typing_game.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.getScene().setRoot(TypingGameRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 메인 화면으로 이동 (임시로 메인으로 가게 해놨음 !!!)
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
