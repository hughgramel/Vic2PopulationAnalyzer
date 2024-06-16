import java.util.*;

public class Province {

    private final int id;
    private String name, owner, controller;
    private int popSize;
    private Set<Population> pops;


    // could just not have size, and instead run through at the end?
    public Province(int id) {
        this.id = id;
        pops = new HashSet<>();
        popSize = 0;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Setter for name
    public void setName(String name) {
        this.name = name;
    }

    // Getter for owner
    public String getOwner() {
        return owner;
    }

    // Setter for owner
    public void setOwner(String owner) {
        this.owner = owner;
    }

    // Getter for controller
    public String getController() {
        return controller;
    }

    // Setter for controller
    public void setController(String controller) {
        this.controller = controller;
    }

    public int getPopSize() {
        return popSize;
    }

    public int getAccSize(Set<String> accSet) {
        int count = 0;
        for (Population population : pops) {
            if (accSet.contains(population.getCulture())) {
                // if set contains the culture, then add.
                count += population.getSize();
            }
        }
        return count;
    }


    public void addPop(Population pop) {
        pops.add(pop);
        popSize += pop.getSize();
    }

    public Set<Population> getPopSet() {
        return pops;
    }

}
