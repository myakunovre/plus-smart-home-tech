package ru.yandex.practicum.telemetry.collector;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
//@ComponentScan(basePackages = "ru.yandex.practicum.telemetry.collector")
//@EnableScheduling
public class Collector {

    public static void main(String[] args) {
        SpringApplication.run(Collector.class, args);
    }

//    @Scheduled(fixedDelay = 5000)
//    public void taskWithFixedDelay() {
//        System.out.println("Очередное выполнение: " + System.currentTimeMillis() / 1000);
//    }
}