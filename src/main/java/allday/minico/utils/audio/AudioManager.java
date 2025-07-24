package allday.minico.utils.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;


public class AudioManager {
    private static AudioManager instance;
    private MediaPlayer currentPlayer;
    private boolean isMuted = false;
    private double volume = 0.5; // 기본 볼륨 50%
    private String currentMusicFile = null; 
    
    private AudioManager() {}
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }



    public void playBackgroundMusic(String audioFileName, boolean loop) {
        try {
            // 기존 재생 중인 음악 정지
            stopCurrentMusic();
            
            // 리소스에서 오디오 파일 로드
            URL audioUrl = getClass().getResource("/allday/minico/audio/" + audioFileName);
            if (audioUrl == null) {
                System.err.println("오디오 파일을 찾을 수 없습니다: " + audioFileName);
                return;
            }
            
            Media media = new Media(audioUrl.toString());
            currentPlayer = new MediaPlayer(media);
            currentMusicFile = audioFileName; // 현재 재생 중인 파일명 저장
            
            // 볼륨 설정
            currentPlayer.setVolume(isMuted ? 0 : volume);
            
            // 반복 재생 설정
            if (loop) {
                currentPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            
            // 오류 처리
            currentPlayer.setOnError(() -> {
                System.err.println("오디오 재생 오류: " + currentPlayer.getError());
            });
            
            // 재생 시작
            currentPlayer.play();
            
            // System.out.println("배경음악 재생 시작: " + audioFileName);
            
        } catch (Exception e) {
            System.err.println("오디오 재생 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void playSound(String audioFileName) {
        try {
            URL audioUrl = getClass().getResource("/allday/minico/audio/" + audioFileName);
            if (audioUrl == null) {
                System.err.println("오디오 파일을 찾을 수 없습니다: " + audioFileName);
                return;
            }
            
            Media media = new Media(audioUrl.toString());
            MediaPlayer soundPlayer = new MediaPlayer(media);
            soundPlayer.setVolume(isMuted ? 0 : volume);
            
            // 재생 완료 후 자동으로 메모리 해제
            soundPlayer.setOnEndOfMedia(() -> soundPlayer.dispose());
            
            soundPlayer.play();
            
        } catch (Exception e) {
            System.err.println("효과음 재생 실패: " + e.getMessage());
        }
    }

    public void stopCurrentMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
            currentMusicFile = null; // 파일명도 초기화
        }
    }
    
    public void pauseMusic() {
        if (currentPlayer != null) {
            currentPlayer.pause();
        }
    }
    
    public void resumeMusic() {
        if (currentPlayer != null) {
            currentPlayer.play();
        }
    }
    
    public void toggleMute() {
        isMuted = !isMuted;
        if (currentPlayer != null) {
            currentPlayer.setVolume(isMuted ? 0 : volume);
        }
    }
    

     //볼륨 설정 

    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
        if (currentPlayer != null && !isMuted) {
            currentPlayer.setVolume(this.volume);
        }
    }

    public double getVolume() {
        return volume;
    }
    

    public boolean isMuted() {
        return isMuted;
    }
    

    public boolean isPlaying() {
        return currentPlayer != null && 
               currentPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    

    public boolean isPlayingMainMusic() {
        return isPlaying() && "main-music.mp3".equals(currentMusicFile);
    }
    

    public void cleanup() {
        stopCurrentMusic();
    }
}
