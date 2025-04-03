import java.util.*;
import java.io.FileReader;
import java.io.IOException;
// import org.json.simple.JSONArray;
// import org.json.simple.JSONObject;
// import org.json.simple.parser.JSONParser;
// import org.json.simple.parser.ParseException;

// class test {
//     public static void test(String filename) {
//         JSONParser parser = new JSONParser();
//         try (FileReader reader = new FileReader(filename)) {
//             JSONArray allCases = (JSONArray) parser.parse(reader);
//             for (Object obj : allCases) {
//                 JSONObject groupObj = (JSONObject) obj;
//                 int groupNum = ((Long) groupObj.get("case")).intValue();
//                 JSONArray dataArray = (JSONArray) groupObj.get("data");
//                 int groupPass = 0;
//                 System.out.println("Case " + groupNum + ":");
//                 int subCaseIndex = 1;
//                 for (Object dataObj : dataArray) {
//                     JSONObject testcase = (JSONObject) dataObj;
//                     JSONArray numsArr = (JSONArray) testcase.get("nums");
//                     int[] nums = new int[numsArr.size()];
//                     for (int i = 0; i < numsArr.size(); i++) {
//                         nums[i] = ((Long) numsArr.get(i)).intValue();
//                     }
//                     int expectedCount = ((Long) testcase.get("RebelsCount")).intValue();

//                     int actualCount = new Rebels().CountofRebels(nums);

//                     boolean countCorrect = (expectedCount == actualCount);

//                     System.out.println("  Subcase " + subCaseIndex + ": " + (countCorrect ? "AC" : "WA"));
//                     if (countCorrect) {
//                         groupPass++;
//                     }
//                     subCaseIndex++;
//                 }
//                 System.out.println("  Score: " + groupPass + " / " + dataArray.size());
//             }
//         } catch (IOException | ParseException e) {
//             e.printStackTrace();
//         }
//     }

//     public static void main(String[] args) {
//         test("test_cases_public.json");
//     }
// }

// 題目要求: 檢查左邊的元素>2*右邊元素的次數
// 解題想法: 相當於merge sort，只是在合併階段，額外增加了一個if，來統計滿足 nums[i] > 2 * nums[j]的次數
// 以[6,5,4,3,2,1]為例，merge sort的過程如下：
// 分治: [6,5,4,3,2,1] -> [6,5] [4,3,2,1] -> [6] [5] [4,3] [2,1] -> [6] [5] [4] [3] [2] [1]
// 合併: [6] [5] [4] [3] [2] [1] -> [5,6] [4] [2,3] [1] -> [4,5,6] [1,2,3] -> [1,2,3,4,5,6]
// 實現細節: 在 [2,3] [1] -> [1,2,3] 時，發現 (3,1) 滿足條件，count++。
//          在下一階段 [4,5,6] [1,2,3] -> [1,2,3,4,5,6] 時，不會拿同樣區域的元素來互相比較，所以不用擔心(3,1)又被重複計算的問題。
//          這個階段只會比較得到 (4,1) (5,1) (5,2) (6,1) (6,2)，最後共有6個pair。
//          並且因為是取出左元素，遍歷右元素，所以只要檢查到右元素不滿足條件，就可以停止這次遍歷，直接跳到下個左元素。
//          只要O(nlogn)時間複雜度。
class Rebels {
    public static int CountofRebels(int[] nums) {
        return mergeSortAndCount(nums, 0, nums.length - 1);
    }

    private static int mergeSortAndCount(int[] nums, int left, int right) {
        int count = 0;
        if (left < right) {
            int mid = left + (right - left) / 2;
            // 遞歸處理左半部分和右半部分，並累加各自的計數
            count += mergeSortAndCount(nums, left, mid);
            count += mergeSortAndCount(nums, mid + 1, right);
            // 合併並統計當前階段的計數
            count += mergeAndCount(nums, left, mid, right);
        }
        return count;
    }

    private static int mergeAndCount(int[] nums, int left, int mid, int right) {
        // 創建左右子數組的副本，範圍為 [left, mid] 和 [mid+1, right]
        int[] leftArr = Arrays.copyOfRange(nums, left, mid + 1);
        int[] rightArr = Arrays.copyOfRange(nums, mid + 1, right + 1);

        int count = 0; // 統計當前階段的Rebels數量
        int p = 0; // 右子數組的指針，用於統計Rebels對

        // 計算左子數組中的元素大於右子數組元素兩倍的對數
        for (int q = 0; q < leftArr.length; q++) {
            // 找到右子數組中第一個不滿足 leftArr[q] > 2 * rightArr[p] 的位置
            while (p < rightArr.length && leftArr[q] > 2 * rightArr[p]) {
                p++;
            }
            // 累加滿足條件的元素數量
            count += p;
        }

        // 合併兩個已排序的子數組到原數組中
        int i = 0, j = 0, k = left;
        while (i < leftArr.length && j < rightArr.length) {
            if (leftArr[i] <= rightArr[j]) {
                nums[k++] = leftArr[i++];
            } else {
                nums[k++] = rightArr[j++];
            }
        }

        // 處理剩餘元素
        while (i < leftArr.length) {
            nums[k++] = leftArr[i++];
        }
        while (j < rightArr.length) {
            nums[k++] = rightArr[j++];
        }

        return count;
    }

    public static void main(String[] args) {
        // Test case
        int[] nums = { 1, 3, 2, 3, 1 };
        int count = CountofRebels(nums);
        System.out.println("Rebels Count: " + count);
    }
}
