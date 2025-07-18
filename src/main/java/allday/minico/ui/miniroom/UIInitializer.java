package allday.minico.ui.miniroom;

import javafx.scene.layout.Pane;

public class UIInitializer {
    
    private final Pane roomPane;
    
    public UIInitializer(Pane roomPane) {
        this.roomPane = roomPane;
    }
    
    public void initializeCSS() {
        javafx.application.Platform.runLater(() -> {
            try {
                if (roomPane.getScene() != null) {
                    String cssPath = getClass().getResource("/sysone/sysonefirst/css/main.css")
                            .toExternalForm();
                    roomPane.getScene().getStylesheets().add(cssPath);

                    String chatCssPath = getClass().getResource("/sysone/sysonefirst/css/chat.css")
                            .toExternalForm();
                    roomPane.getScene().getStylesheets().add(chatCssPath);
                }
            } catch (Exception e) {
                System.out.println("CSS 파일을 로드할 수 없습니다: " + e.getMessage());
            }
        });
    }
    
    public void setupRoomPaneFocus() {
        // roomPane 클릭 시 포커스 이동
        roomPane.setOnMouseClicked(event -> roomPane.requestFocus());
    }
}
