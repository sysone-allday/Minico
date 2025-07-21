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
