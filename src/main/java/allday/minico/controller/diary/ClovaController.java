package allday.minico.controller.diary;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

import static allday.minico.controller.typinggame.ClovaController.parseClovaResponse;

// 김민서 파트
public class ClovaController {

    private static final String API_KEY = "Bearer nv-505f67ad61af42ec810a855312887837EP2M";
    private static final String URL = "https://clovastudio.apigw.ntruss.com/testapp/v1/chat-completions/HCX-003";

    // 감정 3가지로 분류
    public enum Emotion {POSITIVE, NEUTRAL, NEGATIVE}

    public static Emotion analyzeEmotion(String diaryText) throws Exception {
        // ① 프롬프트
        String prompt = """
                다음 일기의 전반적인 감정을 '긍정', '보통', '부정' 중
                하나만 정확히 답해줘. 다른 말은 하지 마.
                
                일기:
                %s
                """.formatted(diaryText);

        // ② 요청 바디
        String body = """
                {
                  "messages":[
                    {"role":"system","content":"너는 감정 분석가야."},
                    {"role":"user","content":%s}
                  ],
                  "topP":0.8,
                  "temperature":0.5
                }
                """.formatted(new JsonPrimitive(prompt));

        // ③ API 호출 (API 키는 **환경변수**나 properties로 관리)
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Authorization", API_KEY)           // ← 그대로 넣기
                .header("X-NCP-APIGW-Request-ID", UUID.randomUUID().toString())
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> resp =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        String answer = parseClovaResponse(resp.body()).replaceAll("\\s", "");

        return switch (answer) {
            case "긍정" -> Emotion.POSITIVE;
            case "부정" -> Emotion.NEGATIVE;
            default -> Emotion.NEUTRAL;
        };
    }

    public static String toEmoji(Emotion e) {
        return switch (e) {
            case POSITIVE -> "😊";
            case NEGATIVE -> "😢";
            case NEUTRAL -> "😐";
        };
    }
}