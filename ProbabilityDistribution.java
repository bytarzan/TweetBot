import java.util.*;
import java.util.Map.Entry;


class ProbabilityDistribution<T extends Comparable<T>> {

    private final Map<T, Integer> records;
    private Integer total = 0;

    public ProbabilityDistribution() {
        this.records = new HashMap<T, Integer>();
    }

  
    public int getTotal() {
        return total;
    }

   
    public Set<Entry<T, Integer>> getEntrySet() {
        
        return new HashSet<Entry<T, Integer>>(records.entrySet());
    }

   
    public Map<T, Integer> getRecords() {
        
        return new HashMap<T, Integer>(records);
    }

  
    public T pick(NumberGenerator generator) {
        return this.pick(generator.next(total));
    }

   
    public T pick(int index) {
        if (index >= total || index < 0) {
            throw new IllegalArgumentException(
                    "Index has to be less than or " +
                            "equal to the total " + "number of records in the PD"
            );
        }

        int currentIndex = 0;
        List<T> rs = new ArrayList<T>(total);
        rs.addAll(records.keySet());
        Collections.sort(rs, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1 == null && o2 == null ? 0
                        : o1 == null ? -1 : o2 == null ? 1 : o1.compareTo(o2);
            }
        });
        for (T key : rs) {
            int currentCount = records.get(key);
            if (currentIndex + currentCount > index) {
                return key;
            }
            currentIndex += currentCount;
        }
        throw new IllegalStateException(
                "Error in ProbabilityDistribution. Make " +
                        "sure to only add new " + "records through " + "record()"
        );
    }

    /**
     * Add an instance to the ProbabilityDistribution. If the element already
     * exists in the ProbabilityDistribution, it will increment the number of
     * occurrences of that element.
     *
     * @param t - an element to add to the distribution
     */
    public void record(T t) {
        records.put(t, records.getOrDefault(t, 0) + 1);
        total++;
    }

 
    public int count(T t) {
        Integer count = records.get(t);
        return count != null ? count.intValue() : 0;
    }

   
    public Set<T> keySet() {
        return records.keySet();
    }

   
    public int index(T element) {
        int currentIndex = 0;
        List<T> rs = new ArrayList<T>(total);
        rs.addAll(records.keySet());
        Collections.sort(rs, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return o1 == null && o2 == null ? 0
                        : o1 == null ? -1 : o2 == null ? 1 : o1.compareTo(o2);
            }
        });

        for (T key : rs) {
            int currentCount = records.get(key);
            if (Objects.equals(element, key)) {
                return currentIndex;
            }
            currentIndex += currentCount;
        }

        throw new IllegalArgumentException("element not in the distribution");
    }

    
    @Override
    public String toString() {
        String res = "";
        for (Entry<T, Integer> r : records.entrySet()) {
            res += ("Frequency of " + r.getKey() + ": " + r.getValue());
        }
        return res;
    }
}
