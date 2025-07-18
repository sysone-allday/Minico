package allday.minico.ui.common;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LoadingSpinner {
    private StackPane overlay;
    private ProgressIndicator spinner;
    private Pane parentPane;
    
    public LoadingSpinner(Pane parentPane) {
        this.parentPane = parentPane;
        createSpinner();
    }
    
    private void createSpinner() {
        // 반투명 배경
        Rectangle background = new Rectangle();
        background.setFill(Color.BLACK);
        background.setOpacity(0.3);
        background.widthProperty().bind(parentPane.widthProperty());
        background.heightProperty().bind(parentPane.heightProperty());
        
        // 스피너
        spinner = new ProgressIndicator();
        spinner.setMaxSize(60, 60);
        spinner.setStyle("-fx-progress-color: #b2fbb5ff;");
        
        // 오버레이 컨테이너
        overlay = new StackPane();
        overlay.getChildren().addAll(background, spinner);
        overlay.setVisible(false);
    }
    
    public void show() {
        Platform.runLater(() -> {
            if (!parentPane.getChildren().contains(overlay)) {
                parentPane.getChildren().add(overlay);
            }
            overlay.setVisible(true);
            overlay.toFront();
        });
    }
    
    public void hide() {
        Platform.runLater(() -> {
            overlay.setVisible(false);
            parentPane.getChildren().remove(overlay);
        });
    }
    
    public void showWithMessage(String message) {
        // 필요시 메시지와 함께 표시할 수 있도록 확장 가능
        show();
    }
}
