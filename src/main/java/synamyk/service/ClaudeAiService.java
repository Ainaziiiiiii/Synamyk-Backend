package synamyk.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import synamyk.config.AnthropicConfig;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClaudeAiService {

    private static final String MODEL = "claude-opus-4-6";
    private static final String API_VERSION = "2023-06-01";

    private final AnthropicConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Generates an explanation for a wrong answer using Claude API.
     *
     * @param questionText   the question text
     * @param options        all answer options
     * @param userWrong      text of the user's wrong answer
     * @param correctAnswer  text of the correct answer
     * @return AI-generated explanation
     */
    public String explainWrongAnswer(
            String questionText,
            List<String> options,
            String userWrong,
            String correctAnswer
    ) {
        String prompt = buildPrompt(questionText, options, userWrong, correctAnswer);

        try {
            Map<String, Object> requestBody = Map.of(
                    "model", MODEL,
                    "max_tokens", 1024,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", config.getApiKey());
            headers.set("anthropic-version", API_VERSION);

            HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    config.getBaseUrl() + "/v1/messages",
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                if (content != null && !content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }

            return "Не удалось получить объяснение.";

        } catch (Exception e) {
            log.error("Error calling Claude API: {}", e.getMessage());
            return "Не удалось получить объяснение: " + e.getMessage();
        }
    }

    private String buildPrompt(String questionText, List<String> options, String userWrong, String correctAnswer) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ты помощник для подготовки к экзаменам. Объясни, почему ответ неправильный и почему правильный ответ верный.\n\n");
        sb.append("Вопрос: ").append(questionText).append("\n\n");
        sb.append("Варианты ответов:\n");
        for (String opt : options) {
            sb.append("- ").append(opt).append("\n");
        }
        sb.append("\nОтвет студента (неправильный): ").append(userWrong).append("\n");
        sb.append("Правильный ответ: ").append(correctAnswer).append("\n\n");
        sb.append("Дай краткое объяснение (2-4 предложения) на русском языке: почему правильный ответ правильный и в чём ошибка студента.");
        return sb.toString();
    }
}