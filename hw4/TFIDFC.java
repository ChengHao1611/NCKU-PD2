import java.util.List;
import java.lang.Math;

public class TFIDFC{
    public double tf(Trie doc, String term) {
        return (double)doc.searchNum(term) / doc.countWord;
    }
    
    public double idf(List<Trie> docs, String term) {
        int docsHaveTerm = 0;
        for(int i = 0;i < docs.size();i++){
            if(docs.get(i).searchNum(term) != 0) docsHaveTerm++;
        }
        return Math.log((double)docs.size() / docsHaveTerm);
    }

    public double tfIdfCalculate(Trie doc, List<Trie> docs, String term,Trie IDFOfWord) {
        return tf(doc, term) * searchDIF(docs, term, IDFOfWord);
    }

    public double searchDIF(List<Trie> docs,String word,Trie IDFOfWord) {
        TrieNode node = IDFOfWord.root; //like search of class "HandleArticle"
        if(IDFOfWord.searchNum(word) == 0) IDFOfWord.insert(word);
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return 0;
            }
        }
        //if node.wordIDF == -1.0,it means that the word of "IDFOfWord" doesn't find the IDF
        if(node.wordIDF == -1.0) node.wordIDF = idf(docs,word); 
        return node.wordIDF;
    }
}
