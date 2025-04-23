class QuickSort {

	// ================== 標準 QuickSort (Hoare Partition) ==================
	public static void quickSort(int[] a, int low, int high) {
		if (low < high) {
			int pivotIndex = partition(a, low, high);
			quickSort(a, low, pivotIndex - 1);
			quickSort(a, pivotIndex + 1, high);
		}
	}

	// Hoare Partition
	private static int partition(int[] a, int left, int right) {
		// 3 median 的版本 只要多這兩行 其他都不用改
		int mid = medianThree(a, left, (left + right) / 2, right);
		swap(a, left, mid);

		int pivot = a[left];
		int i = left, j = right;
		while (i < j) {
			while (i < j && a[j] >= pivot)
				j--;
			while (i < j && a[i] <= pivot)
				i++;
			swap(a, i, j);
		}
		swap(a, i, left);
		return i;
	}

	// ================== 3-Way Partitioning QuickSort ==================
	public static void quickSort3Way(int[] a, int low, int high) {
		if (low >= high)
			return;

		// 使用 3-way partitioning
		int[] bounds = partition3Way(a, low, high);
		int lt = bounds[0]; // 小於 pivot 的區間結束位置
		int gt = bounds[1]; // 大於 pivot 的區間開始位置

		// 遞迴排序小於 pivot 和大於 pivot 的部分
		quickSort3Way(a, low, lt - 1);
		quickSort3Way(a, gt + 1, high);
	}

	// 3-Way Partitioning (Dutch National Flag Algorithm)
	private static int[] partition3Way(int[] a, int low, int high) {
		// 使用三數中值法選 pivot
		int mid = medianThree(a, low, (low + high) / 2, high);
		swap(a, low, mid);

		int pivot = a[low];
		int lt = low; // lt: 小於 pivot 的最後一個位置
		int gt = high; // gt: 大於 pivot 的第一個位置
		int i = low + 1; // i: 當前遍歷的位置

		while (i <= gt) {
			if (a[i] < pivot) {
				swap(a, i, lt);
				lt++;
				i++;
			} else if (a[i] > pivot) {
				swap(a, i, gt);
				gt--;
				// 這裡不移動 i，因為交換過來的元素尚未檢查
			} else {
				i++; // 等於 pivot，直接跳過
			}
		}

		return new int[] { lt, gt }; // 返回等於 pivot 的區間邊界
	}

	// ================== 共用方法 ==================
	private static int medianThree(int[] a, int left, int mid, int right) {
		int l = a[left], m = a[mid], r = a[right];
		if ((l <= m && m <= r) || (r <= m && m <= l))
			return mid;
		if ((m <= l && l <= r) || (r <= l && l <= m))
			return left;
		return right;
	}

	public static void knuthShuffle(int[] a) {
		int n = a.length;
		for (int i = n - 1; i > 0; i--) {
			int j = (int) (Math.random() * (i + 1));
			swap(a, i, j);
		}
	}

	private static void swap(int[] a, int i, int j) {
		if (a[i] != a[j]) {
			a[i] = a[i] ^ a[j];
			a[j] = a[i] ^ a[j];
			a[i] = a[i] ^ a[j];
		}
	}

	// ================== 測試 ==================
	public static void main(String[] args) {
		int[] a = { 3, 6, 8, 10, 1, 2, 1, 1, 5, 5, 5, 5, 8 };

		System.out.println("Original Array:");
		for (int i : a)
			System.out.print(i + " ");
		System.out.println();

		knuthShuffle(a);

		System.out.println("After Knuth Shuffle:");
		for (int i : a)
			System.out.print(i + " ");
		System.out.println();

		// 測試標準 QuickSort
		int[] a1 = a.clone();
		quickSort(a1, 0, a1.length - 1);
		System.out.println("Standard QuickSort:");
		for (int i : a1)
			System.out.print(i + " ");
		System.out.println();

		// 測試 3-Way QuickSort
		int[] a2 = a.clone();
		quickSort3Way(a2, 0, a2.length - 1);
		System.out.println("3-Way QuickSort:");
		for (int i : a2)
			System.out.print(i + " ");
		System.out.println();
	}
}