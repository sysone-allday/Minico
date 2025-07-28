package allday.minico.dao.skin;

import allday.minico.dto.skin.Skin;
import allday.minico.sql.skin.SkinSQL;
import static allday.minico.utils.DBUtil.getConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SkinDAO {

    private static SkinDAO instance;

    private SkinDAO() {}

    public static SkinDAO getInstance() {
        if (instance == null) {
            instance = new SkinDAO();
        }
        return instance;
    }

    // 사용자의 모든 스킨 조회
    public List<Skin> selectUserSkins(String memberId) throws SQLException {
        String sql = SkinSQL.selectUserSkinsSQL;
        List<Skin> skinList = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Skin skin = new Skin();
                skin.setMinimiType(rs.getString("MINIMI_TYPE"));
                skin.setLevelNo(rs.getInt("LEVEL_NO"));
                skin.setImagePath(rs.getString("IMAGE_PATH"));
                skin.setMemberId(rs.getString("MEMBER_ID"));
                skinList.add(skin);
            }
        }
        return skinList;
    }

    // 특정 미니미 타입의 스킨 조회
    public List<Skin> selectSkinsByType(String memberId, String minimiType) throws SQLException {
        String sql = SkinSQL.selectSkinsByTypeSQL;
        List<Skin> skinList = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setString(2, minimiType);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Skin skin = new Skin();
                skin.setMinimiType(rs.getString("MINIMI_TYPE"));
                skin.setLevelNo(rs.getInt("LEVEL_NO"));
                skin.setImagePath(rs.getString("IMAGE_PATH"));
                skin.setMemberId(rs.getString("MEMBER_ID"));
                skinList.add(skin);
            }
        }
        return skinList;
    }

    // 새로운 스킨 추가
    public boolean insertSkin(Skin skin) throws SQLException {
        String sql = SkinSQL.insertSkinSQL;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, skin.getMinimiType());
            pstmt.setInt(2, skin.getLevelNo());
            pstmt.setString(3, skin.getImagePath());
            pstmt.setString(4, skin.getMemberId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // 스킨 소유 여부 확인
    public boolean checkSkinOwnership(String memberId, String minimiType, int levelNo) throws SQLException {
        String sql = SkinSQL.checkSkinOwnershipSQL;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setString(2, minimiType);
            pstmt.setInt(3, levelNo);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }
    
    // 회원가입 시 기본 스킨 추가
    public boolean insertDefaultSkin(String memberId, String minimiType, String imagePath) throws SQLException {
        String sql = SkinSQL.insertDefaultSkinSQL;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, minimiType);
            pstmt.setString(2, imagePath);
            pstmt.setString(3, memberId);

            return pstmt.executeUpdate() > 0;
        }
    }
    
    // 사용자의 현재 레벨에 맞는 스킨 조회
    public Skin selectCurrentSkin(String memberId) throws SQLException {
        String sql = SkinSQL.selectCurrentSkinSQL;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Skin skin = new Skin();
                skin.setMinimiType(rs.getString("MINIMI_TYPE"));
                skin.setLevelNo(rs.getInt("LEVEL_NO"));
                skin.setImagePath(rs.getString("IMAGE_PATH"));
                skin.setMemberId(rs.getString("MEMBER_ID"));
                // System.out.println("[SkinDAO] 조회된 스킨 정보 - 캐릭터: " + skin.getImagePath() + ", 레벨: " + skin.getLevelNo());
                return skin;
            } else {
                System.out.println("[SkinDAO] 해당 사용자의 스킨 정보를 찾을 수 없습니다: " + memberId);
            }
        }
        return null;
    }
    
    
    public boolean insertLevelUpSkin(String memberId, int levelNo, String imagePath) throws SQLException {
        String sql = SkinSQL.insertLevelUpSkinSQL;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, levelNo);
            pstmt.setString(2, imagePath);
            pstmt.setString(3, memberId);
            pstmt.setInt(4, levelNo);

            return pstmt.executeUpdate() > 0;
        }
    }
}