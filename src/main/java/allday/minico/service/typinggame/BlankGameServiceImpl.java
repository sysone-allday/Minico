package allday.minico.service.typinggame;

import allday.minico.controller.typinggame.ClovaController;
import allday.minico.dao.typinggame.BlankGameDAO;
import allday.minico.dto.typinggame.BlankGame;
import allday.minico.dto.typinggame.Word;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

public class BlankGameServiceImpl implements BlankGameService {


    private final BlankGameDAO blankGameDAO;
    private final ExecutorService executor = Executors.newFixedThreadPool(5);  // 비동기 작업용

    // 객체 생성 (초기화) 해야함
    public BlankGameServiceImpl() {
        blankGameDAO = new BlankGameDAO();
    }

    @Override
    public List<BlankGame> getBlankProblems(List<BlankGame> blankGameList, List<Word> successWords) {

        for (Word word : successWords) {
            int count = blankGameDAO.getProblemCountByWordId(word.getWord_id());
            if (count <= 3) {
                executor.submit(() -> {
                    try {
                        // Clova 호출
                        String questionText = ClovaController.generateQuestion(word.getText());

                        // PL/SQL 프로시저 호출
                        blankGameDAO.insertBlankGame(word.getWord_id(), questionText, word.getType_id());

                        System.out.println("[비동기 Clova 생성 완료] word_id: " + word.getWord_id() + " → " + questionText);
                    } catch (Exception e) {
                        System.err.println("[비동기 Clova 오류] word_id: " + word.getWord_id());
                        e.printStackTrace();
                    }
                });
//                try {
//
//                    // clova studio 호출
//                    String questionText = ClovaController.generateQuestion(word.getText());
//                    blankGameDAO.insertBlankGame(word.getWord_id(), questionText, word.getType_id());
//                    System.out.println("[Clova 생성] word_id: " + word.getWord_id() + " → " + questionText);
//                } catch (Exception e) {
//                    System.err.println("[Clova 오류] word_id: " + word.getWord_id());
//                    e.printStackTrace();
//                }
            }
        }

        // 기존 방식으로 5문제 뽑기
        return blankGameDAO.selectBlankProblems(blankGameList);
    }

}
