package allday.minico;

import allday.minico.utils.member.SceneManager;
import javafx.application.Application;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {  // start 가 실행되면 자동으로 stage를 생성해서 매개변수로 넣어준다
        // System.out.println("🚀 애플리케이션 시작");
        
        // 리소스 경로 확인
        String loginPath = "/allday/minico/view/diary/diary.fxml";
        // System.out.println("🔍 리소스 확인: " + loginPath);
        // System.out.println("📂 리소스 URL: " + Main.class.getResource(loginPath));
        
        if (Main.class.getResource(loginPath) == null) {
            System.err.println("❌ Login.fxml을 찾을 수 없습니다!");
            return;
        }
        
        SceneManager.init(stage); // stage
        SceneManager.switchScene("diary");  // 최초 화면을 "Login.fxml"로 설정
        SceneManager.getPrimaryStage().show(); // 스테이지 띄우기
    }


    // 수정한 것
    // module-info 에 opens allday.minico.controller.member to javafx.fxml; 추가함


    /*
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("view/diary.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        stage.setTitle("Minico");
        stage.setScene(scene);
        stage.setMinWidth(1280);
        stage.setMinHeight(800);
        stage.show();
    }
    */



    public static void main(String[] args) {
        launch();
    }
}