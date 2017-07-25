package com.calendar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ICal4jDemoApplication {

	public static void main(String[] args) throws IOException {

		SpringApplication.run(ICal4jDemoApplication.class, args);

		//로컬 템플릿 불러오기
//		Document doc = null;
//		File input = new File("/Users/NAVER/Desktop/ical4j-demo/src/main/resources/static/html/month_6.html");
//		doc = Jsoup.parse(input, "UTF-8");

		//로컬에 새로운 html 파일로 저장
//		String output = "/Users/NAVER/Desktop/ical4j-demo/src/main/resources/static/html/test.html";
//		BufferedWriter htmlWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output), "UTF-8"));
//		htmlWriter.write(doc.toString());
//		htmlWriter.flush();
//		htmlWriter.close();
	}
}
