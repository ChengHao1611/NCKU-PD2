// JsoupExample.java


import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;



class App {
    public static void main(String[] args) {
        try {
            Document doc = Jsoup.connect("https://en.wikipedia.org/").get();
            System.out.println(doc.title());
            Elements newsHeadlines = doc.select("#mp-itn b a");
            for (Element headline : newsHeadlines) {
                System.out.printf("%s\n\t%s", 
                headline.attr("title"), headline.absUrl("href"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
