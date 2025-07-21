package allday.minico.utils.audio;

import allday.minico.utils.audio.AudioManager;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;

public class ButtonSoundHandler {
    
    private static final String DEFAULT_BUTTON_SOUND = "btn-click.mp3";
    
    public static void addButtonSounds(Scene scene) {
        if (scene != null && scene.getRoot() != null) {
            addButtonSounds(scene.getRoot());
        }
    }
    
    /**
     * Parent 노드 하위의 모든 버튼에 클릭 효과음을 추가합니다
     * @param parent 대상 Parent 노드
     */
    public static void addButtonSounds(Parent parent) {
        addButtonSoundsRecursive(parent, DEFAULT_BUTTON_SOUND);
    }
    
    /**
     * Parent 노드 하위의 모든 버튼에 특정 효과음을 추가합니다
     * @param parent 대상 Parent 노드
     * @param soundFileName 효과음 파일명
     */
    public static void addButtonSounds(Parent parent, String soundFileName) {
        addButtonSoundsRecursive(parent, soundFileName);
    }
    
    /**
     * 재귀적으로 모든 버튼을 찾아 효과음을 추가합니다
     */
    private static void addButtonSoundsRecursive(Parent parent, String soundFileName) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof ButtonBase) {
                // 기존 이벤트 핸들러를 보존하면서 효과음 추가
                ButtonBase button = (ButtonBase) child;
                
                // 기존 onAction 핸들러 저장
                var originalHandler = button.getOnAction();
                
                // 새로운 핸들러 설정 (효과음 + 기존 핸들러)
                button.setOnAction(event -> {
                    // 효과음 재생
                    playButtonSound(soundFileName);
                    
                    // 기존 핸들러 실행
                    if (originalHandler != null) {
                        originalHandler.handle(event);
                    }
                });
                
                // 버튼에 CSS 클래스 추가 (선택사항)
                button.getStyleClass().add("sound-enabled-button");
                
            } else if (child instanceof Parent) {
                // 자식 노드들에 대해 재귀 호출
                addButtonSoundsRecursive((Parent) child, soundFileName);
            }
        }
    }
    
    /**
     * 특정 버튼에만 효과음을 추가합니다
     * @param button 대상 버튼
     */
    public static void addButtonSound(ButtonBase button) {
        addButtonSound(button, DEFAULT_BUTTON_SOUND);
    }
    
    /**
     * 특정 버튼에 특정 효과음을 추가합니다
     * @param button 대상 버튼
     * @param soundFileName 효과음 파일명
     */
    public static void addButtonSound(ButtonBase button, String soundFileName) {
        if (button != null) {
            var originalHandler = button.getOnAction();
            
            button.setOnAction(event -> {
                playButtonSound(soundFileName);
                
                if (originalHandler != null) {
                    originalHandler.handle(event);
                }
            });
            
            button.getStyleClass().add("sound-enabled-button");
        }
    }
    
    /**
     * 버튼 효과음 재생
     */
    private static void playButtonSound(String soundFileName) {
        try {
            AudioManager.getInstance().playSound(soundFileName);
        } catch (Exception e) {
            // 효과음 재생 실패해도 버튼 기능은 정상 작동
            System.err.println("버튼 효과음 재생 실패: " + e.getMessage());
        }
    }
    
    /**
     * Scene의 모든 버튼에서 효과음을 제거합니다
     * @param scene 대상 Scene
     */
    public static void removeButtonSounds(Scene scene) {
        if (scene != null && scene.getRoot() != null) {
            removeButtonSoundsRecursive(scene.getRoot());
        }
    }
    
    /**
     * 재귀적으로 모든 버튼에서 효과음 관련 클래스를 제거합니다
     */
    private static void removeButtonSoundsRecursive(Parent parent) {
        for (Node child : parent.getChildrenUnmodifiable()) {
            if (child instanceof ButtonBase) {
                ButtonBase button = (ButtonBase) child;
                button.getStyleClass().remove("sound-enabled-button");
            } else if (child instanceof Parent) {
                removeButtonSoundsRecursive((Parent) child);
            }
        }
    }
}
