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
