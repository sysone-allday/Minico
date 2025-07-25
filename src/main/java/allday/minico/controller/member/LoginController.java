package allday.minico.controller.member;


import allday.minico.Main;
import allday.minico.dto.member.Member;
import allday.minico.service.member.LoginLogService;
import allday.minico.service.member.MemberService;
import allday.minico.sesstion.AppSession;
import allday.minico.utils.member.SceneManager;
import allday.minico.utils.audio.AudioManager;
import allday.minico.utils.audio.ButtonSoundHandler;
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
            try {
                AudioManager.getInstance().playBackgroundMusic("main-music.mp3", true);
                
                // 모든 버튼에 클릭 효과음 추가
                if (loginButton.getScene() != null) {
                    ButtonSoundHandler.addButtonSounds(loginButton.getScene());
                }
            } catch (Exception e) {
                System.err.println("배경음악 재생 실패: " + e.getMessage());
            }
        });
    }

    @FXML
    void signUp(ActionEvent event) { // 회원가입 창으로 이동
        SceneManager.switchTo("SignUp");
    } // 회원가입 창으로 이동

    @FXML
    void login(ActionEvent event) { // 로그인 버튼 클릭 시 마이룸으로 이동 (지금 하드코딩해서 나중에 SceneManager 메서드로 바뀌게 변경하는 것이 좋아보임)
        Member loginmember = memberService.login(idField.getText(), pwField.getText());

        // 화면 전환 효과
//        Parent currentRoot = loginButton.getScene().getRoot();
//        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentRoot);
//        fadeOut.setFromValue(1.0);
//        fadeOut.setToValue(0.0);
//   fadeOut.setOnFinished(e -> {
//            try {
//                // 미니룸 화면 로드
//                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/Miniroom.fxml"));
//                Parent root = loader.load();
//                Scene scene = new Scene(root, 1280, 800);
//
//                // 새 화면을 투명하게 시작
//                root.setOpacity(0.0);
//                SceneManager.getPrimaryStage().setScene(scene);
//
//                // 새 화면에 페이드인 효과 적용
//                FadeTransition fadeIn = new FadeTransition(Duration.millis(500), root);
//                fadeIn.setFromValue(0.0);
//                fadeIn.setToValue(1.0);
//                fadeIn.play();
//
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        });
//
//        fadeOut.play();

        if(loginmember != null) { // 멤버 정보, 로그인 로그 ID 를 세션에 저장
            try {
                AppSession.setLoginMember(loginmember); // 로그인한 멤버 정보 세션에 저장
                LoginLogService loginlogservice = LoginLogService.getInstance(); // 로그 서비스 객체 가져옴
                long logId = loginlogservice.recordLoginLog(loginmember.getMemberId()); // 로그인 로그 INSERT 후 로그 ID 반환
                if(logId > 0){
                    AppSession.setLoginLog(logId); // 로그인 시 로그ID 를 세션에 저장
                } else {
                    AppSession.clear(); // 로그 ID가 비정상적이면 세션 초기화
                    return;
                }

                /// ////////////////////////////////////////////////////////////////////////////////////////////////
                // 로그인 성공 시 배경음악 정지
                AudioManager.getInstance().stopCurrentMusic();
                
                // 로그인 성공 시 미니룸으로 화면전환 (씬만 변경)
                FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/miniroom.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 1280, 800);
                SceneManager.getPrimaryStage().setScene(scene);
                /// ////////////////////////////////////////////////////////////////////////////////////////////////

            } catch (IOException e) {
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
