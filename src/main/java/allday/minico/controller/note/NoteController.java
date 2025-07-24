package allday.minico.controller.note;

import allday.minico.utils.member.SceneManager;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Optional;

public class NoteController {

    @FXML private ImageView btnBack;
    @FXML private ImageView btnDelete;
    
    @FXML private Label wrongQuestion;
    @FXML private TextField answerInputField;
    @FXML private Text feedbackLabel;
    @FXML private ImageView btnSubmitAnswer;

    @FXML private TextArea memoTextArea;
    @FXML private Label btnEditMemo;
    @FXML private Label btndDeleteMemo;

    @FXML
    private void initialize() {
        // 미니룸 이동
        btnBack.setOnMouseClicked(this::handleBackToMiniroom);
        // 다시풀기 칸 클릭 시 내용 비워짐
        answerInputField.setOnMouseClicked(event -> {
            if (answerInputField.getText().equals("정답을 입력하세요...")) {
                answerInputField.clear();
            }
        });
        // 메모 칸 클릭 시 내용 비워짐
        memoTextArea.setOnMouseClicked(event -> {
            if (answerInputField.getText().equals("메모를 입력하세요...")) {
                answerInputField.clear();
            }
        });
        // 문제 삭제 버튼 클릭 시
        btnDelete.setOnMouseClicked(this::onDeleteQuestionClicked);
        // 정답 확인 버튼 클릭 시
        btnSubmitAnswer.setOnMouseClicked(this::onSubmitAnswerClicked);
        // 메모 수정 버튼 클릭 시
        btnEditMemo.setOnMouseClicked(this::onEditMemoClicked);
        // 메모 삭제 버튼 클릭 시
        btndDeleteMemo.setOnMouseClicked(this::onDeleteMemoClicked);
    }

    private void onSubmitAnswerClicked(MouseEvent event) {
        String userAnswer = answerInputField.getText().trim();

        // 이전 스타일 제거
        feedbackLabel.getStyleClass().removeAll("feedback-correct", "feedback-wrong");
        // @@@@@@@@@@@@@@@@@@@@@ 데이터 연결 전 임시 @@@@@@@@@@@@@@@
        String correctAnswer = "O";

        if (userAnswer.equalsIgnoreCase(correctAnswer)) {
            feedbackLabel.setText("정답입니다!");
            feedbackLabel.getStyleClass().add("feedback-correct");
        } else {
            feedbackLabel.setText("오답입니다.");
            feedbackLabel.getStyleClass().add("feedback-wrong");
        }

        // 🔔 2초 후 메시지 초기화
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            feedbackLabel.setText("");
            feedbackLabel.getStyleClass().removeAll("feedback-correct", "feedback-wrong");
        });
        pause.play();
    }

    private void onDeleteQuestionClicked(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("확인");
        alert.setHeaderText(null);
        alert.setContentText("문제를 삭제하시겠습니까?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // 삭제 로직 실행
            System.out.println("문제가 삭제되었습니다.");
            // 예: DB에서 삭제, 목록에서 제거 등
        } else {
            System.out.println("삭제가 취소되었습니다.");
        }
    }

    private void onDeleteMemoClicked(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("확인");
        alert.setHeaderText(null);
        alert.setContentText("저장된 메모를 삭제하시겠습니까?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            memoTextArea.clear(); // 내용 비우기
            memoTextArea.setPromptText("메모를 입력하세요"); // 다시 안내 문구 표시
            System.out.println("메모가 삭제되었습니다.");
        } else {
            System.out.println("삭제 취소됨");
        }
    }

    private void onEditMemoClicked(MouseEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("확인");
        alert.setHeaderText(null);
        alert.setContentText("저장하시겠습니까?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String memo = memoTextArea.getText();
            // 저장 로직 실행 (예: DB나 변수에 저장)
            System.out.println("저장됨: " + memo);
        } else {
            // 저장하지 않음
            System.out.println("저장 취소됨");
        }
    }

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


//    public void loadWrongQuestion() {
//        String question = noteService.getLatestWrongQuestion(currentUserId);
//
//        if (question != null && !question.isEmpty()) {
//            wrongQuestion.setText(question);
//        } else {
//            wrongQuestion.setText("틀린 문제가 없습니다.");
//        }
//    }
}
