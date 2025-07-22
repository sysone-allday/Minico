package allday.minico.sql.typinggame;

public class TypingGameSQL {

    public static final String SELECT_RANDOM_WORD = """
        SELECT * FROM word
        ORDER BY DBMS_RANDOM.VALUE
        FETCH FIRST 20 ROWS ONLY
        """;
}
