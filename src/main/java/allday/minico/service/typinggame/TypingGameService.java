package allday.minico.service.typinggame;

import allday.minico.dto.typinggame.Word;

import java.util.List;

public interface TypingGameService {

    List<Word> getRandomWord();
}
