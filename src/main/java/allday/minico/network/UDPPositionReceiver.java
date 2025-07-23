package allday.minico.network;

import java.net.*;
import java.io.IOException;

/**
 * UDP를 사용하여 다른 클라이언트의 위치 업데이트를 수신하는 클라이언트
 * 서버로부터 브로드캐스트되는 위치 정보를 받아서 처리
 */
public class UDPPositionReceiver {
    private DatagramSocket socket;
    private boolean isListening = false;
    private Thread listenThread;
    private PositionUpdateListener listener;
    
    // 패킷 버퍼 크기
    private static final int BUFFER_SIZE = 1024;
    
    /**
     * 위치 업데이트 수신 리스너
     */
    public interface PositionUpdateListener {
        void onPositionUpdate(String clientId, double x, double y, String direction);
    }
    
    public UDPPositionReceiver() {
        // 기본 생성자
    }
    
    /**
     * 위치 업데이트 리스너 설정
     */
    public void setPositionUpdateListener(PositionUpdateListener listener) {
        this.listener = listener;
    }
    
    /**
     * UDP 수신 시작
     */
    public boolean startListening(int port) {
        try {
            socket = new DatagramSocket(port);
            isListening = true;
            
            // 백그라운드에서 메시지 수신
            listenThread = new Thread(this::listenForUpdates);
            listenThread.setDaemon(true);
            listenThread.start();
            
            System.out.println("UDP Position Receiver 시작됨 - 포트: " + port);
            return true;
            
        } catch (SocketException e) {
            System.err.println("UDP Position Receiver 시작 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * UDP 위치 업데이트 수신 대기
     */
    private void listenForUpdates() {
        byte[] buffer = new byte[BUFFER_SIZE];
        
        while (isListening && socket != null && !socket.isClosed()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                processUpdate(packet);
                
            } catch (IOException e) {
                if (isListening) {
                    System.err.println("UDP 위치 업데이트 수신 오류: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 수신된 위치 업데이트 처리
     */
    private void processUpdate(DatagramPacket packet) {
        String message = new String(packet.getData(), 0, packet.getLength());
        
        String[] parts = message.split(":");
        if (parts.length < 5) return;
        
        String messageType = parts[0];
        
        if ("UPDATE".equals(messageType)) {
            try {
                String clientId = parts[1];
                double x = Double.parseDouble(parts[2]);
                double y = Double.parseDouble(parts[3]);
                String direction = parts[4];
                
                // 리스너에게 위치 업데이트 알림
                if (listener != null) {
                    listener.onPositionUpdate(clientId, x, y, direction);
                }
                
            } catch (NumberFormatException e) {
                System.err.println("잘못된 위치 업데이트 데이터: " + message);
            }
        }
    }
    
    /**
     * UDP 수신 중지
     */
    public void stopListening() {
        isListening = false;
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
            socket = null;
        }
        
        if (listenThread != null) {
            listenThread.interrupt();
            listenThread = null;
        }
        
        System.out.println("UDP Position Receiver 중지됨");
    }
    
    /**
     * 수신 상태 확인
     */
    public boolean isListening() {
        return isListening;
    }
}
