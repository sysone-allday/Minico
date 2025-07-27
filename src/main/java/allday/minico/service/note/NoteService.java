package allday.minico.service.note;

import allday.minico.dto.note.Note;

import java.util.List;

/**
 * NoteService
 *
 * 오답노트 관련 기능을 정의한 서비스 인터페이스입니다.
 * 컨트롤러에서 DB 접근 없이 호출할 수 있도록 DAO와의 중간 계층 역할을 합니다.
 *
 * 주요 메서드:
 * - saveWrongNote(List<Note>)       : 오답노트 저장
 * - getTotalWrongQuestionCount()    : 전체 오답 개수 조회
 * - getWrongQuestionsPaged()        : 페이징 기반 오답 조회
 * - deleteWrongQuestion()           : 오답 삭제
 * - saveMemo()                      : 메모 저장 및 수정
 *
 * 이 인터페이스는 NoteServiceImpl에서 구현되며, DI 또는 싱글톤 형태로 활용됩니다.
 *
 * @author 정소영, 김슬기
 * @version 1.0
 */


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
