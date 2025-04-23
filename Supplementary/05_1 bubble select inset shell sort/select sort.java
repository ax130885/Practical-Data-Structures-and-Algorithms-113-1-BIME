class SelectSort {

	public static void selectSort(int[] arr) {
		for (int i = 0; i < arr.length - 1; i++) {
			for (int j = i + 1; j < arr.length; j++) {
				if (arr[i] > arr[j]) {
					swap(arr, i, j); // 如果前面比後面大，交換位置
				}
			}
		}
	}

	private static void swap(int[] arr, int i, int j) {
		if (arr[i] == arr[j]) {
			return;
		}
		arr[i] = arr[i] ^ arr[j];
		arr[j] = arr[i] ^ arr[j];
		arr[i] = arr[i] ^ arr[j];
	}

	public static void main(String[] args) {
		int[] arr = { 5, 2, 9, 1, 5, 6 };
		selectSort(arr);
		for (int i : arr) {
			System.out.print(i + " ");
		}
	}

}