package allday.minico.utils.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/**
 * 오디오 재생을 관리하는 유틸리티 클래스
 */
public class AudioManager {
    private static AudioManager instance;
    private MediaPlayer currentPlayer;
    private boolean isMuted = false;
    private double volume = 0.5; // 기본 볼륨 50%
    
    private AudioManager() {}
    
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }
    
    /**
     * 배경음악을 재생합니다
     * @param audioFileName 오디오 파일명 (확장자 포함)
     * @param loop 반복 재생 여부
     */
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
            
            System.out.println("배경음악 재생 시작: " + audioFileName);
            
        } catch (Exception e) {
            System.err.println("오디오 재생 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 효과음을 재생합니다 (한 번만)
     * @param audioFileName 오디오 파일명
     */
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
    
    /**
     * 현재 재생 중인 음악을 정지합니다
     */
    public void stopCurrentMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
        }
    }
    
    /**
     * 음악을 일시정지합니다
     */
    public void pauseMusic() {
        if (currentPlayer != null) {
            currentPlayer.pause();
        }
    }
    
    /**
     * 일시정지된 음악을 재개합니다
     */
    public void resumeMusic() {
        if (currentPlayer != null) {
            currentPlayer.play();
        }
    }
    
    /**
     * 음소거 토글
     */
    public void toggleMute() {
        isMuted = !isMuted;
        if (currentPlayer != null) {
            currentPlayer.setVolume(isMuted ? 0 : volume);
        }
    }
    
    /**
     * 볼륨 설정 (0.0 ~ 1.0)
     */
    public void setVolume(double volume) {
        this.volume = Math.max(0.0, Math.min(1.0, volume));
        if (currentPlayer != null && !isMuted) {
            currentPlayer.setVolume(this.volume);
        }
    }
    
    /**
     * 현재 볼륨 반환
     */
    public double getVolume() {
        return volume;
    }
    
    /**
     * 음소거 상태 반환
     */
    public boolean isMuted() {
        return isMuted;
    }
    
    /**
     * 현재 재생 중인지 확인
     */
    public boolean isPlaying() {
        return currentPlayer != null && 
               currentPlayer.getStatus() == MediaPlayer.Status.PLAYING;
    }
    
    /**
     * 애플리케이션 종료 시 리소스 정리
     */
    public void cleanup() {
        stopCurrentMusic();
    }
}
