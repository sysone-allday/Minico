package allday.minico.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Pane;

public class MainController {
    @FXML
    private VBox menuButtons;
    
    @FXML
    private Button boardBtn;
    
    @FXML
    private Button diaryBtn;
    
    @FXML
    private Button inputBtn;
    
    @FXML
    private Button oxBtn;
    
    @FXML
    private Pane roomPane;

    @FXML
    protected void onGuestbookClick() {
        System.out.println("게시판 버튼 클릭");
        // 게시판 기능 구현
    }
    
    @FXML
    protected void onMusicClick() {
        System.out.println("다이어리 버튼 클릭");
        // 다이어리 기능 구현
    }
    
    @FXML
    protected void onSettingClick() {
        System.out.println("타자게임 버튼 클릭");
        // 타자게임 기능 구현
    }
    
    @FXML
    protected void onHelpClick() {
        System.out.println("OX게임 버튼 클릭");
        // OX게임 기능 구현
    }
}