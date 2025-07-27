package allday.minico.controller.miniroom;

/*
@author 김대호
CharacterMovementController 클래스는 미니룸에서 캐릭터의 움직임을 제어하는 클래스입니다.
키보드 입력 처리, 캐릭터 위치 업데이트, 콜백 호출 등의 기능을 제공합니다.
 */

import allday.minico.session.AppSession;
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
        void onSpacebarPressed(double charX, double charY);
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
            
            // 스페이스바 처리
            if ("SPACE".equals(keyCode)) {
                if (character != null && callback != null) {
                    callback.onSpacebarPressed(character.getLayoutX(), character.getLayoutY());
                }
                event.consume();
                return;
            }
            
            // 일반 이동키 처리
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

                // 바닥 영역 제한 설정 (room 이미지의 바닥 부분만 접근 가능)
                // 실제 룸 크기에 맞춰 동적으로 계산
                double floorTopY = paneHeight * 0.23;  // 바닥 높이
                double floorBottomY = paneHeight - charHeight; // 하단 경계

                // 왼쪽 이동 (A, LEFT)
                if (pressedKeys.contains("A") || pressedKeys.contains("LEFT")) {
                    double nextX = x - moveDistance;
                    if (nextX < 0)
                        nextX = 0;
                    character.setLayoutX(nextX);
                    currentDirection = "LEFT";
                    if (!lastDirection.equals("LEFT")) {
                        updateCharacterImage("LEFT");
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
                        updateCharacterImage("RIGHT");
                    }
                }
                // 위쪽 이동 (W, UP) - 바닥 영역으로 제한
                if (pressedKeys.contains("W") || pressedKeys.contains("UP")) {
                    double nextY = y - moveDistance;
                    if (nextY < floorTopY)  // 바닥 위쪽 경계 제한
                        nextY = floorTopY;
                    character.setLayoutY(nextY);
                    currentDirection = "UP";
                    if (!lastDirection.equals("UP")) {
                        updateCharacterImage("UP");
                    }
                }
                // 아래쪽 이동 (S, DOWN) - 바닥 영역으로 제한
                if (pressedKeys.contains("S") || pressedKeys.contains("DOWN")) {
                    double nextY = y + moveDistance;
                    if (nextY > floorBottomY)  // 바닥 하단 경계 제한
                        nextY = floorBottomY;
                    character.setLayoutY(nextY);
                    currentDirection = "DOWN";
                    if (!lastDirection.equals("DOWN")) {
                        updateCharacterImage("DOWN");
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
    
    private void updateCharacterImage(String direction) {
        try {
            String imagePath = getUserCharacterImagePath(direction);
            Image newImage = new Image(getClass().getResource(imagePath).toExternalForm());
            character.setImage(newImage);
            // System.out.println("캐릭터 이미지 변경 성공: " + imagePath);
        } catch (Exception e) {
            // System.out.println("캐릭터 이미지를 변경할 수 없습니다: " + e.getMessage());
            // 기본 이미지로 폴백
            try {
                String defaultPath = "/allday/minico/images/char/male/대호_front.png";
                Image defaultImage = new Image(getClass().getResource(defaultPath).toExternalForm());
                character.setImage(defaultImage);
                // System.out.println("기본 이미지로 변경됨: " + defaultPath);
            } catch (Exception fallbackException) {
                // System.out.println("기본 이미지도 로드할 수 없습니다: " + fallbackException.getMessage());
            }
        }
    }
    
    private String getUserCharacterImagePath(String direction) {
        try {
            String memberId = AppSession.getLoginMember().getMemberId();
            
            if (memberId != null) {
                // 캐싱된 최적화 메서드 사용 - DB 조회를 최소화
                return allday.minico.utils.skin.SkinUtil.getCharacterImagePath(memberId, direction);
            }
        } catch (Exception e) {
            System.out.println("[MovementController] 캐릭터 이미지 경로 생성 실패: " + e.getMessage());
        }
        
        // 정보가 없으면 기본 캐릭터 사용 - SkinUtil의 메서드 활용
        return allday.minico.utils.skin.SkinUtil.getCharacterImagePath("default", direction);
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
