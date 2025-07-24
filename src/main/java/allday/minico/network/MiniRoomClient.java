package allday.minico.network;

import allday.minico.session.AppSession;
import allday.minico.utils.skin.SkinUtil;

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

    // 캐릭터 정보 포함한 확장 인터페이스
    public interface MiniRoomClientListenerWithCharacterInfo extends MiniRoomClientListener {
        void onVisitorUpdateWithCharacterInfo(String visitorName, double x, double y, String direction, String characterInfo);
        void onRoomInfoWithCharacterInfo(String owner, double x, double y, String direction, String characterInfo);
    }

    public MiniRoomClient(String clientId, MiniRoomClientListener listener) {
        this.clientId = clientId;
        this.listener = listener;
    }

    public boolean connectToRoom(String hostIP, int port) {
        try {
            socket = new Socket(hostIP, port);

            // TCP 성능 최적화 옵션 설정
            socket.setTcpNoDelay(true); // Nagle 알고리즘 비활성화로 지연 감소
            socket.setKeepAlive(true); // Keep-alive 활성화

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

    public void sendPositionUpdate(double x, double y, String direction) {
        if (writer != null && isConnected) {
            String characterInfo = SkinUtil.getCurrentUserCharacterInfo(AppSession.getLoginMember().getMemberId());
            String message = String.format("UPDATE_POSITION:%s:%.2f:%.2f:%s:%s",
                    clientId, x, y, direction, characterInfo);
            writer.println(message);
            System.out.println("클라이언트 - 위치 및 캐릭터 정보 전송: " + message);
            System.out.println("클라이언트 - 전송된 캐릭터 정보 상세: " + characterInfo + " (clientId: " + clientId + ")");
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
        // System.out.println("서버 메시지: " + message);

        if (listener == null) return;

        String[] parts = message.split(":");
        if (parts.length < 2) return;

        String messageType = parts[0];

        switch (messageType) {
            case "ROOM_INFO":
                if (parts.length >= 6) { // 캐릭터 정보 포함으로 6개 필요
                    String owner = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    String direction = parts[4];
                    // 캐릭터 정보는 parts[5]:parts[6] 형태로 조합 (Male:온유)
                    String characterInfo = parts.length >= 6 ? parts[5] + ":" + parts[6] : "Male:대호";

                    // System.out.println("[MiniRoomClient] 호스트 방 정보 - 이름: " + owner + ", 캐릭터: " + characterInfo);

                    // 캐릭터 정보 포함한 콜백 호출
                    if (listener instanceof MiniRoomClientListenerWithCharacterInfo) {
                        ((MiniRoomClientListenerWithCharacterInfo) listener).onRoomInfoWithCharacterInfo(owner, x, y, direction, characterInfo);
                    } else {
                        // 기존 방식으로 폴백
                        listener.onRoomInfo(owner, x, y, direction);
                    }
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
                if (parts.length >= 6) { // Female:민서 형태를 고려하여 최소 6개 필요
                    String visitorName = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    String direction = parts[4];
                    // 캐릭터 정보는 parts[5]:parts[6] 형태로 조합 (Female:민서)
                    String characterInfo = parts.length >= 6 ? parts[5] + ":" + parts[6] : "Male:대호";

                    // System.out.println("[MiniRoomClient] 파싱된 방문자 정보 - 이름: " + visitorName + ", 캐릭터: " + characterInfo);

                    // 캐릭터 정보 포함한 업데이트 콜백
                    if (listener instanceof MiniRoomClientListenerWithCharacterInfo) {
                        ((MiniRoomClientListenerWithCharacterInfo) listener).onVisitorUpdateWithCharacterInfo(visitorName, x, y, direction, characterInfo);
                    } else {
                        // 기존 방식으로 폴백
                        listener.onVisitorUpdate(visitorName, x, y, direction);
                    }
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
