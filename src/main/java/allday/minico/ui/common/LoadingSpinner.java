package allday.minico.ui.common;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class LoadingSpinner {
    private StackPane spinnerContainer;
    private ProgressIndicator spinner;
    private Pane parentPane;
    
    public LoadingSpinner(Pane parentPane) {
        this.parentPane = parentPane;
        createSpinner();
    }
    
    private void createSpinner() {
        // 스피너만 생성 (오버레이 배경 제거)
        spinner = new ProgressIndicator();
        spinner.setMaxSize(60, 60);
        spinner.setStyle("-fx-progress-color: #b2fbb5ff;");
        
        // 스피너 컨테이너 (배경 없이)
        spinnerContainer = new StackPane();
        spinnerContainer.getChildren().add(spinner);
        spinnerContainer.setVisible(false);
        
        // 스피너를 중앙에 위치시키기
        spinnerContainer.setLayoutX(0);
        spinnerContainer.setLayoutY(0);
    }
    
    public void show() {
        Platform.runLater(() -> {
            // 스피너를 부모 패널의 중앙에 위치시키기
            double centerX = (parentPane.getWidth() - 60) / 2;
            double centerY = (parentPane.getHeight() - 60) / 2;
            
            spinnerContainer.setLayoutX(Math.max(0, centerX));
            spinnerContainer.setLayoutY(Math.max(0, centerY));
            
            if (!parentPane.getChildren().contains(spinnerContainer)) {
                parentPane.getChildren().add(spinnerContainer);
            }
            spinnerContainer.setVisible(true);
            spinnerContainer.toFront();
        });
    }
    
    public void hide() {
        Platform.runLater(() -> {
            spinnerContainer.setVisible(false);
            parentPane.getChildren().remove(spinnerContainer);
        });
    }
    
    public void showWithMessage(String message) {
        show();
    }
}
