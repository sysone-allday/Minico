package allday.minico.sql.diary;

public class DiarySQL {
    // 일기 조회 (날짜+멤버)
    public static final String SELECT_BY_DATE = """
        SELECT DIARY_ID, CONTENT, WRITTEN_AT, VISIBILITY, MEMBER_ID, EMOTION_ID
        FROM DIARY
        WHERE MEMBER_ID = ?
          AND TO_CHAR(WRITTEN_AT, 'YYYY-MM-DD') = ?
        """;

    // 일기 등록
    public static final String INSERT = """
        INSERT INTO DIARY (CONTENT, WRITTEN_AT, VISIBILITY, MEMBER_ID, EMOTION_ID)
        VALUES (?, ?, ?, ?, ?)
        """;

    // 일기 수정
    public static final String UPDATE = """
        UPDATE DIARY
        SET CONTENT = ?, VISIBILITY = ?, EMOTION_ID = ?
        WHERE TO_CHAR(WRITTEN_AT, 'YYYY-MM-DD') = ?
          AND MEMBER_ID = ?
        """;

    public static final String IMAGE_INSERT = """
            SELECT image_path FROM skin WHERE member_id = ?
            """;
}
