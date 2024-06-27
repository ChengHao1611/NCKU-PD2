import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


//read file
public class HandleArticle {
    private BufferedReader reader;
    private List<Trie> article = new ArrayList<>();
    
    //set Reader
    public void setReader(BufferedReader docName){
        this.reader = docName;
    }

    public void splitArticle() throws IOException{
        String line = "";
        String store = "";
        while((line = reader.readLine()) != null){
            String index = "";
            for(int i = 0;i < line.length();i++){
                if(line.charAt(i) != ' ') index += line.charAt(i); // know index in front of sentence 
                else break;
            }
            
            store += line;
            if(Integer.parseInt(index) % 5 == 0){ // 5 sentences make up an article
                Trie tree = new Trie();
                //handle "store" toLowerCase and remove not a-z and ' '
                store = store.toLowerCase();
                store = store.replaceAll("[^a-z\\s]", " ");
                store = store.replaceAll("\\s+", " ");
                String[] stores = store.split(" ");
                for(int i = 1;i < stores.length;i++){
                    tree.insert(stores[i]);
                }
                article.add(tree);
                store = "";
            }
        }
    }
    
    //let List<String> "strArrayArticle" convert to List<Trie> "article"
    public List<Trie> getArticle() throws IOException{
        splitArticle();
        return article;
    }

}
