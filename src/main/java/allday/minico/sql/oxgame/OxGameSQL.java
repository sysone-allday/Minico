package allday.minico.sql.oxgame;


// 김슬기
public class OxGameSQL {

    // 모든 문제 타입을 찾는 쿼리
    public static final String FIND_ALL_PROBLEM_TYPE_SQL = """
            SELECT
                type_id,
                type_name
            FROM
                problem_type
            """;
    
    //  유저가 고른 난도의 문제를 찾는 쿼리
    public static final String PICK_SELECT_LEVEL_OX_QUESTIONS = """
            SELECT question_text, answer, explanation
             FROM (
                 SELECT question_text, answer, explanation
                 FROM OX_GAME
                 WHERE type_id = ? AND difficulty = ?
                 ORDER BY DBMS_RANDOM.VALUE
             )
             WHERE ROWNUM <= ?
            """;

    // 유저가 난도를 무작위로 선택했을 경우의 쿼리
    public static final String PICK_RANDOM_OX_QUESTIONS = """
            SELECT question_text, answer, explanation
            FROM (
                SELECT question_text, answer, explanation
                FROM OX_GAME
                WHERE type_id = ? 
                ORDER BY DBMS_RANDOM.VALUE
            )
            WHERE ROWNUM <= ?
            """;
}
