import java.io.FileReader;
import java.io.File;

/**
 * This is my hw2 class file
 * that does natural language processing
 * @author Charlie Comeau
 * @version 23456789
*/

public class SourceModel {

    private String name;
    private double[][] counts = new double[26][26];

    /** This is the Source Model Constructor that
     * creates a Source Model object for natural
     * language processing.
     * @param name name of language
     * @param fileName name of corpus file
    */
    public SourceModel(String name, String fileName) throws Exception {
        char c1 = 'a';
        char c2 = 'a';
        int i1, i2, j1, j2;
        int sum = 0;
        this.name = name;
        System.out.printf("Training %s model ... ", getName());
        File f = new File(fileName);
        FileReader reader = new FileReader(f);

        i1 = Character.toLowerCase(reader.read());
        i2 = Character.toLowerCase(reader.read());
        c1 = (char) i1;
        c2 = (char) i2;
        j1 = i1;
        j2 = i2;
        int data = i1;

        while (data != -1) {
            c1 = (char) i1;
            c2 = (char) i2;
            j1 = i1;
            j2 = i2;

            while (data >= 0  && !Character.isAlphabetic(i2)) {
                i2 = Character.toLowerCase(reader.read());
                c2 = (char) i2;
                j2 = i2;
                data = i2;
            }

            if (Character.isAlphabetic(c1) && Character.isAlphabetic(c2)) {
                j1 -= 'a';
                j2 -= 'a';
                for (int row = 0; row < counts.length; row++) {
                    for (int col = 0; col < counts[row].length; col++) {
                        if (row == j1 && col == j2) {
                            counts[row][col]++;
                            sum++;
                        }
                    }
                }
            }

            i1 = i2;
            i2 = Character.toLowerCase(reader.read());
            data = i2;
        }

        int rowSum = 0;
        for (int row = 0; row < counts.length; row++) {
            for (int col = 0; col < counts[row].length; col++) {
                rowSum += counts[row][col];
            }
            for (int col = 0; col < counts[row].length; col++) {
                if (rowSum != 0) {
                    counts[row][col] /= rowSum;
                }
                if (counts[row][col] == 0.00) {
                    counts[row][col] = 0.01;
                }
            }
            rowSum = 0;
        }

        System.out.println("done");
        reader.close();
    }

    /** This is an accessor method for
     * the private instance variable name
     * @return private instance variable name
    */
    public String getName() {
        return name;
    }

    /** This is the toString method for
     * the SourceModel object that prints
     * the String representation of the
     * character matrix.
     * @return String representation of
     * character matrix of probabilities
    */
    public String toString() {
        String result = "Model: " + getName() + "\n ";
        char header = 'a';
        for (int i = 0; i < 26; i++) {
            result += String.format(" %4c", header);
            header++;
        }

        header = 'a';
        result += "\n";

        for (int row = 0; row < counts.length; row++) {
            result += String.format("%c ", header);

            for (int col = 0; col < counts[row].length; col++) {
                result += String.format("%2.2f ", counts[row][col]);
            }

            result += "\n";
            header++;
        }
        return result;
    }

    /** This is the probability method that
     * calculates the character bigram probabilities of
     * the inputted string for each corpus language
     * @param etest String to be tested
     * @return tested string's probability of being corpus
     * file's language
    */
    public  double probability(String etest) {
        double probability = 1.0;
        String test = etest;
        if (test.length() > 1) {
            int i1 = (int) Character.toLowerCase(test.charAt(0));
            int i2 = (int) Character.toLowerCase(test.charAt(1));
            boolean skip = false;
            int j1 = i1;
            int j2 = i2;
            for (int i = 2; i < test.length() + 1; i++) {
                i1 = j1;
                i2 = j2;

                while (!Character.isAlphabetic(i2) && i < test.length()) {
                    i2 = Character.toLowerCase(test.charAt(i));
                    j2 = i2;
                    i++;
                    skip = true;
                }

                if (Character.isAlphabetic(i1) && Character.isAlphabetic(i2)) {
                    i1 -= 'a';
                    i2 -= 'a';

                    for (int row = 0; row < counts.length; row++) {
                        for (int col = 0; col < counts[row].length; col++) {
                            if (i1 == row && i2 == col) {
                                probability *= counts[row][col];
                            }
                        }
                    }
                }

                j1 = j2;
                if (skip && i + 1 > test.length() && i < test.length()) {
                    j2 = (int) Character.toLowerCase(test.charAt(i + 1));
                } else if (i < test.length()) {
                    j2 = (int) Character.toLowerCase(test.charAt(i));
                }
                skip = false;
            }
        }
        return probability;
    }

    /** The main method of this SourceModel program
     * calculates the various probabilities of each inputted
     * string to the various languages of the corpus files
     * @param args inputs the corpus files and the tested String
     * that is given by the user
    */
    public static void main(String[] args) throws Exception {
        String fileName = args[0];
        String test = args[args.length - 1];
        SourceModel[] corps = new SourceModel[args.length - 1];
        double[] pValues = new double[args.length - 1];
        double pSum = 0.0;
        for (int i = 0; i < args.length - 1; i++) {
            String[] parts = args[i].split("\\.");
            corps[i] = new SourceModel(parts[0], args[i]);
            pValues[i] = corps[i].probability(test);
            pSum += pValues[i];
        }
        System.out.println();
        System.out.println("Analyzing: " + test);
        System.out.println();
        int count = 0;

        for (int i = 0; i < pValues.length; i++) {
            pValues[i] /= pSum;
        }

        for (SourceModel i: corps) {
            System.out.printf("Probability that test sting is %8s: %4.2f%n",
                i.getName(), pValues[count]);
            count++;
        }

        double largest = -1;
        int place = 0;
        for (int i = 0; i < pValues.length; i++) {
            if (largest < pValues[i]) {
                largest = pValues[i];
                place = i;
            }
        }
        System.out.printf("Test string is most likely %s."
            , corps[place].getName());
        System.out.println();
    }
}