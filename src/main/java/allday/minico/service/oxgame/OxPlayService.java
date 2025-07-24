package allday.minico.service.oxgame;

import allday.minico.dao.oxgame.OxPlayDAO;
import allday.minico.dto.oxgame.OxQuestion;

import java.util.List;

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
