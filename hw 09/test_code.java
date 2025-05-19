import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

class OutputFormat {
    Map<Integer, String> deviceTypes;
    List<int[]> links;
    int cablingCost;
    int serverToRouter;
    int mostPopularPrinter;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}

class test_InnovationStudioCabling {
    public static void main(String[] args) {
        Gson gson = new Gson();
        int num_ac = 0;
        try {
            // 直接指定測試檔案名稱
            TestCase[] testCases = gson.fromJson(
                    new FileReader("test_cases.json"), TestCase[].class);

            for (TestCase tc : testCases) {
                for (OutputFormat data : tc.data) {
                    InnovationStudioCabling LNC = new InnovationStudioCabling(
                            data.deviceTypes, data.links);
                    int user_cc = LNC.cablingCost();
                    int user_sr = LNC.serverToRouter();
                    int user_mpp = LNC.mostPopularPrinter();
                    if (user_cc == data.cablingCost
                            && user_sr == data.serverToRouter
                            && user_mpp == data.mostPopularPrinter) {
                        System.out.println("AC");
                        num_ac++;
                    } else {
                        System.out.println("WA");
                        System.out.println("Input deviceTypes: " + data.deviceTypes);
                        System.out.println("Input links: ");
                        for (int[] link : data.links) {
                            System.out.print(Arrays.toString(link));
                        }
                        System.out.println();
                        System.out.println("Ans cablingCost: " + data.cablingCost);
                        System.out.println("Your cablingCost: " + user_cc);
                        System.out.println("Ans serverToRouter: " + data.serverToRouter);
                        System.out.println("Your serverToRouter: " + user_sr);
                        System.out.println("Ans mostPopularPrinter: " + data.mostPopularPrinter);
                        System.out.println("Your mostPopularPrinter: " + user_mpp);
                        System.out.println();
                    }
                }
            }
            System.out.println("Score: " + num_ac + "/10");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
}