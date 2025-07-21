package allday.minico.ui.common;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.geometry.Pos;
import javafx.scene.text.TextAlignment;

/**
 * 사용자 정의 알림 모달 - 통일된 UI 시스템
 */
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
        // 오버레이 배경
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setPrefSize(parentPane.getWidth(), parentPane.getHeight());

        // 모달 박스 크기 조정 및 CSS 적용
        VBox modalBox = new VBox(20);
        modalBox.getStyleClass().add("custom-alert-modal");
        // 동적 border-color 적용
        modalBox.setStyle("-fx-border-color: " + type.getColor() + ";");
        // CSS 파일 적용
        try {
            // custom-alert.css 파일이 없으므로 주석 처리
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

        // 중앙 정렬
        javafx.application.Platform.runLater(() -> {
            double centerX = (parentPane.getWidth() - modalWidth) / 2;
            double centerY = (parentPane.getHeight() - modalHeight) / 2;
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
                parentPane.getChildren().remove(overlay);
                onConfirm.run();
            });

            Button cancelBtn = new Button("취소");
            cancelBtn.getStyleClass().addAll("custom-alert-btn", "custom-alert-btn-cancel");
            cancelBtn.setOnAction(e -> parentPane.getChildren().remove(overlay));

            buttonBox.getChildren().addAll(confirmBtn, cancelBtn);
        } else {
            // 일반 알림인 경우 확인 버튼만
            Button okBtn = new Button("확인");
            okBtn.getStyleClass().addAll("custom-alert-btn", "custom-alert-btn-confirm");
            okBtn.setOnAction(e -> parentPane.getChildren().remove(overlay));

            buttonBox.getChildren().add(okBtn);
        }
        
        modalBox.getChildren().addAll(titleSection, messageLabel, buttonBox);
        overlay.getChildren().add(modalBox);
        
        // ESC로 닫기
        overlay.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) {
                parentPane.getChildren().remove(overlay);
            }
        });
        overlay.setFocusTraversable(true);
        
        parentPane.getChildren().add(overlay);
        javafx.application.Platform.runLater(() -> {
            overlay.requestFocus();
            // 첫 번째 버튼에 포커스
            if (!buttonBox.getChildren().isEmpty() && buttonBox.getChildren().get(0) instanceof Button) {
                ((Button) buttonBox.getChildren().get(0)).requestFocus();
            }
        });
    }
}
