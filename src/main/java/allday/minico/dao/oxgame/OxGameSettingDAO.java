package allday.minico.dao.oxgame;

import allday.minico.dao.member.MemberDAO;
import allday.minico.dto.oxgame.ProblemTypeDTO;
import allday.minico.sql.oxgame.OxGameSQL;
import allday.minico.utils.DBUtil;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
