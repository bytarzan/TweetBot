import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.BufferedReader;
import java.io.*;
import java.util.Iterator;


public class TweetBot {

    
    static final String PATH_TO_TWEETS = "files/dog_feelings_tweets.csv";
    static final int TWEET_COLUMN = 2;
    static final String PATH_TO_OUTPUT_TWEETS = "files/generated_tweets.txt";

    
    MarkovChain mc;
    NumberGenerator ng;

    public TweetBot(BufferedReader br, int tweetColumn) {
        this(br, tweetColumn, new RandomNumberGenerator());
    }

    public TweetBot(BufferedReader br, int tweetColumn, NumberGenerator ng) {
        mc = new MarkovChain(ng);
        this.ng = ng;
        List<List<String>> tweet = TweetParser.csvDataToTrainingData(br, tweetColumn);
        for (int i = 0; i < tweet.size(); i++) {
            Iterator<String> iter = tweet.get(i).iterator();
            mc.train(iter);
        } 
    }

   
    public void writeStringsToFile(
            List<String> stringsToWrite, String filePath,
            boolean append
    ) {
        File file = Paths.get(filePath).toFile();
        try {
            Writer a = new FileWriter(file, append);
            BufferedWriter b = new BufferedWriter(a);
            for (String s : stringsToWrite) {
                b.write(s);
                b.newLine();
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

   
    public void writeTweetsToFile(
            int numTweets, int numChars, String filePath,
            boolean append
    ) {
        writeStringsToFile(generateTweets(numTweets, numChars), filePath, append);
    }

   
    public String generateTweet(int numWords) {
        String str = "";
        if (numWords == 0) {
            return str;
        } else if (numWords < 0) {
            throw new IllegalArgumentException();
        }
        mc.reset();
        if (mc.chain.size() > 0) {
            for (int i = 0; i < numWords; i++) {
                if (mc.hasNext()) {
                    str = str + " " + mc.next();
                } else {
                    str = str + randomPunctuation();
                    mc.reset();
                    str = str + " " + mc.next();
                }
            }
        }
        str = str + randomPunctuation();
        return str.substring(1); 
    }

   
    public List<String> generateTweets(int numTweets, int numChars) {
        List<String> tweets = new ArrayList<String>();
        while (numTweets > 0) {
            tweets.add(generateTweetChars(numChars));
            numTweets--;
        }
        return tweets;
    }

   
    public String generateTweetChars(int numChars) {
        if (numChars < 0) {
            throw new IllegalArgumentException(
                    "tweet length cannot be negative"
            );
        }

        String tweet = "";
        int numWords = 1;
        while (true) {
            String newTweet = generateTweet(numWords);
            if (newTweet.length() > numChars) {
                return tweet;
            }
            tweet = newTweet;
            numWords++;
        }
    }

    public String randomPunctuation() {
        char[] puncs = { ';', '?', '!' };
        int m = ng.next(10);
        if (m < puncs.length) {
            return String.valueOf(puncs[m]);
        }
        return ".";
    }

  
    public int fixPunctuation(char punc) {
        switch (punc) {
            case ';':
                return 0;
            case '?':
                return 1;
            case '!':
                return 2;
            default:
                return 3;
        }
    }

   
    public boolean isPunctuation(String s) {
        return s.equals(";") || s.equals("?") || s.equals("!") || s.equals(".");
    }

 
    public static boolean isPunctuated(String s) {
        if (s == null || s.equals("")) {
            return false;
        }
        char[] puncs = TweetParser.getPunctuation();
        for (char c : puncs) {
            if (s.charAt(s.length() - 1) == c) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        BufferedReader br = FileLineIterator.fileToReader(PATH_TO_TWEETS);
        TwitterBot t = new TwitterBot(br, TWEET_COLUMN);
        List<String> tweets = t.generateTweets(10, 280); // 280 chars in a tweet
        for (String tweet : tweets) {
            System.out.println(tweet);
        }

     
    }

   
    public void fixDistribution(List<String> tweet) {
        List<String> puncs = java.util.Arrays.asList(".", "?", "!", ";");

        if (tweet == null) {
            throw new IllegalArgumentException(
                    "fixDistribution(): tweet argument must not be null."
            );
        } else if (tweet.size() == 0) {
            throw new IllegalArgumentException(
                    "fixDistribution(): tweet argument must not be empty."
            );
        } else if (!puncs.contains(tweet.get(tweet.size() - 1))) {
            throw new IllegalArgumentException(
                    "fixDistribution(): Passed in tweet must be punctuated."
            );
        }

        mc.fixDistribution(
                tweet.stream().map(x -> puncs.contains(x) ? null : x)
                        .collect(java.util.stream.Collectors.toList()),
                true
        );
        List<Integer> puncIndices = new LinkedList<>();
        for (int i = 0; i < tweet.size(); i++) {
            String curWord = tweet.get(i);

            if (isPunctuation(curWord)) {
                puncIndices.add(fixPunctuation(curWord.charAt(0)));
            }
        }
        ng = new ListNumberGenerator(puncIndices);
    }
}
