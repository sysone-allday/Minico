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

    @FXML private Label coinText;
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
        nicknameText.setText("닉네임 : " + loginmember.getNickname());
        idText.setText("ID : " + loginmember.getMemberId());
        emailText.setText("이메일 : " + loginmember.getEmail());
        levelText.setText("레벨 : " + loginmember.getLevel().toString());
        coinText.setText("코인 : " + loginmember.getCoin().toString());
        experienceText.setText("경험치 : " + loginmember.getExperience().toString());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedTime = loginmember.getJoinDate() != null
                ? loginmember.getJoinDate().format(formatter) : "가입일 알 수 없음";
        joindateText.setText("가입일 : " + formattedTime);

        /*
        // 내 정보 조회 시, 내 미니미사진이 나오게 하려면 캐릭터 선택 시에 해당 미니미의 이미지파일 이름(확장자 없이)이
        // MEMBER 테이블에 저장되어야 한다 ! (지금은 캐릭터 선택해도 이미지 파일이름과 일치하지 않음)
        String myMinimi = loginmember.getMinimi();
        Image image = new Image(getClass().getResource("/allday/minico/images/member/" +  myMinimi + ".png").toExternalForm());
        myMinimiImg.setImage(image);
         */
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
