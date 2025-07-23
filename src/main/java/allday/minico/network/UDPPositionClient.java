package allday.minico.network;

import java.net.*;
import java.io.IOException;

/**
 * UDP를 사용하여 캐릭터 위치 데이터만 전송하는 클라이언트
 * 실시간성이 중요한 위치 정보를 빠르게 전송하기 위해 UDP 사용
 */
public class UDPPositionClient {
    private DatagramSocket socket;
    private InetAddress serverAddress;
    private int serverPort;
    private boolean isConnected = false;
    private String clientId;
    
    // UDP 패킷 크기 제한
    private static final int MAX_PACKET_SIZE = 512;
    
    public UDPPositionClient(String clientId) {
        this.clientId = clientId;
    }
    
    /**
     * UDP 서버에 연결 설정
     */
    public boolean connect(String serverIP, int port) {
        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getByName(serverIP);
            serverPort = port;
            isConnected = true;
            
            // 연결 확인 메시지 전송
            sendConnectMessage();
            
            // System.out.println("UDP Position Client 연결 성공: " + serverIP + ":" + port);
            return true;
        } catch (IOException e) {
            // System.err.println("UDP Position Client 연결 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 서버에 연결 메시지 전송
     */
    private void sendConnectMessage() {
        if (!isConnected) return;
        
        String message = "CONNECT:" + clientId;
        // System.out.println("UDP 연결 메시지 전송: " + message);
        sendMessage(message);
    }
    
    /**
     * 위치 업데이트 전송 (UDP)
     * TCP와 달리 빠른 전송을 위해 최소한의 데이터만 전송
     */
    public void sendPositionUpdate(double x, double y, String direction) {
        if (!isConnected || socket == null) return;
        
        // 위치 데이터만 간단하게 전송 (캐릭터 정보 제외로 패킷 크기 최소화)
        String message = String.format("POS:%s:%.1f:%.1f:%s", 
                clientId, x, y, direction);
        
        System.out.printf("UDP 위치 전송: %s (%.1f, %.1f) %s%n", clientId, x, y, direction);
        sendMessage(message);
    }
    
    /**
     * UDP 메시지 전송
     */
    private void sendMessage(String message) {
        if (!isConnected || socket == null) return;
        
        try {
            byte[] buffer = message.getBytes();
            
            // 패킷 크기 체크
            if (buffer.length > MAX_PACKET_SIZE) {
                System.err.println("UDP 패킷 크기 초과: " + buffer.length + " bytes");
                return;
            }
            
            DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, serverAddress, serverPort);
            
            socket.send(packet);
            
        } catch (IOException e) {
            System.err.println("UDP 메시지 전송 실패: " + e.getMessage());
        }
    }
    
    /**
     * 연결 상태 확인
     */
    public boolean isConnected() {
        return isConnected && socket != null && !socket.isClosed();
    }
    
    /**
     * 연결 종료
     */
    public void disconnect() {
        isConnected = false;
        
        if (socket != null && !socket.isClosed()) {
            // 종료 메시지 전송
            sendMessage("DISCONNECT:" + clientId);
            
            socket.close();
            socket = null;
        }
        
        // System.out.println("UDP Position Client 연결 종료");
    }
    
    /**
     * 클라이언트 ID 반환
     */
    public String getClientId() {
        return clientId;
    }
}
