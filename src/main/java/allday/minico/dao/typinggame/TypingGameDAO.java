package allday.minico.dao.typinggame;

import allday.minico.dto.typinggame.Word;

import java.util.List;

public interface TypingGameDAO {

    public List<Word> selectRandomWord();
}
