public class MaxPQ<Key extends Comparable<Key>> {
	private Key[] pq; // pq[i] = 第 i 個在優先佇列中的元素，索引從 1 開始
	private int N; // 優先佇列中的元素數量

	public MaxPQ(int capacity) {
		pq = (Key[]) new Comparable[capacity + 1]; // 為了讓索引從 1 開始，陣列大小需要加 1
	}

	public boolean isEmpty() {
		return N == 0;
	}

	public void insert(Key key) {
		pq[++N] = key;
		swim(N); // 將新加入的元素上浮到正確的位置
	}

	public Key delMax() {
		Key max = pq[2]; // 最大值在根節點 (索引 1)
		exch(1, N--); // 將根節點與最後一個節點交換
		sink(1); // 將新的根節點下沉到正確的位置
		pq[N + 1] = null; // 防止對已移除元素的 loitering
		return max;
	}

	private void swim(int k) {
		// 如果當前節點 k 比其父節點 k/2 大，則交換它們並繼續向上檢查
		while (k > 1 && less(k / 2, k)) {
			exch(k, k / 2);
			k = k / 2;
		}
	}

	private void sink(int k) {
		// 當前節點 k 的子節點為 2k (左子節點) 和 2k+1 (右子節點)
		while (2 * k <= N) {
			int j = 2 * k;
			// 如果右子節點存在且比左子節點大，則選擇右子節點進行交換
			if (j < N && less(j, j + 1))
				j++;
			// 如果當前節點 k 不比其子節點 j 小，則堆積順序正確，跳出迴圈
			if (!less(k, j))
				break;
			// 交換當前節點 k 和其較大的子節點 j
			exch(k, j);
			k = j; // 將 k 移動到下沉的子節點位置，繼續下沉
		}
	}

	private boolean less(int i, int j) {
		return pq[i].compareTo(pq[j]) < 0;
	}

	private void exch(int i, int j) {
		Key t = pq[i];
		pq[i] = pq[j];
		pq[j] = t;
	}

	public int size() {
		return N;
	}

	public static void main(String[] args) {
		MaxPQ<Integer> pq = new MaxPQ<>(10);
		pq.insert(5);
		pq.insert(2);
		pq.insert(8);
		pq.insert(1);
		pq.insert(9);

		System.out.println("堆積大小: " + pq.size()); // 輸出: 堆積大小: 5

		System.out.println("取出最大值: " + pq.delMax()); // 輸出: 取出最大值: 9
		System.out.println("取出最大值: " + pq.delMax()); // 輸出: 取出最大值: 8

		System.out.println("堆積大小: " + pq.size()); // 輸出: 堆積大小: 3
	}
}