package allday.minico.service.oxgame;

import allday.minico.dao.oxgame.OxPlayDAO;
import allday.minico.dto.oxgame.OxQuestion;

import java.util.List;

/**
 * OxPlayService
 *
 * OX 퀴즈 게임에서 문제 데이터를 제공하는 서비스 클래스입니다.
 * 문제 유형, 난이도, 문제 개수 등의 조건에 따라 적절한 문제 리스트를 불러오는 역할을 담당합니다.
 *
 * 주요 기능:
 * - 선택한 문제 유형과 난이도, 개수 기반으로 문제 조회 (getQuestionText)
 * - 난이도 무작위로 문제 조회 (getQuestionByRandomLevel)
 * - 내부적으로 OxPlayDAO를 통해 DB 접근 수행
 * - Singleton 패턴 적용
 *
 * 본 클래스는 컨트롤러와 DAO 사이에서 문제 로직을 캡슐화하여 재사용성과 유지보수를 높입니다.
 *
 * @author 김슬기
 * @version 1.0
 */


public class OxPlayService {
    private static final OxPlayDAO oxPlayDAO = OxPlayDAO.getInstance();
    private static OxPlayService instance;

    public static OxPlayService getInstance() {
        if(instance == null) {
            instance = new OxPlayService();
        }
        return instance;
    }

    public List<OxQuestion> getQuestionText(int typeId, String selectedLevel, int selectedCount) {
        try {
            return oxPlayDAO.getQuestionText(typeId, selectedLevel, selectedCount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("문제를 불러올 수 없습니다.");
        }
        return null;
    }

    public List<OxQuestion> getQuestionByRandomLevel(int typeId, int selectedCount) {
        try {
            return oxPlayDAO.getQuestionTextRandom(typeId, selectedCount);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("문제를 불러올 수 없습니다.");
        }
        return null;
    }
}
