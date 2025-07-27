/*
MultipleLoginErrorController 클래스는 중복 로그인 감지 시 표시되는 모달창에서
확인 버튼을 누르면 현재 창을 닫는 기능을 제공합니다.
 */
package allday.minico.controller.member;

import allday.minico.utils.member.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class MultipleLoginErrorController {

    @FXML
    private Button confirmButton;

    @FXML
    void confirmButtonClick(ActionEvent event) {
        Stage stage = (Stage) confirmButton.getScene().getWindow();
        stage.close();
    }
}
