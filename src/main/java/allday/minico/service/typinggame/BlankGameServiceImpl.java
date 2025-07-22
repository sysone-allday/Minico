package allday.minico.service.typinggame;

import allday.minico.dao.typinggame.TypingGameDAO;
import allday.minico.dao.typinggame.TypingGameDAOImpl;
import allday.minico.dto.typinggame.Word;

import java.util.List;

public class BlankGameServiceImpl implements TypingGameService {


    private TypingGameDAO typingGameDAO;

    // 객체 생성 (초기화) 해야함
    public BlankGameServiceImpl() {
        typingGameDAO = new TypingGameDAOImpl();
    }

    @Override
    public List<Word> getRandomWord() {

        List<Word> wordList = typingGameDAO.selectRandomWord();

        // ✅ 콘솔 출력 확인
        if (wordList.isEmpty()) {
            System.out.println("[DEBUG] 단어가 없습니다.");
        } else {
            System.out.println("[DEBUG] 단어 리스트 불러옴 (" + wordList.size() + "개):");
            for (Word word : wordList) {
                System.out.println("→ " + word.getWord_id() + " : " + word.getText());
            }
        }

        return wordList;
    }
}
