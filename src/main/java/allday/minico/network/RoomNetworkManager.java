package allday.minico.network;

import javafx.application.Platform;
import javafx.scene.image.ImageView;
import allday.minico.ui.miniroom.CharacterManager;
import allday.minico.ui.common.CustomAlert;
import allday.minico.ui.common.CustomChoiceDialog;

import java.util.List;

public class RoomNetworkManager {
    
    // 네트워크 관련 변수
    private MiniRoomServer server;
    private MiniRoomClient client;
    private MiniRoomDiscovery discovery;
    private String playerName;
    private boolean isHosting = false;
    private boolean isVisiting = false;
    private String hostName = null;
    private Thread broadcastThread; // 브로드캐스트 스레드 참조 추가
    
    // 캐릭터 관련 참조
    private ImageView character;
    private ImageView hostCharacter;
    
    // 콜백 인터페이스 정의
    public interface NetworkCallback {
        void onHostCharacterCreate(double x, double y, String direction);
        void onHostCharacterUpdate(double x, double y, String direction);
        void onVisitorCharacterCreate(String visitorName, double x, double y, String direction);
        void onVisitorCharacterUpdate(String visitorName, double x, double y, String direction);
        void onCharacterRemove(String characterName);
        void onChatMessage(String senderName, String message);
        void onHostingStatusChanged(boolean isHosting);
        void onVisitingStatusChanged(boolean isVisiting);
        void setHostName(String hostName);
        javafx.scene.layout.Pane getParentPane(); // UI 참조를 위한 메소드 추가
    }
    
    // 캐릭터 정보 포함한 확장 인터페이스
    public interface NetworkCallbackWithCharacterInfo extends NetworkCallback {
        void onVisitorCharacterUpdateWithCharacterInfo(String visitorName, double x, double y, String direction, String characterInfo);
        void onVisitorCharacterCreateWithCharacterInfo(String visitorName, double x, double y, String direction, String characterInfo);
        void onHostCharacterCreateWithCharacterInfo(double x, double y, String direction, String characterInfo);
    }
    
    private NetworkCallback callback;
    
    public RoomNetworkManager(String playerName, CharacterManager characterManager, 
                             ImageView character, NetworkCallback callback) {
        this.playerName = playerName;
        this.character = character;
        this.callback = callback;
        
        setupNetworking();
    }
    
    private void setupNetworking() {
        // 네트워크 탐색 초기화
        discovery = new MiniRoomDiscovery(new MiniRoomDiscovery.DiscoveryListener() {
            @Override
            public void onRoomDiscovered(MiniRoomDiscovery.RoomInfo roomInfo) {
                Platform.runLater(() -> {
                    // System.out.println("방 발견: " + roomInfo.toString()); // 성능 최적화를 위해 주석 처리
                });
            }

            @Override
            public void onRoomLost(String roomOwner) {
                Platform.runLater(() -> {
                    // System.out.println("방 사라짐: " + roomOwner); // 성능 최적화를 위해 주석 처리
                });
            }
        });

        discovery.startDiscovery();
    }
    
