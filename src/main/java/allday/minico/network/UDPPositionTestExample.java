package allday.minico.network;

/**
 * UDP 위치 전송 시스템 테스트 및 사용 예시
 */
public class UDPPositionTestExample {
    
    public static void main(String[] args) {
        // 서버 테스트
        testUDPServer();
        
        // 클라이언트 테스트
        testUDPClient();
    }
    
    /**
     * UDP 위치 서버 테스트
     */
    private static void testUDPServer() {
        System.out.println("=== UDP Position Server 테스트 ===");
        
        UDPPositionServer server = new UDPPositionServer();
        
        // 위치 업데이트 리스너 설정
        server.setPositionUpdateListener(new UDPPositionServer.PositionUpdateListener() {
            @Override
            public void onClientConnected(String clientId) {
                System.out.println("클라이언트 연결: " + clientId);
            }
            
            @Override
            public void onClientDisconnected(String clientId) {
                System.out.println("클라이언트 연결 해제: " + clientId);
            }
            
            @Override
            public void onPositionUpdate(String clientId, double x, double y, String direction) {
                System.out.printf("위치 업데이트: %s -> (%.1f, %.1f) %s%n", 
                                clientId, x, y, direction);
            }
        });
        
        // 서버 시작
        if (server.startServer(9080)) {
            System.out.println("UDP 서버가 포트 9080에서 시작되었습니다.");
            
            // 10초 후 서버 종료
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            server.stopServer();
            System.out.println("UDP 서버가 종료되었습니다.");
        }
    }
    
    /**
     * UDP 위치 클라이언트 테스트
     */
    private static void testUDPClient() {
        System.out.println("\\n=== UDP Position Client 테스트 ===");
        
        UDPPositionClient client = new UDPPositionClient("테스트플레이어");
        
        // 서버에 연결
        if (client.connect("localhost", 9080)) {
            System.out.println("UDP 서버에 연결되었습니다.");
            
            // 위치 업데이트 전송 시뮬레이션
            for (int i = 0; i < 5; i++) {
                double x = 100 + i * 10;
                double y = 200 + i * 5;
                String direction = (i % 2 == 0) ? "RIGHT" : "LEFT";
                
                client.sendPositionUpdate(x, y, direction);
                System.out.printf("위치 전송: (%.1f, %.1f) %s%n", x, y, direction);
                
                try {
                    Thread.sleep(500); // 0.5초 간격
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            client.disconnect();
            System.out.println("UDP 클라이언트 연결이 해제되었습니다.");
        } else {
            System.out.println("UDP 서버 연결에 실패했습니다.");
        }
    }
    
    /**
     * 성능 테스트 - 대량의 위치 업데이트 전송
     */
    public static void performanceTest() {
        System.out.println("\\n=== 성능 테스트 ===");
        
        UDPPositionClient client = new UDPPositionClient("성능테스트");
        
        if (client.connect("localhost", 9080)) {
            long startTime = System.currentTimeMillis();
            int updateCount = 1000;
            
            // 1000번의 위치 업데이트 전송
            for (int i = 0; i < updateCount; i++) {
                client.sendPositionUpdate(i, i, "RIGHT");
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            System.out.printf("%d개 위치 업데이트 전송 완료%n", updateCount);
            System.out.printf("소요 시간: %d ms%n", duration);
            System.out.printf("평균 전송 시간: %.2f ms/update%n", 
                            (double) duration / updateCount);
            
            client.disconnect();
        }
    }
}
