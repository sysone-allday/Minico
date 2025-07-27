package allday.minico.service.note;

import allday.minico.dao.note.NoteDAO;
import allday.minico.dto.note.Note;
import allday.minico.service.oxgame.OxPlayService;

import java.util.List;
/**
 * NoteServiceImpl
 *
 * 오답노트 관련 로직을 처리하는 서비스 구현 클래스입니다.
 * DAO를 호출하여 실제 DB 작업을 수행하고, 예외 처리를 통해 안정성을 제공합니다.
 *
 * 주요 기능:
 * - 오답노트 저장
 * - 사용자 오답 전체 개수 조회
 * - 페이징 기반 오답 리스트 조회
 * - 오답 삭제 (존재하지 않을 경우 예외 발생)
 * - 메모 저장/수정 (실패 시 예외 발생)
 *
 * 싱글톤 패턴을 적용하여 getInstance()로 단일 인스턴스를 제공합니다.
 * NoteDAO와 연결되어 데이터 처리를 위임합니다.
 *
 * @author 정소영, 김슬기
 * @version 1.0
 */


public class NoteServiceImpl implements NoteService{
    private static final int SUCCESS = 1;
    private static final int FAIL = 0;


    NoteDAO noteDAO = new NoteDAO();

    public NoteServiceImpl() {
        noteDAO = new NoteDAO();
    }
    private static NoteServiceImpl instance;

    public static NoteServiceImpl getInstance() {
        if(instance == null) {
            instance = new NoteServiceImpl();
        }
        return instance;
    }


    // 빈칸문제 단어장에 저장
    // ox 게임 문제도 같이 저장되면 사용하면 될듯 !
    @Override
    public void saveWrongNote(List<Note> wrongList) {
        noteDAO.insertWrongNote(wrongList);
    }

    @Override
    public int getTotalWrongQuestionCount(String memberId) {
        return noteDAO.countByMemberId(memberId);
    }

    @Override
    public List<Note> getWrongQuestionsPaged(String memberId, int pageStart, int pageSize) {
        int offset = pageStart;
        return noteDAO.findWrongQuestionsByMemberIdPaged(memberId, offset, pageSize);
    }

    @Override
    public void deleteWrongQuestion(int wrongId) {
        int result = noteDAO.deleteByWrongId(wrongId);
        if (result == FAIL) {
            throw new IllegalArgumentException("삭제할 데이터가 존재하지 않습니다: wrongId=" + wrongId);
        }
    }

    @Override
    public void saveMemo(int wrongId, String newMemo) {
        int result = noteDAO.updateMemo(wrongId, newMemo);
        System.out.println("wrongId: " + wrongId);
        System.out.println("newMemo: " + newMemo);
        if (result == FAIL) {
            throw new IllegalStateException("메모 저장 실패: wrongId = " + wrongId);
        }
    }

}
