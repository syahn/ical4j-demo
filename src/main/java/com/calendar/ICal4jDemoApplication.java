package com.calendar;

import com.calendar.data.DeleteTask;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class ICal4jDemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(ICal4jDemoApplication.class, args);

		Timer timer = new Timer ();
		TimerTask hourlyTask = new TimerTask () {
			@Override
			public void run () {
				DeleteTask task = new DeleteTask();
				task.DeleteFiles();
			}
		};

		timer.schedule (hourlyTask, 0l, 15000);
	}
}
