package allday.minico.ui.common;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;


//사용자 정의 알림 모달 - 통일된 UI 시스템

public class CustomAlert {
    
    public enum AlertType {
        INFORMATION("#4CAF50", "정보"),
        WARNING("#FF9800", "경고"),
        ERROR("#F44336", "오류"),
        CONFIRMATION("#2196F3", "확인");
        
        private final String color;
        private final String title;
        
        AlertType(String color, String title) {
            this.color = color;
            this.title = title;
        }
        
        public String getColor() { return color; }
        public String getTitle() { return title; }
    }
    
    // 정보
    public static void showInformation(Pane parentPane, String title, String message) {
        show(parentPane, AlertType.INFORMATION, title, message, null);
    }
    
// 경고
    public static void showWarning(Pane parentPane, String title, String message) {
        show(parentPane, AlertType.WARNING, title, message, null);
    }
    
//  오류
    public static void showError(Pane parentPane, String title, String message) {
        show(parentPane, AlertType.ERROR, title, message, null);
    }
    
// 확인
    public static void showConfirmation(Pane parentPane, String title, String message, 
                                       Runnable onConfirm) {
        show(parentPane, AlertType.CONFIRMATION, title, message, onConfirm);
    }

    //공용
    private static void show(Pane parentPane, AlertType type, String title, String message, 
                            Runnable onConfirm) {
        // Scene의 root를 찾기
        javafx.scene.Parent sceneRoot = parentPane.getScene().getRoot();
        
        // 오버레이 배경 - 화면 전체를 덮도록 설정
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        
        // Stage의 실제 크기를 가져와서 화면 전체를 덮도록 설정
        final double[] stageDimensions = new double[2]; // [width, height]
        try {
            javafx.stage.Stage stage = (javafx.stage.Stage) parentPane.getScene().getWindow();
            stageDimensions[0] = stage.getWidth();
            stageDimensions[1] = stage.getHeight();
        } catch (Exception e) {
            // Stage를 가져올 수 없는 경우 Scene 크기 사용
            stageDimensions[0] = parentPane.getScene().getWidth();
            stageDimensions[1] = parentPane.getScene().getHeight();
        }
        
        overlay.setPrefSize(stageDimensions[0], stageDimensions[1]);
        overlay.setLayoutX(0);
        overlay.setLayoutY(0);

        // 모달 박스 크기 조정 및 CSS 적용
        VBox modalBox = new VBox(20);
        modalBox.getStyleClass().add("custom-alert-modal");
        // 동적 border-color 적용
        modalBox.setStyle("-fx-border-color: " + type.getColor() + ";");
        // CSS 파일 적용
        try {
            String css = CustomAlert.class.getResource("/allday/minico/css/custom-alert.css").toExternalForm();
            modalBox.getStylesheets().add(css);
        } catch (Exception e) {
        }
        // 크기 설정
        double modalWidth = 450;
        double modalHeight = 250;
        modalBox.setMaxWidth(modalWidth);
        modalBox.setMaxHeight(modalHeight);
        modalBox.setPrefWidth(modalWidth);
        modalBox.setPrefHeight(modalHeight);

        // 중앙 정렬 - Stage 크기 기준으로 계산
        javafx.application.Platform.runLater(() -> {
            double centerX = (stageDimensions[0] - modalWidth) / 2;
            double centerY = (stageDimensions[1] - modalHeight) / 2;
            modalBox.setLayoutX(Math.max(0, centerX));
            modalBox.setLayoutY(Math.max(0, centerY));
        });
        
        // 타이틀 영역
        VBox titleSection = new VBox(8);
        titleSection.getStyleClass().add("custom-alert-title-section");
        titleSection.setAlignment(Pos.CENTER);

        // 타입 라벨 (정보, 경고, 오류 등)
        Label typeLabel = new Label(type.getTitle());
        switch (type) {
            case INFORMATION:
                typeLabel.getStyleClass().add("custom-alert-type-info");
                break;
            case WARNING:
                typeLabel.getStyleClass().add("custom-alert-type-warn");
                break;
            case ERROR:
                typeLabel.getStyleClass().add("custom-alert-type-error");
                break;
            case CONFIRMATION:
                typeLabel.getStyleClass().add("custom-alert-type-confirm");
                break;
        }

        // 제목 라벨
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("custom-alert-title");

        titleSection.getChildren().addAll(typeLabel, titleLabel);
        
        // 메시지 라벨
        Label messageLabel = new Label(message);
        messageLabel.getStyleClass().add("custom-alert-message");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(350);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setAlignment(Pos.CENTER);
        
        // 버튼 영역
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);

