import java.util.*;

public class Country {
    private String countryTag;
    private boolean isHuman;
    private int populationSize, acceptedPopTotal;
    private Set<String> accList;
    private Set<State> states;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Country(String name) {
        this.countryTag = name;
        populationSize = 0;
        acceptedPopTotal = 0;
        accList = new HashSet();
        states = new HashSet();

    }

    public Country(String countryTag, boolean isHuman, 
            Set<String> accList, Set<State> states) {
        this.countryTag = countryTag;
        this.isHuman = isHuman;
        this.accList = accList;
        this.states = states;
    }

    public void setIsHuman() {
        this.isHuman = true;
    }

    /**
     * Returns a list of accepted pops to be used to determine whether to
     * add a pop group to a total or not
     * @return list of all accepted pops of a country
     */
    public Set<String> getAcceptedPopList() {
        return accList;
    }


    /**
     * Gets int for total of pop size
     * @return int for total of pop size
     */
    public int getPopSize() {
        if (states.size() == 0) {
            return 0;
        }
        int count = 0;
        for (State state : states) {
            count += state.getPopSize();
        }
        return count;
    }

    public void addPopSize(int num) {
        populationSize += num;
    }

    /**
     * Gets int for total of accepted pop
     * @return int for total of accepted pop
     */
    public int getAcceptedPopTotal() {
        int count = 0;
        for (State state : states) {
            count += state.getAccSize(accList);
        }
        return count;
    }
}
