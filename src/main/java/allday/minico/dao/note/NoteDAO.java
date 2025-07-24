package allday.minico.dao.note;

import allday.minico.dto.note.Note;
import allday.minico.sql.note.NoteSQL;
import allday.minico.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static allday.minico.utils.DBUtil.getConnection;

public class NoteDAO {



    // 빈칸문제 단어장에 저장
    // ox 게임 문제도 같이 저장되면 사용하면 될듯 !
    public boolean insertWrongNote(List<Note> wrongList) {

        String sql = NoteSQL.INSERT_WRONG_NOTE;


        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            for (Note note : wrongList) {
                pstmt.setString(1, note.getQuestionText());
                pstmt.setString(2, note.getAnswerText());
                pstmt.setString(3, note.getMemo());
                pstmt.setString(4, note.getMemberId());
            }
            pstmt.executeUpdate(); // ❗한 줄씩 INSERT (트랜잭션 사용 가능)
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Note> findWrongQuestionsByMemberIdPaged(String memberId, int offset, int pageSize) {
        String sql = NoteSQL.FIND_WRONG_QUESTIONS_BY_MEMBERID;

        List<Note> list = new ArrayList<>();
//        int offset = (currentPage - 1) * pageSize;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            pstmt.setInt(2, offset);
            pstmt.setInt(3, pageSize);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Note note = new Note();
                note.setWrongId(rs.getInt("WRONG_ID"));
                note.setQuestionText(rs.getString("QUESTION_TEXT"));
                note.setAnswerText(rs.getString("ANSWER_TEXT"));
                note.setMemo(rs.getString("MEMO"));
                list.add(note);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("NoteDAO : findWrongQuestionsByMemberIdPaged " + e.getMessage());
        }
        return list;
    }

    public int countByMemberId(String memberId) {
        String sql = NoteSQL.COUNT_BY_MEMBER_ID;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1); // ✅ 첫 번째 컬럼의 int 값을 꺼냄 (COUNT 결과)
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("NoteDAO : countByMemberId " + e.getMessage());
        }
        return 0; // 결과가 없으면 0
    }

    public int deleteByWrongId(int wrongId) {
        String sql = NoteSQL.DELETE_BY_WRONG_ID;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, wrongId);
            return pstmt.executeUpdate(); // 삭제된 row 수 반환 (0 또는 1)

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("삭제 중 오류 발생", e);
        }
    }

    public int updateMemo(int wrongId, String newMemo) {
        String sql = NoteSQL.UPDATE_MEMO_FOR_WRONG_NOTE_BY_WRONG_ID;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newMemo);
            pstmt.setInt(2, wrongId);
            return pstmt.executeUpdate(); // 삭제된 row 수 반환 (0 또는 1)

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("메모 업데이트 중 오류 발생", e);
        }
    }

}
