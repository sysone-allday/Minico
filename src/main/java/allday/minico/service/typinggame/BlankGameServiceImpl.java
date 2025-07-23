package allday.minico.service.typinggame;

import allday.minico.controller.typinggame.ClovaController;
import allday.minico.dao.typinggame.BlankGameDAO;
import allday.minico.dto.typinggame.BlankGame;
import allday.minico.dto.typinggame.Word;

import java.util.List;

public class BlankGameServiceImpl implements BlankGameService {


    private final BlankGameDAO blankGameDAO;

    // 객체 생성 (초기화) 해야함
    public BlankGameServiceImpl() {
        blankGameDAO = new BlankGameDAO();
    }

    @Override
    public List<BlankGame> getBlankProblems(List<BlankGame> blankGameList, List<Word> successWords) {

        for (Word word : successWords) {
            int count = blankGameDAO.getProblemCountByWordId(word.getWord_id());
            if (count <= 3) {
                try {

                    // clova studio 호출
                    String questionText = ClovaController.generateQuestion(word.getText());
                    blankGameDAO.insertBlankGame(word.getWord_id(), questionText, word.getType_id());
                    System.out.println("[Clova 생성] word_id: " + word.getWord_id() + " → " + questionText);
                } catch (Exception e) {
                    System.err.println("[Clova 오류] word_id: " + word.getWord_id());
                    e.printStackTrace();
                }
            }
        }


        // 기존 방식으로 5문제 뽑기
        return blankGameDAO.selectBlankProblems(blankGameList);
    }

}
