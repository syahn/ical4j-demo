package com.calendar.service;

/**
 * Created by NAVER on 2017-07-07.
 */

import com.calendar.data.PrintRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Arrays;
import java.util.List;

@Service
public class PrintConverterService {

    private static final Logger LOG = LoggerFactory.getLogger(PrintConverterService.class);

    public void createImage(PrintRequest request, int orientation) {

        List<String> pdfCommand = null;

        //가로방향 이미지
        if(orientation==0){
            pdfCommand = Arrays.asList(
                    "wkhtmltoimage",
                    "--height",
                    "735",
                    request.getIn(),
                    request.getOut()

            );
        }
        //세로 방향 이미지
        else {
            pdfCommand = Arrays.asList(
                    "wkhtmltoimage",
                    "--height",
                    "1000",
                    "--width",
                    "700",
                    request.getIn(),
                    request.getOut()
            );
        }

        ProcessBuilder pb = new ProcessBuilder(pdfCommand);
        Process pdfProcess;

        try {
            pdfProcess = pb.start();
            waitForProcessInCurrentThread(pdfProcess);

        } catch (IOException ex) {
            LOG.error("Could not create a PDF file because of an error occurred: ", ex);
            throw new RuntimeException("PDF generation failed");
        }
    }

    public void createPdf(PrintRequest request, HttpServletResponse response) {
        List<String> pdfCommand = Arrays.asList(
                "wkhtmltoimage",
                "--height",
                "735",
                request.getIn(),
                request.getOut()
        );
        ProcessBuilder pb = new ProcessBuilder(pdfCommand);
        Process pdfProcess;

        try {
            pdfProcess = pb.start();
            waitForProcessInCurrentThread(pdfProcess);

        } catch (IOException ex) {
            LOG.error("Could not create a PDF file because of an error occurred: ", ex);
            throw new RuntimeException("PDF generation failed");
        }
    }

    public void create(PrintRequest request, HttpServletResponse response) {
        List<String> pdfCommand = Arrays.asList(
                "wkhtmltopdf",
                request.getIn(),
                "-"
        );

        ProcessBuilder pb = new ProcessBuilder(pdfCommand);
        Process pdfProcess;

        try {
            pdfProcess = pb.start();

            try (InputStream in = pdfProcess.getInputStream()) {
                pdfToResponse(in, response);
                waitForProcessInCurrentThread(pdfProcess);
                requireSuccessfulExitStatus(pdfProcess);
                setResponseHeaders(response, request);

                LOG.info("Wrote PDF file to the response from request: {}", request);
            } catch (Exception ex) {
                writeErrorMessageToLog(ex, pdfProcess);
                throw new RuntimeException("PDF generation failed");
            } finally {
                pdfProcess.destroy();
            }
        } catch (IOException ex) {
            LOG.error("Could not create a PDF file because of an error occurred: ", ex);
            throw new RuntimeException("PDF generation failed");
        }
    }

    private void pdfToResponse(InputStream in, HttpServletResponse response) throws IOException {
        LOG.debug("Writing created PDF file to HTTP response");

        OutputStream out = response.getOutputStream();
        org.apache.tomcat.util.http.fileupload.IOUtils.copy(in, out);
        out.flush();
    }

    private void waitForProcessInCurrentThread(Process process) {
        try {
            process.waitFor();
            //delay
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        LOG.debug("Wkhtmltopdf ended");
    }

    private void requireSuccessfulExitStatus(Process process) {
        if (process.exitValue() != 0) {
            throw new RuntimeException("PDF generation failed");
        }
    }

    private void setResponseHeaders(HttpServletResponse response, PrintRequest request) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + request.getOut() + "\"");
    }

    private void writeErrorMessageToLog(Exception ex, Process pdfProcess) throws IOException {
        LOG.error("Could not create PDF because an exception was thrown: ", ex);
        LOG.error("The exit value of PDF process is: {}", pdfProcess.exitValue());

        String errorMessage = getErrorMessageFromProcess(pdfProcess);
        LOG.error("PDF process ended with error message: {}", errorMessage);
    }

    private String getErrorMessageFromProcess(Process pdfProcess) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(pdfProcess.getErrorStream()));
            StringWriter writer = new StringWriter();

            String line;
            while ((line = reader.readLine()) != null) {
                writer.append(line);
            }

            return writer.toString();
        } catch (IOException ex) {
            LOG.error("Could not extract error message from process because an exception was thrown", ex);
            return "";
        }
    }


    //pdf저장 메소드
    final static String url = "http://localhost:9000/month/";//기본 url뒤에 월을 붙임

    public static void makeAPdf(int startMonth, int endMonth, int orientation) throws InterruptedException, IOException {

        // 각 월에 대한 임시 경로 생성
        String temp = "";
        for(int i=startMonth;i<=endMonth;i++){
            temp+=(url+Integer.toString(i)+" ");
        }

        Process wkhtml; // Create uninitialized process
        String command = null;

        String extendedUrl = "wkhtmltopdf" +
            (orientation == 1 ? " -O landscape " : " ") +
            "%s C:/Users/NAVER/Desktop/prototype/target/classes/static/tempPdf/month_result.pdf";

        command = String.format(extendedUrl, temp);

        wkhtml = Runtime.getRuntime().exec(command); // Start process
        //IOUtils.copy(wkhtml.getErrorStream(), System.err); // Print output to console
        wkhtml.waitFor(); // Allow process to run
        System.out.println(temp);
    }
}

