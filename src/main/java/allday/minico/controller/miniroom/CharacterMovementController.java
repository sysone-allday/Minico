package allday.minico.controller.miniroom;

import javafx.animation.AnimationTimer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CharacterMovementController {
    
    private final Pane roomPane;
    private final ImageView character;
    private final Set<String> pressedKeys = new HashSet<>();
    private AnimationTimer moveTimer;
    
    // 콜백 인터페이스
    public interface MovementCallback {
        void onCharacterMove(double x, double y, String direction);
        void onNameLabelUpdate(Text nameLabel, ImageView character);
        String getPlayerName();
        Map<String, Text> getCharacterNameLabels();
    }
    
    private MovementCallback callback;
    
    public CharacterMovementController(Pane roomPane, ImageView character, MovementCallback callback) {
        this.roomPane = roomPane;
        this.character = character;
        this.callback = callback;
        
        setupKeyboardControls();
    }
    
    private void setupKeyboardControls() {
        roomPane.setFocusTraversable(true);

        roomPane.setOnKeyPressed(event -> {
            String keyCode = event.getCode().toString();
            if (!pressedKeys.contains(keyCode)) {
                pressedKeys.add(keyCode);
            }
            event.consume();
        });

        roomPane.setOnKeyReleased(event -> {
            String keyCode = event.getCode().toString();
            pressedKeys.remove(keyCode);
        });

        startMovementLoop();
    }
    
    private void startMovementLoop() {
        moveTimer = new AnimationTimer() {
            private String lastDirection = "";
            private long lastUpdateTime = 0;
            private long lastNetworkUpdateTime = 0;
            private static final long UPDATE_INTERVAL = 16; 
            private static final long NETWORK_UPDATE_INTERVAL = 50; // 20fps로 네트워크 업데이트

            @Override
            public void handle(long now) {
                if (character == null)
                    return;

             //프레임 제한
                if (now - lastUpdateTime < UPDATE_INTERVAL * 1_000_000) {
                    return;
                }
                lastUpdateTime = now;

                double moveDistance = 5;
                double x = character.getLayoutX();
                double y = character.getLayoutY();
                double charWidth = character.getFitWidth();
                double charHeight = character.getFitHeight();
                double paneWidth = roomPane.getWidth();
                double paneHeight = roomPane.getHeight();
                String currentDirection = "";

                // 왼쪽 이동 (A, LEFT)
                if (pressedKeys.contains("A") || pressedKeys.contains("LEFT")) {
                    double nextX = x - moveDistance;
                    if (nextX < 0)
                        nextX = 0;
                    character.setLayoutX(nextX);
                    currentDirection = "LEFT";
                    if (!lastDirection.equals("LEFT")) {
                        updateCharacterImage("Left.png");
                    }
                }
                // 오른쪽 이동 (D, RIGHT)
                if (pressedKeys.contains("D") || pressedKeys.contains("RIGHT")) {
                    double nextX = x + moveDistance;
                    if (nextX > paneWidth - charWidth)
                        nextX = paneWidth - charWidth;
                    character.setLayoutX(nextX);
                    currentDirection = "RIGHT";
                    if (!lastDirection.equals("RIGHT")) {
                        updateCharacterImage("Right.png");
                    }
                }
                // 위쪽 이동 (W, UP)
                if (pressedKeys.contains("W") || pressedKeys.contains("UP")) {
                    double nextY = y - moveDistance;
                    if (nextY < 0)
                        nextY = 0;
                    character.setLayoutY(nextY);
                    currentDirection = "UP";
                    if (!lastDirection.equals("UP")) {
                        updateCharacterImage("back.png");
                    }
                }
                // 아래쪽 이동 (S, DOWN)
                if (pressedKeys.contains("S") || pressedKeys.contains("DOWN")) {
                    double nextY = y + moveDistance;
                    if (nextY > paneHeight - charHeight)
                        nextY = paneHeight - charHeight;
                    character.setLayoutY(nextY);
                    currentDirection = "DOWN";
                    if (!lastDirection.equals("DOWN")) {
                        updateCharacterImage("front.png");
                    }
                }

                // 캐릭터 위치가 변경되었을 때 이름표 업데이트 (매 프레임)
                if (!currentDirection.isEmpty()) {
                    String playerName = callback.getPlayerName();
                    Map<String, Text> characterNameLabels = callback.getCharacterNameLabels();
                    Text myNameLabel = characterNameLabels.get(playerName);
                    if (myNameLabel != null) {
                        callback.onNameLabelUpdate(myNameLabel, character);
                    }
                }

                // 네트워크 업데이트는 낮은 빈도로 (20fps)
                if (!currentDirection.isEmpty()
                        && (now - lastNetworkUpdateTime) >= NETWORK_UPDATE_INTERVAL * 1_000_000) {
                    lastNetworkUpdateTime = now;

                    callback.onCharacterMove(character.getLayoutX(), character.getLayoutY(), currentDirection);
                }

                lastDirection = currentDirection;
            }
        };
        moveTimer.start();
    }
    
    private void updateCharacterImage(String imageName) {
        try {
            Image newImage = new Image(
                    getClass().getResource("/allday/minico/images/char/" + imageName).toExternalForm());
            character.setImage(newImage);
        } catch (Exception e) {
            System.out.println("캐릭터 이미지를 변경할 수 없습니다: " + e.getMessage());
        }
    }
    
    public void stopMovementTimer() {
        if (moveTimer != null) {
            moveTimer.stop();
        }
    }
    
    public void startMovementTimer() {
        if (moveTimer != null) {
            moveTimer.start();
        }
    }
    
    public void cleanup() {
        stopMovementTimer();
        pressedKeys.clear();
    }
    
    // 현재 눌린 키들을 반환 (디버깅용)
    public Set<String> getPressedKeys() {
        return new HashSet<>(pressedKeys);
    }
    
    // 이동 속도 조정 (필요시 추가)
    public void setMoveDistance(double distance) {
        // 이동 거리 설정 기능을 추가할 수 있음
    }
}
