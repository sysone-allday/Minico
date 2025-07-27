package allday.minico.service.oxgame;

import allday.minico.dao.oxgame.OxGameSettingDAO;
import allday.minico.dto.oxgame.ProblemTypeDTO;

import java.util.List;
/**
 * OxGameSettingService
 *
 * OX 퀴즈 게임에서 문제 유형(주제) 목록을 조회하는 서비스 클래스입니다.
 * 설정 화면에서 사용 가능한 문제 유형들을 DAO를 통해 데이터베이스에서 불러오는 역할을 합니다.
 *
 * 주요 기능:
 * - 문제 유형 전체 조회 (getProblemType)
 * - Singleton 패턴으로 서비스 인스턴스를 관리
 * - 내부적으로 OxGameSettingDAO를 사용하여 DB에 접근
 *
 * 본 클래스는 컨트롤러와 DAO 사이에서 비즈니스 로직을 분리하여 유지보수를 용이하게 합니다.
 *
 * @author 김슬기
 * @version 1.0
 */

public class OxGameSettingService {
    private static final OxGameSettingDAO settingDAO = OxGameSettingDAO.getInstance();
    private static OxGameSettingService instance;

    public static OxGameSettingService getInstance() {
        if(instance == null) {
            instance = new OxGameSettingService();
        }
        return instance;
    }

    public List<ProblemTypeDTO> getProblemType() {
        try{
            return settingDAO.findAllProblemType();
        } catch (Exception e) {
            System.out.println("주제를 불러오는데 실패하였습니다. " + e.getMessage());
        }
        return null;
    }


}
