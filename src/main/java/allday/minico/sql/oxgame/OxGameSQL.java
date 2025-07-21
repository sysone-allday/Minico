package allday.minico.sql.oxgame;

public class OxGameSQL {

    public static final String FIND_ALL_PROBLEM_TYPE_SQL = """
            SELECT
                type_id,
                type_name
            FROM
                problem_type
            """;
}
