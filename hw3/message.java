import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader; 
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

/*  
args[0] is mode
args[1] is task
args[2] is stock name
args[3] is the day of the beginning
args[4] is the day of the end
                                    */

public class message {
    public static void main(String[] args) throws IOException {  
        if(args[0].equals("0")){ // mode 0
            String stockTitle = Title();
            Map<String, String> stockName = stock_Price(); // < day , stock price >
            String filename = "data.csv";
            File file = new File(filename);
            String output = "";

            if(!file.exists()) {
                output = stockTitle + "\n";
            }
            for(String s : stockName.keySet()){
                output += s + " " + stockName.get(s) + "\n";
            }
            try(BufferedWriter bw = new BufferedWriter(new FileWriter("data.csv", true))){
                bw.write(output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{ // mode 1
            if(args[1].equals("0")){ // task 0
                String filename = "data.csv";
                File file = new File(filename);
                BufferedReader br = new BufferedReader(new FileReader(file));
                String st;
                String content = "";
                while((st = br.readLine()) != null) {
                    if(st.contains("day")) {
                        int l = st.indexOf(" ");
                        st = st.substring(l + 1);
                    }
                    String[] data = st.split(" ");
                    for(int i = 0;i < data.length - 1;i++) {
                        content += data[i] + ",";
                    }
                    content += data[data.length - 1] + "\n";
                }
                br.close();
                content = content.substring(0, content.length() - 2);
                try(BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv"))){
                    bw.write(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else { // args.length > 2
                Map<String, List<String>> data = classify();
                String stock = "";
                Boolean find = false;
                int start = Integer.parseInt(args[3]);
                int end = Integer.parseInt(args[4]);
                int endBegin = end - 4;
                double averagePrice = 0;
                
                for(int i = start - 1;i < end;i++) {
                    averagePrice += Double.parseDouble(data.get(args[2]).get(i));
                }
                averagePrice /= (end - start + 1);

                for(String s : data.keySet()) { // to find the stock we need to output ( args[2] )
                    if(s.equals(args[2])) {
                        stock = s;
                        find = true;
                        break;    
                    }
                }

                if(!find) { // the target stock is not found
                    System.out.println("Stock not found");
                    return;
                }

                if(args[1].equals("1")){ // task 1
                    int count = start;
                    int startDay = 0;
                    double[] average = new double[endBegin - start + 1]; // store the average of every 5 days

                    // 從第count天開始算五天的平均
                    while(count <= endBegin) { 
                        double sum = 0;
                        for(int i = 0;i < 5;i++) {
                            sum += Double.parseDouble(data.get(stock).get(count + i - 1));
                        }
                        average[startDay] = sum / 5;
                        startDay++;
                        count++;
                    }

                    // 將average[]四捨五入到小數點後二位
                    for(int i = 0;i < average.length;i++) { 
                        average[i] = Rounding(average[i]);
                    }
                    try(BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true))){
                        String content = "";
                        content += stock + "," + start + "," + end + "\n";
                        for(int i = 0;i < average.length;i++) {
                            if(!String.valueOf(average[i]).endsWith("0")) {
                                content += average[i];
                            }
                            else {
                                content += (int) average[i];                           
                            }
                            if(i != average.length - 1) {
                                content += ",";
                            }
                        }
                        content += "\n";
                        bw.write(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(args[1].equals("2")) { // task 2
                    Set<String> test = data.keySet();
                    System.out.println(test.size());
                    for(String s : data.keySet()) { // to calculate standard deviation 標準差　// bug:索引出界
                        double sum = 0;
                        for(int i = start - 1;i < end;i++) {
                            //System.out.println(data.get(s));
                            double temp = Double.parseDouble(data.get(s).get(i)) - averagePrice;
                            sum += temp * temp;
                        }
                        sum /= (end - start);
                        double temp = sum / 2;
                        sum = squareRoot(sum, temp);
                        sum = Rounding(sum);
                        String Sum = String.valueOf(sum);
                        if(Sum.endsWith("0")){
                            Sum = Sum.substring(0, Sum.length() - 2);
                        }
                        data.get(s).add(Sum);
                    }
                    try(BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv", true))){
                        String content = "";
                        content += stock + "," + start + "," + end + "\n" + data.get(stock).get(30) + "\n";
                        bw.write(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(args[1].equals("3")) { // task 3
                    for(String s : data.keySet()) { // to calculate standard deviation 標準差 // bug:索引出界
                        double sum = 0;
                        for(int i = start - 1;i < end;i++) {
                            double temp = Double.parseDouble(data.get(s).get(i)) - averagePrice;
                            sum += temp * temp;
                        }
                        sum /= (end - start);
                        double temp = sum / 2;
                        sum = squareRoot(sum, temp);
                        sum = Rounding(sum);
                        String Sum = String.valueOf(sum);
                        if(Sum.endsWith("0")){
                            Sum = Sum.substring(0, Sum.length() - 2);
                        }
                        data.get(s).add(Sum);
                    }
                    String[] Top = new String[3];
                    double[] TopPrice = new double[3];
                    for(String s : data.keySet()) {
                        double now = Double.parseDouble(data.get(s).get(30));
                        if(now > TopPrice[0]) {
                            TopPrice[2] = TopPrice[1];
                            TopPrice[1] = TopPrice[0];
                            TopPrice[0] = now;
                            Top[2] = Top[1];
                            Top[1] = Top[0];
                            Top[0] = s;
                        }
                    }
                    try(BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv"))){
                        String content = "";
                        content += Top[0] + "," + Top[1] + "," + Top[2] + "," + start + "," + end + "\n";
                        content += data.get(Top[0]).get(30) + "," + data.get(Top[1]).get(30) + "," + data.get(Top[2]).get(30) + "\n";
                        bw.write(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else { // task 4
                    double averageT = 0; // T平均
                    for(int i = start - 1;i < end;i++) {
                        averageT += Double.parseDouble(data.get(stock).get(i));
                    }
                    averageT /= (end - start + 1);
                    
                    double sum = 0;
                    double sumM = 0;
                    for(int i = start - 1;i < end;i++) {
                        int day = i + 1;
                        sum += (Double.parseDouble(data.get(stock).get(i)) - averagePrice) * (day - averageT);
                        sumM += (day - averageT) * (day - averageT);
                    }
                    double b1 = sum / sumM;
                    double b0 = averagePrice - b1 * averageT;
                    b1 = Rounding(b1);
                    b0 = Rounding(b0);
                    String B1 = String.valueOf(b1);
                    String B0 = String.valueOf(b0);
                    if(B1.endsWith("0")) {
                        B1 = B1.substring(0, B1.length() - 2);
                    }
                    if(B0.endsWith("0")) {
                        B0 = B0.substring(0, B0.length() - 2);
                    }
                    try(BufferedWriter bw = new BufferedWriter(new FileWriter("output.csv"))){
                        String content = "";
                        content += stock + "," + start + "," + end + "\n" + B1 + "," + B0 + "\n";
                        bw.write(content);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    } // main end

    static String Title(){ // every stocks' title
        String dataTitle = "";
        try {
            Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();
            Elements title = doc.select("th");
            for(Element s : title){
                dataTitle += s.text() + " ";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataTitle;
    } 
    
    static Map<String, String> stock_Price() throws IOException {
        Map<String, String> data = new HashMap<String, String>();

        Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();
        data.put(doc.title(), "");
        Elements newsHeadlines = doc.select("td");
        for(Element headline : newsHeadlines) {
            data.put(doc.title(), data.get(doc.title()) + headline.html() + " ");
        }
        return data;
    }

    static Map<String, List<String>> classify() throws IOException { // classify the data
        Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
        String filename = "data.csv";
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        List<String> title = new ArrayList<String>();

        while((st = br.readLine()) != null) {
            String[] data = st.split(" "); // data[0] = day

            if (!st.contains("day")) {
                // store the title of every stocks
                for (String s : data) {
                    title.add(s);
                    dataMap.put(s, new ArrayList<String>());
                }
            }
            else {
                int i = 1;

                // the price of every stocks
                for(String s : title) {
                    dataMap.get(s).add(data[i]);
                    i++;
                }
            }
        }
        br.close();
        return dataMap;
    }

    static double squareRoot(double num, double half) { // square root
        double done = (half + num / half) / 2;
        if(absolute(half - done) < 0.00001) {
            return done;
        }
        else {
            return squareRoot(num, done);
        }
    }

    static double absolute(double num) { // absolute value
        if(num < 0) {
            return -num;
        }
        return num;
    }

    static double Rounding(double num) { // rounding
        return ((int) (num * 100 + 0.5)) / 100.0;
    }

}