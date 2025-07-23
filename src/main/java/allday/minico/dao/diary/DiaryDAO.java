package allday.minico.dao.diary;

import allday.minico.dto.diary.Diary;
import allday.minico.sql.diary.DiarySQL;

import java.sql.*;
import java.time.LocalDate;

import static allday.minico.sql.diary.DiarySQL.IMAGE_INSERT;
import static allday.minico.utils.DBUtil.getConnection;

public class DiaryDAO {
    // 날짜별 일기 조회
    public Diary selectDiaryByDate(String memberId, LocalDate date) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DiarySQL.SELECT_BY_DATE)) {
            ps.setString(1, memberId);
            ps.setString(2, date.toString());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Diary(
                        rs.getLong("DIARY_ID"),
                        rs.getString("CONTENT"),
                        rs.getDate("WRITTEN_AT").toLocalDate(),
                        rs.getString("VISIBILITY"),
                        rs.getString("MEMBER_ID"),
                        rs.getInt("EMOTION_ID")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 일기 추가
    public boolean insertDiary(Diary diary) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DiarySQL.INSERT)) {
            ps.setString(1, diary.getContent());
            ps.setDate(2, Date.valueOf(diary.getWrittenAt()));
            ps.setString(3, diary.getVisibility());
            ps.setString(4, diary.getMemberId());
            ps.setInt(5, diary.getEmotionId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 일기 수정
    public boolean updateDiary(Diary diary) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(DiarySQL.UPDATE)) {
            ps.setString(1, diary.getContent());
            ps.setString(2, diary.getVisibility());
            ps.setInt(3, diary.getEmotionId());
            ps.setString(4, diary.getWrittenAt().toString());
            ps.setString(5, diary.getMemberId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // 미니미 이미지 찾기
    public static String findImagePathByMemberId(String memberId) {
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(IMAGE_INSERT)) {

            ps.setString(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getString("IMAGE_PATH") : null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
