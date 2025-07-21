package allday.minico.controller.miniroom;

import allday.minico.controller.oxgame.OxGameSettingController;
import allday.minico.utils.member.SceneManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
// import javafx.scene.control.DialogPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import allday.minico.network.RoomNetworkManager;
import allday.minico.ui.common.CustomAlert;
import allday.minico.utils.audio.ButtonSoundHandler;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class MiniroomController implements Initializable {

    @FXML
    private Pane roomPane;
    @FXML
    private VBox menuButtons;
    @FXML
    private Button guestbookBtn;
    @FXML
    private Button visitBtn;
    @FXML
    private Button diaryBtn;
    @FXML
    private Button typingBtn;
    @FXML
    private Button oxBtn;
    @FXML
    private Button friendsButton;

    @FXML
    private void friendsButtonClick(){
        SceneManager.showModal("FriendInfo", "친구 찾기");
    }



    private ImageView character;
    private CharacterMovementController movementController;

    // 네트워크 관련 변수
    private RoomNetworkManager networkManager;
    private String playerName = "Player1";
    private boolean isHosting = false;
    private boolean isVisiting = false;

    // 방문자 모드에서 호스트 캐릭터 표시용
    private ImageView hostCharacter;
    private String hostName = null; // 호스트의 실제 이름 저장

    // 캐릭터/이름표 관리
    private java.util.Map<String, ImageView> visitorCharacters = new java.util.HashMap<>();
    private java.util.Map<String, Text> characterNameLabels = new java.util.HashMap<>();
    private allday.minico.ui.miniroom.CharacterManager characterManager;

    // 채팅 말풍선 관리
    private java.util.Map<String, javafx.scene.Group> chatBubbles = new java.util.HashMap<>();

    private ChatManager chatManager;
    private allday.minico.ui.miniroom.ChatInputManager chatInputManager;
    private allday.minico.ui.miniroom.UIInitializer uiInitializer;
    private allday.minico.ui.miniroom.CharacterInitializer characterInitializer;
    private allday.minico.ui.common.LoadingSpinner loadingSpinner;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // UI 초기화
        uiInitializer = new allday.minico.ui.miniroom.UIInitializer(roomPane);
        uiInitializer.initializeCSS();
        uiInitializer.setupRoomPaneFocus();

        // 캐릭터/이름표 매니저 생성
        characterManager = new allday.minico.ui.miniroom.CharacterManager(roomPane, visitorCharacters,
                characterNameLabels);

        // 로딩 스피너 초기화
        loadingSpinner = new allday.minico.ui.common.LoadingSpinner(roomPane);

        // 캐릭터 초기화
        characterInitializer = new allday.minico.ui.miniroom.CharacterInitializer(roomPane);
        characterInitializer.initializeCharacter(this::onCharacterInitialized);

        // 채팅 관리자 초기화
        chatManager = new ChatManager(roomPane);

        // 채팅 입력 관리자 초기화
        chatInputManager = new allday.minico.ui.miniroom.ChatInputManager(roomPane,
                new allday.minico.ui.miniroom.ChatInputManager.ChatInputCallback() {
                    @Override
                    public void onChatMessage(String message) {
                        handleChatMessage(message);
                    }

                    @Override
                    public boolean isHosting() {
                        return MiniroomController.this.isHosting;
                    }

                    @Override
                    public boolean isVisiting() {
                        return MiniroomController.this.isVisiting;
                    }

                    @Override
                    public String getPlayerName() {
                        return playerName;
                    }
                });

        // 플레이어 이름 입력받기
        javafx.application.Platform.runLater(() -> {
            this.setupPlayerName();

            // 미니룸의 모든 버튼에 클릭 효과음 추가
            if (roomPane.getScene() != null) {
                ButtonSoundHandler.addButtonSounds(roomPane.getScene());
            }
        });
    }

    private void onCharacterInitialized(ImageView character) {
        this.character = character;
        initializeMovementController();
    }

    private void handleChatMessage(String message) {
        if (isHosting) {
            // 호스트는 sendChatMessage를 사용하여 모든 클라이언트에게 메시지 전송
            // 호스트 자신에게는 onChatMessage 콜백을 통해 말풍선이 표시됨
            if (networkManager != null) {
                networkManager.sendChatMessage(message);
            }
        } else if (isVisiting) {
            // 방문자는 자신의 화면에 먼저 말풍선 표시
            showChatBubble(playerName, message);
            // 그 다음 서버에게 메시지 전송
            if (networkManager != null) {
                networkManager.sendChatMessage(message);
            }
        }
    }

    private void setupPlayerName() {
        // AppSession에서 로그인한 사용자의 닉네임 가져오기
        String nickname = allday.minico.session.AppSession.getPlayerNickname();

        if (nickname != null && !nickname.trim().isEmpty()) {
            playerName = nickname.trim();
        } else {
            // 세션에 닉네임이 없는 경우 기본값 설정
            playerName = "Player" + (System.currentTimeMillis() % 1000);
        }

        System.out.println("플레이어 이름 설정됨: " + playerName);

        // 플레이어 이름이 설정된 후 네트워크 매니저 초기화
        initializeNetworkManager();
    }

    private void initializeNetworkManager() {
        networkManager = new RoomNetworkManager(playerName, characterManager, character,
                new RoomNetworkManager.NetworkCallback() {
                    @Override
                    public void onHostCharacterCreate(double x, double y, String direction) {
                        createHostCharacter(x, y, direction);
                    }

                    @Override
                    public void onHostCharacterUpdate(double x, double y, String direction) {
                        updateHostCharacter(x, y, direction);
                    }

                    @Override
                    public void onVisitorCharacterCreate(String visitorName, double x, double y, String direction) {
                        createVisitorCharacter(visitorName, x, y, direction);
                    }

                    @Override
                    public void onVisitorCharacterUpdate(String visitorName, double x, double y, String direction) {
                        updateVisitorCharacter(visitorName, x, y, direction);
                    }

                    @Override
                    public void onCharacterRemove(String characterName) {
                        removeCharacterAndLabels(characterName);
                    }

                    @Override
                    public void onChatMessage(String senderName, String message) {
                        showChatBubble(senderName, message);
                    }

                    @Override
                    public void onHostingStatusChanged(boolean isHosting) {
                        MiniroomController.this.isHosting = isHosting;
                        // 네트워크 기능 제거됨
                    }

                    @Override
                    public void onVisitingStatusChanged(boolean isVisiting) {
                        MiniroomController.this.isVisiting = isVisiting;
                        System.out.println("방문 상태 변경: " + isVisiting); // 디버깅용 로그 추가
                        // 네트워크 기능 제거됨
                    }

                    @Override
                    public void setHostName(String hostName) {
                        MiniroomController.this.hostName = hostName;
                    }

                    @Override
                    public javafx.scene.layout.Pane getParentPane() {
                        return roomPane;
                    }
                });
    }

    private void initializeMovementController() {
        movementController = new CharacterMovementController(roomPane, character,
                new CharacterMovementController.MovementCallback() {
                    @Override
                    public void onCharacterMove(double x, double y, String direction) {
                        if (networkManager != null) {
                            networkManager.updateCharacterPosition(x, y, direction);
                        }
                    }

                    @Override
                    public void onNameLabelUpdate(Text nameLabel, ImageView character) {
                        updateNameLabelPosition(nameLabel, character);
                    }

                    @Override
                    public String getPlayerName() {
                        return playerName;
                    }

                    @Override
                    public java.util.Map<String, Text> getCharacterNameLabels() {
                        return characterNameLabels;
                    }
                });
    }

    private void createHostCharacter(double x, double y, String direction) {
        hostCharacter = characterManager.createHostCharacter(hostName, x, y, direction);
        System.out.println("호스트 캐릭터 생성: X=" + x + ", Y=" + y + ", 방향=" + direction + ", 호스트명=" + hostName);
    }

    private void updateHostCharacter(double x, double y, String direction) {
        if (hostCharacter == null) {
            createHostCharacter(x, y, direction);
            return;
        }
        characterManager.updateHostCharacter(hostCharacter, hostName, x, y, direction);
    }

    private void updateVisitorCharacter(String visitorName, double x, double y, String direction) {
        characterManager.updateVisitorCharacter(playerName, visitorName, x, y, direction, isHosting);
    }

    private void createVisitorCharacter(String visitorName, double x, double y, String direction) {
        characterManager.createVisitorCharacter(visitorName, x, y, direction, isHosting);
    }

    public void showChatBubble(String senderName, String message) {
        ImageView targetCharacter = null;

        // 메시지를 보낸 캐릭터 찾기
        if (senderName.equals(playerName)) {
            // 자신이 보낸 메시지
            targetCharacter = character;
        } else if (isVisiting && hostName != null && senderName.equals(hostName)) {
            // 방문자 모드에서 호스트가 메시지를 보낸 경우 (실제 호스트 이름과 비교)
            targetCharacter = hostCharacter;
        } else {
            // 다른 방문자가 메시지를 보낸 경우
            targetCharacter = visitorCharacters.get(senderName);
        }

        // 디버그 출력 (임시)
        System.out.println("채팅 메시지 - 발신자: " + senderName + ", 플레이어: " + playerName +
                ", 호스트명: " + hostName + ", 호스팅: " + isHosting + ", 방문: " + isVisiting +
                ", 타겟 캐릭터: " + (targetCharacter != null ? "찾음" : "null"));

        chatManager.showChatBubble(senderName, message, targetCharacter);
    }

    private void updateNameLabelPosition(Text nameLabel, ImageView character) {
        characterManager.updateNameLabelPosition(nameLabel, character);
    }

    private void removeCharacterAndLabels(String characterName) {
        // 특별한 신호를 받으면 모든 캐릭터 제거
        if ("__REMOVE_ALL__".equals(characterName)) {
            System.out.println("__REMOVE_ALL__ 신호 수신 - 모든 캐릭터 제거 시작");

            // 호스트 캐릭터 제거 (방문 모드에서 볼 수 있는 호스트)
            if (hostCharacter != null) {
                System.out.println("호스트 캐릭터 제거: " + hostName);
                characterManager.removeHostCharacter(hostCharacter, hostName);
                hostCharacter = null;
            }

            // 모든 방문자 캐릭터들 제거
            System.out.println("모든 방문자 캐릭터 제거");
            characterManager.removeAllVisitorCharacters();

            // 모든 말풍선 제거 (자신의 말풍선 제외)
            System.out.println("다른 플레이어 말풍선 제거");
            for (String name : chatBubbles.keySet()) {
                if (!name.equals(playerName)) {
                    javafx.scene.Group bubble = chatBubbles.get(name);
                    if (bubble != null) {
                        roomPane.getChildren().remove(bubble);
                    }
                }
            }
            // 자신의 말풍선을 제외한 나머지 제거
            chatBubbles.entrySet().removeIf(entry -> !entry.getKey().equals(playerName));

            // 캐릭터가 사라진 즉시 스피너 숨김
            loadingSpinner.hide();

            System.out.println("__REMOVE_ALL__ 신호 처리 완료");
            return;
        }

        // 일반적인 개별 캐릭터 제거
        characterManager.removeCharacterAndLabels(characterName);

        // 말풍선 제거는 기존대로 유지
        javafx.scene.Group bubble = chatBubbles.get(characterName);
        if (bubble != null) {
            roomPane.getChildren().remove(bubble);
            chatBubbles.remove(characterName);
        }
    }

    public void clearChatInput() {
        if (chatInputManager != null) {
            chatInputManager.clearInput();
        }
    }

    public void setChatInputVisible(boolean visible) {
        if (chatInputManager != null) {
            chatInputManager.setVisible(visible);
        }
    }

    public void appendTextToChatInput(String text) {
        if (chatInputManager != null) {
            chatInputManager.appendText(text);
        }
    }

    public void setChatInputText(String text) {
        if (chatInputManager != null) {
            chatInputManager.setText(text);
        }
    }

    @FXML
    protected void onGuestbookClick() {
        System.out.println("게시판 버튼 클릭");
        // 게시판 기능 구현
    }

    @FXML
    private void onVisitClick() {
        System.out.println("방문 버튼 클릭됨");

        try {
            if (isVisiting) {
                // 현재 방문 중이면 나가기 - 로딩 스피너 표시
                System.out.println("현재 방문 중 - 방 나가기 시도");
                loadingSpinner.show();

                // 백그라운드에서 방 나가기 처리
                Thread leaveThread = new Thread(() -> {
                    try {
                        networkManager.leaveRoom();
                    } catch (Exception e) {
                        System.out.println("방 나가기 처리 중 오류: " + e.getMessage());
                        e.printStackTrace();

                        Platform.runLater(() -> {
                            loadingSpinner.hide();
                            CustomAlert.showError(roomPane, "오류",
                                    "방 나가기 중 오류가 발생했습니다: " + e.getMessage());
                        });
                    } finally {
                        Platform.runLater(() -> {
                            loadingSpinner.hide();
                        });
                    }
                });
                leaveThread.setDaemon(true);
                leaveThread.start();

            } else if (isHosting) {
                // 호스팅 중에는 다른 방을 방문할 수 없음
                CustomAlert.showWarning(roomPane, "경고",
                        "현재 서버를 호스팅 중입니다. 먼저 호스팅을 중지해주세요.");
            } else {
                // 사용 가능한 방 목록 표시
                System.out.println("방 선택 다이얼로그 표시");
                networkManager.showRoomSelectionDialog();
            }
        } catch (Exception e) {
            loadingSpinner.hide();
            System.out.println("방문 버튼 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();

            // 오류 발생 시 사용자에게 알림
            CustomAlert.showError(roomPane, "오류",
                    "방문 처리 중 오류가 발생했습니다. 다시 시도해주세요.");
        }
    }

    @FXML
    protected void onDiaryClick() {
        System.out.println("다이어리 버튼 클릭");
        // 다이어리 기능 구현
    }

    @FXML
    protected void onTypingClick() {
        System.out.println("타자게임 버튼 클릭");
        // 타자게임 기능 구현
        try {
            Parent gameRoot = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/allday/minico/view/typinggame/typing_game.fxml")
            ));

            // 현재 Stage와 Scene 가져오기
            Stage stage = (Stage) typingBtn.getScene().getWindow();
            Scene scene = stage.getScene();

            // Root 교체
            scene.setRoot(gameRoot);

            // ✅ 타자게임 CSS 적용
            scene.getStylesheets().clear(); // 기존 main.css 제거
            scene.getStylesheets().add(getClass().getResource("/allday/minico/css/typinggame.css").toExternalForm());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onOxClick(ActionEvent event) {
        System.out.println("OX게임 버튼 클릭");
        try {
            FXMLLoader oxGameRoot = new FXMLLoader(getClass().getResource("/allday/minico/view/oxgame/ox-setting.fxml"));
            Parent root = oxGameRoot.load();

            OxGameSettingController controller = oxGameRoot.getController();
            Scene oxScene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(oxScene);

            // layout 강제 적용 (전체화면 안 가도 정상 동작하게)
            Platform.runLater(() -> {
                root.applyCss();
                root.layout();
            });

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cleanup() {
        // 이동 컨트롤러 정리
        if (movementController != null) {
            movementController.cleanup();
        }

        // 네트워크 매니저 정리
        if (networkManager != null) {
            networkManager.cleanup();
        }

        // 채팅 입력 관리자 정리
        if (chatInputManager != null) {
            chatInputManager.cleanup();
        }
    }
}
