package allday.minico.controller.member;

import allday.minico.dto.member.Member;
import allday.minico.session.AppSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    }

}
