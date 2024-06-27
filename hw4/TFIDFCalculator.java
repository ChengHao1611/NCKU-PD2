import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TFIDFCalculator {
    public static void main(String[] args){
        try{
            HandleArticle doc = new HandleArticle();
            TFIDFC math = new TFIDFC();
            doc.setReader(new BufferedReader(new FileReader(args[0])));
            List<Trie> test = doc.getArticle(); //get type Trie of every articles
            Trie IDFOfWord = new Trie(); //record IDF of word
            
            BufferedReader tc = new BufferedReader(new FileReader(args[1]));
            String[] word = tc.readLine().split(" "); //we want to find word in article
            String[] index = tc.readLine().split(" "); // index of article

            FileWriter output = new FileWriter("output.txt");
            
            String store = "";
            for(int i = 0;i < word.length;i++){
                store += String.format("%.5f", math.tfIdfCalculate(test.get(Integer.parseInt(index[i])),test,word[i],IDFOfWord)) + " ";
            }
            output.write(store);
            output.close();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}
