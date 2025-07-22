package allday.minico.dao.member;

import allday.minico.dto.member.Friend;
import allday.minico.sql.member.FriendSQL;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static allday.minico.utils.DBUtil.getConnection;

public class FriendDAO {

    private static final FriendDAO instance = new FriendDAO();
    private FriendDAO() {}
    public static FriendDAO getInstance() {
        return instance;
    }

    public Friend getFriendInfo(String myId, String friendId) throws SQLException { // 친구 찾기
        String friendIdCheckSQL = FriendSQL.friendIdCheckSQL;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(friendIdCheckSQL)) {

            pstmt.setString(1, myId);
            pstmt.setString(2, friendId);
            pstmt.setString(3, friendId);
            pstmt.setString(4, myId);
            pstmt.setString(5, myId);
            pstmt.setString(6, friendId);
            pstmt.setString(7, friendId);
            pstmt.setString(8, myId);
            pstmt.setString(9, friendId);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Friend friend = new Friend();
                friend.setFriendId(rs.getString("MEMBER_ID"));
                friend.setFriendNickname(rs.getString("NICKNAME"));
                friend.setFriendLevel(rs.getInt("LV"));
                friend.setFriendStatus(rs.getString("FRIEND_STATUS"));

                Timestamp logoutTimestamp = rs.getTimestamp("LAST_LOGOUT_TIME");
                if (logoutTimestamp != null) {
                    friend.setLastLogoutTime(logoutTimestamp.toLocalDateTime());
                }
                return friend;
            }
            return null; // 찾는 친구 자체가 없음
        }
    }

    public boolean insertFriendShip(String myId, String friendId) throws SQLException {
        String insertFriendShipSQL = FriendSQL.insertFriendShipSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertFriendShipSQL)) {
            pstmt.setString(1, myId);
            pstmt.setString(2, friendId);

            if(pstmt.executeUpdate() > 0){ return true; }
            else { return false; }
        }
    }

    public List<Friend> SelectReceivedRequests(String myId) throws SQLException { // 나에게 친구 요청한 멤버 목록
        String selectReceivedRequestsSQL = FriendSQL.selectReceivedRequestsSQL;
        List<Friend> receivedList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectReceivedRequestsSQL)) {
            pstmt.setString(1,myId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String friendId = rs.getString("MEMBER_ID");
                String friendNickname = rs.getString("NICKNAME");
                int friendLevel = rs.getInt("LV");
                Timestamp timestamp = rs.getTimestamp("REQUESTED_AT");
                LocalDateTime requestedAt = null;
                if (timestamp != null) { // 친구 요청 시간
                    requestedAt = timestamp.toLocalDateTime();
                }
                Timestamp timestamp2 = rs.getTimestamp("LAST_LOGOUT_TIME");
                LocalDateTime lastLogoutAt = null;
                if(timestamp2 != null){ // 친구 마지막 로그아웃 시간
                    lastLogoutAt = timestamp.toLocalDateTime();
                }

                Friend friend = new Friend(friendId, friendNickname, friendLevel, requestedAt, lastLogoutAt);
                receivedList.add(friend);
            }
        }
        return receivedList;
    }

    public boolean acceptFriendship(String fromId, String toId) throws SQLException {
        String acceptFriendshipSQL = FriendSQL.acceptFriendshipSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(acceptFriendshipSQL)) {

            pstmt.setString(1, fromId);
            pstmt.setString(2, toId);

            if(pstmt.executeUpdate() > 0){ return true; }
        }
        return false;
    }

    public boolean rejectFriendship(String fromId, String toId) throws SQLException {
        String rejectFriendshipSQL = FriendSQL.rejectFriendshipSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(rejectFriendshipSQL)) {

            pstmt.setString(1, fromId);
            pstmt.setString(2, toId);

            if(pstmt.executeUpdate() > 0){ return true; }
        }
        return false;
    }

    public List<Friend> selectMyFriend(String myId) throws SQLException {
        String selectMyFriendSQL = FriendSQL.selectMyFriendSQL;
        List<Friend> myFriendList = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectMyFriendSQL)) {

            pstmt.setString(1, myId);
            pstmt.setString(2, myId);
            pstmt.setString(3, myId);
            pstmt.setString(4, myId);
            pstmt.setString(5, myId);
            pstmt.setString(6, myId);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String friendId = rs.getString("FRIEND_ID");
                String friendNickname = rs.getString("NICKNAME");
                int friendLevel = rs.getInt("LV");
                Timestamp timestamp = rs.getTimestamp("REQUESTED_AT");
                LocalDateTime requestedAt = null;
                if (timestamp != null) {
                    requestedAt = timestamp.toLocalDateTime();
                }
                Timestamp timestamp2 = rs.getTimestamp("LAST_LOGOUT_TIME");
                LocalDateTime lastLogoutAt = null;
                if (timestamp2 != null) {
                    lastLogoutAt = timestamp2.toLocalDateTime();
                }
                Friend myFriend = new Friend(friendId,friendNickname,friendLevel,requestedAt,lastLogoutAt);
                myFriendList.add(myFriend);
            }
        }
        return myFriendList;
    }

    public boolean deleteFriend(String myId, String friendId) throws SQLException {
        String deleteFriendSQL = FriendSQL.deleteFriendSQL;
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteFriendSQL)) {
            pstmt.setString(1, myId);
            pstmt.setString(2, friendId);
            pstmt.setString(3, friendId);
            pstmt.setString(4, myId);

            if(pstmt.executeUpdate() > 0){ return true; }
        }
        return false;
    }
}