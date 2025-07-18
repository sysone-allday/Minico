package allday.minico.ui.miniroom;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.Map;

public class CharacterManager {
    private final Pane roomPane;
    private final Map<String, ImageView> visitorCharacters;
    private final Map<String, Text> characterNameLabels;

    public CharacterManager(Pane roomPane, Map<String, ImageView> visitorCharacters, Map<String, Text> characterNameLabels) {
        this.roomPane = roomPane;
        this.visitorCharacters = visitorCharacters;
        this.characterNameLabels = characterNameLabels;
    }

    public ImageView createHostCharacter(String hostName, double x, double y, String direction) {
        removeCharacterAndLabels(hostName);
        ImageView hostCharacter = createCharacterImageView(x, y, direction);
        hostCharacter.setOpacity(0.8);
        roomPane.getChildren().add(hostCharacter);
        if (hostName != null) {
            createCharacterNameLabel(hostName, hostCharacter);
        }
        return hostCharacter;
    }

    public void updateHostCharacter(ImageView hostCharacter, String hostName, double x, double y, String direction) {
        if (hostCharacter == null) return;
        hostCharacter.setLayoutX(x);
        hostCharacter.setLayoutY(y);
        if (hostName != null) {
            Text hostNameLabel = characterNameLabels.get(hostName);
            if (hostNameLabel != null) {
                updateNameLabelPosition(hostNameLabel, hostCharacter);
            }
        }
        try {
            String imageName = getCharacterImageName(direction);
            Image newImage = new Image(getClass().getResource("/allday/minico/images/char/" + imageName).toExternalForm());
            hostCharacter.setImage(newImage);
        } catch (Exception e) {
            System.out.println("호스트 캐릭터 이미지 업데이트 오류: " + e.getMessage());
        }
    }

    public void updateVisitorCharacter(String playerName, String visitorName, double x, double y, String direction, boolean isHosting) {
        if (visitorName.equals(playerName)) return;
        ImageView visitorChar = visitorCharacters.get(visitorName);
        if (visitorChar == null) {
            createVisitorCharacter(visitorName, x, y, direction, isHosting);
        } else {
            visitorChar.setLayoutX(x);
            visitorChar.setLayoutY(y);
            Text nameLabel = characterNameLabels.get(visitorName);
            if (nameLabel != null) {
                updateNameLabelPosition(nameLabel, visitorChar);
            }
            try {
                String imageName = getCharacterImageName(direction);
                Image newImage = new Image(getClass().getResource("/allday/minico/images/char/" + imageName).toExternalForm());
                visitorChar.setImage(newImage);
            } catch (Exception e) {
                System.out.println("방문자 캐릭터 이미지 업데이트 오류: " + e.getMessage());
            }
        }
    }

