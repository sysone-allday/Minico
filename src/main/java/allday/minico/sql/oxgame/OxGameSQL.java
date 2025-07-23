package allday.minico.sql.oxgame;

public class OxGameSQL {

    public static final String FIND_ALL_PROBLEM_TYPE_SQL = """
            SELECT
                type_id,
                type_name
            FROM
                problem_type
            """;
    public static final String PICK_RANDOM_OX_QUESTIONS = """
            SELECT question_text, answer, explanation
            FROM (
                SELECT question_text, answer, explanation
                FROM OX_GAME
                WHERE type_id = ? AND difficulty = ?
                ORDER BY DBMS_RANDOM.VALUE
            )
            WHERE ROWNUM <= ?
            """;
}
