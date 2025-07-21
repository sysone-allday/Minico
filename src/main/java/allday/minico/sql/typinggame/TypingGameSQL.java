package allday.minico.sql.typinggame;

public class TypingGameSQL {

    public static final String SELECT_RANDOM_WORD = """
        SELECT * FROM word
        ORDER BY DBMS_RANDOM.VALUE
        FETCH FIRST 20 ROWS ONLY
        """;



    // 랜덤으로 단어 가져오기
//    public List<Word> selectRandomWord() {
//        List<Word> wordList = new ArrayList<>();
//        String sql = "SELECT * FROM word ORDER BY DBMS_RANDOM.VALUE FETCH FIRST 20 ROWS ONLY";
//
//        try (Connection conn = DBUtil.getConnection();
//             PreparedStatement pstmt = conn.prepareStatement(sql);
//             ResultSet rs = pstmt.executeQuery()) {
//
//            while (rs.next()) {
//                Word word = new Word();
//                word.setId(rs.getInt("word_id"));           // 컬럼명: id
//                word.setText(rs.getString("word_text"));    // 컬럼명: text
//                wordList.add(word);
//            }
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return wordList;
//    }
}