        if (onConfirm != null) {
            // 확인 모달인 경우 확인/취소 버튼
            Button confirmBtn = new Button("확인");
            confirmBtn.getStyleClass().addAll("custom-alert-btn", "custom-alert-btn-confirm");
            confirmBtn.setOnAction(e -> {
                removeOverlayFromScene(sceneRoot, overlay);
                onConfirm.run();
            });

            Button cancelBtn = new Button("취소");
            cancelBtn.getStyleClass().addAll("custom-alert-btn", "custom-alert-btn-cancel");
            cancelBtn.setOnAction(e -> removeOverlayFromScene(sceneRoot, overlay));

            buttonBox.getChildren().addAll(confirmBtn, cancelBtn);
        } else {
            // 일반 알림인 경우 확인 버튼만
            Button okBtn = new Button("확인");
            okBtn.getStyleClass().addAll("custom-alert-btn", "custom-alert-btn-confirm");
            okBtn.setOnAction(e -> removeOverlayFromScene(sceneRoot, overlay));

            buttonBox.getChildren().add(okBtn);
        }
        
        modalBox.getChildren().addAll(titleSection, messageLabel, buttonBox);
        overlay.getChildren().add(modalBox);
        
        // ESC로 닫기
        overlay.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                removeOverlayFromScene(sceneRoot, overlay);
            }
        });
        overlay.setFocusTraversable(true);
        
        addOverlayToScene(sceneRoot, overlay);
        javafx.application.Platform.runLater(() -> {
            overlay.requestFocus();
            // 첫 번째 버튼에 포커스
            if (!buttonBox.getChildren().isEmpty() && buttonBox.getChildren().get(0) instanceof Button) {
                ((Button) buttonBox.getChildren().get(0)).requestFocus();
            }
        });
    }
    
    // Scene root에 오버레이를 추가하는 헬퍼 메서드
    private static void addOverlayToScene(javafx.scene.Parent sceneRoot, Pane overlay) {
        if (sceneRoot instanceof javafx.scene.layout.BorderPane) {
            javafx.scene.layout.BorderPane borderPane = (javafx.scene.layout.BorderPane) sceneRoot;
            // BorderPane에 전체 화면을 덮는 오버레이 추가
            javafx.scene.layout.StackPane stackPane = new javafx.scene.layout.StackPane();
            stackPane.getChildren().addAll(borderPane.getCenter(), overlay);
            borderPane.setCenter(stackPane);
        } else if (sceneRoot instanceof Pane) {
            ((Pane) sceneRoot).getChildren().add(overlay);
        }
    }
    
    // Scene root에서 오버레이를 제거하는 헬퍼 메서드
    private static void removeOverlayFromScene(javafx.scene.Parent sceneRoot, Pane overlay) {
        if (sceneRoot instanceof javafx.scene.layout.BorderPane) {
            javafx.scene.layout.BorderPane borderPane = (javafx.scene.layout.BorderPane) sceneRoot;
            javafx.scene.Node center = borderPane.getCenter();
            if (center instanceof javafx.scene.layout.StackPane) {
                javafx.scene.layout.StackPane stackPane = (javafx.scene.layout.StackPane) center;
                if (stackPane.getChildren().size() > 1) {
                    javafx.scene.Node originalCenter = stackPane.getChildren().get(0);
                    borderPane.setCenter(originalCenter);
                }
            }
        } else if (sceneRoot instanceof Pane) {
            ((Pane) sceneRoot).getChildren().remove(overlay);
        }
    }
}
