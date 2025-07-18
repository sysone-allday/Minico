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
