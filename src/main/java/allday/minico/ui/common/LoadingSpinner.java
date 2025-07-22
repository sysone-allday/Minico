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
        spinner = new ProgressIndicator();
        spinner.setMaxSize(60, 60);
        spinner.setStyle("-fx-progress-color: #b2fbb5ff;");
        
  
        spinnerContainer = new StackPane();
        spinnerContainer.getChildren().add(spinner);
        spinnerContainer.setVisible(false);
        
     
        spinnerContainer.setLayoutX(0);
        spinnerContainer.setLayoutY(0);
    }
    
    public void show() {
        Platform.runLater(() -> {
        
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
