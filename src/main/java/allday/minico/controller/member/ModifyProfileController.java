package allday.minico.controller.member;

import allday.minico.service.member.MemberService;
import allday.minico.session.AppSession;
import allday.minico.utils.member.SceneManager;
import allday.minico.utils.member.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Map;

public class ModifyProfileController {

    @FXML private Button checkIdButton;
    @FXML private TextField emailField;
    @FXML private Label modifyResultText;
    @FXML private Button mofdifyConfirmButton;
    @FXML private TextField nicknameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField passwordCheckField;
    @FXML private TextField passwordHintField;

    MemberService memberService = new MemberService();

    private Stage myProfileStage; // 회원정보 수정 완료 시, 내 정보 창도 닫게 하기 위해 컨트롤러 가져옴
    public void setMyProfileStage(Stage stage) {
        this.myProfileStage = stage;
    }

    @FXML
    void modifyConfirmButtonClick(ActionEvent event) {

        if(!Validator.isInfoFill(// 변경 시
                nicknameField.getText(), emailField.getText(),
                passwordField.getText(), passwordHintField.getText())){
            modifyResultText.setText("※ 회원정보를 모두 입력해주세요");
            modifyResultText.setStyle("-fx-text-fill: red;");
            return;
        }

        if(!Validator.isNicknameFormatMatch(nicknameField.getText())) {
            modifyResultText.setText("닉네임 형식이 올바르지 않습니다.");
            modifyResultText.setStyle("-fx-text-fill: red;"); return;}// 닉네임이 대,소문자,숫자,한글 4~10자 이내인지
        if(!Validator.isValidEmail(emailField.getText())) {
            modifyResultText.setText("이메일 형식이 올바르지 않습니다.");
            modifyResultText.setStyle("-fx-text-fill: red;"); return;} // 이메일 양식이 적합한지
        if(!Validator.isPwFormatMatch(passwordField.getText())) {
            modifyResultText.setText("비밀번호 형식이 올바르지 않습니다.");
            modifyResultText.setStyle("-fx-text-fill: red;"); return;}// 비밀번호 양식이 적합한지
        if(!Validator.isPasswordMatch(passwordField.getText(),passwordCheckField.getText())) {
            modifyResultText.setText("비밀번호가 일치하지 않습니다.");
            modifyResultText.setStyle("-fx-text-fill: red;"); return;}// 비밀번호, 비밀번호 확인이 일치한지

        // 검증이 끝나면 아래 실행

        String modifyInfoMemberId = AppSession.getLoginMember().getMemberId(); // 지금 로그인한 멤버 ID
        boolean modifyResult = memberService.modifyMemberInfo(modifyInfoMemberId, nicknameField.getText(),emailField.getText(),
                passwordField.getText(), passwordHintField.getText()  ); // DB 업데이트

        if(modifyResult) { // 회원정보 수정 완료 시,

            // 지금 로그인 중인 멤버 정보 변경
            AppSession.getLoginMember().setNickname(nicknameField.getText());
            AppSession.getLoginMember().setEmail(emailField.getText());
            AppSession.getLoginMember().setPassword(passwordField.getText());
            AppSession.getLoginMember().setPasswordHint(passwordHintField.getText());
            System.out.println("지금 세션 반영 완료");


            Stage thisStage = (Stage) mofdifyConfirmButton.getScene().getWindow();
            thisStage.close(); // 회원정보 수정 스테이지 닫기
            myProfileStage.close();  // 회원정보 스테이지 닫기
        }
    }
}
