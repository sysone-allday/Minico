package allday.minico.network;

import java.net.*;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

/**
 * UDP를 사용하여 캐릭터 위치 데이터를 처리하는 서버
 * 실시간 위치 업데이트를 위한 고성능 UDP 서버
 */
public class UDPPositionServer {
    private DatagramSocket socket;
    private boolean isRunning = false;
    private ExecutorService threadPool;
    
    // 연결된 클라이언트들의 정보 저장
    private ConcurrentHashMap<String, ClientInfo> connectedClients;
    
    // 위치 업데이트 리스너
    private PositionUpdateListener positionListener;
    
    // 패킷 버퍼 크기
    private static final int BUFFER_SIZE = 1024;
    
    /**
     * 클라이언트 정보 저장 클래스
     */
    private static class ClientInfo {
        String clientId;
        InetAddress address;
        int port;
        long lastUpdateTime;
        
        ClientInfo(String clientId, InetAddress address, int port) {
            this.clientId = clientId;
            this.address = address;
            this.port = port;
            this.lastUpdateTime = System.currentTimeMillis();
        }
        
        void updateLastSeen() {
            this.lastUpdateTime = System.currentTimeMillis();
        }
    }
    
    /**
     * 위치 업데이트 리스너 인터페이스
     */
    public interface PositionUpdateListener {
        void onClientConnected(String clientId);
        void onClientDisconnected(String clientId);
        void onPositionUpdate(String clientId, double x, double y, String direction);
    }
    
    public UDPPositionServer() {
        this.connectedClients = new ConcurrentHashMap<>();
        this.threadPool = Executors.newCachedThreadPool();
    }
    
    /**
     * 위치 업데이트 리스너 설정
     */
    public void setPositionUpdateListener(PositionUpdateListener listener) {
        this.positionListener = listener;
    }
    
