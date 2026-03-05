package com.example.contactselection.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Swagger / OpenAPI Configuration
 * URL: http://localhost:8080/api/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI contactSelectionOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Contact Selection API – 問合せ先選択")
                        .description("""
                                ## API cho màn hình Chọn nơi liên hệ (問合せ先選択)

                                ### Endpoints:
                                - **ref_select_load** – Tải danh sách khu vực (初期表示)
                                - **ref_select_search** – Tìm kiếm nơi liên hệ (検索)

                                ### Business Rules:
                                - Kết quả = 0 → Error 404
                                - Kết quả > 80 → Hỏi xác nhận (needsConfirmation = true)
                                - kindRef = 0 → Chỉ chọn 1; kindRef = 1 → Chọn nhiều
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Development Team")
                                .email("haile.ktqt@gmail.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080/api")
                                .description("Local Development Server")));
    }
}
