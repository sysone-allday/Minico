package allday.minico.network;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MiniRoomDiscovery {
    private static final int DISCOVERY_PORT = 8081;
    private static final String MULTICAST_ADDRESS = "230.0.0.1";
    
    private MulticastSocket multicastSocket;
    private InetAddress multicastGroup;
    private boolean isRunning = false;
    private Thread discoveryThread;
    private ConcurrentHashMap<String, RoomInfo> availableRooms;
    private DiscoveryListener listener;
    
    public interface DiscoveryListener {
        void onRoomDiscovered(RoomInfo roomInfo);
        void onRoomLost(String roomOwner);
    }
    
    public static class RoomInfo {
        public String owner;
        public String ipAddress;
        public int port;
        public long lastSeen;
        
        public RoomInfo(String owner, String ipAddress, int port) {
            this.owner = owner;
            this.ipAddress = ipAddress;
            this.port = port;
            this.lastSeen = System.currentTimeMillis();
        }
        
        @Override
        public String toString() {
            return owner + " (" + ipAddress + ":" + port + ")";
        }
    }
    
    public MiniRoomDiscovery(DiscoveryListener listener) {
        this.listener = listener;
        this.availableRooms = new ConcurrentHashMap<>();
    }
    
    public void startDiscovery() {
        try {
            multicastSocket = new MulticastSocket(DISCOVERY_PORT);
            multicastGroup = InetAddress.getByName(MULTICAST_ADDRESS);
            // 네트워크 인터페이스 자동 선택 (로컬 주소 기준)
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            SocketAddress groupAddress = new InetSocketAddress(multicastGroup, DISCOVERY_PORT);
            multicastSocket.joinGroup(groupAddress, networkInterface);

            isRunning = true;

            discoveryThread = new Thread(this::discoveryLoop);
            discoveryThread.setDaemon(true);
            discoveryThread.start();

            System.out.println("미니룸 탐색 시작");

        } catch (IOException e) {
            System.out.println("탐색 시작 오류: " + e.getMessage());
        }
    }
    
    public void stopDiscovery() {
        isRunning = false;
        try {
            if (multicastSocket != null) {
                // 네트워크 인터페이스 자동 선택
                NetworkInterface networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
                SocketAddress groupAddress = new InetSocketAddress(multicastGroup, DISCOVERY_PORT);
                multicastSocket.leaveGroup(groupAddress, networkInterface);
                multicastSocket.close();
            }
            if (discoveryThread != null) {
                discoveryThread.interrupt();
            }
            System.out.println("미니룸 탐색 중지");
        } catch (IOException e) {
            System.out.println("탐색 중지 오류: " + e.getMessage());
        }
    }
    
    public void broadcastRoom(String roomOwner, int port) {
        try {
            String message = "ROOM_BROADCAST:" + roomOwner + ":" + port;
            byte[] buffer = message.getBytes();
            
            DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, multicastGroup, DISCOVERY_PORT
            );
            
            multicastSocket.send(packet);
            // System.out.println("방 정보 브로드캐스트: " + message);
            
        } catch (IOException e) {
            System.out.println("브로드캐스트 오류: " + e.getMessage());
        }
    }
    
    public void broadcastRoomClosed(String roomOwner) {
        try {
            String message = "ROOM_CLOSED:" + roomOwner;
            byte[] buffer = message.getBytes();
            
            DatagramPacket packet = new DatagramPacket(
                buffer, buffer.length, multicastGroup, DISCOVERY_PORT
            );
            
            multicastSocket.send(packet);
            System.out.println("방 종료 브로드캐스트: " + message);
            
        } catch (IOException e) {
            System.out.println("방 종료 브로드캐스트 오류: " + e.getMessage());
        }
    }
    
    private void discoveryLoop() {
        byte[] buffer = new byte[1024];
        
        while (isRunning) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                multicastSocket.receive(packet);
                
                String message = new String(packet.getData(), 0, packet.getLength());
                String senderIP = packet.getAddress().getHostAddress();
                
                handleDiscoveryMessage(message, senderIP);
                
            } catch (IOException e) {
                if (isRunning) {
                    System.out.println("탐색 메시지 수신 오류: " + e.getMessage());
                }
            }
        }
    }
    
    private void handleDiscoveryMessage(String message, String senderIP) {
        String[] parts = message.split(":");
        if (parts.length < 2) return;
        
        String messageType = parts[0];
        if ("ROOM_BROADCAST".equals(messageType)) {
            if (parts.length < 3) return;
            String roomOwner = parts[1];
            int port = Integer.parseInt(parts[2]);
            
            RoomInfo roomInfo = new RoomInfo(roomOwner, senderIP, port);
            availableRooms.put(roomOwner, roomInfo);
            
            if (listener != null) {
                listener.onRoomDiscovered(roomInfo);
            }
        } else if ("ROOM_CLOSED".equals(messageType)) {
            String roomOwner = parts[1];
            
            // 방 정보 즉시 제거
            RoomInfo removedRoom = availableRooms.remove(roomOwner);
            if (removedRoom != null && listener != null) {
                listener.onRoomLost(roomOwner);
                System.out.println("방 종료 감지: " + roomOwner);
            }
        }
    }
    
    public List<RoomInfo> getAvailableRooms() {
        // 오래된 방 정보 제거 (10초 이상 응답 없음)
        long currentTime = System.currentTimeMillis();
        List<String> toRemove = new ArrayList<>();
        
        for (RoomInfo room : availableRooms.values()) {
            if (currentTime - room.lastSeen > 10000) {
                toRemove.add(room.owner);
            }
        }
        
        for (String owner : toRemove) {
            availableRooms.remove(owner);
            if (listener != null) {
                listener.onRoomLost(owner);
            }
        }
        
        return new ArrayList<>(availableRooms.values());
    }
}
