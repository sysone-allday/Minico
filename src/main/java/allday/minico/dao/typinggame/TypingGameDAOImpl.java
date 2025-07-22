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

public class TypingGameDAOImpl implements TypingGameDAO {


    @Override
    public List<Word> selectRandomWord() {
        List<Word> wordList = new ArrayList<>();

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(TypingGameSQL.SELECT_RANDOM_WORD);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                Word word = new Word();
                word.setWord_id(rs.getInt("word_id"));
                word.setText(rs.getString("word_text"));
                wordList.add(word);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return wordList;
    }
}
