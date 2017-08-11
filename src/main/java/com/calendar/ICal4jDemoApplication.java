package com.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ICal4jDemoApplication {

	public static void main(String[] args) {


		SpringApplication.run(ICal4jDemoApplication.class, args);


//		Timer timer = new Timer ();
//		TimerTask hourlyTask = new TimerTask () {
//			@Override
//			public void run () {
//				DeleteTask task = new DeleteTask();
//				task.DeleteFiles();
//			}
//		};
//
//		timer.schedule (hourlyTask, 0l, 15000);
	}

//	@Bean
//	public WebMvcConfigurerAdapter webMvcConfigurerAdapter() {
//		return new WebMvcConfigurerAdapter() {
//			@Override
//			public void addInterceptors(InterceptorRegistry registry) {
//				System.out.println("boot");
//
//				registry.addInterceptor(new MyInterceptor()).addPathPatterns("/tempPdf");
//			}
//		};
//	}



}


