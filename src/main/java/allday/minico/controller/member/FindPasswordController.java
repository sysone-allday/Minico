/*
FindPasswordController 클래스는 사용자가 ID를 입력하면
해당 ID의 비밀번호 힌트를 조회하여 결과를 출력하고,
존재하지 않는 ID일 경우 에러 메시지를 안내합니다.
로그인 화면으로 돌아가는 기능도 포함되어 있습니다.
 */

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
    // @FXML private Label hintLabel;
    @FXML private TextField idField;
    @FXML private Label resultHintText;
    @FXML private Button backToLoginButton;

    MemberService memberservice =  new MemberService();

    @FXML
    void findPwHint(ActionEvent event) {
        String hint = memberservice.findPwHint(idField.getText());
        if(!(hint == null)){
            resultHintText.setText(hint);
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
