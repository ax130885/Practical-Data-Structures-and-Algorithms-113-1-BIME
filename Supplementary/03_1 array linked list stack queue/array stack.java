class MyStack_Array {

	int size = 65536;
	int[] stack = new int[size];
	int top = -1;

	private void resize(int newSize) {
		int[] oldStack = this.stack;
		this.stack = new int[newSize]; // 創建更大的 stack 陣列

		// 複製舊數據
		for (int i = 0; i < oldStack.length; i++) {
			this.stack[i] = oldStack[i];
		}

		// 如果在C++ 需要手動 delete oldStack
		// 但是在Java中，由於Java具有自動垃圾回收機制，所以不需要手動刪除舊數據。

	}

	private void ensureCapacity() {
		if (this.top == this.size - 1) {
			resize(this.size *= 2);
		}
	}

	private boolean isEmpty() {
		return (this.top == -1);
	}

	public int peek() {

		// 檢查不為零
		if (isEmpty()) {
			throw new RuntimeException("peek when stack is empty");
		}
		return this.stack[this.top];
	}

	public void push(int i) {
		ensureCapacity();
		this.stack[++this.top] = i;
	}

	public int pop() {
		if (isEmpty()) {
			// 印出錯誤
			throw new RuntimeException("pop when stack is empty");
		}

		return this.stack[this.top--];

	}

}