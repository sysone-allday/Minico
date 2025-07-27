package allday.minico.dao.oxgame;

import allday.minico.dto.oxgame.ProblemTypeDTO;
import allday.minico.sql.oxgame.OxGameSQL;
import allday.minico.utils.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * OxGameSettingDAO
 *
 * OX 게임의 문제 유형 데이터를 DB에서 조회하는 DAO 클래스입니다.
 * 설정 화면에서 사용되는 문제 타입 리스트를 데이터베이스에서 로딩할 때 사용됩니다.
 *
 * 주요 기능:
 * - 문제 유형 전체 조회 (findAllProblemType)
 * - Singleton 패턴으로 인스턴스를 하나만 유지
 * - SQL은 OxGameSQL 클래스에 정의되어 있으며, DB 연결은 DBUtil 사용
 *
 * ProblemTypeDTO를 반환하며, Service 계층(OxGameSettingService)과 연결되어 있습니다.
 *
 * @author 김슬기
 * @version 1.0
 */


public class OxGameSettingDAO {
	private static OxGameSettingDAO instance; // 2. 자기 자신을 참조하는 변수를 만듬

	public static OxGameSettingDAO getInstance() {
		if (instance == null) {
			instance = new OxGameSettingDAO();
		}
		return instance;
	}

    public List<ProblemTypeDTO> findAllProblemType() {
        List<ProblemTypeDTO> list = new ArrayList<>();
        String sql = OxGameSQL.FIND_ALL_PROBLEM_TYPE_SQL;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                ProblemTypeDTO dto = new ProblemTypeDTO();
                dto.setTypeId(rs.getInt("TYPE_ID"));
                dto.setTypeName(rs.getString("TYPE_NAME"));

                list.add(dto);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("findAllProblemType DAO 입니다.");
        }
        return list;
    }




}
