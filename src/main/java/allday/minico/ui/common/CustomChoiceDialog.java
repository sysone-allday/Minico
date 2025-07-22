package allday.minico.ui.common;

import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import allday.minico.network.MiniRoomDiscovery;

import java.util.List;


public class CustomChoiceDialog {
    
    public static void showRoomSelection(Pane parentPane, List<MiniRoomDiscovery.RoomInfo> rooms, 
                                        java.util.function.Consumer<MiniRoomDiscovery.RoomInfo> onRoomSelected) {
        // 오버레이 배경
        Pane overlay = new Pane();
        overlay.setStyle("-fx-background-color: rgba(0,0,0,0.4);");
        overlay.setPrefSize(parentPane.getWidth(), parentPane.getHeight());
        
        // 모달 박스 크기 조정
        VBox modalBox = new VBox(20);
        modalBox.setStyle("-fx-background-color: white; -fx-border-color: #2196F3; " +
                         "-fx-border-width: 2; -fx-background-radius: 16; -fx-border-radius: 16; " +
                         "-fx-padding: 24; -fx-alignment: center;");
        
        double modalWidth = 520;
        double modalHeight = 480;
        modalBox.setMaxWidth(modalWidth);
        modalBox.setMaxHeight(modalHeight);
        modalBox.setPrefWidth(modalWidth);
        modalBox.setPrefHeight(modalHeight);
        
        // 중앙 정렬을 위한 계산 개선
        javafx.application.Platform.runLater(() -> {
            double centerX = (parentPane.getWidth() - modalWidth) / 2;
            double centerY = (parentPane.getHeight() - modalHeight) / 2;
            modalBox.setLayoutX(Math.max(0, centerX));
            modalBox.setLayoutY(Math.max(0, centerY));
        });
        
        // 타이틀 영역
        VBox titleSection = new VBox(8);
        titleSection.setAlignment(Pos.CENTER);
        
        Label typeLabel = new Label("방 선택");
        typeLabel.setStyle("-fx-font-size: 14px; " +
                          "-fx-text-fill: #2196F3; -fx-font-weight: bold;");
        
        Label titleLabel = new Label("미니룸 방문");
        titleLabel.setStyle("-fx-font-size: 18px; " +
                           "-fx-font-weight: bold; -fx-text-fill: #333;");
        
        titleSection.getChildren().addAll(typeLabel, titleLabel);
        
        // 방 목록 영역
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color: transparent;" +
                           " -fx-border-radius: 30; -fx-background-radius: 30;");
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(280);
        scrollPane.setPrefHeight(280);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        
        VBox roomList = new VBox(8);
        roomList.setPadding(new Insets(12));
        roomList.setAlignment(Pos.CENTER);
        
        // 방 목록 버튼들 생성
        MiniRoomDiscovery.RoomInfo selectedRoom[] = {null}; // 배열로 래핑하여 final로 사용
        
