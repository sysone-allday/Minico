/*
@author 최온유
LoginController 클래스는 로그인 화면에서 사용자 입력을 검증하고,
백그라운드 스레드를 통해 로그인 로직을 처리합니다.
성공 시 세션 정보와 로그인 로그를 기록하며,
페이드 전환 효과와 함께 메인 화면으로 이동합니다.
중복 로그인 방지, 로딩 스피너, 배경 음악 전환,
로그인 실패 처리, 회원가입/비밀번호 찾기 화면 전환 등의 기능도 포함됩니다.
 */

package allday.minico.controller.member;


import allday.minico.Main;
import allday.minico.dto.member.Member;
import allday.minico.service.member.LoginLogService;
import allday.minico.service.member.MemberService;
import allday.minico.session.AppSession;
import allday.minico.ui.common.LoadingSpinner;
import allday.minico.utils.member.SceneManager;
import allday.minico.utils.audio.BackgroundMusicManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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
    @FXML private AnchorPane rootPane; // 루트 패널 추가

    private final MemberService memberService;
    private LoadingSpinner loadingSpinner; // 로딩 스피너 추가

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
            
            // LoadingSpinner 초기화 (루트 패널을 가져와서 초기화)
            if (rootPane != null) {
                loadingSpinner = new LoadingSpinner(rootPane);
            }
        });
    }

    @FXML
    void signUp(ActionEvent event) { // 회원가입 창으로 이동
        SceneManager.switchTo("SignUp");
    } // 회원가입 창으로 이동

    @FXML
   void login(ActionEvent event) { // 로그인 버튼 클릭 시 마이룸으로 이동
        
        // 입력값 검증
        String userId = idField.getText();
        String password = pwField.getText();
        
        if (userId == null || userId.trim().isEmpty()) {
            SceneManager.showModal("LoginFail", "아이디를 입력해주세요.");
            return;
        }
        
        if (password == null || password.trim().isEmpty()) {
            SceneManager.showModal("LoginFail", "비밀번호를 입력해주세요.");
            return;
        }
        
        // 로딩 스피너 표시
        if (loadingSpinner != null) {
            loadingSpinner.show();
        }
        
        // 로그인 버튼 비활성화
        loginButton.setDisable(true);
        
        // 백그라운드에서 로그인 처리
        Task<Member> loginTask = new Task<Member>() {
            @Override
            protected Member call() throws Exception {
                // 동시 로그인이 되지 않도록 로그아웃정보를 기반으로 로그인 가능여부 체크
                Boolean isMultipleLogin = memberService.preventMultipleLogins(userId);
                if(Boolean.FALSE.equals(isMultipleLogin)) {
                    throw new Exception("MULTIPLE_LOGIN");
                }
                
                // 로그인 시도
                Member loginmember = memberService.login(userId, password);
                if(loginmember == null) {
                    throw new Exception("LOGIN_FAILED");
                }
                
                return loginmember;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    try {
                        Member loginmember = getValue();
                        
                        AppSession.setLoginMember(loginmember); // 로그인한 멤버 정보 세션에 저장
                        LoginLogService loginlogservice = LoginLogService.getInstance();
                        long logId = loginlogservice.recordLoginLog(loginmember.getMemberId()); // 로그인 로그 INSERT 후 로그 ID 반환
                        
                        if(logId > 0){
                            AppSession.setLoginLog(logId); // 로그인 시 로그ID 를 세션에 저장
                        } else {
                            AppSession.clear(); // 로그 ID가 비정상적이면 세션 초기화
                            if (loadingSpinner != null) {
                                loadingSpinner.hide();
                            }
                            loginButton.setDisable(false);
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
                                // 로딩 스피너 숨기기 및 버튼 활성화
                                if (loadingSpinner != null) {
                                    loadingSpinner.hide();
                                }
                                loginButton.setDisable(false);
                            }
                        });

                        fadeOut.play();

                    } catch (Exception e) {
                        AppSession.clear();
                        e.printStackTrace();
                        // 로딩 스피너 숨기기 및 버튼 활성화
                        if (loadingSpinner != null) {
                            loadingSpinner.hide();
                        }
                        loginButton.setDisable(false);
                    }
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    // 로딩 스피너 숨기기 및 버튼 활성화
                    if (loadingSpinner != null) {
                        loadingSpinner.hide();
                    }
                    loginButton.setDisable(false);
                    
                    Throwable exception = getException();
                    if (exception != null && exception.getMessage() != null) {
                        String message = exception.getMessage();
                        if ("MULTIPLE_LOGIN".equals(message)) {
                            SceneManager.showModal("MultipleLoginError", "중복 로그인");
                        } else if ("LOGIN_FAILED".equals(message)) {
                            SceneManager.showModal("LoginFail", "로그인 실패 !");
                        } else {
                            SceneManager.showModal("LoginFail", "로그인 중 오류가 발생했습니다.");
                        }
                    } else {
                        SceneManager.showModal("LoginFail", "로그인 중 오류가 발생했습니다.");
                    }
                });
            }
        };
        
        // 백그라운드 스레드에서 실행
        Thread loginThread = new Thread(loginTask);
        loginThread.setDaemon(true);
        loginThread.start();
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
