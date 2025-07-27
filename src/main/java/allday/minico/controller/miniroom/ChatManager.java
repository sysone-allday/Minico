package allday.minico.controller.miniroom;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;

import java.util.HashMap;
import java.util.Map;

/*
@author 김대호
ChatManager 클래스는 미니룸에서 채팅 말풍선을 관리하는 클래스입니다.
말풍선 생성, 위치 설정, 자동 제거 등의 기능을 제공합니다.
 */
@SuppressWarnings("unused")
public class ChatManager {

    private final Pane roomPane;
    private final Map<String, Group> chatBubbles = new HashMap<>();

    public ChatManager(Pane roomPane) {
        this.roomPane = roomPane;
    }

    public void showChatBubble(String senderName, String message, ImageView targetCharacter) {
        // 기존 말풍선 제거
        Group existingBubble = chatBubbles.get(senderName);
        if (existingBubble != null) {
            roomPane.getChildren().remove(existingBubble);
        }

        // 새 말풍선 생성
        Group bubble = createChatBubble(message);

        // 말풍선 위치 설정
        if (targetCharacter == null) {
            // 기본 위치 설정 (화면 중앙)
            double bubbleX = (roomPane.getWidth() - 100) / 2;
            double bubbleY = (roomPane.getHeight() - 50) / 2;
            bubble.setLayoutX(bubbleX);
            bubble.setLayoutY(bubbleY);
        } else {
            // 캐릭터 위쪽에 말풍선 위치 설정
            double bubbleX = targetCharacter.getLayoutX() + targetCharacter.getFitWidth() / 2 - 30;
            double bubbleY = targetCharacter.getLayoutY() - 30;
            bubble.setLayoutX(bubbleX);
            bubble.setLayoutY(bubbleY);
        }

        roomPane.getChildren().add(bubble);
        chatBubbles.put(senderName, bubble);

        // 3초 후 말풍선 제거
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            roomPane.getChildren().remove(bubble);
            chatBubbles.remove(senderName);
        }));
        timeline.play();
    }

    private Group createChatBubble(String message) {
        Group bubble = new Group();

        // 텍스트 생성
        Text text = new Text(message);

        // 텍스트 크기 계산
        double textWidth = text.getBoundsInLocal().getWidth();
        double textHeight = text.getBoundsInLocal().getHeight();

        // 배경 박스 생성
        Rectangle background = new Rectangle(textWidth + 20, textHeight + 15);
        background.setFill(Color.WHITE);
        background.setStroke(Color.GRAY);
        background.setStrokeWidth(1);
        background.setArcWidth(10);
        background.setArcHeight(10);

        // 그림자 효과
        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(2);
        shadow.setOffsetY(2);
        shadow.setColor(Color.LIGHTGRAY);
        background.setEffect(shadow);

        // 텍스트 위치 조정
        text.setX(10);
        text.setY(textHeight + 5);

        background.getStyleClass().add("chat-bubble");
        text.getStyleClass().add("chat-text");

        bubble.getChildren().addAll(background, text);
        return bubble;
    }
}
