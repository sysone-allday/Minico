package allday.minico.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MiniRoomServer {
    private static final int DEFAULT_PORT = 8080;
    private ServerSocket serverSocket;
    private boolean isRunning = false;
    private ExecutorService threadPool;
    private ConcurrentHashMap<String, ClientHandler> connectedClients;
    private String roomOwner;
    private double characterX;
    private double characterY;
    private String characterDirection;
    private long lastUpdateTime = 0;
    private static final long UPDATE_INTERVAL = 50; // 50ms 간격으로 업데이트 (20fps)
    private HostUpdateListener hostUpdateListener;

    public interface HostUpdateListener {
        void onVisitorJoined(String visitorName);

        void onVisitorLeft(String visitorName);

        void onVisitorUpdate(String visitorName, double x, double y, String direction);

        void onChatMessage(String senderName, String message);
    }

    public MiniRoomServer(String roomOwner) {
        this.roomOwner = roomOwner;
        this.connectedClients = new ConcurrentHashMap<>();
        this.threadPool = Executors.newCachedThreadPool();
        this.characterX = 0;
        this.characterY = 0;
        this.characterDirection = "front";
    }

    public void setHostUpdateListener(HostUpdateListener listener) {
        this.hostUpdateListener = listener;
    }

    // 호스트의 초기 위치를 설정하는 메서드
    public void setInitialHostPosition(double x, double y, String direction) {
        this.characterX = x;
        this.characterY = y;
        this.characterDirection = direction;
        System.out.println(String.format("호스트 초기 위치 설정: X=%.2f, Y=%.2f, 방향=%s", x, y, direction));
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            serverSocket.setReuseAddress(true);
            isRunning = true;
            System.out.println("미니룸 서버 시작됨 - 포트: " + DEFAULT_PORT);
            System.out.println("방 주인: " + roomOwner);

            // 클라이언트 연결 대기
            while (isRunning) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientSocket.setTcpNoDelay(true); // Nagle 알고리즘 비활성화 (즉시 전송)
                    clientSocket.setSoTimeout(30000); // 30초 타임아웃
                    clientSocket.setKeepAlive(true);
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    threadPool.submit(clientHandler);
                } catch (IOException e) {
                    if (isRunning) {
                        System.out.println("클라이언트 연결 수락 오류: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("서버 시작 오류: " + e.getMessage());
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            threadPool.shutdown();
            System.out.println("미니룸 서버 중지됨");
        } catch (IOException e) {
            System.out.println("서버 중지 오류: " + e.getMessage());
        }
    }

    public void updateCharacterPosition(double x, double y, String direction) {
        long currentTime = System.currentTimeMillis();

        // 업데이트 빈도 제한 (성능 최적화)
        if (currentTime - lastUpdateTime < UPDATE_INTERVAL) {
            return;
        }

        this.characterX = x;
        this.characterY = y;
        this.characterDirection = direction;
        this.lastUpdateTime = currentTime;

        // 모든 연결된 클라이언트에게 캐릭터 위치 업데이트 전송
        String updateMessage = String.format("CHARACTER_UPDATE:%s:%.2f:%.2f:%s",
                roomOwner, x, y, direction);
        broadcastToClients(updateMessage);
    }

    public void broadcastToClients(String message) {
        for (ClientHandler client : connectedClients.values()) {
            client.sendMessage(message);
        }
    }

    public void addClient(String clientId, ClientHandler handler) {
        connectedClients.put(clientId, handler);
        System.out.println("새 방문자 접속: " + clientId);

        // 호스트에게 새 방문자 접속 알림
        if (hostUpdateListener != null) {
            hostUpdateListener.onVisitorJoined(clientId);
        }
    }

    public void removeClient(String clientId) {
        connectedClients.remove(clientId);
        System.out.println("방문자 나감: " + clientId);

        // 모든 클라이언트에게 해당 방문자가 나갔다는 메시지 브로드캐스트
        String disconnectMessage = String.format("VISITOR_LEFT:%s", clientId);
        broadcastToClients(disconnectMessage);

        // 호스트에게 방문자 나감 알림
        if (hostUpdateListener != null) {
            hostUpdateListener.onVisitorLeft(clientId);
        }
    }

    public String getRoomInfo() {
        return String.format("ROOM_INFO:%s:%.2f:%.2f:%s",
                roomOwner, characterX, characterY, characterDirection);
    }

    public void sendChatMessage(String message) {
        String chatBroadcast = String.format("CHAT:%s:%s", roomOwner, message);
        broadcastToClients(chatBroadcast);

        // 호스트에게도 채팅 메시지 알림
        if (hostUpdateListener != null) {
            hostUpdateListener.onChatMessage(roomOwner, message);
        }
    }

    // 클라이언트 처리 내부 클래스
    private static class ClientHandler implements Runnable {
        private Socket socket;
        private MiniRoomServer server;
        private BufferedReader reader;
        private PrintWriter writer;
        private String clientId;

        public ClientHandler(Socket socket, MiniRoomServer server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                // 클라이언트 ID 받기
                clientId = reader.readLine();
                server.addClient(clientId, this);

                // 현재 방 정보 전송
                sendMessage(server.getRoomInfo());

                // 클라이언트 메시지 처리
                String message;
                while ((message = reader.readLine()) != null) {
                    handleClientMessage(message);
                }

            } catch (IOException e) {
                System.out.println("클라이언트 처리 오류: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void handleClientMessage(String message) {
            // System.out.println("클라이언트 메시지: " + message); // 성능 최적화를 위해 주석 처리

            // 방문자 캐릭터 업데이트 처리
            if (message.startsWith("VISITOR_UPDATE:")) {
                String[] parts = message.split(":");
                if (parts.length >= 5) {
                    String visitorName = parts[1];
                    double x = Double.parseDouble(parts[2]);
                    double y = Double.parseDouble(parts[3]);
                    String direction = parts[4];

                    String updateMessage = String.format("VISITOR_UPDATE:%s:%.2f:%.2f:%s",
                            visitorName, x, y, direction);
                    // 다른 클라이언트들에게 방문자 움직임 브로드캐스트
                    server.broadcastToClients(updateMessage);

                    // 호스트에게도 방문자 업데이트 알림
                    if (server.hostUpdateListener != null) {
                        server.hostUpdateListener.onVisitorUpdate(visitorName, x, y, direction);
                    }
                }
            }
            // 방 나가기 처리
            else if (message.startsWith("LEAVE_ROOM:")) {
                String[] parts = message.split(":");
                if (parts.length >= 2) {
                    String leavingPlayerName = parts[1];
                    // 해당 클라이언트 즉시 제거 (cleanup에서도 처리되지만 명시적으로 처리)
                    server.removeClient(leavingPlayerName);
                    System.out.println("클라이언트가 명시적으로 방을 나감: " + leavingPlayerName);
                }
            }
            // 채팅 메시지 처리 (CHAT: 형식)
            else if (message.startsWith("CHAT:")) {
                String[] parts = message.split(":", 3); // 3개로 제한하여 메시지에 콜론이 있어도 처리
                if (parts.length >= 3) {
                    String senderName = parts[1];
                    String chatMessage = parts[2];

                    String chatBroadcast = String.format("CHAT:%s:%s", senderName, chatMessage);
                    // 모든 클라이언트에게 채팅 메시지 브로드캐스트
                    server.broadcastToClients(chatBroadcast);

                    // 호스트에게도 채팅 메시지 알림
                    if (server.hostUpdateListener != null) {
                        server.hostUpdateListener.onChatMessage(senderName, chatMessage);
                    }
                }
            }
        }

        public void sendMessage(String message) {
            if (writer != null) {
                writer.println(message);
            }
        }

        private void cleanup() {
            try {
                if (clientId != null) {
                    server.removeClient(clientId);
                }
                if (reader != null)
                    reader.close();
                if (writer != null)
                    writer.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                System.out.println("클라이언트 정리 오류: " + e.getMessage());
            }
        }
    }
}
