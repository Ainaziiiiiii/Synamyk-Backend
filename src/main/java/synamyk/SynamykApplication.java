package synamyk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import synamyk.config.AnthropicConfig;
import synamyk.config.FinikConfig;

@SpringBootApplication
@EnableConfigurationProperties({FinikConfig.class, AnthropicConfig.class})
public class SynamykApplication {

    public static void main(String[] args) {
        SpringApplication.run(SynamykApplication.class, args);
    }
}