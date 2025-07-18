package allday.minico.ui.miniroom;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class CharacterInitializer {
    
    private final Pane roomPane;
    
    public interface CharacterInitCallback {
        void onCharacterInitialized(ImageView character);
    }
    
    public CharacterInitializer(Pane roomPane) {
        this.roomPane = roomPane;
    }
    
    public void initializeCharacter(CharacterInitCallback callback) {
        try {
            // 캐릭터 이미지 로드
            Image characterImage = new Image(
                    getClass().getResource("/allday/minico/images/char/front.png").toExternalForm());
            ImageView character = new ImageView(characterImage);
            character.setFitWidth(100);
            character.setFitHeight(100);
            character.setPreserveRatio(true);

            roomPane.getChildren().add(character);

            // UI가 완전히 렌더링된 후 캐릭터 위치 설정
            javafx.application.Platform.runLater(() -> {
                double centerX = (roomPane.getWidth() - character.getFitWidth()) / 2;
                double centerY = (roomPane.getHeight() - character.getFitHeight()) / 2;
                character.setLayoutX(centerX);
                character.setLayoutY(centerY);

                // 캐릭터 초기화 완료 콜백 호출
                callback.onCharacterInitialized(character);

//                System.out.println("=== 캐릭터 초기화 (렌더링 후) ===");
//                System.out.println("캐릭터 초기 위치 - X: " + character.getLayoutX() + ", Y: " + character.getLayoutY());
//                System.out.println("미니룸 실제 크기 - Width: " + roomPane.getWidth() + ", Height: " + roomPane.getHeight());
            });

        } catch (Exception e) {
            System.out.println("캐릭터 이미지를 로드할 수 없습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
