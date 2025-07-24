package allday.minico.controller.member;


import allday.minico.Main;
import allday.minico.dto.member.Member;
import allday.minico.service.member.LoginLogService;
import allday.minico.service.member.MemberService;
import allday.minico.session.AppSession;
import allday.minico.utils.member.SceneManager;
import allday.minico.utils.audio.BackgroundMusicManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
// import javafx.scene.image.Image;

import java.io.IOException;


public class LoginController {

    @FXML private Button ExitButton;
    @FXML private ImageView characterImage;
    @FXML private Button findPasswordButton;
    @FXML private TextField idField;
    @FXML private Button loginButton;
    @FXML private PasswordField pwField;
    @FXML private Button signUpButton;
    /*
    @FXML private Button logoutButton;
    @FXML
    void logout(ActionEvent event) {
        boolean logoutresult = AppSession.logout();  --------------------------------- 로그아웃 하려면 이렇게
        System.out.println(logoutresult);
    }
    */

    private final MemberService memberService;

    public LoginController() {
        this.memberService = new MemberService();
    }


    @FXML
    public void initialize() { // 이 컨트롤러와 연결된 fxml 이 로딩될 때 자동으로 실행되는 메서드
        // 로그인 화면 배경음악 재생
        Platform.runLater(() -> {
            if (loginButton.getScene() != null) {
                BackgroundMusicManager.playLoginMusic(loginButton.getScene());
            }
        });
    }

    @FXML
    void signUp(ActionEvent event) { // 회원가입 창으로 이동
        SceneManager.switchTo("SignUp");
    } // 회원가입 창으로 이동

    @FXML

   void login(ActionEvent event) { // 로그인 버튼 클릭 시 마이룸으로 이동

        // 동시 로그인이 되지 않도록 로그아웃정보를 기반으로 로그인 가능여부 체크
        Boolean isMultipleLogin = memberService.preventMultipleLogins(idField.getText());
        if(Boolean.FALSE.equals(isMultipleLogin)) {
            SceneManager.showModal("MultipleLoginError", "중복 로그인");
            return;
        }

        Member loginmember = memberService.login(idField.getText(), pwField.getText());

        if(loginmember != null) { // 멤버 정보, 로그인 로그 ID 를 세션에 저장
            try {
                AppSession.setLoginMember(loginmember); // 로그인한 멤버 정보 세션에 저장
                LoginLogService loginlogservice = LoginLogService.getInstance();
                long logId = loginlogservice.recordLoginLog(loginmember.getMemberId()); // 로그인 로그 INSERT 후 로그 ID 반환
                if(logId > 0){
                    AppSession.setLoginLog(logId); // 로그인 시 로그ID 를 세션에 저장
                } else {
                    AppSession.clear(); // 로그 ID가 비정상적이면 세션 초기화
                    return;
                }

                // 로그인 성공 시 배경음악을 main-music으로 변경
                BackgroundMusicManager.switchToMainMusic();
                
                // 화면 전환 효과
                Parent currentRoot = loginButton.getScene().getRoot();
                FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentRoot);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(e -> {
                    try {
                        // 미니룸 화면 로드
                        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/miniroom.fxml"));
                        Parent root = loader.load();
                        Scene scene = new Scene(root, 1280, 800);

                        // 새 화면을 투명하게 시작
                        root.setOpacity(0.0);
                        SceneManager.getPrimaryStage().setScene(scene);

                        // 새 화면에 페이드인 효과 적용
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.play();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                fadeOut.play();

            } catch (Exception e) {
                AppSession.clear();
                e.printStackTrace();
            }
        } else {
            SceneManager.showModal("LoginFail", "로그인 실패 !");
        }
    }



    @FXML
    void Exit(ActionEvent event) {
        Platform.exit();
    } // 프로그램 종료



    @FXML
    void findPassword(ActionEvent event) { // 비밀번호 찾기 버튼 클릭 시
        SceneManager.showModal("findPassword", "비밀번호 찾기"); // 모달창 띄우기
    }

}
