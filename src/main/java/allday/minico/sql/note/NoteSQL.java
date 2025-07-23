package allday.minico.sql.note;

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
}
