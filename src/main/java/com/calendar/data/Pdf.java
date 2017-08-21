package com.calendar.data;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by NAVER on 2017-08-11.
 */
public class Pdf {

    String html = "<p>An <a href='http://example.com/'><b>example</b></a> link.</p>";
    Document doc = Jsoup.parse(html);
    Element link = doc.select("a").first();

    String text = doc.body().text(); // "An example link"
    String linkHref = link.attr("href"); // "http://example.com/"
    String linkText = link.text(); // "example""

    String linkOuterH = link.outerHtml();
    String linkInnerH = link.html();
}



