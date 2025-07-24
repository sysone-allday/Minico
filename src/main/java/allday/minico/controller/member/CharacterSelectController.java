package allday.minico.controller.member;

import allday.minico.dto.member.Member;
import allday.minico.service.member.LoginLogService;
import allday.minico.service.member.MemberService;
import allday.minico.service.skin.SkinService;
import allday.minico.utils.member.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class CharacterSelectController {

    @FXML private Button backToSignUpButton;
    @FXML private Button selectMaleButton;
    @FXML private Button selectFemaleButton;
    @FXML private VBox minimiSelectionArea;
    @FXML private Label selectionLabel;
    
    @FXML private ImageView minimiOption1Image;
    @FXML private ImageView minimiOption2Image;
    @FXML private ImageView minimiOption3Image;
    
    @FXML private Button selectOption1Button;
    @FXML private Button selectOption2Button;
    @FXML private Button selectOption3Button;
    
    @FXML private ImageView selectedMinimiPreview;
    @FXML private Label selectedMinimiInfo;

    @FXML private Button characterSelectConfirmButton;

    private Member member;
    private MemberService memberService = new MemberService();
    private SkinService skinService = SkinService.getInstance();
    
    // 선택된 미니미 정보
    private String selectedGender;
    private String selectedVariant;

    @FXML
    void showMaleOptions(ActionEvent event) {
        selectedGender = "Male";
        showMinimiOptions("male");
    }

    @FXML
    void showFemaleOptions(ActionEvent event) {
        selectedGender = "Female";
        showMinimiOptions("female");
    }
    
    private void showMinimiOptions(String gender) {
        minimiSelectionArea.setVisible(true);
        selectionLabel.setText(gender.equals("male") ? "남자 미니미를 선택하세요" : "여자 미니미를 선택하세요");
        
        // 남자 캐릭터들의 이미지 로드
        try {
            String basePath = "/allday/minico/images/char/" + gender + "/";
            if (gender.equals("male")) {
                minimiOption1Image.setImage(new Image(getClass().getResource(basePath + "대호_front.png").toExternalForm()));
                minimiOption2Image.setImage(new Image(getClass().getResource(basePath + "마리오_front.png").toExternalForm()));
                minimiOption3Image.setImage(new Image(getClass().getResource(basePath + "온유_front.png").toExternalForm()));
            } else {
                // 여자 캐릭터들
                minimiOption1Image.setImage(new Image(getClass().getResource(basePath + "민서_front.png").toExternalForm()));
                minimiOption2Image.setImage(new Image(getClass().getResource(basePath + "소영_front.png").toExternalForm()));
                minimiOption3Image.setImage(new Image(getClass().getResource(basePath + "슬기_front.png").toExternalForm()));
            }
        } catch (Exception e) {
            // 이미지가 없으면 기본 이미지 또는 빈 이미지
            // System.out.println("미니미 이미지를 로드할 수 없습니다: " + e.getMessage());
        }
        
        // 버튼 텍스트 설정
        if (gender.equals("male")) {
            selectOption1Button.setText("대호");
            selectOption2Button.setText("마리오");
            selectOption3Button.setText("온유");
        } else {
            selectOption1Button.setText("민서");
            selectOption2Button.setText("소영");
            selectOption3Button.setText("슬기");
        }
    }
    
    @FXML
    void selectMinimiOption1(ActionEvent event) {
        if (selectedGender.equals("Male")) {
            selectMinimiVariant("대호");
        } else {
            selectMinimiVariant("민서");
        }
    }
    
    @FXML
    void selectMinimiOption2(ActionEvent event) {
        if (selectedGender.equals("Male")) {
            selectMinimiVariant("마리오");
        } else {
            selectMinimiVariant("소영");
        }
    }
    
    @FXML
    void selectMinimiOption3(ActionEvent event) {
        if (selectedGender.equals("Male")) {
            selectMinimiVariant("온유");
        } else {
            selectMinimiVariant("슬기");
        }
    }
    
    private void selectMinimiVariant(String variant) {
        selectedVariant = variant;
        
        // 선택된 미니미 미리보기 업데이트
        try {
            String imagePath;
            // 남자/여자 모두 동일한 파일명 규칙 사용: {이름}_front.png
            imagePath = String.format("/allday/minico/images/char/%s/%s_front.png", 
                                   selectedGender.toLowerCase(), variant);
            
            selectedMinimiPreview.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
            selectedMinimiInfo.setText(String.format("%s %s 선택됨", selectedGender, variant));
            
            // 확인 버튼 활성화
            characterSelectConfirmButton.setVisible(true);
            
        } catch (Exception e) {
            // System.out.println("미니미 미리보기 이미지를 로드할 수 없습니다: " + e.getMessage());
            selectedMinimiInfo.setText(String.format("%s %s 선택됨 (이미지 없음)", selectedGender, variant));
            characterSelectConfirmButton.setVisible(true);
        }
    }

    public void setPendingMember(Member member) {
        this.member = member;
    }

    @FXML
    public void characterSelectConfirm(ActionEvent event) {
        // selectedGender만 MINIMI_TYPE에 저장
        if (selectedGender != null) {
            member.setMinimi(selectedGender);
        }
        
        boolean success = memberService.signUp(member);


        if (success) {
            // 회원가입 성공 시 기본 스킨 생성
            if (selectedGender != null && selectedVariant != null) {
                skinService.createDefaultSkin(member.getMemberId(), selectedGender, selectedVariant);
            }
            System.out.println("회원가입 성공");
            SceneManager.showModal("signUpComplete", "회원가입 완료");
        } else {
            System.out.println("회원가입 오류 발생");
        }
    }
    
    @FXML
    void backToSignUp(ActionEvent event) { // 회원가입 창으로 돌아가기
        SceneManager.switchTo("SignUp");
    }


}
