class MyQueue_Linkedlist<item> {

	private static class Node<item> {
		item data;
		Node<item> next = null; // next 是更底層 // node也要加上泛型
	}

	Node<item> head = null; // 代表最前面
	Node<item> tail = null; // 代表最後面

	public boolean isEmpty() {
		return head == null;
	}

	public void enqueue(item i) {
		if (isEmpty()) {
			head = new Node<item>();
			head.data = i;
			head.next = null;
			tail = head;
		} else {
			Node<item> old = tail;
			tail = new Node<item>();
			tail.data = i;
			tail.next = null;
			old.next = tail;
		}
	}

	public item dequeue() {
		if (isEmpty()) {
			throw new RuntimeException("dequeue when queue is empty");
		}

		if (head == tail) { // 如果只有一個元素
			tail = null; // tail也要設為空
		}

		item ans = head.data;
		head = head.next;

		return ans;
	}

	public item peek() {
		if (isEmpty()) {
			throw new RuntimeException("peek when queue is empty");
		}
		return head.data;
	}

}