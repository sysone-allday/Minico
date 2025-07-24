package allday.minico.controller.member;

import allday.minico.dto.member.Friend;
import allday.minico.service.member.FriendService;
import allday.minico.session.AppSession;
import allday.minico.utils.member.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class FriendInfoController implements Initializable {

    @FXML private Label friendListLabel;
    @FXML private ListView<Friend> friendListView;
    @FXML private Label receivedFriendRequestsLabel;
    @FXML private ListView<Friend> receivedFriendRequestsListView;
    @FXML private Button searchFriendButton;

    private ObservableList<Friend> friendList = FXCollections.observableArrayList();
    private ObservableList<Friend> receivedRequests = FXCollections.observableArrayList();
    private final FriendService friendService = FriendService.getInstance();

    @FXML
    void searchFriend(ActionEvent event) {
        SceneManager.showModal("findFriend", "친구 찾기");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String myId = AppSession.getLoginMember().getMemberId();

        // 1. 친구 목록 받아오기
        List<Friend> myFriends = friendService.getFriendList(myId); //
        friendList.addAll(myFriends);
        friendListView.setItems(friendList);

        friendListView.setCellFactory(listView -> new ListCell<Friend>() {
            @Override
            protected void updateItem(Friend friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty || friend == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label nicknameLabel = new Label(friend.getFriendNickname());
                    Label levelLabel = new Label("LV: " + friend.getFriendLevel());
                    Label idLabel = new Label("ID: " + friend.getFriendId());

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String formattedTime = friend.getLastLogoutTime() != null // 회원가입만 하고 로그인 기록이 없는 유저는 마지막 로그아웃시간이 null 임을 고려!
                            ? friend.getLastLogoutTime().format(formatter) : "알 수 없음";
                    Label logoutTimeLabel = new Label("마지막 접속: " + formattedTime);

                    Button deleteBtn = new Button("삭제");
                    deleteBtn.setOnAction(e -> {
                        boolean success = friendService.deleteFriend(myId, friend.getFriendId());
                        if (success) {
                            System.out.println(friend.getFriendId() + " 삭제됨");
                            friendListView.getItems().remove(friend);
                        }
                    });
                    deleteBtn.getStyleClass().addAll("friendinfo-action-btn", "delete-btn"); // 스타일 클래스 추가

                    nicknameLabel.setMinWidth(100);
                    levelLabel.setMinWidth(60);
                    idLabel.setMinWidth(120);
                    logoutTimeLabel.setMinWidth(180);

                    HBox hbox = new HBox(10, nicknameLabel, levelLabel, idLabel, logoutTimeLabel, deleteBtn);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });

        // ========================================= 친구 요청 목록 ===============================================

        List<Friend> receivedList = friendService.getReceivedFriendRequestsList(myId); // 나에게 친추 요청한 친구 DTO 리스트
        receivedRequests.addAll(receivedList); // 친구 요청 목록 리스트 뷰에 추가
        receivedFriendRequestsListView.setItems(receivedRequests);

        receivedFriendRequestsListView.setCellFactory(listView -> new ListCell<Friend>() {
            @Override
            protected void updateItem(Friend friend, boolean empty) {
                super.updateItem(friend, empty);
                if (empty || friend == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label nicknameLabel = new Label(friend.getFriendNickname());
                    Label levelLabel = new Label("LV: " + friend.getFriendLevel());
                    Label idLabel = new Label("ID: " + friend.getFriendId());
                    Label timeLabel = new Label("요청시간: " +
                            friend.getRequestedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

                    Button acceptBtn = new Button("수락");
                    Button rejectBtn = new Button("거절");

                    acceptBtn.setOnAction(e -> { // 수락 버튼 클릭 시 !
                        boolean success = friendService.acceptRequest(friend.getFriendId(), myId);
                        if (success) {
                            System.out.println(friend.getFriendId() + " 수락됨");
                            receivedFriendRequestsListView.getItems().remove(friend); // 버튼 클릭하면 요청목록에서 삭제
                            friendList.add(friend); // 친구 목록에 추가!
                        }
                    });

                    rejectBtn.setOnAction(e -> { // 거절 버튼 클릭 시 !
                        boolean success = friendService.rejectRequest(friend.getFriendId(), myId);
                        if (success) {
                            System.out.println(friend.getFriendId() + " 거절됨");
                            receivedFriendRequestsListView.getItems().remove(friend); // 버튼 클릭하면 요청목록에서 삭제
                        }
                    });

                    acceptBtn.getStyleClass().addAll("friendinfo-action-btn", "accept-btn"); //  수락 버튼 스타일 추가
                    rejectBtn.getStyleClass().addAll("friendinfo-action-btn", "reject-btn"); //  거절 버튼 스타일 추가

                    nicknameLabel.setMinWidth(100);
                    levelLabel.setMinWidth(60);
                    idLabel.setMinWidth(120);
                    timeLabel.setMinWidth(180);

                    HBox hbox = new HBox(10, nicknameLabel, levelLabel, idLabel, timeLabel, acceptBtn, rejectBtn);
                    hbox.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(hbox);
                }
            }
        });
    }
}