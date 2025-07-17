package allday.minico.controller.member;

import allday.minico.service.member.MemberService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class FindPasswordController {

    @FXML private Button findPwHintButton;
    @FXML private Label hintLabel;
    @FXML private TextField idField;
    @FXML private Label resultHintText;
    @FXML private Button backToLoginButton;

    MemberService memberservice =  new MemberService();

    @FXML
    void findPwHint(ActionEvent event) {
        String hint = memberservice.findPwHint(idField.getText());
        if(!(hint == null)){
            resultHintText.setText("힌트 :\n" + hint);
            resultHintText.setStyle("-fx-text-fill: green;");}
        else { // null 을 반환하면 존재하지 않는 ID (Hint 는 NOT NULL 이기 때문에 null 가져오면 오류임)
            resultHintText.setText("존재하지 않는 ID 입니다");
            resultHintText.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    void backToLogin(ActionEvent event) {
        Stage stage = (Stage) backToLoginButton.getScene().getWindow();
        stage.close();
    }

}
