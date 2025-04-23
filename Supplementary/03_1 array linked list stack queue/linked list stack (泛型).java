class MyStack_LinkedList<item> {

	private static class Node<item> {
		item value; // 泛型不可初始化
		Node<item> next = null; // next 是更底層 // node也要加上泛型
	}

	Node<item> top = null;

	private boolean isEmpty() {
		return top == null;
	}

	public void push(item i) {
		if (isEmpty()) {
			top = new Node<item>();
			top.value = i;
			top.next = null;
		} else {
			Node<item> old = top;
			top = new Node<item>();
			top.value = i;
			top.next = old;
		}

	}

	public item pop() {
		if (isEmpty()) {
			throw new RuntimeException("pop when stack is empty");
		}

		item ans = top.value;
		top = top.next;

		return ans;

	}

	public item peek() {
		if (isEmpty()) {
			throw new RuntimeException("peek when stack is empty");
		}

		return top.value;
	}
}