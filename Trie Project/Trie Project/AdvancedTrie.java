import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.Buffer;
import java.util.*;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AdvancedTrie {
    private Node root;
    private TreeMap<String, Node> wordFreqHolder = new TreeMap<>();
    private HashMap<String, AdvancedTrie> magic = new HashMap<>();
    private HashSet<String> englishSet = new HashSet<>();
    private HashSet<String> spanishSet = new HashSet<>();
    private HashSet<String> frenchSet = new HashSet<>();

    public AdvancedTrie() {
        root = new Node();
        loadSets(); // risky operation, potentially too much
        doMagic("song_texts");
    }

    public void loadSets() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("\\language_texts\\english.txt"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                for (String s : line.split(" "))
                    englishSet.add(s);
            }
            reader.close();

            reader = new BufferedReader(new FileReader("\\language_texts\\spanish.txt"));
            line = "";
            while ((line = reader.readLine()) != null) {
                for (String s : line.split(" "))
                    spanishSet.add(s);
            }
            reader.close();

            reader = new BufferedReader(new FileReader("\\language_texts\\french.txt"));
            line = "";
            while ((line = reader.readLine()) != null) {
                for (String s : line.split(" "))
                    frenchSet.add(s);
            }
            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public HashMap<String, AdvancedTrie> getMagic() {
        return this.magic;
    }

    public TreeMap<String, Node> getWordFreqs() {
        return this.wordFreqHolder;
    }

    public void insert(String word) {

        // pass count and end count are used for predictoins later

        if (word == null || word.length() == 0)
            return; // Not a word, do nothing
        root.passCount++; // Note total amount # of words
        Node curr = root;
        for (char c : word.toCharArray()) {
            Node child = curr.children.get(c); // is there a node for char c? if so get it.
            if (child == null) {
                child = new Node(); // was no child so I make one
                curr.children.put(c, child); // put the new node into the map
            }
            child.passCount++;
            curr = child; // move down a level
        }
        curr.endCount++;

         printWordFrequenciesBackground(root, "", wordFreqHolder);
    }

    public boolean contains(String word) {
        // basically like insert but stop when the "search" stops
        if (word == null || word.length() == 0)
            return false;
        Node curr = root;
        for (char c : word.toCharArray()) {
            Node child = curr.children.get(c); // is there a node for char c? if so get it.
            if (child == null)
                return false;
            curr = child; // move down a level

        }
        return curr.isEndOfWord(); // how intutive

        // Returns true if and only if the exact word has been inserted at least once.
        // Returns false if word was never inserted or is invalid (null, empty).
    }

    public HashMap<String, Node> mostLikelyNextWordBackground(String prefix, Node curr, String currWord,
            HashMap<String, Node> holder) {
        if (prefix == null)
            return holder;
        if (curr.children == null)
            return holder;
        // if (currWord.length() == 0) currWord = prefix.toString(); // funny code

        for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
            Character character = entry.getKey();
            Node node = entry.getValue();

            if (node.isEndOfWord()) {
                holder.put(currWord + character, node);
            }

            // System.out.println("The children of " + character + " are " + node.children);

            if (node.children != null) {
                mostLikelyNextWordBackground(prefix, node, currWord + character, holder);
            } else {
                holder.put(currWord + character, node);
            }
        }
        return holder;
    }

    public TreeMap<String, Node> printWordFrequenciesBackground(Node curr, String currWord,
            TreeMap<String, Node> holder) {
        if (curr.children == null)
            return holder;
        // if (currWord.length() == 0) currWord = prefix.toString(); // funny code

        for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
            Character character = entry.getKey();
            Node node = entry.getValue();

            if (node.isEndOfWord()) {
                holder.put(currWord + character, node); // might be unnecessary
            }

            if (node.children != null) {
                printWordFrequenciesBackground(node, currWord + character, holder);
            } else {
                holder.put(currWord + character, node);
            }
        }
        return holder;
    }

    public String mostLikelyNextWord(String prefix) {

        Node curr = root;
        for (char c : prefix.toCharArray()) {
            Node child = curr.children.get(c); // is there a node for char c? if so get it.
            if (child == null)
                return "_";
            curr = child; // move down a level
        }

        HashMap<String, Node> temp = new HashMap<>();
        HashMap<String, Node> holder = mostLikelyNextWordBackground(prefix, curr, prefix, temp);

        int max_value = 0;
        String max_word = "_";
        for (Map.Entry<String, Node> entry : holder.entrySet()) {
            String word = entry.getKey();
            int nodeEnd = entry.getValue().endCount;
            if (nodeEnd > max_value) {
                max_word = word;
                max_value = nodeEnd;
            }
        }
        return max_word;
    }

    public void printWordFrequencies() {
        if (root == null)
            return;
        printWordFrequenciesBackground(root, "", wordFreqHolder);
        // System.out.println(wordFreqHolder);

        for (Map.Entry<String, Node> entry : wordFreqHolder.entrySet()) {
            String string = entry.getKey();
            Node node = entry.getValue();
            System.out.println(string + " : " + node.endCount);
        }
    }

    // method works
    public char mostLikelyNextChar(String prefix) {
        if (prefix == null)
            return '_';

        Node curr = root;
        for (char c : prefix.toCharArray()) {
            Node child = curr.children.get(c);
            if (child == null) // prefix not in trie
                return '_';
            curr = child;
        }

        if (curr.children == null || curr.children.isEmpty())
            return '_';

        char bestChar = '_';
        int bestCount = -1;

        for (Map.Entry<Character, Node> entry : curr.children.entrySet()) {
            char next = entry.getKey();
            Node node = entry.getValue();

            int count = node.passCount;

            if (count > bestCount || (count == bestCount && next < bestChar)) {
                bestCount = count;
                bestChar = next;
            }
        }

        return bestChar;

    }

    class Node {
        Map<Character, Node> children;
        int passCount;
        int endCount;

        Node() {
            children = new HashMap<Character, Node>();
            passCount = endCount = 0;
        }

        boolean isEndOfWord() {
            return endCount > 0;
        }

        @Override
        public String toString() {
            return "(pass=" + passCount + ", end=" + endCount + ")";
        }
    }

    // compile map with names and tries of each hsongs
    public void doMagic(String folderName) {
        File folder = new File("song_texts");
        File[] songs = folder.listFiles();
        for (File file : songs) {
            System.out.println(file.getName());
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "";
                AdvancedTrie temp = new AdvancedTrie();
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                    for (String s : line.split(" ")) {
                        temp.insert(s);
                    }
                }
                magic.put(file.getName(), temp);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // predicts song based off of word and using freq maps
    public String songPredictor(String predictedWord) {
        HashMap<String, AdvancedTrie> magic = getMagic();
        String currBest = "";
        int bestCount = 0;
        for (Map.Entry<String, AdvancedTrie> entry : magic.entrySet()) {
            TreeMap<String, Node> freqs = entry.getValue().getWordFreqs();
            if (freqs.containsKey(predictedWord)) {
                Node theNode = freqs.get(predictedWord);
                if (theNode.endCount > bestCount) {
                    currBest = entry.getKey();
                    bestCount = theNode.endCount;
                }
            }
        }
        return currBest;
    }

    public ArrayList<String> languagePredictor(String predictedWord) {
        ArrayList<String> output = new ArrayList<>();
        if (englishSet.contains(predictedWord))
            output.add("English");
        if (spanishSet.contains(predictedWord))
            output.add("Spanish");
        if (frenchSet.contains(predictedWord))
            output.add("French");

        return output;
    }

    public Clip playSong(String songName) {
        try {
            File file = new File("song_audios/" + songName);
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
            return clip;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public AdvancedTrie getBigTrie(String folderName) {
        AdvancedTrie output = new AdvancedTrie();
        File folder = new File("song_texts");
        File[] songs = folder.listFiles();
        for (File file : songs) {
            System.out.println(file.getName());
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line = "";
                while ((line = reader.readLine()) != null) {
                    for (String s : line.split(" ")) {
                        output.insert(s);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return output;
    }
}