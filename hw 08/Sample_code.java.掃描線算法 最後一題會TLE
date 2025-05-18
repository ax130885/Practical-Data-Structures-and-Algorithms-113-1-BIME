import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import com.google.gson.Gson;
import java.util.TreeSet;
import java.util.NavigableSet;


class OutputFormat {
    double[][] boxes; // 對應題目中的 territoryList（所有狗的領土座標）
    int[] dogs_to_remove; // 對應預期應該被移除的狗索引（正確答案）
    int overlap_threshold; // 對應題目中的重疊閾值（觸發移除的臨界值）
}

class TestCase {
    int Case; // 測試案例的編號（如 case1, case2...）
    int score; // 這個測試案例的分數權重
    ArrayList<OutputFormat> data; // 包含多組測試數據的列表
}

class Dog_catcher {
    private double[][] territories; // 儲存輸入的領土列表（等同於 boxes）
    private int overlap_threshold; // 儲存輸入的重疊閾值
    private int[] overlapCounts; // 計算結果：每個領土的重疊次數陣列

    // 建構子：初始化並計算重疊
    public Dog_catcher(double[][] territories, int overlap_threshold) {
        this.territories = territories;
        this.overlap_threshold = overlap_threshold;
        this.overlapCounts = new int[territories.length];
        calculateOverlapsWithSweepLine();
    }

    // 掃描線算法核心實現
    private void calculateOverlapsWithSweepLine() {
        List<Event> events = new ArrayList<>();

        // 建立事件列表（x軸方向）
        for (int i = 0; i < territories.length; i++) {
            double[] rect = territories[i];
            events.add(new Event(rect[0], true, i)); // 矩形左邊界
            events.add(new Event(rect[0] + rect[2], false, i)); // 矩形右邊界
        }

        // 排序事件：先按位置，同位置時開始事件優先
        events.sort((a, b) -> {
            if (a.position != b.position) {
                return Double.compare(a.position, b.position);
            }
            // 確保開始事件在結束事件前處理
            return a.isStart ? -1 : 1;
        });

        Set<Integer> activeRects = new HashSet<>(); // 當前活動的矩形集合

        // 處理所有事件
        for (Event event : events) {
            if (event.isStart) {
                // 新矩形進入，與所有活動矩形檢查Y軸重疊
                for (int activeId : activeRects) {
                    if (checkYOverlap(territories[event.rectId], territories[activeId])) {
                        overlapCounts[event.rectId]++;
                        overlapCounts[activeId]++;
                    }
                }
                activeRects.add(event.rectId);
            } else {
                activeRects.remove(event.rectId);
            }
        }
    }

    // 檢查Y軸重疊條件
    private boolean checkYOverlap(double[] a, double[] b) {
        return a[1] <= b[1] + b[3] && b[1] <= a[1] + a[3];
    }

    // 事件類別（內部類）
    private static class Event {
        double position; // 事件位置（x座標）
        boolean isStart; // 是否為矩形開始事件
        int rectId; // 對應矩形ID

        public Event(double position, boolean isStart, int rectId) {
            this.position = position;
            this.isStart = isStart;
            this.rectId = rectId;
        }
    }

    // 返回需移除的狗索引
    public int[] dogs_to_remove() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < overlapCounts.length; i++) {
            if (overlapCounts[i] >= overlap_threshold) {
                result.add(i);
            }
        }
        return result.stream().mapToInt(i -> i).toArray();
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
        // System.out.println(Arrays.toString(result)); // [0, 1, 2, 3, 4, 6, 7]

        // ==== this is for testing with json file, uncomment the following to test :)
        // ==== switch to the filePath to the file path of the testcases.json

        String filePath = "testdatas\\testcases.json";

        Gson gson = new Gson();
        int num_ac = 0;

        try {
            TestCase[] testCases = gson.fromJson(new FileReader(filePath),
                    TestCase[].class);

            for (int i = 0; i < testCases.length; ++i) {
                int problem = 0;
                for (OutputFormat data : testCases[i].data) {
                    int[] ans = data.dogs_to_remove;
                    Dog_catcher sol = new Dog_catcher(data.boxes, data.overlap_threshold);
                    int[] result = sol.dogs_to_remove();
                    boolean correctness = Arrays.equals(ans, result);
                    if (correctness) {
                        num_ac++;
                    } else {
                        System.out.println("=== Wrong answer detected! ===");
                        System.out.println("Case " + i + " Problem " + problem + " failed.");
                        System.out.println("Got: " + Arrays.toString(result));
                        System.out.println("Expected: " + Arrays.toString(ans));
                    }
                    problem++;
                }
            }
            System.out.println("Score: " + num_ac + "/10");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
