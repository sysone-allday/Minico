package allday.minico.service.typinggame;

import allday.minico.dto.typinggame.BlankGame;
import allday.minico.dto.typinggame.Word;

import java.util.List;

public interface BlankGameService {

    List<BlankGame> getBlankProblems(List<BlankGame> blankGameList, List<Word> successWords);
}
