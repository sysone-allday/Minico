package allday.minico.service.note;

import allday.minico.dao.note.NoteDAO;
import allday.minico.dto.note.Note;
import allday.minico.service.oxgame.OxPlayService;

import java.util.List;

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
