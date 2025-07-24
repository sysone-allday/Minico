package allday.minico.sql.diary;

public class TodolistSQL {
    public static final String SELECT_BY_MEMBER_AND_DATE = """
        SELECT TODO_ID, CONTENT, IS_DONE
        FROM TODO
        WHERE MEMBER_ID = ? AND TRUNC(CREATE_AT) = ?
    """;
    public static final String INSERT = """
        INSERT INTO TODO (CONTENT, IS_DONE, CREATE_AT, MEMBER_ID)
        VALUES (?, 'N', ?, ?)
    """;
    public static final String UPDATE_DONE = """
        UPDATE TODO SET IS_DONE=? WHERE TODO_ID=?
    """;
    public static final String UPDATE_CONTENT = """
        UPDATE TODO SET CONTENT=? WHERE TODO_ID=?
    """;
    public static final String DELETE = """
        DELETE FROM TODO WHERE TODO_ID=?
    """;

    // 잡초 비율 계산 함수 호출 (progress: 0.0 ~ 1.0 반환)
    public static final String CALL_GET_WEED_RATIO = """
             { ? = call GET_WEED_RATIO(?, ?) }
            """;

}