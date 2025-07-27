/*
LoginFailController 클래스는 로그인 실패 시 표시되는 모달창에서
사용자가 확인 버튼을 누르면 현재 창을 닫고 로그인 화면으로 돌아가도록 처리합니다.
 */

package allday.minico.controller.member;

import allday.minico.utils.member.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginFailController {

    @FXML
    private Button backToLoginButton;

    @FXML
    void backToLogin(ActionEvent event) {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        stage.close();
    }

}
