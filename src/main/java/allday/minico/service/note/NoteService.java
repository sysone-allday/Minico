package allday.minico.service.note;

import allday.minico.dto.note.Note;

import java.util.List;

public interface NoteService {

    // 빈칸문제 단어장에 저장
    // ox 게임 문제도 같이 저장되면 사용하면 될듯 !
    void saveWrongNote(List<Note> wrongList);
}
