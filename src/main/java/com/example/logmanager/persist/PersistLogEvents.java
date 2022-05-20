package com.example.logmanager.persist;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.example.logmanager.model.Alert;
import com.example.logmanager.model.Event;
import com.example.logmanager.model.LogFileLocation;
import com.example.logmanager.model.State;
import com.example.logmanager.repository.AlertRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


class ThreadTask implements Runnable {
	private static final Logger log = LoggerFactory.getLogger(ThreadTask.class);
	private Event event;
	private AlertRepository alertRepository;
	private static long THRESHOLD_TIME = 4;

	private static Map<String, Event> eventMap = new HashMap<>();
	private static Map<String, Alert> alertMap = new HashMap<>();

	public ThreadTask(Event e, AlertRepository alertRepository) {
		this.event = e;
		this.alertRepository = alertRepository;
	}

	public void run() {
		if (eventMap.containsKey(event.getId())) {
			Event e1 = eventMap.get(event.getId());
			long executionTime = findEventExecutionTime(event, e1);
			Alert alert = new Alert(event, Math.toIntExact(executionTime));
			if (executionTime > THRESHOLD_TIME) {
				alert.setAlert(Boolean.TRUE);
				log.trace("Event execution time: {} is {} ms", event.getId(), executionTime);
			}			
			alertMap.put(event.getId(), alert);
			alertRepository.save(alert);
			eventMap.remove(event.getId());
		} else {
			// Add event into eventMap for the first time
			eventMap.put(event.getId(), event);
			log.trace("Adding {} events with ID {} to eventMap ...", eventMap.size(), event.getId());
		}
	}

	private long findEventExecutionTime(Event event1, Event event2) {
		Event endEvent = Stream.of(event1, event2).filter(e -> State.FINISHED.equals(e.getState())).findFirst()
				.orElse(null);
		Event startEvent = Stream.of(event1, event2).filter(e -> State.STARTED.equals(e.getState())).findFirst()
				.orElse(null);
		return Objects.requireNonNull(endEvent).getTimestamp() - Objects.requireNonNull(startEvent).getTimestamp();
	}
}

@Component
public class PersistLogEvents {

	private static final Logger log = LoggerFactory.getLogger(PersistLogEvents.class);
	private static int THREADS = 10;
	
	@Autowired
	private AlertRepository alertRepository;

	public void parseAndPersistEvents(LogFileLocation logFileLocation) {
		
		ExecutorService exec = Executors.newFixedThreadPool(THREADS);
		log.info("Parsing the log file JSONs...");
		try (LineIterator li = FileUtils
				.lineIterator(new ClassPathResource("files/" + logFileLocation.getLogFilePath()).getFile())) {
			while (li.hasNext()) {
				try {
					Event event = new ObjectMapper().readValue(li.nextLine(), Event.class);
					exec.submit(new ThreadTask(event, alertRepository));
				} catch (JsonProcessingException e) {
					log.error("JSON parsing failed {}", e.getMessage());
				}
			}		
			log.info("Shutting down Executor Service... !");
			exec.shutdown();
			exec.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);

		} catch (IOException e) {
			log.error(" Can't access the file: {}", e.getMessage());
		} catch (InterruptedException e) {
			log.info("InterruptedException: ",e);
		}
	}

}
