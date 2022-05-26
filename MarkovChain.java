import java.util.*;

public class MarkovChain implements Iterator<String> {
    private NumberGenerator ng;
    final Map<String, ProbabilityDistribution<String>> chain;
    final ProbabilityDistribution<String> startWords;
    private String presentWord;
   
    public MarkovChain() {
        this(new RandomNumberGenerator());
    }

    
    public MarkovChain(NumberGenerator ng) {
        if (ng == null) {
            throw new IllegalArgumentException(
                    "NumberGenerator input cannot " +
                            "be null"
            );
        }
        this.chain = new TreeMap<String, ProbabilityDistribution<String>>();
        this.ng = ng;
        this.startWords = new ProbabilityDistribution<String>();
        reset();
    }

    
    void addBigram(String first, String second) {
        if (first == null) {
            throw new IllegalArgumentException();
        }
        if (chain.containsKey(first)) {
            ProbabilityDistribution<String> a = chain.get(first);
            a.record(second);
        } else {
            ProbabilityDistribution<String> b = new ProbabilityDistribution<String>();
            b.record(second);
            chain.put(first, b);
        }

    }

    
    public void train(Iterator<String> sentence) {
        if (sentence == null) {
            throw new IllegalArgumentException();
        } else if (sentence.hasNext()) {
            String current = sentence.next();
            if (!current.isEmpty()) {
                startWords.record(current);
            }
            while (sentence.hasNext()) {
                String next = sentence.next();
                if (!next.isEmpty() && !current.isEmpty()) {
                    addBigram(current, next);
                    current = next;
                }
            }
            if (!current.isEmpty()) {
                addBigram(current, null);
            }
        }
    }

  
    ProbabilityDistribution<String> get(String token) {
        return chain.get(token);
    }

   
    public void reset(String start) {
        presentWord = start;
    }

    /**
     * DO NOT EDIT THIS METHOD. WE COMPLETED IT FOR YOU.
     * <p>
     * Sets up the Iterator functionality with a random start word such that the
     * MarkovChain will now move along a walk beginning with that start word.
     * <p>
     * The first call to next() after calling reset() will return the random
     * start word selected by this call to reset().
     */
    public void reset() {
        if (startWords.getTotal() == 0) {
            reset(null);
        } else {
            reset(startWords.pick(ng));
        }
    }

   
    @Override
    public boolean hasNext() {
        return (presentWord != null && presentWord != "");
    }

  
    @Override
    public String next() {
        String nextWord = presentWord;
        if (presentWord == "" || presentWord == null) {
            throw new NoSuchElementException();
        }
        if (chain.get(presentWord) != null) {
            String next = chain.get(presentWord).pick(ng);
            presentWord = next;
        } else {
            presentWord = null;
        }
        return nextWord;
    }

    
    public void fixDistribution(List<String> words) {
        fixDistribution(words, false);
    }

    
    public void fixDistribution(List<String> words, boolean pickFirst) {
        if (words.size() < 1) {
            throw new IllegalArgumentException(
                    "must have words in order to " +
                            "fix distribution"
            );
        }

        String curWord = words.remove(0);
        if (startWords.count(curWord) < 1) {
            throw new IllegalArgumentException(
                    "first word " + curWord + " " +
                            "not present in " + "startWords"
            );
        }

        List<Integer> probabilityNumbers = new LinkedList<>();
        if (pickFirst) {
            probabilityNumbers.add(startWords.index(curWord));
        }

        while (words.size() > 0) {
            ProbabilityDistribution<String> curDistribution;
            // if we were just at null, reset. otherwise, continue on the chain
            if (curWord == null) {
                curDistribution = startWords;
            } else {
                curDistribution = chain.get(curWord);
            }

            String nextWord = words.remove(0);
            if (nextWord != null) {
                if (curDistribution.count(nextWord) < 1) {
                    throw new IllegalArgumentException(
                            "word " + nextWord +
                                    " not found as a child of" + " word " + curWord
                    );
                }
                probabilityNumbers.add(curDistribution.index(nextWord));
            } else {
                probabilityNumbers.add(0);
            }
            curWord = nextWord;
        }

        ng = new ListNumberGenerator(probabilityNumbers);
    }

   
    @Override
    public String toString() {
        String res = "";
        for (Map.Entry<String, ProbabilityDistribution<String>> c : chain.entrySet()) {
            res += (c.getKey() + ": " + c.getValue().toString() + "\n");
        }
        return res;
    }
}
