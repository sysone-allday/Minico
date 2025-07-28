/*
@author 최온유
MyProfileController 클래스는 로그인한 사용자의 프로필 정보를 화면에 표시하고,
회원정보 수정 창 열기, 회원 탈퇴 처리, 로그인 화면 전환 기능을 제공합니다.
가입일, 레벨, 경험치 등의 정보를 불러와 UI에 반영하며,
회원정보 수정 시 해당 Stage를 주입하여 수정 완료 후 창을 닫을 수 있도록 합니다.
 */
package allday.minico.controller.member;

import allday.minico.dto.member.Member;
import allday.minico.service.member.MemberService;
import allday.minico.session.AppSession;
import allday.minico.utils.member.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class MyProfileController {

    // @FXML private Label coinText; 코인 삭제 0_0
    @FXML private Label emailText;
    @FXML private Label experienceText;
    @FXML private Label idText;
    @FXML private Label joindateText;
    @FXML private Label levelText;
    @FXML private Button modifyProfileButton;
    @FXML private Label nicknameText;
    @FXML private ImageView myMinimiImg;
    @FXML private Button deleteAccountButton;

    MemberService memberService = new MemberService();

    @FXML
    public void initialize(){
        Member loginmember = AppSession.getLoginMember();
        nicknameText.setText(loginmember.getNickname());
        idText.setText(loginmember.getMemberId());
        emailText.setText(loginmember.getEmail());
        levelText.setText("레벨 : " + loginmember.getLevel());
        // coinText.setText("코인 : " + loginmember.getCoin()); 코인 삭제 !
        experienceText.setText("경험치 : " + loginmember.getExperience());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedTime = loginmember.getJoinDate() != null
                ? loginmember.getJoinDate().format(formatter) : "가입일 알 수 없음";
        joindateText.setText(formattedTime);
    }

    @FXML
    void modifyProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/allday/minico/view/member/ModifyProfile.fxml"));
            Parent root = loader.load();

            ModifyProfileController controller = loader.getController();
            Stage myProfileStage = (Stage) modifyProfileButton.getScene().getWindow(); // 이 창 (내정보창)
            controller.setMyProfileStage(myProfileStage); // 내정보창 Stage 주입

            Stage modalStage = new Stage();
            modalStage.initOwner(myProfileStage);
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.setScene(new Scene(root));
            modalStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteAccountButtonClick(ActionEvent event){
        String deleteId = AppSession.getLoginMember().getMemberId();
        boolean deleteMemberResult = memberService.deleteAccount(deleteId);
        if(deleteMemberResult){
            // 회원탈퇴 성공 시 회원정보 스테이지 종료
            Stage thisStage = (Stage) deleteAccountButton.getScene().getWindow();
            thisStage.close();
            // 앱세션 클리어 (로그아웃 처리)
            AppSession.logout();
            // 로그인 화면으로 전환
            SceneManager.switchScene("login");
        }
    }
}
