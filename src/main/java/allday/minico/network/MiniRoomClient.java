package allday.minico.network;

import java.io.*;
import java.net.*;

public class MiniRoomClient {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private String clientId;
    private boolean isConnected = false;
    private MiniRoomClientListener listener;
    
    public interface MiniRoomClientListener {
        void onConnected(String roomOwner);
        void onDisconnected();
        void onCharacterUpdate(String owner, double x, double y, String direction);
        void onRoomInfo(String owner, double x, double y, String direction);
        void onVisitorUpdate(String visitorName, double x, double y, String direction);
        void onChatMessage(String senderName, String message);
        void onVisitorLeft(String visitorName); // 새로 추가
    }
    
    public MiniRoomClient(String clientId, MiniRoomClientListener listener) {
        this.clientId = clientId;
        this.listener = listener;
    }
    
    public boolean connectToRoom(String hostIP, int port) {
        try {
            socket = new Socket(hostIP, port);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            
            // 클라이언트 ID 전송
            writer.println(clientId);
            
            isConnected = true;
            System.out.println("미니룸 접속 성공: " + hostIP + ":" + port);
            
            // 서버 메시지 수신 시작
            Thread messageThread = new Thread(this::listenForMessages);
            messageThread.setDaemon(true);
            messageThread.start();
            
            if (listener != null) {
                listener.onConnected("연결됨");
            }
            
            return true;
            
        } catch (IOException e) {
            System.out.println("미니룸 접속 실패: " + e.getMessage());
            return false;
        }
    }
    
    public void disconnect() {
        isConnected = false;
        try {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
            if (socket != null) socket.close();
            
            if (listener != null) {
                listener.onDisconnected();
            }
            
            System.out.println("미니룸 연결 종료");
        } catch (IOException e) {
            System.out.println("연결 종료 오류: " + e.getMessage());
        }
    }
    
    public void sendMessage(String message) {
        if (writer != null && isConnected) {
            writer.println(message);
        }
    }
    
    public void sendChatMessage(String message) {
        if (writer != null && isConnected) {
            String chatMessage = String.format("CHAT:%s:%s", clientId, message);
            writer.println(chatMessage);
        }
    }
    
    private void listenForMessages() {
        try {
            String message;
            while (isConnected && (message = reader.readLine()) != null) {
                handleServerMessage(message);
            }
        } catch (IOException e) {
            if (isConnected) {
                System.out.println("서버 메시지 수신 오류: " + e.getMessage());
            }
        }
    }
    
    private void handleServerMessage(String message) {
        // System.out.println("서버 메시지: " + message); // 성능 최적화를 위해 주석 처리
        
        if (listener == null) return;
        
        String[] parts = message.split(":");
        if (parts.length < 2) return;
        
        String messageType = parts[0];
        
        switch (messageType) {
            case "ROOM_INFO":
                if (parts.length >= 5) {
                    String owner = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    String direction = parts[4];
                    listener.onRoomInfo(owner, x, y, direction);
                }
                break;
                
            case "CHARACTER_UPDATE":
                if (parts.length >= 5) {
                    String owner = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    String direction = parts[4];
                    listener.onCharacterUpdate(owner, x, y, direction);
                }
                break;
                
            case "VISITOR_UPDATE":
                if (parts.length >= 5) {
                    String visitorName = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    String direction = parts[4];
                    listener.onVisitorUpdate(visitorName, x, y, direction);
                }
                break;
                
            case "VISITOR_LEFT":
                if (parts.length >= 2) {
                    String visitorName = parts[1];
                    listener.onVisitorLeft(visitorName);
                }
                break;
                
            case "CHAT":
                if (parts.length >= 3) {
                    String senderName = parts[1];
                    String chatMessage = parts[2];
                    listener.onChatMessage(senderName, chatMessage);
                }
                break;
        }
    }
    
    public boolean isConnected() {
        return isConnected;
    }
}