    /**
     * UDP 서버 시작
     */
    public boolean startServer(int port) {
        try {
            socket = new DatagramSocket(port);
            isRunning = true;
            
            // System.out.println("UDP Position Server 시작됨 - 포트: " + port);
            
            // 메시지 수신 스레드 시작
            threadPool.execute(this::listenForMessages);
            
            // 클라이언트 정리 스레드 시작 (비활성 클라이언트 제거)
            threadPool.execute(this::cleanupInactiveClients);
            
            return true;
            
        } catch (SocketException e) {
            // System.err.println("UDP Position Server 시작 실패: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * UDP 메시지 수신 대기
     */
    private void listenForMessages() {
        byte[] buffer = new byte[BUFFER_SIZE];
        
        while (isRunning && socket != null && !socket.isClosed()) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                
                // 메시지 처리를 별도 스레드에서 수행
                threadPool.execute(() -> processMessage(packet));
                
            } catch (IOException e) {
                if (isRunning) {
                    // System.err.println("UDP 메시지 수신 오류: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 수신된 메시지 처리
     */
    private void processMessage(DatagramPacket packet) {
        String message = new String(packet.getData(), 0, packet.getLength());
        int clientPort = packet.getPort();
        
        // System.out.println("UDP 서버 수신: " + message + " from " + packet.getAddress() + ":" + clientPort);
        
        String[] parts = message.split(":");
        if (parts.length < 2) return;
        
        String messageType = parts[0];
        String clientId = parts[1];
        
        switch (messageType) {
            case "CONNECT":
                handleClientConnect(clientId, packet.getAddress(), clientPort);
                break;
                
            case "POS":
                if (parts.length >= 5) {
                    try {
                        double x = Double.parseDouble(parts[2]);
                        double y = Double.parseDouble(parts[3]);
                        String direction = parts[4];
                        
                        handlePositionUpdate(clientId, x, y, direction, 
                                           packet.getAddress(), clientPort);
                    } catch (NumberFormatException e) {
                        // System.err.println("잘못된 위치 데이터: " + message);
                    }
                }
                break;
                
            case "DISCONNECT":
                handleClientDisconnect(clientId);
                break;
        }
    }
    
    /**
     * 클라이언트 연결 처리
     */
    private void handleClientConnect(String clientId, InetAddress address, int port) {
        ClientInfo clientInfo = new ClientInfo(clientId, address, port);
        connectedClients.put(clientId, clientInfo);
        
        // System.out.println("UDP 클라이언트 연결: " + clientId + " (" + address + ":" + port + ")");
        
        if (positionListener != null) {
            positionListener.onClientConnected(clientId);
        }
    }
    
    /**
     * 위치 업데이트 처리
     */
    private void handlePositionUpdate(String clientId, double x, double y, String direction,
                                    InetAddress address, int port) {
        // 클라이언트 정보 업데이트 또는 추가
        ClientInfo clientInfo = connectedClients.get(clientId);
        if (clientInfo == null) {
            clientInfo = new ClientInfo(clientId, address, port);
            connectedClients.put(clientId, clientInfo);
        } else {
            clientInfo.updateLastSeen();
        }
        
        // 위치 업데이트 리스너에게 알림
        if (positionListener != null) {
            positionListener.onPositionUpdate(clientId, x, y, direction);
        }
        
        // 다른 모든 클라이언트에게 위치 업데이트 브로드캐스트
        broadcastPositionUpdate(clientId, x, y, direction);
    }
    
    /**
     * 클라이언트 연결 해제 처리
     */
    private void handleClientDisconnect(String clientId) {
        connectedClients.remove(clientId);
        
        // System.out.println("UDP 클라이언트 연결 해제: " + clientId);
        
        if (positionListener != null) {
            positionListener.onClientDisconnected(clientId);
        }
    }
    
    /**
     * 모든 클라이언트에게 위치 업데이트 브로드캐스트
     */
    private void broadcastPositionUpdate(String senderClientId, double x, double y, String direction) {
        String message = String.format("UPDATE:%s:%.1f:%.1f:%s", 
                senderClientId, x, y, direction);
        
        byte[] buffer = message.getBytes();
        
        // 송신자를 제외한 모든 클라이언트에게 전송
        for (ClientInfo client : connectedClients.values()) {
            if (!client.clientId.equals(senderClientId)) {
                try {
                    DatagramPacket packet = new DatagramPacket(
                        buffer, buffer.length, client.address, client.port);
                    socket.send(packet);
                } catch (IOException e) {
                    // System.err.println("브로드캐스트 전송 실패 to " + client.clientId + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * 비활성 클라이언트 정리 (30초 이상 업데이트 없음)
     */
    private void cleanupInactiveClients() {
        while (isRunning) {
            try {
                Thread.sleep(10000); // 10초마다 체크
                
                long currentTime = System.currentTimeMillis();
                long timeout = 30000; // 30초 타임아웃
                
                connectedClients.entrySet().removeIf(entry -> {
                    ClientInfo client = entry.getValue();
                    if (currentTime - client.lastUpdateTime > timeout) {
                        // System.out.println("비활성 클라이언트 제거: " + client.clientId);
                        
                        if (positionListener != null) {
                            positionListener.onClientDisconnected(client.clientId);
                        }
                        return true;
                    }
                    return false;
                });
                
            } catch (InterruptedException e) {
                break;
            }
        }
    }
    
    /**
     * 서버 중지
     */
    public void stopServer() {
        isRunning = false;
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        
        if (threadPool != null) {
            threadPool.shutdown();
        }
        
        connectedClients.clear();
        
        // System.out.println("UDP Position Server 중지됨");
    }
    
    /**
     * 연결된 클라이언트 수 반환
     */
    public int getConnectedClientCount() {
        return connectedClients.size();
    }
    
    /**
     * 서버 실행 상태 확인
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * 호스트 위치를 모든 클라이언트에게 브로드캐스트
     */
    public void broadcastHostPosition(String hostName, double x, double y, String direction) {
        if (!isRunning || socket == null || socket.isClosed()) {
            return;
        }
        
        String message = String.format("HOST_UPDATE:%s:%.1f:%.1f:%s", 
                hostName, x, y, direction);
        
        byte[] buffer = message.getBytes();
        
        // 모든 연결된 클라이언트에게 전송
        for (ClientInfo client : connectedClients.values()) {
            try {
                DatagramPacket packet = new DatagramPacket(
                    buffer, buffer.length, client.address, client.port);
                socket.send(packet);
            } catch (IOException e) {
                // System.err.println("호스트 위치 브로드캐스트 실패 to " + client.clientId + ": " + e.getMessage());
            }
        }
        
        // System.out.printf("[UDP] 호스트 위치 브로드캐스트: %s (%.1f, %.1f) %s -> %d 클라이언트%n", 
        //                  hostName, x, y, direction, connectedClients.size());
    }
}
