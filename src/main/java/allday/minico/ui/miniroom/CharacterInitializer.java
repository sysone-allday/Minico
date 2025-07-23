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
            
            // 오류 발생 시 기본 이미지로 폴백
            loadDefaultCharacter(callback);
        }
    }
    
    /**
     * 사용자의 캐릭터 이미지 경로 가져오기 (front 방향)
     */
    private String getUserCharacterImagePath() {
        try {
            String memberId = allday.minico.session.AppSession.getLoginMember().getMemberId();
            
            if (memberId != null) {
                // 캐싱된 최적화 메서드 사용 - DB 조회를 최소화
                String imagePath = allday.minico.utils.skin.SkinUtil.getCharacterImagePath(memberId, "front");
                System.out.println("[CharacterInitializer] 생성된 초기 이미지 경로: " + imagePath);
                return imagePath;
            }
        } catch (Exception e) {
            System.out.println("[CharacterInitializer] 사용자 캐릭터 정보를 가져올 수 없습니다: " + e.getMessage());
        }
        
        // 정보가 없으면 기본 캐릭터 사용
        String defaultPath = "/allday/minico/images/char/male/대호_front.png";
        System.out.println("[CharacterInitializer] 기본 이미지 경로: " + defaultPath);
        return defaultPath;
    }
    
    /**
     * 기본 캐릭터로 폴백 (오류 발생 시)
     */
    private void loadDefaultCharacter(CharacterInitCallback callback) {
        try {
            String defaultPath = "/allday/minico/images/char/male/대호_front.png";
            Image defaultImage = new Image(getClass().getResource(defaultPath).toExternalForm());
            ImageView character = new ImageView(defaultImage);
            character.setFitWidth(100);
            character.setFitHeight(100);
            character.setPreserveRatio(true);

            roomPane.getChildren().add(character);
            
            System.out.println("[CharacterInitializer] 기본 캐릭터로 폴백: " + defaultPath);

            javafx.application.Platform.runLater(() -> {
                double centerX = (roomPane.getWidth() - character.getFitWidth()) / 2;
                double centerY = (roomPane.getHeight() - character.getFitHeight()) / 2;
                character.setLayoutX(centerX);
                character.setLayoutY(centerY);
                callback.onCharacterInitialized(character);
            });
            
        } catch (Exception fallbackException) {
            System.out.println("[CharacterInitializer] 기본 캐릭터도 로드할 수 없습니다: " + fallbackException.getMessage());
        }
    }
}
