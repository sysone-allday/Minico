package allday.minico.sql.member;

public class FriendSQL {

    public static String friendIdCheckSQL = """
        SELECT
            m.MEMBER_ID,
            m.NICKNAME,
            m.LV,
            (
                SELECT MAX(LOGOUT_TIME)
                FROM LOGIN_LOG l
                WHERE l.MEMBER_ID = m.MEMBER_ID
            ) AS LAST_LOGOUT_TIME,
            CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM FRIEND
                    WHERE (
                        (MEMBER_ID = ? AND FRIEND_ID = ?)
                        OR (MEMBER_ID = ? AND FRIEND_ID = ?)
                    )
                    AND ACCEPTED_AT IS NOT NULL
                ) THEN 'already'

                WHEN EXISTS (
                    SELECT 1
                    FROM FRIEND
                    WHERE MEMBER_ID = ? AND FRIEND_ID = ?
                      AND ACCEPTED_AT IS NULL
                ) THEN 'AtoB'

                WHEN EXISTS (
                    SELECT 1
                    FROM FRIEND
                    WHERE MEMBER_ID = ? AND FRIEND_ID = ?
                      AND ACCEPTED_AT IS NULL
                ) THEN 'BtoA'

                ELSE 'available'
            END AS FRIEND_STATUS
        FROM MEMBER m
        WHERE m.MEMBER_ID = ?
        """;
    public static String insertFriendShipSQL = """
            INSERT INTO FRIEND
            (MEMBER_ID, FRIEND_ID,REQUESTED_AT, ACCEPTED_AT)
            VALUES (?, ?, ?, NULL)
            """;
    public static String selectReceivedRequestsSQL = """
            SELECT
                m.MEMBER_ID,
                m.NICKNAME,
                m.LV,
                f.REQUESTED_AT,
                ll.LAST_LOGOUT_TIME
            FROM
                FRIEND f
                JOIN MEMBER m ON f.MEMBER_ID = m.MEMBER_ID
                LEFT JOIN (
                    SELECT MEMBER_ID, MAX(LOGOUT_TIME) AS LAST_LOGOUT_TIME
                    FROM LOGIN_LOG
                    GROUP BY MEMBER_ID
                ) ll ON m.MEMBER_ID = ll.MEMBER_ID
            WHERE
                f.FRIEND_ID = ?
                AND f.ACCEPTED_AT IS NULL
            """;
    public static String acceptFriendshipSQL = """
            UPDATE FRIEND
            SET ACCEPTED_AT = SYSDATE
            WHERE MEMBER_ID = ? AND FRIEND_ID = ?
            """;
    public static String rejectFriendshipSQL = """
            DELETE FROM FRIEND
            WHERE MEMBER_ID = ? AND FRIEND_ID = ?
            """;
    public static String selectMyFriendSQL = """
            SELECT
                CASE
                    WHEN f.FRIEND_ID = ? THEN f.MEMBER_ID
                    ELSE f.FRIEND_ID
                END AS FRIEND_ID,
                m.NICKNAME,
                m.LV,
                f.REQUESTED_AT,
                ll.LAST_LOGOUT_TIME
            FROM
                FRIEND f
            JOIN MEMBER m
                ON m.MEMBER_ID = CASE 
                        WHEN f.FRIEND_ID  = ? THEN f.MEMBER_ID 
                        ELSE f.FRIEND_ID
                    END
            LEFT JOIN (
                SELECT
                    MEMBER_ID,
                    MAX(LOGOUT_TIME) AS LAST_LOGOUT_TIME
                FROM
                    LOGIN_LOG
                GROUP BY MEMBER_ID
            ) ll
                ON m.MEMBER_ID = ll.MEMBER_ID
                WHERE
                    f.ACCEPTED_AT IS NOT NULL
                AND (
                    (f.MEMBER_ID = ? AND f.FRIEND_ID != ?)
                    OR (f.FRIEND_ID = ? AND f.MEMBER_ID != ?)
                )
            """;
    public static String deleteFriendSQL = """
            DELETE FROM FRIEND
            WHERE
                ((MEMBER_ID = ? AND FRIEND_ID = ?) 
                OR (MEMBER_ID = ? AND FRIEND_ID = ?))
                AND ACCEPTED_AT IS NOT NULL
            """;
}