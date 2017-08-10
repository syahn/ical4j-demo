package com.calendar.data;

import java.io.File;

/**
 * Created by NAVER on 2017-08-10.
 */
public class DeleteTask {

    public void DeleteFiles() {
        File tempHtml = new File("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/html/");
        File tempPdf = new File("C:/Users/NAVER/Desktop/ical4j-demo/target/classes/static/tempPdf/");
        System.out.println("Called deleteFiles");
        DeleteFiles(tempHtml);
        DeleteFiles(tempPdf);
    }

    public void DeleteFiles(File file) {
        System.out.println("Now will search folders and delete files,");
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                DeleteFiles(f);
            }
        } else {
            file.delete();
        }
    }
}
