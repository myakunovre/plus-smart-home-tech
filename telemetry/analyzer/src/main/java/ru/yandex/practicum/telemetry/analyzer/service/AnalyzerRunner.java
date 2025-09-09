package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AnalyzerRunner implements CommandLineRunner {
    final HubEventProcessor hubEventProcessor;
    final SnapshotProcessor snapshotProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread hubEventsThread = new Thread(hubEventProcessor);
        Thread snapshotThread = new Thread(snapshotProcessor);

        hubEventsThread.setName("HubEventHandlerThread");
        snapshotThread.setName("SnapshotHandlerThread");

        hubEventsThread.start();
        snapshotThread.start();
    }
}
