package allday.minico.utils.audio;

import javafx.application.Platform;
import javafx.scene.Scene;


public class BackgroundMusicManager {
    

    public static void playLoginMusic(Scene scene) {
        Platform.runLater(() -> {
            try {
                AudioManager.getInstance().playBackgroundMusic("login-music.mp3", true);
                
                if (scene != null) {
                    ButtonSoundHandler.addButtonSounds(scene);
                }
            } catch (Exception e) {
                System.err.println("로그인 배경음악 재생 실패: " + e.getMessage());
            }
        });
    }
    

    public static void playMainMusic(Scene scene) {
        Platform.runLater(() -> {
            try {
                AudioManager.getInstance().playBackgroundMusic("main-music.mp3", true);
                
                if (scene != null) {
                    ButtonSoundHandler.addButtonSounds(scene);
                }
            } catch (Exception e) {
                System.err.println("메인 배경음악 재생 실패: " + e.getMessage());
            }
        });
    }
    

    public static void ensureMainMusicPlaying(Scene scene) {
        Platform.runLater(() -> {
            try {
                AudioManager audioManager = AudioManager.getInstance();
                
                // 이미 main-music이 재생 중이면 그대로 유지
                if (!audioManager.isPlayingMainMusic()) {
                    audioManager.playBackgroundMusic("main-music.mp3", true);
                }
                
                if (scene != null) {
                    ButtonSoundHandler.addButtonSounds(scene);
                }
            } catch (Exception e) {
                System.err.println("메인 배경음악 연속 재생 실패: " + e.getMessage());
            }
        });
    }
    

    public static void switchToMainMusic() {
        try {
            AudioManager.getInstance().playBackgroundMusic("main-music.mp3", true);
        } catch (Exception e) {
            System.err.println("메인 음악 전환 실패: " + e.getMessage());
        }
    }
}
