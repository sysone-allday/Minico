package allday.minico.controller.member;


import allday.minico.Main;
import allday.minico.utils.member.SceneManager;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
// import javafx.scene.image.Image;
import javafx.util.Duration;

import java.io.IOException;


public class LoginController {

    @FXML private Button ExitButton;
    @FXML private ImageView characterImage;
    @FXML private Button findPasswordButton;
    @FXML private TextField idField;
    @FXML private Button loginButton;
    @FXML private PasswordField pwField;
    @FXML private Button signUpButton;

    @FXML
    public void initialize() { // 이 컨트롤러와 연결된 fxml 이 로딩될 때 자동으로 실행되는 메서드
    }

    @FXML
    void signUp(ActionEvent event) { // 회원가입 창으로 이동
        SceneManager.switchTo("SignUp");
    } // 회원가입 창으로 이동

    @FXML
    void login(ActionEvent event) { // 로그인 버튼 클릭 시 미니룸으로 이동
        // 현재 화면에 페이드아웃 효과 적용
        Parent currentRoot = loginButton.getScene().getRoot();
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            try {
                // 미니룸 화면 로드
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/miniroom.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1280, 800);
                
                // 새 화면을 투명하게 시작
                root.setOpacity(0.0);
                SceneManager.getPrimaryStage().setScene(scene);
                
                // 새 화면에 페이드인 효과 적용
                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);
                fadeIn.play();
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        
        fadeOut.play();
    }



    @FXML
    void Exit(ActionEvent event) {
        Platform.exit();
    } // 프로그램 종료



    @FXML
    void findPassword(ActionEvent event) { // 비밀번호 찾기 버튼 클릭 시
        SceneManager.showModal("findPassword", "비밀번호 찾기"); // 모달창 띄우기
    }

}
