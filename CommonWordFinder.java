import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Comparator;

/**
 * Class that finds and prints the most common words in a given text.
 * @author Amelie Sharples aes2367
 * @version 1.0.0 December 17, 2022
 */
public class CommonWordFinder<K , V> implements Comparator<Entry<String, Integer>> {
    private static int limit = 10; //Default limit to print unique words, changes if specified in args[2]

    /**
     * Main method, reads in arguments and calls methods to read input file, sort the map of unique words,
     * and print the result.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        if(args.length != 2 && args.length != 3) {
            System.err.print("Usage: java CommonWordFinder <filename> <bst|avl|hash> [limit]");
            System.exit(1);
        }

        FileInputStream stream = null;
        String mapArg = args[1];
        try {
            stream = new FileInputStream(args[0]);
        } catch (FileNotFoundException e) {
            System.err.print("Error: Cannot open file '" + args[0] + "' for input.");
            System.exit(1);
        }

        MyMap<String, Integer> map = null;
        if(mapArg.equals("bst")) {
            map = new BSTMap<>();
        } else if(mapArg.equals("avl")) {
            map = new AVLTreeMap<>();
        } else if(mapArg.equals("hash")) {
            map = new MyHashMap<>();
        } else {
            System.err.print("Error: Invalid data structure '" + args[1] + "' received.");
            System.exit(1);
        }

        if(args.length == 3) {
            try {
                limit = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                System.err.print("Error: Invalid limit '" + args[2] + "' received.");
                System.exit(1);
            }
            if(limit < 1) {
                System.err.print("Error: Invalid limit '" + args[2] + "' received.");
                System.exit(1);
            }
        }
        CommonWordFinder<String, Integer> map2 = new CommonWordFinder<>();
        map2.mappedWordsFromFile(stream, map, args[0]);
        Entry<String, Integer>[] arr = map2.sortedValuesFromMap(map);
        map2.printResult(arr);
    }

    /**
     * Reads in the words from the input file and puts them into the map.
     * @param stream
     * @param map
     * @return map with every unique word
     */
    private MyMap<String, Integer> mappedWordsFromFile(FileInputStream stream, MyMap<String, Integer> map, String args0) {
        //Used stackoverflow to learn about using BufferedReader to parse a file:
        //https://stackoverflow.com/questions/16104616/using-bufferedreader-to-read-text-file
        BufferedReader input = new BufferedReader(new InputStreamReader(stream));
        String word = "";
        String line = "";
        while(line != null) {
            try {
                 line = input.readLine();
            } catch (IOException e) {
                System.err.print("Error: An I/O error occurred reading '" + args0 + "'.");
                System.exit(1);
            }
            if(line != null) {
                String[] words = line.split("\\ ");
                for(int i = 0; i < words.length; i++) {
                    word = words[i];
                    word = word.toLowerCase();
                    if(isNumeric(word) || word.isEmpty() || word.equals("-")) {
                        continue;
                    }
                    word = removeIllegalCharacters(word);
                    if(word.equals("-")) {
                        continue;
                    }
                    if(word.isEmpty()) {
                        continue;
                    }
                    if (map.get(word) != null) {
                        int newCount = map.get(word) + 1;
                        map.put(word, newCount);
                    } else {
                        map.put(word, 1);
                    }
                }
            }
        }
        return map;
    }

    /**
     * Checks to see if a word is only made of numbers, returns true if so.
     * @param word
     * @return boolean
     */
    private boolean isNumeric(String word) {
        try {
            Double.parseDouble(word);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    /**
     * Checks to see whether a character is an illegal character, returns true if so.
     * @param i
     * @return boolean
     */
    private boolean isIllegalCharacter(char i) {
        String str = Character.toString(i);
        if(isNumeric(str)) {
            return true;
        }
        if(i == '#' || i == '@' || i == '!' || i == '?' || i == ',' || i == ']' ||
        i == '.' || i == ']' || i == '[' || i == ';' || i == '(' || i == ')' || i == ':' ||
        i == '"' || i == '{' || i == '}' || i == '$' || i == '&' || i == '^' || i == '*' ||
        i == '<' || i == '>') {
            return true;
        }
        return false;
    }

    /**
     * Removes illegal characters from a word, returning the word without illegal characters.
     * @param word
     * @return String
     */
    private String removeIllegalCharacters(String word) {
        while(word.length() > 1 && word.charAt(0) == '-') {
            word = word.substring(1);
            if(word.length() == 1 && word.equals("-")) {
                word = "";
            }
        }
        char[] wordChar = word.toCharArray();
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < wordChar.length; i++) {
            if(isIllegalCharacter(wordChar[i])) {
                continue;
            }
            sb.append(wordChar[i]);
        }
        word = sb.toString();
        return word;
    }

    /**
     * Sorts the map into an array of values paired with their count.
     * @param map
     * @return sorted Entry array
     */
    private Entry<String, Integer>[] sortedValuesFromMap(MyMap<String, Integer> map) {
        Iterator<Entry<String, Integer>> iter = map.iterator();
        Entry<String, Integer>[] arr = new Entry[map.size()];
        int index = 0;
        while(iter.hasNext()) {
            arr[index] = iter.next();
            index++;
        }
        //Used stackoverflow to learn about how Comparators work:
        //https://stackoverflow.com/questions/2839137/how-to-use-comparator-in-java-to-sort
        Comparator<Entry<String, Integer>> comparator = new Comparator<Entry<String, Integer>>() {
            @Override
            public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
                if(o1.value > o2.value) {
                    return 1;
                } else if(o1.value == o2.value) {
                    if(o2.key.compareTo(o1.key) > 0) {
                        return 1;
                    }
                }
                return -1;
            }
        };
        Arrays.sort(arr, comparator);
        return arr;
    }

    /**
     * Takes in the sorted array, and prints out the sorted map in the correct format.
     * @param arr
     */
    public void printResult(Entry<String, Integer>[] arr) {
        System.out.println("Total unique words: " + arr.length);
        int longestWord = 0;
        if(arr.length < limit) {
            limit = arr.length;
        }
        for(int i = arr.length-1; i >= arr.length-limit; i--) {
            if(arr[i].key.length() > longestWord) {
                longestWord = arr[i].key.length();
            }
        }
        int maxDigits = String.valueOf(limit).length();
        int numSpaces = 0;
        for(int i = 1; i < limit+1; i++) {
            numSpaces = maxDigits - String.valueOf(i).length();
            while(numSpaces > 0) {
                System.out.print(" ");
                numSpaces--;
            }
            System.out.print(i + ". " + arr[arr.length-i].key);
            numSpaces = longestWord - arr[arr.length-i].key.length() + 1;
            while(numSpaces > 0) {
                System.out.print(" ");
                numSpaces--;
            }
            System.out.println(arr[arr.length-i].value);
            System.lineSeparator();
        }
    }

    /**
     * Used to compare two different Entry objects.
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return int
     */
    @Override
    public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
        if(o1.value > o2.value) {
            return 1;
        } else if(o1.value == o2.value) {
            if(o1.key.compareTo(o2.key) > 0) {
                return 1;
            }
        }
        return -1;
    }
}
