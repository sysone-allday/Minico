package allday.minico.sql.typinggame;

public class BlankGameSQL {

    // word_id에 해당하는 문제 개수 조회
    public static final String COUNT_BY_WORD_ID = """
        SELECT COUNT(*) FROM blank_game WHERE word_id = ?
        """;

    // 문제 삽입
    public static final String INSERT_BLANK_GAME = """
        INSERT INTO blank_game (blank_id, word_id, question_text, type_id)
        VALUES (SEQ_BLANK_GAME_ID.NEXTVAL, ?, ?, ?)
        """;

    // word_id 리스트 기반으로 문제 5개 조회
    public static final String SELECT_BLANK_PROBLEM = """
        SELECT *
        FROM (
            SELECT BG.*, ROW_NUMBER() OVER (PARTITION BY WORD_ID ORDER BY DBMS_RANDOM.VALUE) AS PROBLEMS
            FROM BLANK_GAME BG
            WHERE WORD_ID IN (%s)
        )
        WHERE PROBLEMS = 1
        AND ROWNUM <= 5
        """;
}