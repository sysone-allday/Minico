package allday.minico.controller.member;


import allday.minico.Main;
import allday.minico.utils.member.SceneManager;
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
import javafx.scene.image.Image;

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
        Image image = new Image(getClass().getResource("/allday/minico/images/member/Logincharacter.png").toExternalForm());
        characterImage.setImage(image); // 로그인 화면 이미지 삽입
    }

    @FXML
    void signUp(ActionEvent event) { // 회원가입 창으로 이동
        SceneManager.switchTo("SignUp");
    } // 회원가입 창으로 이동

    @FXML
    void login(ActionEvent event) { // 로그인 버튼 클릭 시 마이룸으로 이동 (지금 하드코딩해서 나중에 SceneManager 메서드로 바뀌게 변경하는 것이 좋아보임)
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/main.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 800);
            SceneManager.getPrimaryStage().setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
