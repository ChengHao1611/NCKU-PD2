// "wordTime" can know the word appear times

import java.util.List;

class TrieNode {
    TrieNode[] children = new TrieNode[26];
    int wordTime = 0;
    double wordIDF = -1.0;
}

public class Trie {
    TrieNode root = new TrieNode();
    int countWord = 0;//know how many words in this "Trie"
    // 插入一個單詞到 Trie
    
    public void insert(String word) {
        countWord++;
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            if (node.children[c - 'a'] == null) {
                node.children[c - 'a'] = new TrieNode();
            }
            node = node.children[c - 'a'];
        }
        node.wordTime += 1;
    }

    // find number of word in article
    public int searchNum(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            node = node.children[c - 'a'];
            if (node == null) {
                return 0;
            }
        }
        return node.wordTime;
    }

    public int getCountWord(){
        return countWord;
    }
}
