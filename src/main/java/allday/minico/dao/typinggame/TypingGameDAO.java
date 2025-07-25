package allday.minico.dao.typinggame;

import allday.minico.dto.typinggame.Word;
import allday.minico.sql.typinggame.TypingGameSQL;
import allday.minico.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TypingGameDAO {

    // 랜덤 단어 조회
    public List<Word> selectRandomWord() {
        List<Word> wordList = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(TypingGameSQL.SELECT_RANDOM_WORD);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Word word = new Word();
                word.setWord_id(rs.getInt("word_id"));
                word.setText(rs.getString("word_text"));
                word.setDifficulty(rs.getString("difficulty"));
                word.setType_id(rs.getInt("type_id"));
                wordList.add(word);
            }

        } catch (SQLException e) {
            e.printStackTrace(); // 필요시 로그로 대체 가능
        }

        return wordList;
    }
}
