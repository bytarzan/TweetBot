import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;


public class TweetParser {

  
    private static final String BADWORD_REGEX = ".*[\\W&&[^']].*";
    private static final String URL_REGEX = "\\bhttp\\S*";
    private static final String URL_REGEX_END_SPACE = "\\bhttp\\S*\\.\\s";
    private static final String URL_REGEX_END_STRING = "\\bhttp\\S*\\.$";

   
    private static final char[] PUNCS = new char[] { '.', '?', '!', ';' };

   
    public static char[] getPunctuation() {
        return PUNCS.clone();
    }

  
    static String replacePunctuation(String tweet) {
        for (char c : PUNCS) {
            tweet = tweet.replace(c, '.');
        }
        return tweet;
    }

   
    static List<String> tweetSplit(String tweet) {
        List<String> sentences = new LinkedList<String>();
        for (String sentence : replacePunctuation(tweet).split("\\.")) {
            sentence = sentence.trim();
            if (!sentence.equals("")) {
                sentences.add(sentence);
            }
        }
        return sentences;
    }

   
    static String extractColumn(String csvLine, int csvColumn) {
        if (csvLine == null || csvLine.isEmpty()) {
            return null; 
        }

        String[] str = csvLine.split(",");

        if (csvColumn >= str.length || csvColumn < 0) {
            return null;
        }
        return str[csvColumn]; 
    }

   
    static List<String> csvDataToTweets(BufferedReader br, int tweetColumn) {
        LinkedList<String> comments = new LinkedList<>();
        FileLineIterator iter = new FileLineIterator(br);
        while (iter.hasNext()) {
            String line = iter.next();
            if (!line.isEmpty() && line != null) {
                String colword = extractColumn(line, tweetColumn); 
                if (colword != null && !colword.isEmpty()) {
                    comments.add(colword);
                }
            }
        }
        return comments;
    }

    static String cleanWord(String word) {
        String cleaned = word.trim().toLowerCase();
        if (cleaned.matches(BADWORD_REGEX) || cleaned.isEmpty()) {
            return null;
        }
        return cleaned;
    }


    static List<String> parseAndCleanSentence(String sentence) {
        LinkedList<String> cleaned = new LinkedList<String>();
        String[] words = sentence.split(" ");
        for (int i = 0; i < words.length; i++) {
            if (cleanWord(words[i]) != null && !words[i].matches(BADWORD_REGEX)) {
                cleaned.add(cleanWord(words[i]));
            }
        }
        return cleaned; 
    }

 
    static String removeURLs(String s) {
        s = s.replaceAll(URL_REGEX_END_STRING, ".");
        s = s.replaceAll(URL_REGEX_END_SPACE, ". ");
        return s.replaceAll(URL_REGEX, "");
    }

    
    static List<List<String>> parseAndCleanTweet(String tweet) {
        String withoutUrls = removeURLs(tweet);
        List<String> sentences = tweetSplit(withoutUrls);
        LinkedList<List<String>> cleaned = new LinkedList<List<String>>();

        for (int i = 0; i < sentences.size(); i++) {
            List<String> sentence = parseAndCleanSentence(sentences.get(i));
            if (!sentence.isEmpty() && sentence != null) {
                cleaned.add(sentence);
            }
        }
        return cleaned;
    }

    
    public static List<List<String>> csvDataToTrainingData(
            BufferedReader br,
            int tweetColumn
    ) {
        List<String> finalTweets = csvDataToTweets(br, tweetColumn);
        List<List<String>> trainingData = new LinkedList<>();

        for (String s : finalTweets) {
            for (List<String> tweet : parseAndCleanTweet(s)) {
                if (!tweet.isEmpty() && tweet != null) {
                    trainingData.add(tweet);
                }
            }
        }
        return trainingData; // Complete this method
    }

}
