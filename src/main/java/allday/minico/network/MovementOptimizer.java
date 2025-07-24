package allday.minico.network;

import javafx.application.Platform;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;


//  네트워크 통신과 UI 업데이트를 최적화하는 스레드 풀 관리자
// 네트워크 메시지 처리와 UI 업데이트를 분리
//  Platform.runLater 호출 최소화
// 배치 업데이트를 통한 성능 향상
 
public class MovementOptimizer {
    private static final int NETWORK_THREAD_POOL_SIZE = 4;
    private static final int UI_UPDATE_BATCH_SIZE = 10;
    private static final long UI_UPDATE_INTERVAL_MS = 16; // ~60fps
    
    // 스레드 풀들
    private final ExecutorService networkThreadPool;
    private final ScheduledExecutorService uiUpdateScheduler;
    private final ExecutorService messageProcessorPool;
    
    // UI 업데이트 배치 처리
    private final ConcurrentLinkedQueue<UIUpdateTask> pendingUIUpdates;
    private final AtomicLong lastUIUpdateTime;
    
    // 싱글톤 인스턴스
    private static MovementOptimizer instance;
    
    private MovementOptimizer() {
        this.networkThreadPool = Executors.newFixedThreadPool(NETWORK_THREAD_POOL_SIZE, 
            r -> {
                Thread t = new Thread(r, "Network-Worker");
                t.setDaemon(true);
                return t;
            });
            
        this.messageProcessorPool = Executors.newFixedThreadPool(2, 
            r -> {
                Thread t = new Thread(r, "Message-Processor");
                t.setDaemon(true);
                return t;
            });
            
        this.uiUpdateScheduler = Executors.newSingleThreadScheduledExecutor(
            r -> {
                Thread t = new Thread(r, "UI-Update-Scheduler");
                t.setDaemon(true);
                return t;
            });
            
        this.pendingUIUpdates = new ConcurrentLinkedQueue<>();
        this.lastUIUpdateTime = new AtomicLong(System.currentTimeMillis());
        
        // UI 업데이트 스케줄러 시작
        startUIUpdateScheduler();
    }
    
    public static synchronized MovementOptimizer getInstance() {
        if (instance == null) {
            instance = new MovementOptimizer();
        }
        return instance;
    }
    
 // 네트워크 작업을 스레드 풀에서 처리
  
    public void processNetworkTask(Runnable networkTask) {
        networkThreadPool.submit(() -> {
            try {
                networkTask.run();
            } catch (Exception e) {
                System.err.println("네트워크 작업 처리 오류: " + e.getMessage());
            }
        });
    }
    
    //메시지 처리 작업을 전용 스레드 풀에서 처리
     
    public void processMessage(Runnable messageTask) {
        messageProcessorPool.submit(() -> {
            try {
                messageTask.run();
            } catch (Exception e) {
                System.err.println("메시지 처리 오류: " + e.getMessage());
            }
        });
    }
    
    
    //UI 업데이트를 배치로 처리 (Platform.runLater 호출 최소화)
    
    public void scheduleUIUpdate(UIUpdateTask task) {
        pendingUIUpdates.offer(task);
    }
    
   
    //즉시 UI 업데이트가 필요한 경우 (채팅 메시지 등)
    
    public void immediateUIUpdate(Runnable uiTask) {
        Platform.runLater(() -> {
            try {
                uiTask.run();
            } catch (Exception e) {
                System.err.println("UI 업데이트 오류: " + e.getMessage());
            }
        });
    }
    
  
     // UI 업데이트 스케줄러 시작
    
    private void startUIUpdateScheduler() {
        uiUpdateScheduler.scheduleAtFixedRate(() -> {
            if (!pendingUIUpdates.isEmpty()) {
                processBatchUIUpdates();
            }
        }, UI_UPDATE_INTERVAL_MS, UI_UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }
    

     //배치 UI 업데이트 처리

    private void processBatchUIUpdates() {
        if (pendingUIUpdates.isEmpty()) return;
        
        // 배치로 UI 업데이트 수집
        var updates = new ConcurrentLinkedQueue<UIUpdateTask>();
        for (int i = 0; i < UI_UPDATE_BATCH_SIZE && !pendingUIUpdates.isEmpty(); i++) {
            UIUpdateTask task = pendingUIUpdates.poll();
            if (task != null) {
                updates.offer(task);
            }
        }
        
        if (!updates.isEmpty()) {
            Platform.runLater(() -> {
                // 모든 업데이트를 한 번에 처리
                while (!updates.isEmpty()) {
                    UIUpdateTask task = updates.poll();
                    if (task != null) {
                        try {
                            task.execute();
                        } catch (Exception e) {
                            System.err.println("배치 UI 업데이트 오류: " + e.getMessage());
                        }
                    }
                }
                lastUIUpdateTime.set(System.currentTimeMillis());
            });
        }
    }
    
    
    //위치 업데이트 최적화 (중복 업데이트 필터링)
    
    public void schedulePositionUpdate(String characterId, double x, double y, String direction, 
                                     PositionUpdateCallback callback) {
        scheduleUIUpdate(new UIUpdateTask() {
            private final String id = characterId;
            private final double newX = x;
            private final double newY = y;
            private final String newDirection = direction;
            
            @Override
            public void execute() {
                callback.updatePosition(id, newX, newY, newDirection);
            }
            
            @Override
            public String getTaskId() {
                return "position_" + id;
            }
            
            @Override
            public boolean shouldReplace(UIUpdateTask other) {
                // 같은 캐릭터의 위치 업데이트는 최신 것으로 교체
                return other.getTaskId().equals(getTaskId());
            }
        });
    }

    public void shutdown() {
        try {
            networkThreadPool.shutdown();
            messageProcessorPool.shutdown();
            uiUpdateScheduler.shutdown();
            
            if (!networkThreadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                networkThreadPool.shutdownNow();
            }
            if (!messageProcessorPool.awaitTermination(5, TimeUnit.SECONDS)) {
                messageProcessorPool.shutdownNow();
            }
            if (!uiUpdateScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                uiUpdateScheduler.shutdownNow();
            }
            
            System.out.println("MovementOptimizer 리소스 정리 완료");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("MovementOptimizer 종료 중 인터럽트 발생");
        }
    }

    public interface UIUpdateTask {
        void execute();
        String getTaskId();
        default boolean shouldReplace(UIUpdateTask other) {
            return false;
        }
    }

    public interface PositionUpdateCallback {
        void updatePosition(String characterId, double x, double y, String direction);
    }
    
 
    public String getStatistics() {
        return String.format(
            "네트워크 스레드 풀: %s, 메시지 처리 풀: %s, 대기 중인 UI 업데이트: %d",
            networkThreadPool.toString(),
            messageProcessorPool.toString(),
            pendingUIUpdates.size()
        );
    }
}
