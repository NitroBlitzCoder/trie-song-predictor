public class TrieTest {

    public static void main(String[] args){

        AdvancedTrie trie = new AdvancedTrie();

        // Space-separated word block (nice & readable)
        String data = """
            apple banana apple apple
            and and and any any
            cat dog dog any any
            apple any banana any
            """;

        // Load words
        for (String w : data.split("\\s+")){ // split on white space
            trie.insert(w.toLowerCase());
        }

        System.out.println("---- contains ----");
        System.out.println("contains(\"apple\") --> " + trie.contains("apple"));
        System.out.println("contains(\"banana\") --> " + trie.contains("banana"));
        System.out.println("contains(\"ban\") --> " + trie.contains("ban"));
        System.out.println("contains(\"zebra\") --> " + trie.contains("zebra"));

        System.out.println("\n---- mostLikelyNextChar ----");
        System.out.println("mostLikelyNextChar(\"a\") --> " + trie.mostLikelyNextChar("a"));
        System.out.println("mostLikelyNextChar(\"ap\") --> " + trie.mostLikelyNextChar("ap"));
        System.out.println("mostLikelyNextChar(\"do\") --> " + trie.mostLikelyNextChar("do"));
        System.out.println("mostLikelyNextChar(\"x\") --> " + trie.mostLikelyNextChar("x"));

        System.out.println("\n---- mostLikelyNextWord ----");
        System.out.println("mostLikelyNextWord(\"a\") --> " + trie.mostLikelyNextWord("a"));
        System.out.println("mostLikelyNextWord(\"ap\") --> " + trie.mostLikelyNextWord("ap"));
        System.out.println("mostLikelyNextWord(\"b\") --> " + trie.mostLikelyNextWord("b"));
        System.out.println("mostLikelyNextWord(\"z\") --> " + trie.mostLikelyNextWord("z"));

        System.out.println("\n---- printWordFrequencies ----");
        trie.printWordFrequencies();
        System.out.println("\nNOTE:  Alphabetical sorting also acceptable\n\n" );

        System.out.println(trie.getWordFreqs());
    }
}
