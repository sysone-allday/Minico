package allday.minico.controller.member;


import allday.minico.dto.member.Member;
import allday.minico.service.member.MemberService;
import allday.minico.utils.member.SceneManager;
import allday.minico.utils.member.Validator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.time.LocalDate;
import java.util.Map;

public class SignUpController {

    @FXML private Button backToLoginButton;
    @FXML private ImageView characterImage;
    @FXML private Button checkIdButton;
    @FXML private TextField emailField;
    @FXML private TextField idField;
    @FXML private TextField nicknameField;
    @FXML private PasswordField pwCheckField;
    @FXML private PasswordField pwField;
    @FXML private Label signUpResultText;
    @FXML private Button submitButton;
    @FXML private TextField passwordHint;

    private String checkAvailabilityStatus = null; // ID 중복 체크 상태
    MemberService memberservice =  new MemberService();

    @FXML
    public void initialize() { // 이 컨트롤러와 연결된 fxml 이 로딩될 때 자동으로 실행되는 메서드
    }

    @FXML
    void checkId(ActionEvent event) { // ID 중복체크 버튼 클릭 시 실행
        String memberId = idField.getText().trim(); // 공백 제거
        System.out.println("사용자가 가입하려는 ID : " + memberId);

        if(!Validator.isIdFormat(memberId)){// id 양식이 적합한지 체크
            signUpResultText.setText("※ 아이디는 영문자와 숫자를 포함해 8자 이상 30자 이내로 입력하세요.");
            signUpResultText.setStyle("-fx-text-fill: red;");
            return;}

        if (memberservice.isMemberIdAvailable(memberId)) { // DB 에서 유저 ID 목록을 가져와서 중복 ID 면 "이미 존재하는 ID" 알림
            signUpResultText.setText("※ 사용 가능한 ID입니다");
            signUpResultText.setStyle("-fx-text-fill: green;");
            checkAvailabilityStatus = memberId; // ID 중복 아니면 중복 확인 상태
        } else {
            signUpResultText.setText("※ 이미 존재하는 ID입니다");
            signUpResultText.setStyle("-fx-text-fill: red;");
            checkAvailabilityStatus = null; // ID 중복이면 중복확인 안한 상태 유지
            return;
        }
    }

    @FXML
    void submit(ActionEvent event) {

        if(!Validator.isIdChecked(idField.getText(),checkAvailabilityStatus )) { // ID 중복확인을 했는지
            signUpResultText.setText("ID 중복확인을 해주세요");
            signUpResultText.setStyle("-fx-text-fill: red;");
            return;}
        if(!Validator.isInfoFill(
                idField.getText(),nicknameField.getText(), emailField.getText(),
                passwordHint.getText(), pwField.getText(),pwCheckField.getText())){ // 회원가입 완료 버튼 클릭 시 미기입란 여부 확인
            signUpResultText.setText("※ 회원정보를 모두 입력해주세요");
            signUpResultText.setStyle("-fx-text-fill: red;");
            return;
        }
        if(!Validator.isValidEmail(emailField.getText())) {
            signUpResultText.setText("이메일 형식이 올바르지 않습니다.");
            signUpResultText.setStyle("-fx-text-fill: red;"); return;} // 이메일 양식이 적합한지
        if(!Validator.isPwFormatMatch(pwField.getText())) {
            signUpResultText.setText("비밀번호 형식이 올바르지 않습니다.(소문자,숫자 조합 8~30자)");
            signUpResultText.setStyle("-fx-text-fill: red;"); return;}// 비밀번호 양식이 적합한지
        if(!Validator.isPasswordMatch(pwField.getText(),pwCheckField.getText())) {
            signUpResultText.setText("비밀번호가 일치하지 않습니다.");
            signUpResultText.setStyle("-fx-text-fill: red;"); return;}// 비밀번호, 비밀번호 확인이 일치한지
        if(!Validator.isNicknameFormatMatch(nicknameField.getText())) {
            signUpResultText.setText("닉네임 형식이 올바르지 않습니다.");
            signUpResultText.setStyle("-fx-text-fill: red;"); return;}// 닉네임이 대,소문자,숫자,한글 4~10자 이내인지
        if(!Validator.isPwHintFormatMatch(passwordHint.getText())){
            signUpResultText.setText("비밀번호 힌트가 너무 깁니다.(20자 이하)");
            signUpResultText.setStyle("-fx-text-fill: red;"); return;} // 비밀번호 힌트 20 글자 이내인지

        // 멤버 정보 DTO 생성
        Member member = new Member();
        member.setMemberId(idField.getText().trim());
        member.setPassword(pwField.getText().trim());
        member.setNickname(nicknameField.getText().trim());
        member.setEmail(emailField.getText().trim());
        member.setJoinDate(java.time.LocalDateTime.now());
        member.setPasswordHint(passwordHint.getText());
        member.setCoin(0);
        member.setLevel(1);
        member.setExperience(0);
        member.setMinimi("Male"); // 기본값 미니미는 Male
        member.setVisitCount(0);

        Map<String,Object> map = SceneManager.loadWithController("CharacterSelect"); // 캐릭터 선택 컨트롤러 객체 생성
        CharacterSelectController controller = (CharacterSelectController) map.get("controller");

        controller.setPendingMember(member); // 캐릭터선택 컨트롤러에 멤버정보 전달

        // 캐릭터 선택창으로 이동 (루트만 바꿈)
        SceneManager.getPrimaryStage().getScene().setRoot((Parent) map.get("root"));    }

    @FXML
    void backToLogin(ActionEvent event) { // 로그인 화면으로 돌아가기
        SceneManager.switchTo("Login");
    }
}
