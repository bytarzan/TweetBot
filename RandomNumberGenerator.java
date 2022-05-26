import java.util.Random;

public class RandomNumberGenerator implements NumberGenerator {

    Random r;

    
    public RandomNumberGenerator() {
        r = new Random();
    }

    
    public RandomNumberGenerator(long seed) {
        r = new Random(seed);
    }

   
    public int next(int bound) {
        return r.nextInt(bound);
    }

}
