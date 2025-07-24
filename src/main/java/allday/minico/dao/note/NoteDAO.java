package allday.minico.dao.note;

import allday.minico.dto.note.Note;
import allday.minico.sql.note.NoteSQL;
import allday.minico.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class NoteDAO {

    // 빈칸문제 단어장에 저장
    // ox 게임 문제도 같이 저장되면 사용하면 될듯 !
    public boolean insertWrongNote(List<Note> wrongList) {

        String sql = NoteSQL.INSERT_WRONG_NOTE;


        try (Connection conn = DBUtil.getConnection();
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
}
