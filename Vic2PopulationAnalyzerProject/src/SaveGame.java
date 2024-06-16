import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class represents a Victoria 2 save that contains data regarding it's
 * countries, states, and provinces, and their corresponding population counts
 * and types, gotten using a currProvIDed Save text file.
 */

 public class SaveGame {
    private final File save;
    private int year;
    private int month;
    private int day;
    private Map<Integer, Province> provMap = new TreeMap<>();
    private boolean isProcessingProvince;
    private boolean dateSet = false;
    private Map<String, Country> countryMap = new TreeMap<>();
    private Set<String> humanSet = new HashSet();

    /**
     * Initializes the object using the currProvIDed save game file. File must be a
     * text file that adheres to a standard Victoria 2 save game format, gotten
     * by saving the game.
     * @param file Save game .txt / .v2 file used for parsing
     */
    public SaveGame(File save) {
        this.save = save;
    }

    @SuppressWarnings("unchecked")
    public void scanFile() throws IOException {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(Files.newInputStream(save.toPath())));
        String line, currType, currCulture, currReligion, currProvName, 
                currProvController, currProvOwner, currCountry;
        String[] splitLine;
        String[] splitLineTwo;
        boolean inPopType, inProvince, inCountry, currCountryHasPops, 
                currIsHuman, isGettingCulture, gettingStateProvinces;
        int id, lastSizeRecorded, bracketCount, currProvID, count, currSize;
        double taxBase;
        Set<String> currAcceptedSet;
        Set<State> statesSet;
        Set<Province> provSet;
        currAcceptedSet  = new HashSet<>();
        statesSet = new HashSet<>();
        provSet = new HashSet<>();
        
        splitLine = new String[2];

        inPopType = false;
        inCountry = false;
        inProvince = false;
        isGettingCulture = false;
        currIsHuman = false;
        gettingStateProvinces = false;

        currCulture = "";
        currReligion = "";
        currSize = 0;
        currType = "";

        currProvName = "";
        currProvController = "";
        currProvOwner = "";
        currProvID = 0;

        currCountry = "";
        line = "";
        count = 500;
        bracketCount = 0;
        taxBase = 0;

        // keeps track of brackets
        

        Long currTime = System.currentTimeMillis();
        while ((line = scanner.readLine()) != null) {
            if (line.contains("{")) {
                bracketCount++;
            }
            // if there's an ending bracket, reduce bracket count
            if (line.contains("}")) {
                bracketCount--;
            }

            if (inProvince) {
                // pre: We have a province id, and an object associated with
                // that province id
                if (line.contains("name=\"")) {
                    // sets the name of the province
                    currProvName = extractName(line.trim(), 6, true);
                }

                if (line.contains("owner=\"")) {
                    // sets the name of the province
                    currProvOwner = extractName(line.trim(), 7, true);
                }

                if (line.contains("controller")) {
                    // sets the name of the province
                    currProvController = extractName(line.trim(), 12, true);
                }              
                
                if (line.matches("^\t([a-z]+)=$") && !line.contains("rgo")) {
                    inPopType = true;
                    currType = line.trim().substring(0, line.length() - 2);
                    
                } else if (bracketCount == 1 && inPopType) {
                    // here currID is always valid
                    inPopType = false;
                    //  now, if we have a valid province, create it, but only if
                    //  we got a valid name
                    if (!currProvOwner.equals("")) {
                        if (provMap.get(currProvID) == null) {
                            provMap.put(currProvID, new Province(currProvID));
                            provMap.get(currProvID).setController(currProvController);
                            provMap.get(currProvID).setOwner(currProvOwner);
                            provMap.get(currProvID).setName(currProvName);
                        }
                        provMap.get(currProvID).addPop(new Population(currType, currCulture, currReligion, currSize));
                        currCulture = "";
                        currReligion = "";
                        currSize = 0;
                        currType = "";
                    }
                }
                // now currPop type is functioning
                if (inPopType) {
                    if (line.contains("size=")) {
                        // this sets the current pop size of the popType to the
                        // value on size.
                        currSize = Integer.parseInt(extractName(line.trim(), 5, false));
                    }

                    if (line.matches("^\t\t([a-z_]+)=([a-z_]+)$")) {
                        line = line.trim();
                        splitLine = line.split("=");
                        // now it's anglo[0], protestant[1];
                        currCulture = splitLine[0];
                        currReligion = splitLine[1];
                    }
                }
            }

            if (!dateSet && line.startsWith("date=\"")) {
                setDates((extractName(line, 6, true)));
                dateSet = true;
            }
            
            if (line.matches("^\\d+=$")) {
                // this means that every time we have 
                inProvince = true;
                id = Integer.parseInt(line.substring(0, line.length() - 1));
                currProvID = id;
                // now if bracketCount goes down to 0, we want to reset stuff
                // and change curr prov
            } else if (bracketCount == 0 && inProvince) {
                inProvince = false;
                currProvController = "";
                currProvName = "";
                currProvOwner = "";
                currProvID = 0;
                // this means that if it doesn't match the currProvIDed regular
                // expression, and the bracket count is 0, we are NOT in a province
            }

            // now we test for accepted
            if (line.matches("^[A-Z]{3}=$")) {
                // this means that there's a country to scan
                currCountry = line.substring(0, 3);
                inCountry = true;
            } else if (bracketCount == 0 && inCountry) {
                // now we want to check if tax_base is not 0, and if so, do
                // everything
                Country country = new Country(currCountry, currIsHuman, currAcceptedSet, statesSet);
                if (statesSet.size() != 0 && country.getPopSize() != 0) {
                    countryMap.put(currCountry, country);
                    if (currIsHuman) {
                        humanSet.add(currCountry);
                    }
                    // System.out.println("CurrCountry: " + currCountry + ", isHuman: " + currIsHuman + ", accepted: " + currAcceptedSet + ", size: " + country.getPopSize());
                }
                // before we do all this, create country and add it to country map
                inCountry = false;
                currCountry = "";
                currCountryHasPops = false;
                currIsHuman = false;
                isGettingCulture = false;
                currAcceptedSet = new HashSet<>();
                statesSet = new HashSet<>(); // all states here must be added
                
                // provSet is fine because added to state each time
                // gettingStateProvinces is fine because dealt with
                // currAcceptedSet is fine because is created every time

                // don't need to do anything for set because it resets every time
                isGettingCulture = false;
            }

            if (inCountry) {
                // this means we know currCountry
                if (line.contains("human=")) {
                    currIsHuman = true;
                }
                if (line.contains("tax_base=")) {
                    taxBase = Double.parseDouble(extractName(line.trim(), 9, false));
                    currCountryHasPops = taxBase > 0;
                }

                if (line.contains("primary_culture=")) {
                    currAcceptedSet = new HashSet<>();
                    currAcceptedSet.add(extractName(line.trim(), 17, true));
                }

                if (line.trim().startsWith("culture=")) {
                    isGettingCulture = true;
                } else if (bracketCount == 1 && isGettingCulture) {
                    isGettingCulture = false;
                    System.out.println(currCountry + ": " + currAcceptedSet);
                    // now not getting culture, but continue in inCountry
                }
                
                if (isGettingCulture) {
                    if (line.startsWith("\"")){
                        String culture = extractName(line, 1, true);
                        currAcceptedSet.add(culture);
                        // adds cultures if getting culture
                    }
                }
                // can do anything else in country
                // now wait for states
                if (line.matches("^\t\tprovinces=$")) {
                    gettingStateProvinces = true;
                    // System.out.println("gettinstate brack " + bracketCount);
                } else if (bracketCount == 2 && gettingStateProvinces) {
                    // then stop getting bracket count. 
                    // here it also contains all provIDs
                    line = line.substring(0, line.length() - 2).trim();
                    State state = new State();
                    splitLineTwo = line.split(" ");
                    for (int i = 0; i < splitLineTwo.length; i++) {
                        // for each number in the array
                        // here put a state.add(province) method
                        state.addProv(provMap.get(Integer.parseInt(splitLineTwo[i])));
                        // adds the province associated with the given province
                    }
                    // now this state is done. Add to set of all state
                    statesSet.add(state);
                    //states set. add state
                    gettingStateProvinces = false;
                }

            }

            // now in province functions correctly, so if in province, we can do
            // stuff!

            if (line.matches("^[A-Z][0-9][0-9]=$")) {
                break;
            }
        }
        System.out.println("Time for whole scan: " + (System.currentTimeMillis() - currTime));
        scanner.close();
    }

    /**
     * Extracts text data depending on which word to be removed, the index of
     * where is to be removed, and if the last value is to be removed
     * @param line - represents the line the inputStreamReader is on, the one to
     * be removed
     * @param index - index of how many characters to remove
     * @param removeLast - set to true if wanting to remove last value in a char
     * @return the extracted string
     */
    private String extractName(String line, int index, boolean removeLast) {
        StringBuilder sb = new StringBuilder(line);
        sb.delete(0, index);
        if (removeLast) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Sets date array based on the date separated by "."
     */
    private void setDates(String date) {
        String[] stringDateArray = date.split("\\.");
        this.year = Integer.parseInt(stringDateArray[0]);
        this.month = Integer.parseInt(stringDateArray[1]);
        this.day = Integer.parseInt(stringDateArray[2]);
    }

    /**
     * Keeps track of the global bracket count, updating fields in the class
     * @param line - Line currently being parsed
     */
    public void countBrackets(String line, int bracketCount) {
        if (line.contains("{")) {
            bracketCount++;
        }
        if (line.contains("}")) {
            bracketCount--;
        }
    }

     // Getter for countryMap
    public Map<String, Country> getCountryMap() {
        return countryMap;
    }

    public Set<String> getHumanSet() {
        return humanSet;
    }

    // Getter for year
    public int getYear() {
        return year;
    }

    // Getter for month
    public int getMonth() {
        return month;
    }

    // Getter for day
    public int getDay() {
        return day;
    }

    public String getDate() {
        return year + "." + month + "." + day;
    }
 }