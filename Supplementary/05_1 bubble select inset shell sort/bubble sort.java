class BubbleSort {

	public static void bubbleSort(int[] arr) { // 冒泡排序:從後面開始穩定
		int n = arr.length;
		for (int i = n - 1; i > 0; i--) { // 左指標 從後面數
			for (int j = 0; j < i; j++) { // 右指標 數到左指標即可
				if (arr[j] > arr[j + 1]) {
					swap(arr, j, j + 1);
				}
			}
		}

	}

	private static void swap(int[] arr, int i, int j) {
		if (arr[i] == arr[j]) { // 如果兩個指標相同，用這種方法，會導致兩個都變成0。
			return; // 並且如果原本兩個就一樣，本來就不用交換。
		}

		arr[i] = arr[i] ^ arr[j];
		arr[j] = arr[i] ^ arr[j];
		arr[i] = arr[i] ^ arr[j];
	}

	public static void main(String[] args) {
		int arr[] = { 12, 11, 13, 5, 6 };
		int n = arr.length;
		bubbleSort(arr);
		for (int i = 0; i < n; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}

}