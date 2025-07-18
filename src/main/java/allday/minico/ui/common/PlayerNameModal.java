package allday.minico.ui.common;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

/**
 * 플레이어 이름 입력 모달 오버레이
 */
public class PlayerNameModal {
    /**
     * 오버레이 모달을 생성하고, 이름 입력 완료 시 콜백을 호출합니다.
     * @param roomPane 오버레이를 띄울 Pane
     * @param defaultName 기본 이름 (null 가능)
     * @param onNameSet 이름이 입력되면 호출되는 콜백 (String name)
     */
    public static void show(Pane roomPane, String defaultName, java.util.function.Consumer<String> onNameSet) {
        // 오버레이 배경
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setPrefSize(roomPane.getWidth(), roomPane.getHeight());

        // 모달 박스 크기 조정
        VBox modalBox = new VBox(16);
        modalBox.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2; -fx-background-radius: 16; -fx-border-radius: 16; -fx-padding: 24; -fx-alignment: center;");
        double modalWidth = 380;
        double modalHeight = 220;
        modalBox.setMaxWidth(modalWidth);
        modalBox.setMaxHeight(modalHeight);
        modalBox.setPrefWidth(modalWidth);
        modalBox.setPrefHeight(modalHeight);
        
        // 중앙 정렬을 위한 계산 개선
        javafx.application.Platform.runLater(() -> {
            double centerX = (roomPane.getWidth() - modalWidth) / 2;
            double centerY = (roomPane.getHeight() - modalHeight) / 2;
            modalBox.setLayoutX(Math.max(0, centerX));
            modalBox.setLayoutY(Math.max(0, centerY));
        });

        Label title = new Label("플레이어 이름 설정");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label desc = new Label("미니룸에서 사용할 이름을 입력하세요");
        desc.setStyle("-fx-font-size: 14px;");

        HBox inputRow = new HBox(8);
        Label nameLabel = new Label("이름:");
        nameLabel.setStyle(" -fx-font-size: 15px;");
        TextField nameField = new TextField();
        nameField.setText(defaultName != null ? defaultName : ("Player" + (System.currentTimeMillis() % 1000)));
        nameField.setPrefWidth(160);
        inputRow.getChildren().addAll(nameLabel, nameField);
        inputRow.setAlignment(Pos.CENTER);

        Button okBtn = new Button("확인");
        okBtn.setStyle("-fx-font-size: 15px; -fx-background-color: #acff07ff; -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12;");
        okBtn.setOnAction(e -> {
            String input = nameField.getText().trim();
            String result = input.isEmpty() ? ("Player" + (System.currentTimeMillis() % 1000)) : input;
            roomPane.getChildren().remove(overlay);
            if (onNameSet != null) onNameSet.accept(result);
        });

        modalBox.getChildren().addAll(title, desc, inputRow, okBtn);
        overlay.getChildren().add(modalBox);

        // ESC로 닫기
        overlay.setOnKeyPressed(ev -> {
            if (ev.getCode() == javafx.scene.input.KeyCode.ESCAPE) roomPane.getChildren().remove(overlay);
        });
        overlay.setFocusTraversable(true);

        roomPane.getChildren().add(overlay);
        javafx.application.Platform.runLater(nameField::requestFocus);
    }
}
