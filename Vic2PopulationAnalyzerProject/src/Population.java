
public class Population {
    private String type, culture, religion;
    private int size;

    public Population(String type, String culture, String religion, int size) {
        this.type = type;
        this.culture = culture;
        this.religion = religion;
        this.size = size;
    }


    // Getter for type
    public String getType() {
        return type;
    }

    // Setter for type
    public void setType(String type) {
        this.type = type;
    }

    // Getter for culture
    public String getCulture() {
        return culture;
    }

    // Setter for culture
    public void setCulture(String culture) {
        this.culture = culture;
    }

    // Getter for religion
    public String getReligion() {
        return religion;
    }

    // Setter for religion
    public void setReligion(String religion) {
        this.religion = religion;
    }

    // Getter for size
    public int getSize() {
        return size;
    }
}
