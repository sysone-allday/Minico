package allday.minico.dao.typinggame;

import allday.minico.dto.typinggame.BlankGame;
import allday.minico.sql.typinggame.BlankGameSQL;
import allday.minico.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlankGameDAO{


    public List<BlankGame> selectBlankProblems(List<BlankGame> blankGameList) {

        List<BlankGame> problemList = new ArrayList<>();

        // word_id만 추출
        List<Integer> wordIds = blankGameList.stream()
                .map(BlankGame::getWord_id)
                .distinct()
                .collect(Collectors.toList());

        // 비어있으면 바로 반환
        if (wordIds.isEmpty()) return problemList;


        // IN 조건의 ? 개수만큼 placeholders 생성
        String placeholders = wordIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));


        // SQL 생성
        String sql = String.format(BlankGameSQL.SELECT_BLANK_PROBLEM, placeholders);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // ?에 파라미터 바인딩
            for (int i = 0; i < wordIds.size(); i++) {
                pstmt.setInt(i + 1, wordIds.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    BlankGame blankGame = new BlankGame();
                    blankGame.setBlank_id(rs.getInt("blank_id"));
                    blankGame.setWord_id(rs.getInt("word_id"));
                    blankGame.setQuestion_text(rs.getString("question_text"));
                    blankGame.setType_id(rs.getInt("type_id"));

                    problemList.add(blankGame);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return problemList;
    }
}