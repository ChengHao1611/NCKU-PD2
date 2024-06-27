// JsoupExample.java


import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




class HtmlParser {

    public static List<String> storeOutputCsv(BufferedReader reader,List<String> storeOuput) throws IOException{
        String line;
        if((line = reader.readLine()) != null) storeOuput.add(line);
        while ((line = reader.readLine()) != null) { 
            storeOuput.add("\n"+line);
        }

        return storeOuput;
    }

    public static void inputStroedOutput(BufferedWriter outputCsv,List<String> storeOutput) throws IOException{
        for(String x : storeOutput){
            outputCsv.write(x);
        }
        outputCsv.write('\n');
    }
    
    public static List<String> mode0Read(BufferedReader reader,List<String> storeRead,Document doc){
        try{
            String line;
            if((line = reader.readLine()) != null) storeRead.add(line);
            while ((line = reader.readLine()) != null) { //if reader have content,store those to storeRead
                storeRead.add("\n"+line);
            }
            if(storeRead.size() == 0){ // if reader dosen't have any content
                Elements stockName = doc.select("th");
                String storeString = "";
                for (Element headline : stockName) {
                    storeString += headline.html().concat(" ");
                }
                storeString = storeString.trim();
                storeRead.add(storeString);
                for(int i = 0;i < 30;i++){
                    storeRead.add("\nspace");
                }
            }
            return storeRead;
        } catch(IOException e){
            e.printStackTrace();
            return storeRead;
        }    
    }

    public static void mode0Write(BufferedWriter writer,List<String> storeRead,Document doc) throws IOException{
        String title = doc.title();
        String storeString = "";
        Elements stockPrice = doc.select("td");            
        for (Element headline : stockPrice) {
            storeString += headline.html().concat(" ");
        }
        storeString = storeString.trim();
        title = title.substring(3);
        int day = Integer.valueOf(title);
        storeRead.set(day,'\n'+storeString);
        for(String x : storeRead){
            writer.write(x);
        }
        
    }
    
    public static void outputStoredStock(List<String> storeRead,BufferedWriter outputCsv) throws IOException{
        storeRead.set(0, storeRead.get(0).replace(' ', ','));
        outputCsv.write(storeRead.get(0));
        int len = storeRead.size();
        for(int i = 1;i < len ;i++){
            storeRead.set(i, storeRead.get(i).replace(' ', ','));
            outputCsv.newLine();
            outputCsv.write(storeRead.get(i));
        }
    }

    public static void grabDataToStringArray(BufferedReader data,List<String> storeRead) throws IOException{
        String line = "";
        while ((line = data.readLine()) != null) {
            storeRead.add(line);
        }
    }
    
    public static double averagerPrice(int start,int end,List<String> storeRead,String stock){
        double average = 0;

        //search index of stock in storeRead 
        String[] stockName = storeRead.get(0).split(" ");
        int index = 0;
        while(true){
            if(stockName[index].equals(stock)) break;
            index++;
        }

        //sum stock price from start to end
        for(int i = start; i <= end;i++){
            average += Double.parseDouble((storeRead.get(i).split(" "))[index]); 
        }

        //sum stock price from start t
        average = average / (end - start + 1);
        
        return average;
        //String convert = String.format("%.2f", average);
        //System.out.println(average+" "+Double.parseDouble(erase0(convert)));
        //return Double.parseDouble(erase0(convert));
    }
    
    public static String findAverageFromStartToEnd(int start,int end,List<String> storeRead,String stock){
        String re = "";
        re += stock+","+start+","+end+"\n";

        //first average and output .2f
        re += String.format("%.02f",averagerPrice(start,start+4, storeRead, stock));
        start++;

        //second or more average
        while((start + 4) <= end){
            String convert = String.format("%.02f",averagerPrice(start,start+4, storeRead, stock));
            re += ","+erase0(convert);
            start++;
        }
        return re;
    }
    
    public static double fineStandardDeviationFromStartToEnd(int start,int end,List<String> storeRead,String stock){
        double squareSum = 0;
        int index = 0;
        String[] stockName = storeRead.get(0).split(" ");
        //find index
        while(true){
            if(stockName[index].equals(stock)) break;
            index++;
        }

        double average = averagerPrice(start, end, storeRead, stock);

        //daily price squared
        for(int i = start;i <= end;i++){
            double value = Double.parseDouble((storeRead.get(i).split(" "))[index]);
            squareSum += (value - average) * (value - average);
        }
        
        //find standard deviation 
        double standardDeviation = Math.sqrt((squareSum / (end - start)));
        
        //convert double to .2f 
        String convert = String.format("%.2f", standardDeviation);
        return Double.parseDouble(erase0(convert));
    }

