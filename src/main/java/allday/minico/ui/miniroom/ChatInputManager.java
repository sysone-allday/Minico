package allday.minico.ui.miniroom;

import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;

public class ChatInputManager {
    
    private final Pane roomPane;
    private TextField chatInput;
    
    public interface ChatInputCallback {
        void onChatMessage(String message);
        boolean isHosting();
        boolean isVisiting();
        String getPlayerName();
    }
    
    private ChatInputCallback callback;
    
    public ChatInputManager(Pane roomPane, ChatInputCallback callback) {
        this.roomPane = roomPane;
        this.callback = callback;
        
        setupChatInput();
    }
    
    private void setupChatInput() {
        chatInput = new TextField();
        chatInput.setPromptText("채팅을 입력하세요.. (Enter 전송)");
        chatInput.setPrefWidth(300);
        chatInput.setLayoutX(10);
        chatInput.setLayoutY(roomPane.getPrefHeight() - 40);
        chatInput.getStyleClass().add("chat-input");

        chatInput.setOnAction(event -> {
            String message = chatInput.getText().trim();
            if (!message.isEmpty()) {
                callback.onChatMessage(message);
                chatInput.clear();
                // 채팅 입력 후에도 포커스 유지
                chatInput.requestFocus();
            }
        });

        roomPane.getChildren().add(chatInput);

        // roomPane 클릭 시에만 포커스가 roomPane으로 이동
        roomPane.setOnMouseClicked(event -> {
            roomPane.requestFocus();
        });
    }
    
    public void focusInput() {
        if (chatInput != null) {
            // 즉시 포커스 요청
            chatInput.requestFocus();
            // 다시 한번 Platform.runLater로 확실히 포커스 설정
            javafx.application.Platform.runLater(() -> {
                chatInput.requestFocus();
            });
        }
    }
    
    public void clearInput() {
        if (chatInput != null) {
            chatInput.clear();
        }
    }
    
    public void setVisible(boolean visible) {
        if (chatInput != null) {
            chatInput.setVisible(visible);
        }
    }
    
    public void updatePosition() {
        if (chatInput != null) {
            chatInput.setLayoutY(roomPane.getPrefHeight() - 40);
        }
    }
    
    public void cleanup() {
        // 이벤트 리스너 정리
        if (chatInput != null) {
            chatInput.setOnAction(null);
        }
        
        // 필요시 추가 정리 작업
    }
    
    public void appendText(String text) {
        if (chatInput != null) {
            String currentText = chatInput.getText();
            chatInput.setText(currentText + text);
            chatInput.positionCaret(chatInput.getText().length());
        }
    }
    
    public void setText(String text) {
        if (chatInput != null) {
            chatInput.setText(text);
            chatInput.positionCaret(text.length());
        }
    }
}
