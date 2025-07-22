package allday.minico;

import allday.minico.utils.member.SceneManager;
import allday.minico.utils.audio.AudioManager;
import javafx.application.Application;
// import javafx.fxml.FXMLLoader;
// import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {


    @Override
    public void start(Stage stage) throws IOException {  // start 가 실행되면 자동으로 stage를 생성해서 매개변수로 넣어준다
        SceneManager.init(stage); // stage
        SceneManager.switchScene("Login");  // 최초 화면을 "Login.fxml"로 설정
        SceneManager.getPrimaryStage().show(); // 스테이지 띄우기
    }

    @Override
    public void stop() throws Exception {
        // 애플리케이션 종료 시 오디오 리소스 정리
        AudioManager.getInstance().cleanup();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}