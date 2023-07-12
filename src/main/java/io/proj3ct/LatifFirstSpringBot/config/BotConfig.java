package io.proj3ct.LatifFirstSpringBot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;


@Configuration
@EnableScheduling // есть медоты, которые подледат автоматичекому запуску
@PropertySource("application.properties")
@Data
public class BotConfig {

    @Value("${bot.name}")
    private String botName;
    @Value("${bot.key}")
    private String token;
    @Value("${bot.owner}")
    private Long ownerID;
}
