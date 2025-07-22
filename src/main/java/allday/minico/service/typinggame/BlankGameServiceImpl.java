package allday.minico.service.typinggame;

import allday.minico.dao.typinggame.BlankGameDAO;
import allday.minico.dto.typinggame.BlankGame;

import java.util.List;

public class BlankGameServiceImpl implements BlankGameService {


    private final BlankGameDAO blankGameDAO;

    // 객체 생성 (초기화) 해야함
    public BlankGameServiceImpl() {
        blankGameDAO = new BlankGameDAO();
    }

    @Override
    public List<BlankGame> getBlankProblems(List<BlankGame> blankGameList) {

        List<BlankGame> ProblemList = blankGameDAO.selectBlankProblems(blankGameList);

        // ✅ 콘솔 출력 확인
        if (ProblemList.isEmpty()) {
            System.out.println("[DEBUG] 단어가 없습니다.");
        } else {
            System.out.println("[DEBUG] 단어 리스트 불러옴 (" + ProblemList.size() + "개):");
            for (BlankGame blankGame: ProblemList) {
                System.out.println("→ " + blankGame.getWord_id() + " : " + blankGame.getQuestion_text());
            }
        }

        return ProblemList;
    }
}
