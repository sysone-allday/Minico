package allday.minico.ui.miniroom;

import allday.minico.session.AppSession;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import java.util.Map;
import java.util.HashMap;

public class CharacterManager {
    private final Pane roomPane;
    private final Map<String, ImageView> visitorCharacters;
    private final Map<String, Text> characterNameLabels;
    private ImageView hostCharacter;
    
    // 사용자별 캐릭터 정보 캐시
    private final Map<String, CharacterInfo> userCharacterCache = new HashMap<>();
    
    // 캐릭터 정보를 저장하는 내부 클래스
    public static class CharacterInfo {
        public final String minimi;
        public final String variant;
        
        public CharacterInfo(String minimi, String variant) {
            this.minimi = minimi;
            this.variant = variant;
        }
    }

    public CharacterManager(Pane roomPane, Map<String, ImageView> visitorCharacters, Map<String, Text> characterNameLabels) {
        this.roomPane = roomPane;
        this.visitorCharacters = visitorCharacters;
        this.characterNameLabels = characterNameLabels;
    }

    /**
     * 사용자의 캐릭터 정보를 캐시에 설정
     * @param userName 사용자명
     * @param characterInfo 캐릭터 정보 (형식: "Male:온유")
     */
    public void setUserCharacterInfo(String userName, String characterInfo) {
        if (userName != null && characterInfo != null && characterInfo.contains(":")) {
            String[] parts = characterInfo.split(":");
            if (parts.length >= 2) {
                userCharacterCache.put(userName, new CharacterInfo(parts[0], parts[1]));
                System.out.println("[CharacterManager] 사용자 캐릭터 정보 설정: " + userName + " -> " + characterInfo);
                System.out.println("[CharacterManager] 분리된 정보 - minimi(성별): " + parts[0] + ", variant(캐릭터명): " + parts[1]);
            }
        }
    }
    
    /**
     * 사용자의 캐릭터 정보를 캐시에서 가져옴
     * @param userName 사용자명
     * @return 캐릭터 정보
     */
    public CharacterInfo getUserCharacterInfo(String userName) {
        return userCharacterCache.get(userName);
    }

    public ImageView createHostCharacter(String hostName, double x, double y, String direction) {
        return createHostCharacter(hostName, x, y, direction, null);
    }
    
    // 캐릭터 정보 포함한 호스트 캐릭터 생성 (새로운 메서드명)
    public ImageView createHostCharacterWithInfo(String hostName, double x, double y, String direction, String characterInfo) {
        return createHostCharacter(hostName, x, y, direction, characterInfo);
    }
    
    // 캐릭터 정보 포함한 호스트 캐릭터 생성
    public ImageView createHostCharacter(String hostName, double x, double y, String direction, String characterInfo) {
        // 호스트 캐릭터 정보 캐시에 저장
        if (characterInfo != null) {
            setUserCharacterInfo(hostName, characterInfo);
        }
        
        removeCharacterAndLabels(hostName);
        ImageView hostCharacter = createCharacterImageView(x, y, direction, hostName);
        hostCharacter.setOpacity(0.8);
        roomPane.getChildren().add(hostCharacter);
        if (hostName != null) {
            createCharacterNameLabel(hostName, hostCharacter);
        }
        return hostCharacter;
    }

    public void updateHostCharacter(ImageView hostCharacter, String hostName, double x, double y, String direction) {
        updateHostCharacter(hostCharacter, hostName, x, y, direction, null, null);
    }
    
