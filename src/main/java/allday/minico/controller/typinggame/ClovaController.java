package allday.minico.controller.typinggame;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

public class ClovaController {

    private static final String API_KEY = "Bearer nv-505f67ad61af42ec810a855312887837EP2M";
    private static final String URL = "https://clovastudio.apigw.ntruss.com/testapp/v1/chat-completions/HCX-003";

    public static String generateQuestion(String keyword) throws Exception {
        String prompt = """
                자바 키워드 '%s'가 포함된 문장을 하나 만들어줘.
                   - 반드시 키워드는 영어 그대로 사용해야 해 (예: 'class', 'inheritance')
                   - 문장은 '~다'로 끝나는 서술형 문장으로 작성해.
                   - 예: "자바에서 class는 객체를 정의하는 틀이다."
                   - 너무 길지 않게 한 문장 정도로 해줘.
            """.formatted(keyword).trim();

            // Escape 처리된 문자열로 body 구성
                    String body = """
            {
                "messages": [
                    {"role": "system", "content": "너는 자바 키워드로 퀴즈 문장을 만드는 전문가야."},
                    {"role": "user", "content": %s}
                ],
                "topP": 0.8,
                "temperature": 0.7
            }
            """.formatted(new JsonPrimitive(prompt).toString());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Authorization", API_KEY)
                .header("X-NCP-APIGW-Request-ID", UUID.randomUUID().toString())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        String parsedResult = parseClovaResponse(response.body());

        // 정답 -> ____빈칸으로 변경하기
        String quizText = parsedResult.replace(keyword, "_____");


        System.out.println("🧪 [Clova 문제 생성 결과]");
        System.out.println("→ 단어: " + keyword);
        System.out.println("→ 생성된 문제: " + parsedResult);

        return quizText;
    }

    public static String parseClovaResponse(String json) {
        JsonObject root = JsonParser.parseString(json).getAsJsonObject();

        // Clova Studio 응답은 "result" → "message" → "content"
        if (!root.has("result")) {
            System.err.println("🚫 Clova 응답에 result 없음:\n" + json);
            return null;
        }

        return root.getAsJsonObject("result")
                .getAsJsonObject("message")
                .get("content")
                .getAsString()
                .trim();
    }


}
