# TweetBot
In this project, I read tweets from CSV files into a program, build my own machine learning model, and create an AI that generates realistic tweets!

Here is the file breakdown: 

FileLineIterator.java - This class is a wrapper around the Java I/O class BufferedReader (see lecture slides here) that provides a simple way to work with file data line-by-line. By writing this class, you are creating a simple, nifty file reading utility for yourself.

TweetParser.java - This class reads in tweet data from a file, and then cleans, filters and formats the data to improve the quality of the training data. Its interface also makes it easy for you to add that cleaned data to the Markov Chain.

MarkovChain.java - You will implement a Markov Chain in this class. The MarkovChain will count all the times that certain words follow other words in the training data. In addition to storing that data, it implements Iterator, and does so to provide a convenient way to continuously pick successive random elements. This means a MarkovChain instance is an iterator, which you can call next() and hasNext() on to generate words for your new tweet!

TweetBot.java - This is the class where you put everything together. Here, you will use TweetParser to clean and parse tweet data, write logic for adding that data to an instance of MarkovChain, and then use the populated MarkovChain to generate tweets.

ProbabilityDistribution.java - This class is useful for counting occurrences of things. You will use it in building MarkovChain. It contains a Map<T, Integer>, where the keys are instances of objects of type T and the Integer represents the count for the number of times that object occurred. It also provides a convenient way to randomly pick one of its keys.

NumberGenerator.java - This is an interface for any class that can generate numbers. It is used in ProbabilityDistribution for picking keys.

RandomNumberGenerator.java - This class implements NumberGenerator and just generates numbers randomly.

ListNumberGenerator.java - This class implements NumberGenerator and also just generates numbers. Instead of being random, though, it takes a list of numbers as an input, so the user has control over what numbers it outputs. You don’t need to worry about using this class, but our fixDistribution methods use this to guarantee that our ProbabilityDistribution outputs what you want it to.

How do these files interact with one another?

You have tweets in a CSV file. Pass the file path of this CSV file to FileLineIterator.java to iterate through each line of the file.

Then TweetParser.java uses this FileLineIterator to extract the tweet from each line. TweetParser.java also cleans and formats each tweet and breaks each tweet down into sentences and then words. So after all the processing that TweetParser does, you end up with a list of sentences, where each sentence is a list of words.

Then give this list to an instance of MarkovChain, which adds these words to a dictionary as bigrams. The dictionary maps from a word, w, to a ProbabilityDistribution, which stores the number of times another word, m, comes after w in our tweets.

ProbabilityDistribution.java has a function pick(), which returns one of words, m, that follow word w. This function uses a NumberGenerator to choose which word to return. MarkovChain.java uses this pick() function to generate the words in our new tweet.

Finally, you have TweetBot.java, the class that puts all of this together. TweetBot has an instance of MarkovChain, which you train with the input reader of tweets. Since the instance of MarkovChain is an iterator, you can repeatedly call next() on the MarkovChain to keep generating words for our new tweet. Every time next() is called, the MakovChain returns a word that is most likely to follow the word that you generated before it.
