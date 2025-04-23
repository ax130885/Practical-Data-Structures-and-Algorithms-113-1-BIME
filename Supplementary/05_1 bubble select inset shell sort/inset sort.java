// 時間複雜度: 算法 theta(n^2), 最佳 omega(n), 最差 O(n^2) 
// 空間複雜度: O(1) 除了原本的array以外 只要多一個temp放要插入的牌。
// 穩定性: 穩定的排序算法，因為不會改變相同元素的相對位置。
// 優點: 資料量小，或接近有序時，效率最好。

class InsertSort {

	public static void insertSort(int arr[]) {
		int n = arr.length;
		for (int i = 1; i < n; i++) { // 從第二個元素開始，視第一個元素為已排序
			int temp = arr[i]; // 取出當前要插入的元素
			int j;
			for (j = i - 1; j >= 0 && arr[j] > temp; j--) { // 當前元素比 temp 大，繼續向前比較
				arr[j + 1] = arr[j]; // 將元素向後移動
			}
			arr[j + 1] = temp; // 將 temp 插入到正確位置
		}
	}

	public static void main(String[] args) {
		int arr[] = { 12, 11, 13, 5, 6 };
		int n = arr.length;
		insertSort(arr);
		for (int i = 0; i < n; i++) {
			System.out.print(arr[i] + " ");
		}
		System.out.println();
	}
}