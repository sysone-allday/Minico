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
/**
 * OxPlayDAO
 *
 * OX 퀴즈 문제 데이터를 데이터베이스에서 조회하는 DAO 클래스입니다.
 * 사용자의 설정(문제 유형, 난이도, 개수)에 따라 적절한 문제 리스트를 가져오는 기능을 수행합니다.
 *
 * 주요 기능:
 * - 문제 유형 및 난이도 기반 문제 조회 (getQuestionText)
 * - 난이도 무작위 문제 조회 (getQuestionTextRandom)
 * - DB 연결은 DBUtil을 통해 수행되며, SQL문은 OxGameSQL 클래스에서 관리됩니다.
 *
 * Singleton 패턴을 적용하여 하나의 DAO 인스턴스만 사용하도록 구성되어 있습니다.
 *
 * @author 김슬기
 * @version 1.0
 */

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
        String sql = OxGameSQL.PICK_SELECT_LEVEL_OX_QUESTIONS;
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

    public List<OxQuestion> getQuestionTextRandom(int typeId, int selectedCount) {
        List<OxQuestion> list = new ArrayList<>();
        String sql = OxGameSQL.PICK_RANDOM_OX_QUESTIONS;
        try(Connection conn = getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, typeId);
            pstmt.setInt(2, selectedCount);

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
            System.out.println("OxPlayDAO getQuestionByRandomLevel 입니다.");
        }
        return list;
    }
}
