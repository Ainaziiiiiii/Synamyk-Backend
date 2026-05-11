package synamyk.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Synamyk API")
                        .version("1.0.0")
                        .description("""
                                ## Описание
                                REST API для мобильного приложения **Synamyk** — подготовка к экзаменам (ОРТ)

                                ## Авторизация
                                Большинство эндпоинтов требуют JWT-токен в заголовке:
                                ```
                                Authorization: Bearer <token>
                                ```
                                Токен выдается при регистрации (`POST /api/auth/register`) и входе (`POST /api/auth/login`).

                                ## Публичные эндпоинты (без JWT)
                                - `POST /api/auth/**` — регистрация, вход, OTP, сброс пароля
                                - `GET /api/regions` — список регионов
                                - `GET /api/feed/**` — рейтинг и новости

                                ## Поддержка языков
                                Контент хранится на двух языках: **русский (RU)** и **кыргызский (KY)**.
                                - Для авторизованных запросов язык определяется автоматически из профиля пользователя (`PUT /api/profile/language`).
                                - Для публичных эндпоинтов (лента) передайте параметр `?lang=KY`.
                                - Если перевод на KY не заполнен — возвращается русский текст.

                                ## Роли
                                - **USER** — обычный пользователь
                                - **ADMIN** — доступ к `/api/admin/**`

                                ---

                                ## WebSocket — Игра в реальном времени

                                ### Подключение
                                | Параметр | Значение |
                                |----------|----------|
                                | Эндпоинт (raw WS) | `ws://<host>/ws` |
                                | Эндпоинт (SockJS) | `http://<host>/ws` |
                                | Протокол | STOMP over WebSocket |
                                | Авторизация | Заголовок STOMP CONNECT: `Authorization: Bearer <token>` |

                                ### Шаги подключения (клиент)
                                ```
                                1. POST /api/game/join/{gameTestId}  → { status, roomId }
                                2. WS CONNECT  headers: { Authorization: "Bearer <token>" }
                                3. SUBSCRIBE  /topic/game/{roomId}
                                4. SUBSCRIBE  /topic/game/{roomId}/answers/{myUserId}
                                5. Ждать событие GAME_STARTED, затем NEXT_QUESTION
                                6. SEND  /app/game/{roomId}/answer  { "optionId": 103 }
                                7. Получить ANSWER_RESULT → следующий NEXT_QUESTION или GAME_OVER
                                ```

                                ### Входящие события (сервер → клиент)

                                #### GAME_STARTED  (`/topic/game/{roomId}`)
                                ```json
                                {
                                  "type": "GAME_STARTED",
                                  "roomId": 7,
                                  "player1Id": 42,  "player1Name": "Айнур",  "player1Avatar": "https://...",
                                  "player2Id": 55,  "player2Name": "Бекзат", "player2Avatar": null,
                                  "player1Score": 0, "player2Score": 0
                                }
                                ```

                                #### NEXT_QUESTION  (`/topic/game/{roomId}`)
                                ```json
                                {
                                  "type": "NEXT_QUESTION",
                                  "roomId": 7,
                                  "questionIndex": 0,
                                  "totalQuestions": 10,
                                  "timeLimitSeconds": 30,
                                  "question": {
                                    "id": 88,
                                    "text": "Чему равно среднее арифметическое 10, 20, 30?",
                                    "imageUrl": null,
                                    "options": [
                                      { "id": 101, "text": "10" },
                                      { "id": 102, "text": "20" },
                                      { "id": 103, "text": "30" },
                                      { "id": 104, "text": "60" }
                                    ]
                                  },
                                  "player1Score": 1,
                                  "player2Score": 0
                                }
                                ```

                                #### ANSWER_RESULT  (`/topic/game/{roomId}/answers/{userId}`)
                                > Личное событие — приходит только тому игроку, который ответил
                                ```json
                                {
                                  "type": "ANSWER_RESULT",
                                  "roomId": 7,
                                  "correct": true,
                                  "player1Score": 2,
                                  "player2Score": 1
                                }
                                ```

                                #### GAME_OVER  (`/topic/game/{roomId}`)
                                ```json
                                {
                                  "type": "GAME_OVER",
                                  "roomId": 7,
                                  "player1Score": 7,
                                  "player2Score": 5,
                                  "winnerId": 42
                                }
                                ```
                                > `winnerId: null` при ничьей

                                ### Исходящие сообщения (клиент → сервер)

                                #### Отправить ответ
                                ```
                                DESTINATION: /app/game/{roomId}/answer
                                BODY: { "optionId": 102 }
                                ```
                                > `optionId` — id из массива `question.options` текущего вопроса

                                ### Таймер вопроса
                                Если игрок не ответил за `timeLimitSeconds` секунд — вопрос считается пропущенным (0 очков) и сервер автоматически переходит к следующему.

                                ### Автоматический соперник
                                Если за **15 секунд** после создания комнаты реальный соперник не найден — система автоматически присоединяет бота с именем реального пользователя. Игрок не уведомляется об этом.
                                """))
                .servers(List.of(
                        new Server().url("/").description("Текущий сервер")
                ))
                .components(new Components()
                        .addSecuritySchemes("Bearer", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT-токен из ответа /api/auth/login или /api/auth/register")));
    }
}