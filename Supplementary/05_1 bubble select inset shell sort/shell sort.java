// 先分組做初步排序 讓資料接近有序，最後一次用插入排序幾乎只要O(n)。
// 分組初步排序，第一次gap取n/2，第二次gap取n/4，第三次gap取n/8...直到gap=1。
// 意思是說  第一次為分出 n/2組 每組長度2的陣列，每組用插入排序
// 			第二次為分出 n/4組 每組長度4的陣列，每組用插入排序
// 時間複雜度為O(nlogn)
// 空間複雜度為O(1)

class ShellSort {

	public static void shellSort(int arr[]) {
		int n = arr.length;
		for (int gap = n / 2; gap > 0; gap /= 2) { // 計算gap
			for (int i = 0; i < n - gap; i++) { // 跟插入排序不一樣 不是從1開始
				int temp = arr[i + gap]; // 以下做法幾乎=插入排序
				int j;
				for (j = i; j >= 0 && arr[j] > temp; j -= gap) { // 只差在這裡的j -= gap
					arr[j + gap] = arr[j];
				}
				arr[j + gap] = temp;
			}
		}
	}

	public static void main(String[] args) {
		int arr[] = { 12, 34, 54, 2, 3 };
		int n = arr.length;

		shellSort(arr);

		for (int i = 0; i < n; i++)
			System.out.print(arr[i] + " ");
		System.out.println();

	}
}