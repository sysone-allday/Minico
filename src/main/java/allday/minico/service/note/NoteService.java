package allday.minico.service.note;

import allday.minico.dto.note.Note;

import java.util.List;

public interface NoteService {

    // 빈칸문제 단어장에 저장
    // ox 게임 문제도 같이 저장되면 사용하면 될듯 !
    void saveWrongNote(List<Note> wrongList);
    
    // 사용자의 틀린 문제 전체 count
    int getTotalWrongQuestionCount(String memberId);

    // 사용자가 틀린 문제 list 형태로 가져옴
    List<Note> getWrongQuestionsPaged(String memberId, int offset, int pageSize);

    // 문제 삭제
    void deleteWrongQuestion(int wrongId);
    // 메모 저장 및 수정
    void saveMemo(int wrongId, String text);
}
