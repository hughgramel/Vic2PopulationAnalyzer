package Vic2PopulationAnalyzerProject.src;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * This class represents a Victoria 2 save that contains data regarding it's
 * countries, states, and provinces, and their corresponding population counts
 * and types, gotten using a provided Save text file.
 */

 public class SaveGame implements Comparable<SaveGame> {
    private final File save;
    private int year;
    private int month;
    private int day;
    private final Map<String, Country> countryMap = new TreeMap<>();

    private int bracketCount = 0;
    private boolean isProcessingProvince;
    private boolean dateSet = false;
    public Set<String> humanSet = new TreeSet<>();




    /**
     * Initializes the object using the provided save game file. File must be a
     * text file that adheres to a standard Victoria 2 save game format, gotten
     * by saving the game.
     * @param file Save game .txt / .v2 file used for parsing
     */
    public SaveGame(File file) {
        this.file = file;
    }

    public void scanFile() throws IOException {
        BufferedReader scanner = new BufferedReader(new InputStreamReader(Files.newInputStream(save.toPath())));
        String line;
        String currentOwner = "";
        boolean gettingAccepted = false;
        int lastSizeRecorded = 0;
        while ((line = scanner.readLine()) != null) {
            // doesn't remove tab this time
            if (!dateSet) {
                date = extractName(line, 6, true);
                setDateArray();
                dateSet = true;
            }

        }
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
    private Strings extractName(String line, int index, boolean removeLast) {
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
    private void setDates() {
        String[] stringDateArray = date.split("\\.");
        this.year = Integer.parseInt(stringDateArray[0]);
        this.month = Integer.parseInt(stringDateArray[1]);
        this.day = Integer.parseInt(stringDateArray[2]);
    }

    /**
     * Compares two SaveGame objects by chronological order in terms of the
     * year, month, and day of the save
     * @param o - Other SaveGame object for it to be compared to
     */
    public int compareTo(SaveGame o) {
        // First compare by year
        if (this.year != o.year) {
            return this.year - o.year;
        }
        // If years are equal, compare by month
        if (this.month != o.month) {
            return this.month - o.month;
        }
        // If months are also equal, compare by day
        return this.day - o.day;
    }
 }