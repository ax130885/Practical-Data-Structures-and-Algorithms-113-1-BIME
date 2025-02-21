// 解題想法: 概念與上課教的union find完全相同，差別只在多了一個likeship[][]來記錄每個節點的關係。
// 並且使用addlike function建立union。
class SocialNetwork {

	private int id[]; // 紀錄每個節點的父節點
	private int size[]; // 紀錄每個節點的大小
	private boolean likeship[][]; // 紀錄每個節點的關係

	public SocialNetwork(int n) {
		// todo: Initialize the social network with 'n' users.
		// return: none

		if (n < 0) {
			throw new IllegalArgumentException("n should be greater than 0"); // throw 代表拋出例外 new 代表建立物件
																				// IllegalArgumentException 代表參數不合法
		}

		// 初始化每個節點的父節點為自己 ; 初始化每個節點的大小為 1 ; 初始化每個節點的like關係為 false
		id = new int[n];
		size = new int[n];
		likeship = new boolean[n][n];

		for (int i = 0; i < n; i++) {
			id[i] = i;
			size[i] = 1;
			for (int j = 0; j < n; j++) {
				likeship[i][j] = false; // 將所有元素初始化為 false
			}
		}

	}

	// 找到節點的根節點 並且使用路徑壓縮(直接儲存祖父節點 跳過父節點)
	public int root(int i) {
		while (i != id[i]) {
			id[i] = id[id[i]];
			i = id[i];
		}
		return i;
	}

	public void union(int p, int q) {
		p = root(p);
		q = root(q);

		// System.out.println("start union p: " + p + " q: " + q);

		if (p == q) {
			// System.out.println("no union p: " + p + " q: " + q);
			return;
		}

		// 將小的樹接在大的樹上
		if (size[p] <= size[q]) { // 記得要加等於 不能只有 < / > 否則一開始 size 相等 會全都被跳過不處理
			id[p] = q;
			size[q] += size[p];
			// System.out.println("Union p: " + p + " q: " + q + "with" + id[p]);
		}

		else if (size[p] > size[q]) {
			id[q] = p;
			size[p] += size[q];
			// System.out.println("Union p: " + p + " q: " + q + "with" + id[q]);
		}
	}

	public void addLike(int a, int b) {
		// todo: add friendship if needed; if no friendship added, memorize the fact
		// user 'a' likes user 'b'
		// note: Friendship is mutual, meaning if user 'a' likes user 'b',
		// user 'b' must also like user 'a' for them to become friends.
		// return: none

		if (a < 0 || a >= id.length || b < 0 || b >= id.length) {
			throw new IllegalArgumentException("a and b should be greater than 0 and less than n");
		}

		likeship[a][b] = true;

		// System.out.println("AddLike a: " + a + " b: " + b);

		if (likeship[b][a]) {
			union(a, b);
			// System.out.println("Union a: " + a + " b: " + b);
		}

	}

	public boolean areConnected(int a, int b) {
		// todo: Check if 'a' and 'b' are in the same friend circle.
		// return: boolean

		if (a < 0 || a >= id.length || b < 0 || b >= id.length) {
			throw new IllegalArgumentException("a and b should be greater than 0 and less than n");
		}

		return root(a) == root(b);
	}

	public int countFriendCircles() {
		// todo: Count the number of friend circles (groups of connected users).
		// return: int

		int count = 0;
		for (int i = 0; i < id.length; i++) {
			if (i == id[i]) {
				count++;
			}
		}

		return count;
	}

	public int getLargestFriendCircleSize() {
		// todo: Find the size of the largest friend circle.
		// return: int

		int max = 0;
		for (int i = 0; i < id.length; i++) {
			if (i == id[i]) {
				if (size[i] > max)
					max = size[i];
			}
		}
		return max;
	}
}