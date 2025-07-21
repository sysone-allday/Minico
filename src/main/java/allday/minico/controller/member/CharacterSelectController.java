package allday.minico.controller.member;


import allday.minico.dto.member.Member;
import allday.minico.service.member.MemberService;
import allday.minico.utils.member.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CharacterSelectController {
    @FXML private ImageView ViewSelectedCharacter;
    @FXML private Button backToSignUpButton;
    @FXML private Button selectFemaleMinimiButton;
    @FXML private Button selectMaleMinimiButton;
    @FXML private Button characterSelectConfirmButton;

    private Member member;
    MemberService memberService = new MemberService();

    @FXML
    void selectFemaleMinimi(ActionEvent event) { // 여자 미니미 클릭 시
        ViewSelectedCharacter.setImage(new Image(getClass().getResource("/allday/minico/images/member/Minimi_Female.png").toExternalForm()));
        member.setMinimi("Minimi_Female");
    }

    @FXML
    void selectMaleMinimi(ActionEvent event) { // 남자 미니미 클릭 시
        ViewSelectedCharacter.setImage(new Image(getClass().getResource("/allday/minico/images/member/Minimi_Male.png").toExternalForm()));
        member.setMinimi("Minimi_Male");
    }

    public void setPendingMember(Member member) {
        this.member = member; // Member 객체 전달받기
    }

    @FXML
    public void characterSelectConfirm(ActionEvent event) { // 캐릭터 선택 완료 버튼 클릭 시
        boolean success = memberService.signUp(member);
        if(success) {// Insert 성공 시 모달창 뜨고 메인으로 돌아가기
            System.out.println("회원가입 성공");
            SceneManager.showModal("signUpComplete", "회원가입 완료");
        } else{ // 실패하면 회원가입 실패 메세지
            System.out.println("회원가입 오류 발생");
        }
    }

    @FXML
    void backToSignUp(ActionEvent event) { // 회원가입 창으로 돌아가기
        SceneManager.switchTo("SignUp");
    }
}
