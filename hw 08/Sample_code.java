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
import java.util.HashMap;

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

    public Dog_catcher(double[][] territories, int overlap_threshold) {
        this.territories = territories;
        this.overlap_threshold = overlap_threshold;
        this.overlapCounts = new int[territories.length];
        calculateOverlapsWithSweepLine(); // 使用掃描線算法計算重疊
    }

    /** 核心算法：掃描線算法實現（X軸掃描 + Y軸樹結構優化） */
    private void calculateOverlapsWithSweepLine() {
        List<Event> events = new ArrayList<>();

        // 建立事件列表（掃描X軸方向）
        for (int i = 0; i < territories.length; i++) {
            double[] rect = territories[i];
            events.add(new Event(rect[0], true, i)); // 矩形左邊界事件
            events.add(new Event(rect[0] + rect[2], false, i)); // 矩形右邊界事件
        }

        // 事件排序規則：先按X座標排序，相同X座標時「開始事件」優先處理
        events.sort((a, b) -> {
            if (a.position != b.position) {
                return Double.compare(a.position, b.position);
            }
            return a.isStart ? -1 : 1; // 確保開始事件先處理
        });

        /*
         * 使用 TreeSet 維護當前活動的矩形（按Y座標排序）
         * 1. 使用自定義的 RectInfo 儲存Y座標和高度
         * 2. 排序規則：先按Y下邊界排序，若Y相同則按rectId排序（避免重複）
         * 3. 配合 HashMap 實現快速刪除操作
         */
        TreeSet<RectInfo> activeRects = new TreeSet<>();
        HashMap<Integer, RectInfo> idToRectInfo = new HashMap<>();

        // 處理所有事件（掃描線從左到右移動）
        for (Event event : events) {
            if (event.isStart) {
                // 處理「矩形開始」事件 ==========================================
                int rectId = event.rectId;
                double[] rect = territories[rectId];
                double y = rect[1]; // 當前矩形的Y下邊界
                double h = rect[3]; // 當前矩形的高度
                RectInfo current = new RectInfo(y, h, rectId);

                // 將當前矩形加入活動集合
                activeRects.add(current);
                idToRectInfo.put(rectId, current); // 建立映射用於後續刪除

                /*
                 * 核心優化：利用 TreeSet 的排序特性快速查找可能重疊的矩形
                 * 1. 計算當前矩形的Y上邊界 (y + h)
                 * 2. 使用 headSet(upperBound) 獲取所有Y下邊界 <= 當前矩形Y上邊界的矩形
                 * 3. 遍歷這些候選矩形檢查Y軸是否真的重疊
                 */
                RectInfo upperBound = new RectInfo(y + h, 0, Integer.MAX_VALUE);
                NavigableSet<RectInfo> candidates = activeRects.headSet(upperBound, true);

                // 與候選矩形比較Y軸重疊
                for (RectInfo candidate : candidates) {
                    if (candidate.rectId == rectId)
                        continue; // 跳過自己
                    // 檢查候選矩形的Y上邊界 (candidate.y + candidate.h) 是否 >= 當前矩形的Y下邊界 (y)
                    if (candidate.y + candidate.h >= y) {
                        overlapCounts[rectId]++; // 當前矩形重疊次數+1
                        overlapCounts[candidate.rectId]++; // 被比較的矩形重疊次數+1
                    }
                }
            } else {
                // 處理「矩形結束」事件 ==========================================
                int rectId = event.rectId;
                RectInfo current = idToRectInfo.remove(rectId); // 從映射中取出
                if (current != null) {
                    activeRects.remove(current); // 從活動集合移除
                }
            }
        }
    }

    /** 自定義類別：用於在 TreeSet 中儲存矩形Y軸信息 */
    private static class RectInfo implements Comparable<RectInfo> {
        double y; // 矩形的Y下邊界
        double h; // 矩形的高度
        int rectId; // 矩形ID（用於區分Y座標相同的不同矩形）

        public RectInfo(double y, double h, int rectId) {
            this.y = y;
            this.h = h;
            this.rectId = rectId;
        }

        /** 定義排序規則：先按Y下邊界排序，Y相同時按rectId排序（確保唯一性） */
        @Override
        public int compareTo(RectInfo other) {
            if (this.y != other.y) {
                return Double.compare(this.y, other.y);
            }
            return Integer.compare(this.rectId, other.rectId);
        }
    }

    /** 事件類別（用於掃描線算法） */
    private static class Event {
        double position; // 事件位置（X座標）
        boolean isStart; // 是否為矩形開始事件
        int rectId; // 對應的矩形ID

        public Event(double position, boolean isStart, int rectId) {
            this.position = position;
            this.isStart = isStart;
            this.rectId = rectId;
        }
    }

    // 返回需移除的狗索引（與原始版本相同）
    public int[] dogs_to_remove() {
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < overlapCounts.length; i++) {
            if (overlapCounts[i] >= overlap_threshold) {
                result.add(i);
            }
        }
        return result.stream().mapToInt(i -> i).toArray();
    }

    // 測試主程式（與原始版本相同）
    public static void main(String[] args) {
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