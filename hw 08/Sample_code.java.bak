import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import com.google.gson.Gson;

class OutputFormat {
    double[][] boxes;
    int[] dogs_to_remove;
    int overlap_threshold;

}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}

class Dog_catcher {
    public Dog_catcher(double[][] territories, int overlap_threshold) {
    }

    public int[] dogs_to_remove() {
        return new int[] {};
    }

    public static void main(String[] args) {

        // this is the sample case

        // double territories[][] = new double[][] {
        // { 0.02, 0.01, 0.1, 0.05 }, // => index = 0
        // { 0.04, 0.02, 0.1, 0.05 }, // => index = 1
        // { 0.06, 0.03, 0.1, 0.05 }, // => index = 2
        // { 0.08, 0.04, 0.1, 0.05 }, // => index = 3
        // { 0.24, 0.01, 0.1, 0.05 }, // => index = 4
        // { 0.20, 0.00, 0.1, 0.05 }, // => index = 5
        // { 0.28, 0.02, 0.1, 0.05 }, // => index = 6
        // { 0.32, 0.03, 0.1, 0.05 }, // => index = 7
        // { 0.36, 0.04, 0.1, 0.05 }, // => index = 8
        // };
        // int overlap_threshold = 3;
        // Dog_catcher sol = new Dog_catcher(
        // territories, overlap_threshold);
        // int[] result = sol.dogs_to_remove();
        // System.out.println(Arrays.toString(result));

        // ==== this is for testing with json file, uncomment the following to test :)
        // ====

        // switch to the filePath to the file path of the testcases.json
        // String filePath = "yourpath/testcase.json";

        // Gson gson = new Gson();
        // int num_ac = 0;

        // try {
        // TestCase[] testCases = gson.fromJson(new FileReader(filePath),
        // TestCase[].class);

        // for (int i = 0; i < testCases.length; ++i) {
        // int problem = 0;
        // for (OutputFormat data : testCases[i].data) {
        // int[] ans = data.dogs_to_remove;
        // Dog_catcher sol = new Dog_catcher(data.boxes, data.overlap_threshold);
        // int[] result = sol.dogs_to_remove();
        // boolean correctness = Arrays.equals(ans, result);
        // if (correctness) {
        // num_ac++;
        // } else {
        // System.out.println("=== Wrong answer detected! ===");
        // System.out.println("Case " + i + " Problem " + problem + " failed.");
        // System.out.println("Got: " + Arrays.toString(result));
        // System.out.println("Expected: " + Arrays.toString(ans));
        // }
        // problem++;
        // }
        // }
        // System.out.println("Score: " + num_ac + "/10");
        // } catch (FileNotFoundException e) {
        // e.printStackTrace();
        // }
    }
}
