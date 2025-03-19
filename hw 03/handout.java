
class DefenseSystem {

	public int[] result(int[] levels, int[] ranges) {
		// Given the traits of each member levels,nd output
		// the leftmost and rightmost index of member
		// can be attacked by each member.

		// complete the code by returning an int[]
		// flatten the results since we only need an 1-dimentional array.

		int[] result = new int[2 * levels.length];

		// 遍歷每個塔
		for (int i = 0; i < levels.length; i++) {

			// 初始化左右攻擊範圍 最短是自己
			result[2 * i] = i;
			result[2 * i + 1] = i;

			// 計算左右攻擊範圍
			int left = -1;
			int right = -1;

			left = (i - ranges[i] < 0) ? 0 : i - ranges[i];
			right = ((i + ranges[i]) > (levels.length - 1)) ? levels.length - 1 : i + ranges[i];

			// 往左檢查
			for (int j = i - 1; j >= left; j--) {

				if (levels[j] < levels[i]) {
					result[2 * i] = j;
				} else {
					break;
				}
			}

			// 往右檢查
			for (int j = i + 1; j <= right; j++) {

				if (levels[j] < levels[i]) {
					result[2 * i + 1] = j;
				} else {
					break;
				}

			}

		}

		return result;
	}
}