    // 오버로드된 메서드 - 호스트 캐릭터 정보를 받아서 사용
    public void updateHostCharacter(ImageView hostCharacter, String hostName, double x, double y, String direction, String hostMemberId, String characterInfo) {
        // 캐릭터 정보가 있으면 캐시에 저장
        if (characterInfo != null) {
            setUserCharacterInfo(hostName, characterInfo);
        }
        
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
            String imagePath = getCharacterImagePath(hostName, direction);
            Image newImage = new Image(getClass().getResource(imagePath).toExternalForm());
            hostCharacter.setImage(newImage);
        } catch (Exception e) {
            System.out.println("호스트 캐릭터 이미지 업데이트 오류: " + e.getMessage());
        }
    }

    public void updateVisitorCharacter(String playerName, String visitorName, double x, double y, String direction, boolean isHosting) {
        updateVisitorCharacter(playerName, visitorName, x, y, direction, isHosting, null, null);
    }
    
    // 오버로드된 메서드 - 캐릭터 정보를 받아서 사용
    public void updateVisitorCharacter(String playerName, String visitorName, double x, double y, String direction, boolean isHosting, String visitorMemberId, String characterInfo) {
        // 캐릭터 정보가 있으면 캐시에 저장
        if (characterInfo != null) {
            setUserCharacterInfo(visitorName, characterInfo);
        }
        
        if (visitorName.equals(playerName)) return;
        ImageView visitorChar = visitorCharacters.get(visitorName);
        if (visitorChar == null) {
            createVisitorCharacter(visitorName, x, y, direction, isHosting, visitorMemberId, characterInfo);
        } else {
            visitorChar.setLayoutX(x);
            visitorChar.setLayoutY(y);
            Text nameLabel = characterNameLabels.get(visitorName);
            if (nameLabel != null) {
                updateNameLabelPosition(nameLabel, visitorChar);
            }
            try {
                String imagePath;
                // 전달받은 캐릭터 정보가 있으면 직접 사용
                if (characterInfo != null && characterInfo.contains(":")) {
                    imagePath = getCharacterImagePath(visitorMemberId, characterInfo, direction);
                    System.out.println("방문자 업데이트 (완전한 캐릭터 정보) - 이름: " + visitorName + ", 캐릭터 정보: " + characterInfo + ", 생성된 경로: " + imagePath);
                } else {
                    // 기존 방식 (캐시된 정보 사용)
                    imagePath = getCharacterImagePath(visitorName, direction);
                    System.out.println("방문자 업데이트 (캐시된 정보) - 이름: " + visitorName + ", 생성된 경로: " + imagePath);
                }
                
                // 리소스 존재 확인
                if (getClass().getResource(imagePath) == null) {
                    System.out.println("리소스가 존재하지 않음: " + imagePath);
                    throw new RuntimeException("이미지 리소스를 찾을 수 없습니다: " + imagePath);
                }
                
                Image newImage = new Image(getClass().getResource(imagePath).toExternalForm());
                visitorChar.setImage(newImage);
            } catch (Exception e) {
                System.out.println("방문자 캐릭터 이미지 업데이트 오류: " + e.getMessage());
                e.printStackTrace();
                
                // 기본 캐릭터 이미지로 폴백
                try {
                    String defaultPath = "/allday/minico/images/char/male/대호_" + getDirectionSuffix(direction) + ".png";
                    System.out.println("기본 이미지로 폴백 시도: " + defaultPath);
                    Image defaultImage = new Image(getClass().getResource(defaultPath).toExternalForm());
                    visitorChar.setImage(defaultImage);
                    System.out.println("기본 이미지로 업데이트 완료");
                } catch (Exception fallbackException) {
                    System.out.println("기본 이미지 폴백도 실패: " + fallbackException.getMessage());
                    fallbackException.printStackTrace();
                }
            }
        }
    }

    public void createVisitorCharacter(String visitorName, double x, double y, String direction, boolean isHosting) {
        createVisitorCharacter(visitorName, x, y, direction, isHosting, null, null);
    }
    
    // 오버로드된 메서드 - 캐릭터 정보를 받아서 사용
    public void createVisitorCharacter(String visitorName, double x, double y, String direction, boolean isHosting, String visitorMemberId, String characterInfo) {
        try {
            String imagePath = getCharacterImagePath(visitorMemberId, characterInfo, direction);
            System.out.println("방문자 캐릭터 생성 - 이름: " + visitorName + ", 캐릭터 정보: " + characterInfo + ", 생성된 경로: " + imagePath);
            
            // 리소스 존재 확인
            if (getClass().getResource(imagePath) == null) {
                System.out.println("리소스가 존재하지 않음: " + imagePath);
                throw new RuntimeException("이미지 리소스를 찾을 수 없습니다: " + imagePath);
            }
            
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
            
            // 기본 캐릭터로 폴백
            try {
                String defaultPath = "/allday/minico/images/char/male/대호_" + getDirectionSuffix(direction) + ".png";
                System.out.println("기본 캐릭터로 폴백 시도: " + defaultPath);
                
                Image defaultImage = new Image(getClass().getResource(defaultPath).toExternalForm());
                ImageView visitorChar = new ImageView(defaultImage);
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
                System.out.println("기본 캐릭터로 방문자 생성 완료: " + visitorName);
            } catch (Exception fallbackException) {
                System.out.println("기본 캐릭터로 폴백도 실패: " + fallbackException.getMessage());
                fallbackException.printStackTrace();
            }
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

    // 오버로드된 메서드 - 사용자명을 받아서 해당 사용자의 캐릭터 이미지 생성
    private ImageView createCharacterImageView(double x, double y, String direction, String userName) {
        try {
            String imagePath = getCharacterImagePath(userName, direction);
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
            imageView.setLayoutX(x);
            imageView.setLayoutY(y);
            return imageView;
        } catch (Exception e) {
            System.out.println("사용자 캐릭터 이미지를 로드할 수 없습니다: " + e.getMessage());
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
        // 현재 로그인한 사용자의 캐릭터 이미지 경로
        try {
            String memberId = AppSession.getLoginMember().getMemberId();
            
            if (memberId != null) {
                // 캐싱된 최적화 메서드 사용 - DB 조회를 최소화
                return allday.minico.utils.skin.SkinUtil.getCharacterImagePath(memberId, direction);
            }
        } catch (Exception e) {
            System.out.println("사용자 캐릭터 정보를 가져올 수 없습니다: " + e.getMessage());
        }
        
        // 정보가 없으면 기본 캐릭터 사용
        return "/allday/minico/images/char/male/마리오_" + getDirectionSuffix(direction) + ".png";
    }
    
    /**
     * 특정 사용자의 캐릭터 이미지 경로 생성 (방문자용)
     * @param memberId 사용자 ID (null이면 현재 사용자)
     * @param characterInfo 캐릭터 정보 (형식: "gender:characterName", 예: "Male:온유")
     * @param direction 방향
     * @return 캐릭터 이미지 경로
     */
    public String getCharacterImagePath(String memberId, String characterInfo, String direction) {
        // SkinUtil의 오버로드된 메서드 직접 사용 (캐시 없이 전달받은 정보만 사용)
        return allday.minico.utils.skin.SkinUtil.getCharacterImagePath(memberId, characterInfo, direction);
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

    // 캐릭터 이미지 경로 반환 (사용자 이름과 방향으로)
    public String getCharacterImagePath(String userName, String direction) {
        CharacterInfo info = getUserCharacterInfo(userName);
        System.out.println("[CharacterManager] getCharacterImagePath - userName: " + userName + ", 캐시된 정보: " + (info != null ? info.minimi + ":" + info.variant : "null"));
        
        if (info != null) {
            return getCharacterImagePath(null, info.minimi + ":" + info.variant, direction);
        } else {
            // 캐시된 정보가 없으면 기본 캐릭터 사용 (SkinUtil 통해 기본값 가져오기)
            String defaultPath = allday.minico.utils.skin.SkinUtil.getCharacterImagePath(null, "Male:대호", direction);
            System.out.println("[CharacterManager] 기본 캐릭터 경로 사용: " + defaultPath);
            return defaultPath;
        }
    }
}
