import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import java.io.FileReader;

// return 解題組合數量 % 1_000_000_007
// 組合的順序不重要，例如先選 type1 再選 type2 和先選 type2 再選 type1 視為相同，不重複計數

// target: 目標分數
// types: [題目數量, 每題分數, 每題所需時間, 這幾題的難度]
// timeLimit: 時間限制
// L: 高難度題目最多能做幾題
// D: 難度分界（超過視為高難度）
class ScoreControl {

	// 解題想法: 題目數量有限，可以使用 DP 當中多種背包問題(每個種類(題型)不只一個)來解決
	// DP 表：dp[s][t][h] 表示達到分數 s，使用時間 t，選擇了 h 個高難度題目的方式數，矩陣內的元素為組合數量
	// 相當於多種背包問題，需要反向更新 DP (i--)，避免重複計算。
	// 選擇 k 個當前類型的題目，檢查是否滿足條件，計算最終結果
	public int waysToReachTarget(int target, int[][] types, int timeLimit, int L, int D) {
		final int MOD = 1_000_000_007;
		int[][][] dp = new int[target + 1][timeLimit + 1][L + 1]; // DP 表的大小記得加 1，因為0要當作初始狀態。
		dp[0][0][0] = 1; // 設定初始狀態

		// 遍歷每種可選的題目類型，每次取出一種題目類型
		for (int[] type : types) { // for (class a : b) 把b解包至class的格式，並且存為a，類似python的for
			int count = type[0]; // 這個類型的題目數量
			int mark = type[1]; // 這個類型的題目分數
			int time = type[2]; // 這個類型的題目時間
			int difficulty = type[3]; // 這個類型的題目難度
			boolean isHighDifficulty = difficulty >= D; // 這個類型是否為高難度題目

			// 取出題目要求(分數、題型、時限、難題數量限)以後，遍歷dp表，找到所有符合要求的子任務
			// 反向更新 DP 狀態，避免重複計算
			for (int s = target; s >= 0; s--) { // 累積得分
				for (int t = timeLimit; t >= 0; t--) { // 累積消耗時間
					for (int h = L; h >= 0; h--) { // 累積難題數量
						// dp[s][t][h]是已經計算過的子任務。
						if (dp[s][t][h] == 0)
							continue; // 第一次迴圈 只有[0][0][0]會被執行，相當於已經知道子任務的結果以後，才繼續添加新的選擇進dp。

						// 選擇 k 個當前類型的題目 (記得改回正向更新)
						// 假設題目類型是[2,2,1,3]，表示有2題，每題2分，每題1分鐘，難度3
						// 第一次迴圈相當於把[2][1][1]=[2][1][1](初始為0)+[0][0][0](初始為1)
						// 第二次迴圈相當於把[4][2][2]=[4][2][2](初始為0)+[0][0][0](初始為1)
						for (int k = 1; k <= count; k++) {
							int newScore = s + k * mark; // 新分數 = 當前分數 + k * 題目分數
							int newTime = t + k * time; // 新時間 = 當前時間 + k * 題目時間
							int newHigh = h + (isHighDifficulty ? k : 0); // 新高難度題目數量 = 當前高難度題目數量 + k（如果題目是高難度）

							// 如果分數還沒到，並且時間與難題數量沒超過，就繼續加題目，更新相同題型的dp表
							if (newScore <= target && newTime <= timeLimit && newHigh <= L) {
								// 更新 DP 表，可以直接取mod，不用等最後才取
								dp[newScore][newTime][newHigh] = (dp[newScore][newTime][newHigh] + dp[s][t][h]) % MOD;
							} else { // 如果分數、時間、難題數量超過限制
								break; // 超出限制，提前終止
							}
						}
					}
				}
			}
		}

		// 計算最終目標分數有幾種組合：固定分數，把時間與難題數量的所有組合加總。
		int result = 0;
		for (int t = 0; t <= timeLimit; t++) {
			for (int h = 0; h <= L; h++) {
				result = (result + dp[target][t][h]) % MOD;
			}
		}

		return result;
	}

	// No need to modify this block
	public static class TestCase {
		String name;
		int target;
		int[][] types;
		int timeLimit;
		int L;
		int D;
		int answer; // Expected answer for verification
	}

	// No need to modify this block
	public static class TestCaseGroup {
		@SerializedName("case")
		int caseNum;
		int score;
		TestCase[] data;
	}

	public static void main(String[] args) {
		ScoreControl ScoreControl = new ScoreControl();
		String testCasesFilePath = "test_cases.json"; // Put test_data.json to
		try {
			Gson gson = new Gson();
			try (FileReader fileReader = new FileReader(testCasesFilePath)) {
				TestCaseGroup[] testCaseGroups = gson.fromJson(fileReader, TestCaseGroup[].class);
				int totalScore = 0;

				for (TestCaseGroup group : testCaseGroups) {
					for (TestCase testCase : group.data) {
						int result = ScoreControl.waysToReachTarget(
								testCase.target,
								testCase.types,
								testCase.timeLimit,
								testCase.L,
								testCase.D);

						if (result == testCase.answer) {
							System.out.println("Test Case " + group.caseNum + ": AC");
							totalScore += 20;
						} else {
							System.out.println("Test Case " + group.caseNum + ": WA");
						}
					}
				}
				System.out.println("Total score : " + totalScore + "/100");
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}
}