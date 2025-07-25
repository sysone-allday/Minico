package allday.minico.controller.note;

import allday.minico.dto.note.Note;
import allday.minico.service.note.NoteService;
import allday.minico.service.note.NoteServiceImpl;
import allday.minico.session.AppSession;
import allday.minico.utils.member.SceneManager;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class NoteController {

    /* ------------------------------
     * FXML 바인딩
     * ------------------------------ */
    @FXML private HBox boxPagination;
    @FXML private Text currentPageText;
    @FXML private Text totalPageText;
    @FXML private Button btnPrev;
    @FXML private HBox pageDots;
    @FXML private Button btnNext;

    @FXML private Button btnBack;
    @FXML private Button btnDelete;
    @FXML private Label wrongQuestion;
    @FXML private TextField answerInputField;
    @FXML private Text feedbackLabel;
    @FXML private Button btnSubmitAnswer;
    @FXML private TextArea memoTextArea;
    @FXML private Button btnSaveMemo;

    /* ------------------------------
     * 상수
     * ------------------------------ */
    private static final String PLACEHOLDER_ANSWER = "정답을 입력하세요...";
    private static final String PLACEHOLDER_MEMO   = "메모를 입력하세요...";
    private static final int    PAGE_SIZE          = 1;   // 문제 1개씩
    private static final int    CHUNK_SIZE         = 10;  // 10페이지(문제) 단위로 버퍼링

    /* ------------------------------
     * 필드
     * ------------------------------ */
    private final NoteService noteService = NoteServiceImpl.getInstance();
    private final String memberId         = AppSession.getLoginMember().getMemberId();

    private List<Note> noteList           = new ArrayList<>(); // 현재 chunk 내 문제 리스트
    private int chunkStartPage            = 1;                 // 현재 버퍼의 시작 페이지 번호 (1, 11, 21 …)
    private int currentPage               = 1;                 // 실제 화면에 표시 중인 페이지 번호
    private int totalPage                 = 1;                 // 전체 페이지 수

    /* ------------------------------
     * 초기화
     * ------------------------------ */
    @FXML
    private void initialize() {
        boxPagination.setVisible(false);
        boxPagination.setManaged(false);
        initListeners();
        loadChunkAndPage(currentPage);    // 첫 페이지 로딩
    }

    private void initListeners() {
        Font.loadFont(getClass().getResourceAsStream("/allday/minico/fonts/NEODGM.ttf"), 14);

        btnBack.setOnMouseClicked(this::handleBackToMiniroom);
        btnDelete.setOnMouseClicked(this::onDeleteQuestionClicked);
        btnSubmitAnswer.setOnMouseClicked(this::onSubmitAnswerClicked);
        btnSaveMemo.setOnMouseClicked(this::onSaveMemoClicked);

        Platform.runLater(() -> wrongQuestion.requestFocus());
        answerInputField.setOnMouseClicked(e -> clearPlaceholder(answerInputField, PLACEHOLDER_ANSWER));
        memoTextArea.setOnMouseClicked(e   -> clearPlaceholder(memoTextArea,   PLACEHOLDER_MEMO));

        btnPrev.setOnMouseClicked(e -> changePage(currentPage - 1));
        btnNext.setOnMouseClicked(e -> changePage(currentPage + 1));
    }

    /* ------------------------------
     * 페이지/버퍼 로딩 핵심 로직
     * ------------------------------ */

    /**
     * 페이지 버튼 클릭 시 호출되는 메서드.
     * - 1) 유효 범위를 벗어나면 무시
     * - 2) 동일 chunk 안이면 UI만 갱신
     * - 3) 벗어나면 새 chunk를 읽어온 뒤 UI 갱신
     */
    private void changePage(int targetPage) {
        if (targetPage < 1 || targetPage > totalPage) return;

        if (isInCurrentChunk(targetPage)) {
            currentPage = targetPage;
            updateUI();
        } else {
            loadChunkAndPage(targetPage);
        }
    }

    /**
     * targetPage 가 현재 버퍼(chunk) 안에 있는지 확인
     */
    private boolean isInCurrentChunk(int page) {
        return page >= chunkStartPage && page < chunkStartPage + CHUNK_SIZE;
    }

    /**
     * targetPage가 속한 chunk(10개 묶음)를 DB에서 읽어온 뒤 화면 갱신
     */
    private void loadChunkAndPage(int targetPage) {
        int chunkIndex  = (targetPage - 1) / CHUNK_SIZE; // 0,1,2…
        int offset      = chunkIndex * CHUNK_SIZE;       // 0,10,20…
        int startPage   = chunkIndex * CHUNK_SIZE + 1;   // 1,11,21…

        Task<List<Note>> loadTask = new Task<>() {
            @Override
            protected List<Note> call() {
                int totalCount = noteService.getTotalWrongQuestionCount(memberId);
                totalPage      = Math.max(1,
                        (int) Math.ceil(totalCount / (double) PAGE_SIZE));
                return noteService.getWrongQuestionsPaged(memberId,
                        offset,      // ← offset
                        CHUNK_SIZE); // ← limit
            }
        };

        loadTask.setOnSucceeded(e -> {
            noteList       = loadTask.getValue();
            chunkStartPage = startPage;              // 여전히 UI에서 필요
            currentPage    = Math.min(targetPage, totalPage);

            if (noteList.isEmpty()) showEmptyView();
            else                    updateUI();
        });

        new Thread(loadTask).start();
    }


    /* ------------------------------
     * UI 업데이트
     * ------------------------------ */
    private void updateUI() {
        if (noteList.isEmpty()) {
            showEmptyView();
            return;
        }

        int offset = currentPage - chunkStartPage;
        if (offset < 0 || offset >= noteList.size()) {
            changePage(chunkStartPage);
            return;
        }

        Note note = noteList.get(offset);
        wrongQuestion.setText(note.getQuestionText());
        memoTextArea.setText(note.getMemo());

        currentPageText.setText(String.valueOf(currentPage));
        totalPageText.setText(String.valueOf(totalPage));

        boxPagination.setVisible(true);
        boxPagination.setManaged(true);
        btnPrev.setVisible(currentPage > 1);
        btnNext.setVisible(currentPage < totalPage);

        updatePageDots();
    }

    private void updatePageDots() {
        pageDots.getChildren().clear();
        int startDot = ((currentPage - 1) / CHUNK_SIZE) * CHUNK_SIZE + 1;
        int endDot   = Math.min(startDot + CHUNK_SIZE - 1, totalPage);

        for (int i = startDot; i <= endDot; i++) {
            Label dot = new Label("●");
            dot.getStyleClass().add(i == currentPage ? "page-dot-current" : "page-dot-inactive");
            dot.setPrefSize(20, 20);
            dot.setAlignment(Pos.CENTER);
            int target = i;
            dot.setOnMouseClicked(e -> changePage(target));
            pageDots.getChildren().add(dot);
        }
    }

    /* ------------------------------
     * Empty View 처리
     * ------------------------------ */
    private void showEmptyView() {
        wrongQuestion.setText("틀린 문제가 없습니다.");
        memoTextArea.clear();
        boxPagination.setVisible(false);
        boxPagination.setManaged(false);
        btnPrev.setVisible(false);
        btnNext.setVisible(false);
        currentPageText.setText("0");
        totalPageText.setText("0");
    }

    /* ------------------------------
     * 유틸
     * ------------------------------ */
    private static void clearPlaceholder(TextInputControl field, String placeholder) {
        if (placeholder.equals(field.getText())) field.clear();
    }

    private boolean confirm(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK, ButtonType.CANCEL);
        return alert.showAndWait().filter(bt -> bt == ButtonType.OK).isPresent();
    }

    /* ------------------------------
     * 이벤트 핸들러
     * ------------------------------ */
    private void onSubmitAnswerClicked(MouseEvent e) {
        int offset = currentPage - chunkStartPage;
        if (offset < 0 || offset >= noteList.size()) return;

        String userAnswer    = answerInputField.getText().trim();
        String correctAnswer = noteList.get(offset).getAnswerText();

        boolean isCorrect = userAnswer.equalsIgnoreCase(correctAnswer);
        feedbackLabel.setText(isCorrect ? "정답입니다!" : "오답입니다!");
        feedbackLabel.getStyleClass().setAll(isCorrect ? "feedback-correct" : "feedback-wrong");

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(evt -> feedbackLabel.setText(""));
        pause.play();
    }


    private void onSaveMemoClicked(MouseEvent e) {
        if (!confirm("메모를 저장하시겠습니까?")) return;
        int offset = currentPage - chunkStartPage;
        if (offset < 0 || offset >= noteList.size()) return;
        noteService.saveMemo(noteList.get(offset).getWrongId(), memoTextArea.getText());
    }

    private void onDeleteQuestionClicked(MouseEvent e) {
        if (!confirm("문제를 삭제하시겠습니까?")) return;
        int offset = currentPage - chunkStartPage;
        if (offset < 0 || offset >= noteList.size()) return;
        noteService.deleteWrongQuestion(noteList.get(offset).getWrongId());
        // 삭제 후 페이지 수가 변할 수 있으므로 현재 페이지를 다시 반영
        loadChunkAndPage(Math.min(currentPage, totalPage));
    }

    private void handleBackToMiniroom(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/miniroom.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, 1280, 800);
            SceneManager.getPrimaryStage().setScene(scene);
        } catch (Exception e) {
            System.err.println("미니룸 이동 실패: " + e.getMessage());
        }
    }
}