    public static String findTop3HeightStandardDeviation(int start,int end,List<String> storeRead){
        String re = "";
        String[] stockName = storeRead.get(0).split(" ");
        int stockNum = stockName.length;

        //index of top 3 of standard deviation
        int height = 0,medium = 1, low = 2,swap = 0; 
        Map<Integer,Double> storeStandardDeviation = new HashMap<>();
        //List<Double> storeStandardDeviation = new ArrayList<>(3);
        storeStandardDeviation.put(height,fineStandardDeviationFromStartToEnd(start, end, storeRead, stockName[height])); 
        storeStandardDeviation.put(medium,fineStandardDeviationFromStartToEnd(start, end, storeRead, stockName[medium])); 
        storeStandardDeviation.put(low,fineStandardDeviationFromStartToEnd(start, end, storeRead, stockName[low]));
        
        //sort height to low
        if(storeStandardDeviation.get(height) < storeStandardDeviation.get(medium)){
            swap = height;
            height = medium;
            medium = swap;
        }
        if(storeStandardDeviation.get(medium) < storeStandardDeviation.get(low)){
            swap = low;
            low = medium;
            medium = swap;
        }
        if(storeStandardDeviation.get(height) < storeStandardDeviation.get(medium)){
            swap = height;
            height = medium;
            medium = swap;
        }

        for(int i = 3; i < stockNum;i++){
            //if standard deviation of stock of index height than standard deviation of stock of low index
            double iIndexStandardDeviation = fineStandardDeviationFromStartToEnd(start, end, storeRead, stockName[i]);
            if(iIndexStandardDeviation > storeStandardDeviation.get(low)){
                storeStandardDeviation.remove(low);
                low = i;
                storeStandardDeviation.put(low, iIndexStandardDeviation);

                //if if standard deviation of stock of low height than standard deviation of stock of medium index, swap(medium,low)
                if(iIndexStandardDeviation > storeStandardDeviation.get(medium)){
                    swap = low;
                    low = medium;
                    medium = swap;
                    if(iIndexStandardDeviation > storeStandardDeviation.get(height)){
                        swap = height;
                        height = medium;
                        medium = swap;
                    }
                }
            }
        }
        re += stockName[height]+","+stockName[medium]+","+stockName[low]+","+start+","+end+"\n";

        //remove interger of double type
        re += erase0(storeStandardDeviation.get(height).toString())+",";
        re += erase0(storeStandardDeviation.get(medium).toString()) +",";
        re += erase0(storeStandardDeviation.get(low).toString());

        return re;
    }
    
    public static String findRegressionLine(int start,int end,List<String> storeRead,String stock){
        String re = stock+","+start+","+end+"\n";
        double timeAve = (double)(start + end) / 2;
        double priceAve = averagerPrice(start, end, storeRead, stock);
        double slopeDenominator = 0;
        double slopeNumerator = 0;
        double slope = 0;
        double intercept = 0;
        String convert = "";
        
        //fine stock index 
        String[] stockName = storeRead.get(0).split(" ");
        int index = 0;
        while(true){
            if(stockName[index].equals(stock)) break;
            index++;
        }

        for(double i = (double)start;i <= (double)end;i++){
            slopeDenominator += (i - timeAve) * (i - timeAve);

            slopeNumerator += (i - timeAve) * (Double.parseDouble(storeRead.get((int)i).split(" ")[index]) - priceAve);
        }

        slope = slopeNumerator / slopeDenominator;
        intercept = priceAve - (slope * timeAve);

        convert = String.format("%.02f", slope);
        re += erase0(convert)+",";

        convert = String.format("%.02f", intercept);
        re += erase0(convert);

        return re;
    }

    public static String erase0(String re){
        while(re.charAt(re.length()-1) == '0') re = re.substring(0, re.length()-1);

        if(re.charAt(re.length()-1) == '.') re = re.substring(0, re.length()-1);
        return re;
    }
    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("data.csv"));
            List<String> storeRead = new ArrayList<>(31);
            List<String> storeOutput = new ArrayList<>();
            Document doc = Jsoup.connect("https://pd2-hw3.netdb.csie.ncku.edu.tw/").get();
            
            //mode 0
            if(args[0].equals("0")){
                storeRead = mode0Read(reader,storeRead,doc);
                BufferedWriter writer1 = new BufferedWriter(new FileWriter("data.csv"));
                mode0Write(writer1, storeRead, doc);
                writer1.close();
            }
            else{//mode 1
                try {
                    
                    BufferedReader readOutput = new BufferedReader(new FileReader("output.csv"));
                    storeOutput = storeOutputCsv(readOutput,storeOutput);
                } catch (IOException e) {
                    BufferedWriter outputCsv = new BufferedWriter(new FileWriter("output.csv"));
                }
                BufferedReader data = new BufferedReader(new FileReader("data.csv"));
                BufferedWriter outputCsv = new BufferedWriter(new FileWriter("output.csv"));
                if(storeOutput.size() != 0)inputStroedOutput(outputCsv, storeOutput);
                grabDataToStringArray(data, storeRead);
                //task 0
                if(args[1].equals("0")){
                    outputStoredStock(storeRead,outputCsv);
                }
                else{ //task 1 2 3 4
                    String stock = args[2];
                    int start = Integer.valueOf(args[3]);
                    int end = Integer.valueOf(args[4]);
                    if(args[1].equals("1"))  //task 1
                        outputCsv.write(findAverageFromStartToEnd(start, end, storeRead, stock));
                    else if(args[1].equals("2"))  //task 2
                        outputCsv.write(stock+","+start+","+end+"\n"+fineStandardDeviationFromStartToEnd(start, end, storeRead, stock));
                    else if(args[1].equals("3")) 
                        outputCsv.write(findTop3HeightStandardDeviation(start, end, storeRead));
                    else if(args[1].equals("4")) 
                        outputCsv.write(findRegressionLine(start, end, storeRead, stock));
                }
                outputCsv.close();
            }


            System.out.println("--------------------");


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
