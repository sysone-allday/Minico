package allday.minico.sql.note;

// 정소영, 김슬기
public class NoteSQL {
    public static final String INSERT_WRONG_NOTE = """
            INSERT INTO WRONG_NOTE (
                wrong_id,
                question_text,
                answer_text,
                memo,
                member_id
            ) VALUES (
                SEQ_WRONG_NOTE_ID.NEXTVAL,
                ?, ?, ?, ?
            )
    """;
    
    // 틀린문제 불러오기(페이지네이션)
    public static final String FIND_WRONG_QUESTIONS_BY_MEMBERID = """
            SELECT wrong_id, 
                   question_text, 
                   answer_text, 
                   memo
             FROM wrong_note
            WHERE member_id = ?
            ORDER BY wrong_id DESC
            OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
            """;
    
    // 전체 틀린 문제 갯수 count
    public static final String COUNT_BY_MEMBER_ID = """
            SELECT COUNT(*)
              FROM wrong_note
             WHERE member_id = ?
            """;
    
    // 선택한 문제 삭제
    public static final String DELETE_BY_WRONG_ID = """
            DELETE 
              FROM wrong_note 
             WHERE wrong_id = ?
            """;
    public static final String UPDATE_MEMO_FOR_WRONG_NOTE_BY_WRONG_ID = """
            UPDATE wrong_note
               SET memo = ?
             WHERE wrong_id = ?
            """;
    public static final String CLEAR_MEMO_BY_WRONG_ID = """
            UPDATE wrong_note
               SET memo = ''
               WHERE wrong_id = 7;
            """;
}
