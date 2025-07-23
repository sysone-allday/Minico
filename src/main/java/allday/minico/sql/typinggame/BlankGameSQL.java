package allday.minico.sql.typinggame;

public class BlankGameSQL {

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
