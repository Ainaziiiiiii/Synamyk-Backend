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