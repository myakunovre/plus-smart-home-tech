package ru.yandex.practicum.telemetry.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = "ru.yandex.practicum.telemetry.collector")
@EnableScheduling
public class Collector {

    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }

    @Scheduled(fixedDelay = 5000)
    public void taskWithFixedDelay() {
        System.out.println("Очередное выполнение: " + System.currentTimeMillis() / 1000);
    }
}