    public void createVisitorCharacter(String visitorName, double x, double y, String direction, boolean isHosting) {
        try {
            String imageName = getCharacterImageName(direction);
            Image visitorImage = new Image(getClass().getResource("/allday/minico/images/char/" + imageName).toExternalForm());
            ImageView visitorChar = new ImageView(visitorImage);
            visitorChar.setFitWidth(100);
            visitorChar.setFitHeight(100);
            visitorChar.setPreserveRatio(true);
            visitorChar.setLayoutX(x);
            visitorChar.setLayoutY(y);
            if (isHosting) {
                visitorChar.setOpacity(0.7);
                visitorChar.setStyle("-fx-effect: dropshadow(gaussian, blue, 5, 0, 0, 0);");
            } else {
                visitorChar.setOpacity(0.9);
                visitorChar.setStyle("-fx-effect: dropshadow(gaussian, green, 3, 0, 0, 0);");
            }
            roomPane.getChildren().add(visitorChar);
            visitorCharacters.put(visitorName, visitorChar);
            createCharacterNameLabel(visitorName, visitorChar);
        } catch (Exception e) {
            System.out.println("방문자 캐릭터 생성 오류: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void createCharacterNameLabel(String name, ImageView character) {
        Text nameLabel = new Text(name);
        nameLabel.setFont(Font.font("Arial", 10));
        nameLabel.setFill(javafx.scene.paint.Color.WHITE);
        nameLabel.setStroke(javafx.scene.paint.Color.BLACK);
        nameLabel.setStrokeWidth(0.5);
        updateNameLabelPosition(nameLabel, character);
        roomPane.getChildren().add(nameLabel);
        characterNameLabels.put(name, nameLabel);
    }

    public void updateNameLabelPosition(Text nameLabel, ImageView character) {
        double labelX = character.getLayoutX() + character.getFitWidth() / 2 - nameLabel.getBoundsInLocal().getWidth() / 2;
        double labelY = character.getLayoutY() + character.getFitHeight() + 15;
        nameLabel.setX(labelX);
        nameLabel.setY(labelY);
    }

    public void removeCharacterAndLabels(String characterName) {
        ImageView visitorChar = visitorCharacters.get(characterName);
        if (visitorChar != null) {
            roomPane.getChildren().remove(visitorChar);
            visitorCharacters.remove(characterName);
        }
        Text nameLabel = characterNameLabels.get(characterName);
        if (nameLabel != null) {
            roomPane.getChildren().remove(nameLabel);
            characterNameLabels.remove(characterName);
        }
    }

    // 모든 방문자 캐릭터 제거
    public void removeAllVisitorCharacters() {
        System.out.println("removeAllVisitorCharacters 호출됨. 방문자 수: " + visitorCharacters.size() + ", 이름표 수: " + characterNameLabels.size());
        
        // 방문자 캐릭터들 제거
        for (String name : visitorCharacters.keySet()) {
            ImageView character = visitorCharacters.get(name);
            if (character != null) {
                System.out.println("방문자 캐릭터 제거: " + name);
                roomPane.getChildren().remove(character);
            }
        }
        visitorCharacters.clear();
        
        // 해당 이름표들도 제거
        for (String name : characterNameLabels.keySet()) {
            Text nameLabel = characterNameLabels.get(name);
            if (nameLabel != null) {
                System.out.println("이름표 제거: " + name);
                roomPane.getChildren().remove(nameLabel);
            }
        }
        characterNameLabels.clear();
        
        System.out.println("모든 방문자 캐릭터와 이름표가 제거되었습니다.");
    }

    // 호스트 캐릭터 제거 (특정 호스트 캐릭터를 직접 제거)
    public void removeHostCharacter(ImageView hostCharacter, String hostName) {
        System.out.println("removeHostCharacter 호출됨. 호스트명: " + hostName + ", 캐릭터: " + (hostCharacter != null ? "존재" : "null"));
        
        if (hostCharacter != null) {
            roomPane.getChildren().remove(hostCharacter);
            System.out.println("호스트 캐릭터 UI에서 제거됨");
        }
        
        // 호스트 이름표도 제거
        if (hostName != null) {
            Text hostNameLabel = characterNameLabels.get(hostName);
            if (hostNameLabel != null) {
                roomPane.getChildren().remove(hostNameLabel);
                characterNameLabels.remove(hostName);
                System.out.println("호스트 이름표 제거됨: " + hostName);
            }
        }
        
        System.out.println("호스트 캐릭터가 제거되었습니다: " + hostName);
    }

    private ImageView createCharacterImageView(double x, double y, String direction) {
        try {
            String imageName = getCharacterImageName(direction);
            Image image = new Image(getClass().getResource("/allday/minico/images/char/" + imageName).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            imageView.setLayoutX(x);
            imageView.setLayoutY(y);
            return imageView;
        } catch (Exception e) {
            System.out.println("캐릭터 이미지를 로드할 수 없습니다: " + e.getMessage());
            return null;
        }
    }

    private String getCharacterImageName(String direction) {
        switch (direction) {
            case "LEFT": return "Left.png";
            case "RIGHT": return "Right.png";
            case "UP": return "back.png";
            case "DOWN":
            case "front":
            default: return "front.png";
        }
    }
}
