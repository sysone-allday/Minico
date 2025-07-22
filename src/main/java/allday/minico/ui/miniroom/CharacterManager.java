package allday.minico.ui.miniroom;

import allday.minico.session.AppSession;
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
            String imagePath = getUserCharacterImagePath(direction);
            Image newImage = new Image(getClass().getResource(imagePath).toExternalForm());
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
                String imagePath = getUserCharacterImagePath(direction);
                Image newImage = new Image(getClass().getResource(imagePath).toExternalForm());
                visitorChar.setImage(newImage);
            } catch (Exception e) {
                System.out.println("방문자 캐릭터 이미지 업데이트 오류: " + e.getMessage());
            }
        }
    }

    public void createVisitorCharacter(String visitorName, double x, double y, String direction, boolean isHosting) {
        try {
            String imagePath = getUserCharacterImagePath(direction);
            Image visitorImage = new Image(getClass().getResource(imagePath).toExternalForm());
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
            String imagePath = getUserCharacterImagePath(direction);
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            imageView.setLayoutX(x);
            imageView.setLayoutY(y);
            return imageView;
        } catch (Exception e) {
            System.out.println("캐릭터 이미지를 로드할 수 없습니다: " + e.getMessage());
            // 기본 이미지 사용 시도
            return createDefaultCharacterImageView(x, y, direction);
        }
    }
    
    private ImageView createDefaultCharacterImageView(double x, double y, String direction) {
        try {
            // 기본 캐릭터 이미지 (대호 사용)
            String imageName = getDefaultCharacterImageName(direction);
            Image image = new Image(getClass().getResource("/allday/minico/images/char/male/" + imageName).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            imageView.setLayoutX(x);
            imageView.setLayoutY(y);
            return imageView;
        } catch (Exception e) {
            System.out.println("기본 캐릭터 이미지도 로드할 수 없습니다: " + e.getMessage());
            return null;
        }
    }
    
    private String getUserCharacterImagePath(String direction) {
        try {
            // SkinService를 통해 현재 사용자의 스킨 정보 가져오기
            String memberId = AppSession.getLoginMember().getMemberId();
            String minimiType = AppSession.getLoginMember().getMinimi();
            
            if (minimiType != null && memberId != null) {
                // SKIN 테이블에서 사용자의 실제 캐릭터 정보 조회
                String characterName = allday.minico.utils.skin.SkinUtil.getCurrentUserCharacterName(memberId);
                String gender = minimiType.toLowerCase(); // "male" 또는 "female"
                
                // 방향에 따른 이미지 파일명 생성
                String directionSuffix = getDirectionSuffix(direction);
                String imagePath = String.format("/allday/minico/images/char/%s/%s_%s.png", 
                                   gender, characterName, directionSuffix);
                
                System.out.println("생성된 이미지 경로: " + imagePath);
                System.out.println("사용자 캐릭터: " + characterName + ", 방향: " + direction + " → " + directionSuffix);
                
                return imagePath;
            }
        } catch (Exception e) {
            System.out.println("사용자 캐릭터 정보를 가져올 수 없습니다: " + e.getMessage());
        }
        
        // 정보가 없으면 기본 캐릭터 사용
        String defaultPath = "/allday/minico/images/char/male/대호_" + getDirectionSuffix(direction) + ".png";
        System.out.println("기본 이미지 경로: " + defaultPath);
        return defaultPath;
    }
    
    private String getDirectionSuffix(String direction) {
        switch (direction) {
            case "LEFT": return "left";
            case "RIGHT": return "right";
            case "UP": return "back";
            case "DOWN":
            case "front":
            default: return "front";
        }
    }

    private String getDefaultCharacterImageName(String direction) {
        String suffix = getDirectionSuffix(direction);
        return "대호_" + suffix + ".png";
    }
}
