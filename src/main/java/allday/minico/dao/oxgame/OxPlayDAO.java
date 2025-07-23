package allday.minico.dao.oxgame;

import allday.minico.dto.oxgame.OxQuestion;
import allday.minico.dto.oxgame.ProblemTypeDTO;
import allday.minico.sql.oxgame.OxGameSQL;
import allday.minico.utils.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static allday.minico.utils.DBUtil.getConnection;

public class OxPlayDAO {
	private static OxPlayDAO instance;

	public static OxPlayDAO getInstance() {
		if (instance == null) {
			instance = new OxPlayDAO();
		}
		return instance;
	}


    public List<OxQuestion> getQuestionText(int typeId, String selectedLevel, int selectedCount) {
        List<OxQuestion> list = new ArrayList<>();
        String sql = OxGameSQL.PICK_RANDOM_OX_QUESTIONS;
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, typeId);
            pstmt.setString(2, selectedLevel);
            pstmt.setInt(3, selectedCount);
            System.out.println("타입Id : " + typeId + " 난이도 : " + selectedLevel + " 문제 수 : " + selectedCount);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                OxQuestion dto = new OxQuestion();
                dto.setQuestionText(rs.getString("QUESTION_TEXT"));
                dto.setAnswer(rs.getString("ANSWER"));
                dto.setExplanation(rs.getString("EXPLANATION"));

                list.add(dto);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("OxPlayDAO getQuestionText 입니다.");
        }
        return list;
    }
}
