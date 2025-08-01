package allday.minico.sql.skin;

public class SkinSQL {

    // 회원가입 시 선택한 미니미의 레벨 1 스킨 추가
    public static String insertDefaultSkinSQL = """
        INSERT INTO SKIN (MINIMI_TYPE, LEVEL_NO, IMAGE_PATH, MEMBER_ID)
        VALUES (?, 1, ?, ?)
        """;
    
    // 사용자의 현재 레벨에 맞는 스킨 조회 (간단한 버전)
    public static String selectCurrentSkinSQL = """
        SELECT MINIMI_TYPE, LEVEL_NO, IMAGE_PATH, MEMBER_ID
        FROM SKIN 
        WHERE MEMBER_ID = ?
        ORDER BY LEVEL_NO DESC
        """;
    
    // 레벨업 시 새 스킨 자동 해금
    public static String insertLevelUpSkinSQL = """
        INSERT INTO SKIN (MINIMI_TYPE, LEVEL_NO, IMAGE_PATH, MEMBER_ID)
        SELECT m.MINIMI_TYPE, ?, ?, m.MEMBER_ID
        FROM MEMBER m
        WHERE m.MEMBER_ID = ?
        AND NOT EXISTS (
            SELECT 1 FROM SKIN s 
            WHERE s.MEMBER_ID = m.MEMBER_ID 
            AND s.MINIMI_TYPE = m.MINIMI_TYPE 
            AND s.LEVEL_NO = ?
        )
        """;
}