    public void startHosting() {
        if (isHosting) return;

        try {
            server = new MiniRoomServer(playerName);

            // 호스트 업데이트 리스너 설정
            server.setHostUpdateListener(new MiniRoomServer.HostUpdateListener() {
                @Override
                public void onVisitorJoined(String visitorName) {
                    Platform.runLater(() -> {
                        // System.out.println("방문자 접속: " + visitorName);
                        // 방문자 캐릭터를 기본 위치에 생성 (나중에 실제 위치로 업데이트됨)
                        callback.onVisitorCharacterCreate(visitorName, 100, 100, "front");
                    });
                }

                @Override
                public void onVisitorLeft(String visitorName) {
                    Platform.runLater(() -> {
                        // System.out.println("방문자 나감: " + visitorName);
                        // 방문자 캐릭터 제거
                        callback.onCharacterRemove(visitorName);
                    });
                }

                @Override
                public void onVisitorUpdate(String visitorName, double x, double y, String direction) {
                    Platform.runLater(() -> {
                        // System.out.println(String.format("호스트 - 방문자 업데이트: %s - X:%.2f Y:%.2f 방향:%s",
                        // visitorName, x, y, direction)); // 성능 최적화를 위해 주석 처리
                        // 호스트 화면에 방문자 캐릭터 업데이트 (캐릭터 정보 없음)
                        callback.onVisitorCharacterUpdate(visitorName, x, y, direction);
                    });
                }

                @Override
                public void onVisitorUpdateWithCharacterInfo(String visitorName, double x, double y, String direction, String characterInfo) {
                    Platform.runLater(() -> {
                        // System.out.println(String.format("호스트 - 방문자 업데이트 (캐릭터 정보 포함): %s - X:%.2f Y:%.2f 방향:%s 캐릭터:%s",
                        //         visitorName, x, y, direction, characterInfo));
                        // 호스트 화면에 방문자 캐릭터 업데이트 (캐릭터 정보 포함)
                        if (callback instanceof NetworkCallbackWithCharacterInfo) {
                            ((NetworkCallbackWithCharacterInfo) callback).onVisitorCharacterUpdateWithCharacterInfo(visitorName, x, y, direction, characterInfo);
                        } else {
                            // 기존 방식으로 폴백
                            callback.onVisitorCharacterUpdate(visitorName, x, y, direction);
                        }
                    });
                }

                @Override
                public void onChatMessage(String senderName, String message) {
                    Platform.runLater(() -> {
                        // System.out.println("채팅 메시지: " + senderName + " - " + message);
                        callback.onChatMessage(senderName, message);
                    });
                }
            });

            Thread serverThread = new Thread(server::startServer);
            serverThread.setDaemon(true);
            serverThread.start();

            // UI 쓰레드에서 호스트의 현재 위치를 설정 (서버 시작 후)
            Platform.runLater(() -> {
                if (character != null) {
                    server.setInitialHostPosition(
                        character.getLayoutX(), 
                        character.getLayoutY(), 
                        "front"
                    );
                }
            });

            // 방 정보 브로드캐스트 시작
            broadcastThread = new Thread(() -> {
                while (isHosting) {
                    discovery.broadcastRoom(playerName, server.getActualPort());
                    try {
                        Thread.sleep(5000); // 5초마다 브로드캐스트
                    } catch (InterruptedException e) {
                        break; // 스레드 중단 시 루프 종료
                    }
                }
            });
            broadcastThread.setDaemon(true);
            broadcastThread.start();

            isHosting = true;
            callback.onHostingStatusChanged(true);
            // System.out.println("미니룸 호스팅 시작");

        } catch (Exception e) {
            // System.out.println("호스팅 시작 오류: " + e.getMessage());
        }
    }
    
    public void stopHosting() {
        if (!isHosting) return;

        try {
            // 먼저 호스팅 상태를 false로 설정하여 브로드캐스트 중지
            isHosting = false;
            
            // 브로드캐스트 스레드 중단
            if (broadcastThread != null && broadcastThread.isAlive()) {
                broadcastThread.interrupt();
                broadcastThread = null;
            }
            
            // 방 종료 메시지 한 번만 브로드캐스트
            if (discovery != null) {
                discovery.broadcastRoomClosed(playerName);
            }
            
            if (server != null) {
                server.stopServer();
                server = null;
            }

            // 호스팅 중지 시 모든 방문자 캐릭터 제거
            Platform.runLater(() -> {
                callback.onCharacterRemove("__REMOVE_ALL__");
            });

            callback.onHostingStatusChanged(false);
            // System.out.println("미니룸 호스팅 중지");
            
        } catch (Exception e) {
            // System.out.println("호스팅 중지 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            // 오류가 발생해도 상태는 초기화
            isHosting = false;
            callback.onHostingStatusChanged(false);
        }
    }
    
