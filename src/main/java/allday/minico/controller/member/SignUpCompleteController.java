/*
SignUpCompleteController 클래스는 회원가입 완료 후 표시되는 창에서
확인 버튼 클릭 시 현재 창을 닫고 로그인 화면으로 전환하는 기능을 제공합니다.
 */
package allday.minico.controller.member;

import allday.minico.utils.member.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SignUpCompleteController {

    @FXML
    private Button signUpCompleteButton;

    @FXML
    void signUpComplete(ActionEvent event) {
        Stage stage = (Stage) signUpCompleteButton.getScene().getWindow();
        stage.close();
        SceneManager.switchTo("Login");
    }

}