        for (MiniRoomDiscovery.RoomInfo room : rooms) {
            Button roomButton = new Button();
            roomButton.setMaxWidth(Double.MAX_VALUE);
            roomButton.setPrefHeight(60);
            roomButton.setMinHeight(60);
            
            // 방 정보 텍스트 설정 - IP 주소 대신 닉네임만 표시
            String roomText = String.format("🏠 %s의 미니룸", room.owner);
            roomButton.setText(roomText);
            roomButton.setStyle("-fx-font-size: 14px; " +
                               "-fx-background-color: #f8f9fa; -fx-text-fill: #333; " +
                               "-fx-border-color: #ddd; -fx-border-width: 1; " +
                               "-fx-background-radius: 30; -fx-border-radius: 30; " +
                               "-fx-padding: 8; -fx-cursor: hand; -fx-text-alignment: center;");
            
            // 선택 효과
            roomButton.setOnAction(e -> {
                selectedRoom[0] = room;
                // 모든 버튼 스타일 초기화
                for (javafx.scene.Node node : roomList.getChildren()) {
                    if (node instanceof Button) {
                        ((Button) node).setStyle("-fx-font-size: 12px; " +
                                                "-fx-background-color: #f8f9fa; -fx-text-fill: #333; " +
                                                "-fx-border-color: #ddd; -fx-border-width: 1; " +
                                                "-fx-background-radius: 30; -fx-border-radius: 30; " +
                                                "-fx-padding: 8; -fx-cursor: hand; -fx-text-alignment: center;");
                    }
                }
                // 선택된 버튼 스타일 변경
                roomButton.setStyle("-fx-font-size: 12px; " +
                                   "-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; " +
                                   "-fx-border-color: #FFABBD; -fx-border-width: 2; " +
                                   "-fx-background-radius: 30; -fx-border-radius: 30; " +
                                   "-fx-padding: 8; -fx-cursor: hand; -fx-text-alignment: center;");
            });
            
            // 호버 효과
            roomButton.setOnMouseEntered(e -> {
                if (selectedRoom[0] != room) {
                    roomButton.setStyle("-fx-font-size: 12px; " +
                                       "-fx-background-color: #e8f5e8; -fx-text-fill: #333; " +
                                       "-fx-border-color: #4caf50; -fx-border-width: 1; " +
                                       "-fx-background-radius: 8; -fx-border-radius: 8; " +
                                       "-fx-padding: 8; -fx-cursor: hand; -fx-text-alignment: center;");
                }
            });
            
            roomButton.setOnMouseExited(e -> {
                if (selectedRoom[0] != room) {
                    roomButton.setStyle("-fx-font-size: 12px; " +
                                       "-fx-background-color: #f8f9fa; -fx-text-fill: #333; " +
                                       "-fx-border-color: #ddd; -fx-border-width: 1; " +
                                       "-fx-background-radius: 8; -fx-border-radius: 8; " +
                                       "-fx-padding: 8; -fx-cursor: hand; -fx-text-alignment: center;");
                }
            });
            
            roomList.getChildren().add(roomButton);
        }
        
        scrollPane.setContent(roomList);
        
        // 버튼 영역
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button connectBtn = new Button("접속");
        connectBtn.setStyle("-fx-font-family: 'NeoDunggeunmo'; -fx-font-size: 14px; " +
                           "-fx-background-color: #2196F3; -fx-text-fill: white; " +
                           "-fx-border-color: #2196F3; -fx-border-width: 2; " +
                           "-fx-background-radius: 8; -fx-border-radius: 8; " +
                           "-fx-padding: 8 16 8 16; -fx-cursor: hand;");
        connectBtn.setOnAction(e -> {
            if (selectedRoom[0] != null) {
                parentPane.getChildren().remove(overlay);
                onRoomSelected.accept(selectedRoom[0]);
            }
        });
        
        Button cancelBtn = new Button("취소");
        cancelBtn.setStyle("-fx-font-family: 'NeoDunggeunmo'; -fx-font-size: 14px; " +
                          "-fx-background-color: #f0f0f0; -fx-text-fill: #333; " +
                          "-fx-border-color: #ccc; -fx-border-width: 2; " +
                          "-fx-background-radius: 8; -fx-border-radius: 8; " +
                          "-fx-padding: 8 16 8 16; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> parentPane.getChildren().remove(overlay));
        
        buttonBox.getChildren().addAll(connectBtn, cancelBtn);
        
        // 버튼 호버 효과
        connectBtn.setOnMouseEntered(e -> connectBtn.setStyle(connectBtn.getStyle() + "; -fx-opacity: 0.8;"));
        connectBtn.setOnMouseExited(e -> connectBtn.setStyle(connectBtn.getStyle().replace("; -fx-opacity: 0.8;", "")));
        
        cancelBtn.setOnMouseEntered(e -> cancelBtn.setStyle(cancelBtn.getStyle() + "; -fx-background-color: #e0e0e0;"));
        cancelBtn.setOnMouseExited(e -> cancelBtn.setStyle(cancelBtn.getStyle().replace("; -fx-background-color: #e0e0e0;", "")));
        
        modalBox.getChildren().addAll(titleSection, scrollPane, buttonBox);
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
            if (!roomList.getChildren().isEmpty() && roomList.getChildren().get(0) instanceof Button) {
                ((Button) roomList.getChildren().get(0)).requestFocus();
            }
        });
    }
}
