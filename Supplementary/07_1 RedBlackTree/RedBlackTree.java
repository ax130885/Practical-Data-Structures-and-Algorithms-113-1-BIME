class RedBlackTree {
	private static final boolean RED = true; // 定義紅色節點
	private static final boolean BLACK = false; // 定義黑色節點

	// 節點類別，表示紅黑樹中的每個節點
	private class Node {
		int key; // 節點的鍵值
		Node left, right, parent; // 左子節點、右子節點及父節點
		boolean color; // 節點的顏色（紅或黑）

		// 節點的建構子，初始化鍵值並將顏色設為紅色
		Node(int key) {
			this.key = key;
			this.color = RED; // 新插入的節點預設為紅色
		}
	}

	private Node root; // 樹的根節點

	// 左旋轉操作
	private void leftRotate(Node x) {
		Node y = x.right;
		x.right = y.left;
		if (y.left != null) {
			y.left.parent = x;
		}
		y.parent = x.parent;
		if (x.parent == null) {
			root = y;
		} else if (x == x.parent.left) {
			x.parent.left = y;
		} else {
			x.parent.right = y;
		}
		y.left = x;
		x.parent = y;
	}

	// 右旋轉操作
	private void rightRotate(Node y) {
		Node x = y.left;
		y.left = x.right;
		if (x.right != null) {
			x.right.parent = y;
		}
		x.parent = y.parent;
		if (y.parent == null) {
			root = x;
		} else if (y == y.parent.right) {
			y.parent.right = x;
		} else {
			y.parent.left = x;
		}
		x.right = y;
		y.parent = x;
	}

	// 插入新鍵值
	public void insert(int key) {
		Node newNode = new Node(key);
		Node y = null;
		Node x = root;

		while (x != null) {
			y = x;
			if (newNode.key < x.key) {
				x = x.left;
			} else {
				x = x.right;
			}
		}

		newNode.parent = y;
		if (y == null) {
			root = newNode;
		} else if (newNode.key < y.key) {
			y.left = newNode;
		} else {
			y.right = newNode;
		}

		newNode.color = RED;
		fixInsert(newNode);
	}

	// 修正插入後的紅黑樹性質
	private void fixInsert(Node z) {
		while (z.parent != null && z.parent.color == RED) {
			if (z.parent == z.parent.parent.left) {
				Node y = z.parent.parent.right;
				if (y != null && y.color == RED) {
					z.parent.color = BLACK;
					y.color = BLACK;
					z.parent.parent.color = RED;
					z = z.parent.parent;
				} else {
					if (z == z.parent.right) {
						z = z.parent;
						leftRotate(z);
					}
					z.parent.color = BLACK;
					z.parent.parent.color = RED;
					rightRotate(z.parent.parent);
				}
			} else {
				Node y = z.parent.parent.left;
				if (y != null && y.color == RED) {
					z.parent.color = BLACK;
					y.color = BLACK;
					z.parent.parent.color = RED;
					z = z.parent.parent;
				} else {
					if (z == z.parent.left) {
						z = z.parent;
						rightRotate(z);
					}
					z.parent.color = BLACK;
					z.parent.parent.color = RED;
					leftRotate(z.parent.parent);
				}
			}
		}
		root.color = BLACK;
	}

	// 中序遍歷
	public void inOrderTraversal() {
		inOrderHelper(root);
	}

	// 中序遍歷的輔助函式
	private void inOrderHelper(Node node) {
		if (node != null) {
			inOrderHelper(node.left);
			System.out.print(node.key + " ");
			inOrderHelper(node.right);
		}
	}

	// 刪除指定鍵值的節點
	public void delete(int key) {
		Node z = searchNode(root, key);
		if (z == null) {
			return; // 如果找不到節點，直接返回
		}

		Node y = z;
		Node x;
		boolean yOriginalColor = y.color;

		if (z.left == null) {
			x = z.right;
			transplant(z, z.right);
		} else if (z.right == null) {
			x = z.left;
			transplant(z, z.left);
		} else {
			y = minimum(z.right);
			yOriginalColor = y.color;
			x = y.right;
			if (y.parent == z) {
				x.parent = y;
			} else {
				transplant(y, y.right);
				y.right = z.right;
				y.right.parent = y;
			}
			transplant(z, y);
			y.left = z.left;
			y.left.parent = y;
			y.color = z.color;
		}
		if (yOriginalColor == BLACK) {
			fixDelete(x);
		}
	}

	// 修正刪除後的紅黑樹性質
	private void fixDelete(Node x) {
		while (x != root && x.color == BLACK) {
			if (x == x.parent.left) {
				Node w = x.parent.right;
				if (w.color == RED) {
					w.color = BLACK;
					x.parent.color = RED;
					leftRotate(x.parent);
					w = x.parent.right;
				}
				if (w.left.color == BLACK && w.right.color == BLACK) {
					w.color = RED;
					x = x.parent;
				} else {
					if (w.right.color == BLACK) {
						w.left.color = BLACK;
						w.color = RED;
						rightRotate(w);
						w = x.parent.right;
					}
					w.color = x.parent.color;
					x.parent.color = BLACK;
					w.right.color = BLACK;
					leftRotate(x.parent);
					x = root;
				}
			} else {
				Node w = x.parent.left;
				if (w.color == RED) {
					w.color = BLACK;
					x.parent.color = RED;
					rightRotate(x.parent);
					w = x.parent.left;
				}
				if (w.right.color == BLACK && w.left.color == BLACK) {
					w.color = RED;
					x = x.parent;
				} else {
					if (w.left.color == BLACK) {
						w.right.color = BLACK;
						w.color = RED;
						leftRotate(w);
						w = x.parent.left;
					}
					w.color = x.parent.color;
					x.parent.color = BLACK;
					w.left.color = BLACK;
					rightRotate(x.parent);
					x = root;
				}
			}
		}
		x.color = BLACK;
	}

	// 替換節點 u 為 v
	private void transplant(Node u, Node v) {
		if (u.parent == null) {
			root = v;
		} else if (u == u.parent.left) {
			u.parent.left = v;
		} else {
			u.parent.right = v;
		}
		if (v != null) {
			v.parent = u.parent;
		}
	}

	// 查找指定鍵值的節點
	private Node searchNode(Node node, int key) {
		while (node != null) {
			if (key == node.key) {
				return node;
			} else if (key < node.key) {
				node = node.left;
			} else {
				node = node.right;
			}
		}
		return null;
	}

	// 查找最小值節點
	private Node minimum(Node node) {
		while (node.left != null) {
			node = node.left;
		}
		return node;
	}

	// 主函式
	public static void main(String[] args) {
		RedBlackTree tree = new RedBlackTree();
		tree.insert(10);
		tree.insert(20);
		tree.insert(30);
		tree.insert(15);
		tree.insert(25);

		System.out.println("紅黑樹的中序遍歷結果：");
		tree.inOrderTraversal();

		tree.delete(20);
		System.out.println("\n刪除 20 後的中序遍歷結果：");
		tree.inOrderTraversal();
	}
}