package allday.minico.utils.member;

import allday.minico.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/// ////////////////////////////////
/// 화면 전환 용 클래스
/// //////////////////////////////

public class SceneManager {
    private static Stage primaryStage; // 최초 실행 시 사용되는 기본 스테이지
    private static Scene scene;

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void switchScene(String fxml) {// 씬 변경 메서드
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/member/" + fxml + ".fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 1280, 800);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void switchTo(String fxml) { // 루트 변경 메서드
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/member/" + fxml + ".fxml"));
            // FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();
            primaryStage.getScene().setRoot(root); // 씬은 그대로 두고 루트만 변경
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String,Object> loadWithController(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/member/" + fxml + ".fxml"));
            // FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"));
            Parent root = loader.load();
            Object controller = loader.getController();
            Map<String, Object> map = new HashMap<>();
            map.put("root", root);
            map.put("controller", controller);
            return map;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FXML 로드 실패: " + fxml);
        }
    }

    public static void showModal(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/allday/minico/view/member/" + fxmlFileName + ".fxml"));
            Parent root = loader.load();

            Stage modalStage = new Stage(); // 새로운 스테이지 사용
            modalStage.setTitle(title); // 스테이지 창 이름
            modalStage.setScene(new Scene(root));
            modalStage.initOwner(getPrimaryStage()); // 메인스테이지를 부모스테이지로 설정
            modalStage.initModality(Modality.APPLICATION_MODAL); // 부모 스테이지 조작 못하게 잠금

            modalStage.showAndWait(); // 모달창 종료까지 대기 (추후 기능 추가용)
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() { // 메인 스테이지 반환 메서드
        return primaryStage;
    }
}
