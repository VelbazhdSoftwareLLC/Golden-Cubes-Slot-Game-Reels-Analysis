import java.util.ArrayList;
import java.util.List;

public class Chromosome {
    double rtp = 0.96;

    double fitness = Double.MAX_VALUE;

    List<List<GoldenCubes.Symbol>> reels = new ArrayList<>();

    Chromosome() {
        for(int i=0; i<5; i++) {
            reels.add(new ArrayList<>());
        }
    }
}