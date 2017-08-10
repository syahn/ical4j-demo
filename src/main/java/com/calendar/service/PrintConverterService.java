package com.calendar.service;

/**
 * Created by NAVER on 2017-07-07.
 */

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionNamed;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

@Service
public class PrintConverterService {

    //pdf저장 메소드
    final static String fileUrl = "C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/html/month";//기본 url뒤에 월을 붙임

    public void makeAPdf(int startMonth, int endMonth, int orientation) throws InterruptedException, IOException {

        // 각 월에 대한 임시 경로 생성
        StringBuilder selectFiles = new StringBuilder();
        for (int month = startMonth; month <= endMonth; month++) {
            selectFiles.append(fileUrl).append(Integer.toString(month)).append(".html ");
        }

        String extendedUrl = "wkhtmltopdf" +
                (orientation == 1 ? " -O landscape " : " ") +
                "%s C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/tempPdf/month_result.pdf";

        String command = String.format(extendedUrl, selectFiles.toString());
        Process wkhtml = Runtime.getRuntime().exec(command); // Start process
        wkhtml.waitFor(); // Allow process to run

        File file = new File("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/tempPdf/month_result.pdf");

        PDDocument document = PDDocument.load(file);

        //Creating PDActionJavaScript object
        PDActionNamed action = new PDActionNamed();
        action.setN("Print");

        //Embedding java script
        document.getDocumentCatalog().setOpenAction(action);

        //Saving the document
        document.save( new File("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/tempPdf/month_result.pdf") );
        System.out.println("Data added to the given PDF");

        //Closing the document
        document.close();
    }
}

