import java.util.*;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class test {
    public static void test(String filename) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(filename)) {
            JSONArray allCases = (JSONArray) parser.parse(reader);
            for (Object obj : allCases) {
                JSONObject groupObj = (JSONObject) obj;
                int groupNum = ((Long) groupObj.get("case")).intValue();
                JSONArray dataArray = (JSONArray) groupObj.get("data");
                int groupPass = 0;
                System.out.println("Case " + groupNum + ":");
                int subCaseIndex = 1;
                for (Object dataObj : dataArray) {
                    JSONObject testcase = (JSONObject) dataObj;
                    JSONArray numsArr = (JSONArray) testcase.get("nums");
                    int[] nums = new int[numsArr.size()];
                    for (int i = 0; i < numsArr.size(); i++) {
                        nums[i] = ((Long) numsArr.get(i)).intValue();
                    }
                    int expectedCount = ((Long) testcase.get("RebelsCount")).intValue();

                    int actualCount = new Rebels().CountofRebels(nums);

                    boolean countCorrect = (expectedCount == actualCount);

                    System.out.println("  Subcase " + subCaseIndex + ": " + (countCorrect ? "AC" : "WA"));
                    if (countCorrect) {
                        groupPass++;
                    }
                    subCaseIndex++;
                }
                System.out.println("  Score: " + groupPass + " / " + dataArray.size());
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test("test_cases_public.json");
    }
}

class Rebels {
    public static int CountofRebels(int[] nums) {
        // Write your code here.
        // Count the number of Rebels.
        return 0;// return int
    }

    public static void main(String[] args) {
        // Test case
        int[] nums = { 1, 3, 2, 3, 1 };
        int count = CountofRebels(nums);
        System.out.println("Rebels Count: " + count);
    }
}