    public void visitRoom(String hostIP, int port) {
        if (isVisiting) {
            // System.out.println("이미 방문 중입니다. 기존 연결을 종료하고 새로운 연결을 시작합니다.");
            leaveRoom();
            // 짧은 대기 시간을 두어 완전히 정리되도록 함
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        try {
            client = new MiniRoomClient(playerName, new MiniRoomClient.MiniRoomClientListenerWithCharacterInfo() {
                @Override
                public void onConnected(String roomOwner) {
                    Platform.runLater(() -> {
                        isVisiting = true;
                        callback.onVisitingStatusChanged(true);
                        // 방문 모드에서도 자신의 캐릭터는 보이게 유지
                        // System.out.println("방 접속 성공: " + roomOwner);

                        // 접속 직후 자신의 초기 위치를 서버에 전송 (캐릭터 정보 포함)
                        if (client != null && character != null) {
                            String characterInfo = getUserCharacterInfo();
                            client.sendMessage(String.format("VISITOR_UPDATE:%s:%.2f:%.2f:%s:%s",
                                    playerName, character.getLayoutX(), character.getLayoutY(), "front", characterInfo));
                        }
                    });
                }

                @Override
                public void onDisconnected() {
                    Platform.runLater(() -> {
                        isVisiting = false;
                        callback.onVisitingStatusChanged(false);
                        
                        // 연결 해제 시 모든 다른 캐릭터들 제거
                        callback.onCharacterRemove("__REMOVE_ALL__");
                        
                        // 호스트 정보 초기화
                        hostName = null;
                        callback.setHostName(null);
                        hostCharacter = null;
                        
                        // System.out.println("방 접속 종료 및 캐릭터 정리 완료");
                    });
                }

                @Override
                public void onCharacterUpdate(String owner, double x, double y, String direction) {
                    Platform.runLater(() -> {
                        // System.out.println(String.format("캐릭터 업데이트: %s - X:%.2f Y:%.2f 방향:%s",
                        // owner, x, y, direction)); // 성능 최적화를 위해 주석 처리
                        // 호스트 캐릭터 위치 업데이트
                        callback.onHostCharacterUpdate(x, y, direction);
                    });
                }

                @Override
                public void onRoomInfo(String owner, double x, double y, String direction) {
                    Platform.runLater(() -> {
                        // System.out.println(String.format("방 정보: %s - X:%.2f Y:%.2f 방향:%s",
                                // owner, x, y, direction));
                        // 호스트 이름 저장
                        hostName = owner;
                        callback.setHostName(owner);
                        // 호스트 캐릭터 초기 위치 설정
                        callback.onHostCharacterCreate(x, y, direction);
                    });
                }
                
                @Override
                public void onRoomInfoWithCharacterInfo(String owner, double x, double y, String direction, String characterInfo) {
                    Platform.runLater(() -> {
                        // System.out.println(String.format("방 정보 (캐릭터 정보 포함): %s - X:%.2f Y:%.2f 방향:%s 캐릭터:%s",
                        //         owner, x, y, direction, characterInfo));
                        // 호스트 이름 저장
                        hostName = owner;
                        callback.setHostName(owner);
                        
                        // 캐릭터 정보 포함한 호스트 생성 콜백이 있는지 확인
                        if (callback instanceof NetworkCallbackWithCharacterInfo) {
                            ((NetworkCallbackWithCharacterInfo) callback).onHostCharacterCreateWithCharacterInfo(x, y, direction, characterInfo);
                        } else {
                            // 기존 방식으로 폴백
                            callback.onHostCharacterCreate(x, y, direction);
                        }
                    });
                }

                @Override
                public void onVisitorUpdate(String visitorName, double x, double y, String direction) {
                    Platform.runLater(() -> {
                        // System.out.println(String.format("방문자 업데이트: %s - X:%.2f Y:%.2f 방향:%s",
                        // visitorName, x, y, direction)); // 성능 최적화를 위해 주석 처리
                        // 다른 방문자 캐릭터 업데이트
                        callback.onVisitorCharacterUpdate(visitorName, x, y, direction);
                    });
                }

                @Override
                public void onVisitorUpdateWithCharacterInfo(String visitorName, double x, double y, String direction, String characterInfo) {
                    Platform.runLater(() -> {
                        // System.out.println(String.format("방문자 업데이트 (캐릭터 정보 포함): %s - X:%.2f Y:%.2f 방향:%s 캐릭터:%s",
                                // visitorName, x, y, direction, characterInfo));
                        
                        // 캐릭터 정보 포함한 업데이트 콜백이 있는지 확인
                        if (callback instanceof NetworkCallbackWithCharacterInfo) {
                            ((NetworkCallbackWithCharacterInfo) callback).onVisitorCharacterUpdateWithCharacterInfo(visitorName, x, y, direction, characterInfo);
                        } else {
                            // 기존 방식으로 폴백
                            callback.onVisitorCharacterUpdate(visitorName, x, y, direction);
                        }
                    });
                }

                @Override
                public void onVisitorLeft(String visitorName) {
                    Platform.runLater(() -> {
                        System.out.println("방문자 나감: " + visitorName);
                        // 나간 방문자 캐릭터 제거
                        callback.onCharacterRemove(visitorName);
                    });
                }

                @Override
                public void onChatMessage(String senderName, String message) {
                    Platform.runLater(() -> {
                        // System.out.println("채팅 메시지: " + senderName + " - " + message);
                        callback.onChatMessage(senderName, message);
                    });
                }
            });

            if (client.connectToRoom(hostIP, port)) {
                // System.out.println("방 접속 시도: " + hostIP + ":" + port);
            } else {
                // System.out.println("방 접속 실패: " + hostIP + ":" + port);
                // 연결 실패 시 클라이언트 객체 정리
                client = null;
                Platform.runLater(() -> {
                    CustomAlert.showError(callback.getParentPane(), "연결 오류", 
                                         "서버에 연결할 수 없습니다. 다시 시도해주세요.");
                });
            }
        } catch (Exception e) {
            // System.out.println("방 접속 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            client = null;
            Platform.runLater(() -> {
                CustomAlert.showError(callback.getParentPane(), "연결 오류", 
                                     "방 접속 중 오류가 발생했습니다: " + e.getMessage());
            });
        }
    }
    
    public void leaveRoom() {
        if (!isVisiting) return;

        try {
            // 서버에 나간다는 메시지 전송 (다른 클라이언트들에게 알림)
            if (client != null && client.isConnected()) {
                client.sendMessage("LEAVE_ROOM:" + playerName);
                
                // 잠시 대기하여 메시지가 전송되도록 함
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            
            // 클라이언트 연결 해제 - 이때 MiniRoomClient.disconnect()에서 onDisconnected() 콜백이 호출됨
            if (client != null) {
                client.disconnect(); // 이 메서드에서 onDisconnected() 콜백이 호출됨
                client = null;
            }
            
            // 상태 초기화는 onDisconnected() 콜백에서 처리되므로 여기서는 제거
            // System.out.println("방 나가기 완료");
            
        } catch (Exception e) {
            // System.out.println("방 나가기 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            
            // 오류가 발생해도 상태는 초기화 (UI 스레드에서)
            Platform.runLater(() -> {
                isVisiting = false;
                callback.onVisitingStatusChanged(false);
                callback.onCharacterRemove("__REMOVE_ALL__");
                // 호스트 이름 초기화
                hostName = null;
                callback.setHostName(null);
                hostCharacter = null;
            });
            
            throw e; // 오류를 다시 던져서 호출자가 처리할 수 있도록
        }
    }
    
    public void sendChatMessage(String message) {
        if (isHosting && server != null) {
            // 호스트는 sendChatMessage를 사용하여 모든 클라이언트에게 메시지 전송
            // 호스트 자신에게는 onChatMessage 콜백을 통해 말풍선이 표시됨
            server.sendChatMessage(message);
        } else if (isVisiting && client != null) {
            // 방문자는 서버에게 메시지 전송
            client.sendMessage("CHAT:" + playerName + ":" + message);
        }
    }
    
    public void updateCharacterPosition(double x, double y, String direction) {
        String characterInfo = getUserCharacterInfo();
        // System.out.println("[RoomNetworkManager] updateCharacterPosition - 전송할 캐릭터 정보: " + characterInfo);
        
        if (isHosting && server != null) {
            // 호스팅 중이면 서버에 업데이트
            server.updateCharacterPosition(x, y, direction);
        } else if (isVisiting && client != null) {
            // 방문 중이면 클라이언트에서 서버로 전송 (캐릭터 정보 포함)
            String visitorMessage = String.format("VISITOR_UPDATE:%s:%.2f:%.2f:%s:%s",
                    playerName, x, y, direction, characterInfo);
            // System.out.println("[RoomNetworkManager] 방문자 메시지 전송: " + visitorMessage);
            client.sendMessage(visitorMessage);
        }
    }
    
    /**
     * 현재 사용자의 캐릭터 정보를 가져옵니다 (성별:캐릭터명 형식)
     */
    private String getUserCharacterInfo() {
        try {
            if (allday.minico.session.AppSession.getLoginMember() != null) {
                String memberId = allday.minico.session.AppSession.getLoginMember().getMemberId();
                
                // SkinUtil을 사용해서 캐릭터 정보 가져오기 (형식: "Male:온유")
                String result = allday.minico.utils.skin.SkinUtil.getCurrentUserCharacterInfo(memberId);
                // System.out.println("[RoomNetworkManager] getUserCharacterInfo 반환: " + result + " (memberId: " + memberId + ")");
                return result;
            }
        } catch (Exception e) {
            // // System.out.println("[RoomNetworkManager] 캐릭터 정보 조회 실패: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 기본값
        // System.out.println("[RoomNetworkManager] 기본값 반환: Male:대호");
        return "Male:대호";
    }
    
    public void showRoomSelectionDialog() {
        List<MiniRoomDiscovery.RoomInfo> rooms = discovery.getAvailableRooms();
        if (rooms.isEmpty()) {
            CustomAlert.showInformation(callback.getParentPane(), "미니룸 방문", 
                                       "현재 접속 가능한 미니룸이 없습니다.");
        } else {
            // 방 선택 다이얼로그
            CustomChoiceDialog.showRoomSelection(callback.getParentPane(), rooms, 
                                               room -> visitRoom(room.ipAddress, room.port));
        }
    }
    
    public void showHostingDialog(boolean startHosting) {
        String message;
        if (startHosting) {
            message = "미니룸 호스팅을 시작했습니다.\n다른 사용자들이 접속할 수 있습니다.";
        } else {
            message = "미니룸 호스팅을 중지했습니다.";
        }
        
        CustomAlert.showInformation(callback.getParentPane(), "미니룸 호스팅", message);
    }
    
    // Getter 메서드들
    public boolean isHosting() {
        return isHosting;
    }
    
    public boolean isVisiting() {
        return isVisiting;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    public void setHostCharacter(ImageView hostCharacter) {
        this.hostCharacter = hostCharacter;
    }
    
    public ImageView getHostCharacter() {
        return hostCharacter;
    }
    
    public void cleanup() {
        try {
            // 서버 정리
            if (server != null) {
                server.stopServer();
                server = null;
            }
            
            // 클라이언트 정리
            if (client != null) {
                client.disconnect();
                client = null;
            }
            
            // 디스커버리 정리
            if (discovery != null) {
                discovery.stopDiscovery(); // 만약 이런 메서드가 있다면
                // discovery cleanup if needed
            }
            
            // 상태 초기화
            isHosting = false;
            isVisiting = false;
            hostName = null;
            
            System.out.println("네트워크 매니저 정리 완료");
            
        } catch (Exception e) {
            // System.out.println("네트워크 매니저 정리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
