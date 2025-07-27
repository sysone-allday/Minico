/*
FriendService 클래스는 친구 검색, 요청 전송 및 수락/거절,
받은 요청 및 친구 목록 조회, 친구 삭제 등
친구 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
FriendDAO를 통해 DB와 통신하며, 예외 발생 시 로그를 출력하고
기능의 성공 여부를 반환합니다.
 */

package allday.minico.service.member;

import allday.minico.dao.member.FriendDAO;
import allday.minico.dto.member.Friend;
import allday.minico.session.AppSession;

import java.sql.SQLException;
import java.util.List;

public class FriendService {

    private static final FriendService instance = new FriendService();
    private FriendService() {}
    public static FriendService getInstance() { return instance; }
    private FriendDAO friendDAO = FriendDAO.getInstance(); // DAO 객체


    public Friend searchFriend(String myId, String friendId) {

        if(myId.equals(friendId)) { // 자기에게 친구 걸었을 시!
            Friend friend = new Friend();
            friend.setFriendStatus("identical");
            return friend;
        }

        try {
            return friendDAO.getFriendInfo(myId, friendId);
        } catch (SQLException e){
            System.out.println("친구 찾기 중 예외 발생");
            e.printStackTrace();
            return null;
        }
    }

    public boolean sendFriendRequest(String myId, String friendId) {
        try {
            boolean sendResult = friendDAO.insertFriendShip(myId, friendId);
            if (sendResult) { System.out.println(friendId + " 에게 친구 요청 완료"); return true;}
            else { return false;}
        } catch (SQLException e){
            System.out.println("친구 요청 중 예외 발생");
            e.printStackTrace();
            return false;
        }
    }

    public List<Friend> getReceivedFriendRequestsList(String myId) {
        try {
            List<Friend> receivedRequestsLists = friendDAO.SelectReceivedRequests(myId);
            return receivedRequestsLists;
        } catch (SQLException e){
            System.out.println("나에게 친구 추가한 멤버 목록 반환 중 실패");
            e.printStackTrace();
            return null;
        }
    }

    public boolean acceptRequest(String fromId, String toId) {
        try {
            boolean updateResult = friendDAO.acceptFriendship(fromId, toId);
            return updateResult;
        } catch (SQLException e){
            System.out.println("친구 요청 수락 중 예외 발생");
            e.printStackTrace();
            return false;
        }
    }

    public boolean rejectRequest(String fromId, String toId) {
        try {
            boolean deleteResult = friendDAO.rejectFriendship(fromId,toId);
            return deleteResult;
        } catch (SQLException e){
            System.out.println("친구 요청 거부 중 예외 발생");
            e.printStackTrace();
            return false;
        }
    }

    public List<Friend> getFriendList(String myId) {
        try {
            List<Friend> myFriendList = friendDAO.selectMyFriend(myId);
            return myFriendList;
        } catch (SQLException e){
            System.out.println("친구 목록 불러오기 중 예외 발생");
            e.printStackTrace();
            return null;
        }
    }

    public boolean deleteFriend(String myId, String friendId) {
        try {
            boolean deleteResult = friendDAO.deleteFriend(myId, friendId);
            return deleteResult;
        } catch (SQLException e ){
            System.out.println("친구 삭제 중 예외 발생");
            e.printStackTrace();
            return false;
        }
    }
}










