import java.util.*;

public class State {
    private int popSize, accSize;
    private Set<Province> provs;

    public State() {
        popSize = 0;
        accSize = 0;
        provs = new HashSet<>();
    }

    public Set<Province> getProvSet() {
        return provs;
    }

    // Getter for popSize
    public int getPopSize() {
        return popSize;
    }

    public void addProv(Province province) {
        if (province != null) {
            provs.add(province);
            popSize += province.getPopSize();
        }
    }

    // Setter for accSize
    public int getAccSize(Set<String> accepted) {
        int count = 0;
        for (Province province: provs) {
            count += province.getAccSize(accepted);
        }
        return count;
    }

    
}
