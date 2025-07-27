/*
FindFriendController 클래스는 친구 ID를 입력받아 검색하고,
해당 유저의 친구 상태에 따라 결과를 안내하며 친구 요청을 보낼 수 있도록 처리합니다.
검색 결과는 ListView에 사용자 정보와 요청 버튼 형태로 표시되며,
사용자가 버튼을 누르면 FriendService를 통해 친구 요청을 전송합니다.
 */
package allday.minico.controller.member;

import allday.minico.dto.member.Friend;
import allday.minico.service.member.FriendService;
import allday.minico.session.AppSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class FindFriendController {

    @FXML private Button searchFriendButton;
    @FXML private Label searchFriendTitle;
    @FXML private TextField searchFrinedTextField;
    @FXML private ListView<Friend> searchResultListView;
    @FXML private Label searchResultText;

    private final FriendService friendService =  FriendService.getInstance();

    @FXML
    public void initialize() { // 친구 찾기 창의 초기화 메서드
        searchResultListView.setCellFactory(listView -> new ListCell<>() {
            private final Button requestButton = new Button("요청");
            {
                requestButton.setOnAction(event -> {
                    Friend friend = getItem();
                    if (friend != null) {
                        boolean sendResult = friendService.sendFriendRequest(AppSession.getLoginMember().getMemberId(),searchFrinedTextField.getText());
                        if(sendResult) {
                            searchResultText.setText("친구 요청이 완료되었습니다.");
                            searchResultText.setStyle("-fx-text-fill: green");
                            requestButton.setDisable(true);
                        }
                    }
                });
                requestButton.getStyleClass().add("findfriend-button"); // 스타일 클래스 추가
            }

            @Override
            protected void updateItem(Friend friend, boolean empty) {
                super.updateItem(friend, empty);

                if (empty || friend == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label infoLabel = new Label(
                            "[닉네임] " + friend.getFriendNickname() +
                                    " | LV " + friend.getFriendLevel() +
                                    " | ID " + friend.getFriendId()
                    );

                    HBox hBox = new HBox();
                    hBox.setSpacing(20); // 텍스트와 버튼 사이 간격
                    hBox.setMaxWidth(Double.MAX_VALUE);
                    hBox.setStyle("-fx-alignment: center-left;"); // 전체 정렬 방향

                    // 텍스트를 왼쪽, 버튼을 오른쪽에 위치시키기 위해 Region 사용
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    hBox.getChildren().addAll(infoLabel, spacer, requestButton);
                    setGraphic(hBox);
                }
            }
        });
    }

    @FXML
    void searchFriendButton(ActionEvent event) { // 친구 검색 버튼

        searchResultText.setText("");

        // 친구 ID 를 입력한걸 받아오고
        String friendId = searchFrinedTextField.getText().trim();
        // Friend Service -> DAO 를 거쳐서
        Friend foundFriend = friendService.searchFriend(AppSession.getLoginMember().getMemberId(), friendId);

        if(foundFriend != null) {

            if(foundFriend.getFriendStatus().equals("available")) {
                searchResultListView.getItems().clear(); // 이전 결과 지우고
                searchResultListView.getItems().add(foundFriend); // 새 친구 정보 추가
            } else if (foundFriend.getFriendStatus().equals("already")){
                searchResultText.setText("이미 친구인 유저입니다");
                searchResultText.setStyle("-fx-text-fill: red");
            } else if (foundFriend.getFriendStatus().equals("AtoB")){
                searchResultText.setText("이미 친구요청 중입니다");
                searchResultText.setStyle("-fx-text-fill: red");

            } else if (foundFriend.getFriendStatus().equals("BtoA")) {
                searchResultText.setText("나에게 친구 요청을 보낸 유저입니다");
                searchResultText.setStyle("-fx-text-fill: red");
            } else {
                searchResultText.setText("자신에게 친구 요청을 할 수 없습니다.");
                searchResultText.setStyle("-fx-text-fill: red");
            }
        } else {
            searchResultText.setText("존재하지 않는 유저입니다");
            searchResultText.setStyle("-fx-text-fill: red;");
        }
    }
}