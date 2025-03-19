import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Stack; // 直接用stack函式庫

import com.google.gson.*;

class test {
    public static void main(String[] args) {
        DefenseSystem sol = new DefenseSystem();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("test_cases.json")) {
            JsonArray all = gson.fromJson(reader, JsonArray.class);
            for (JsonElement caseInList : all) {
                JsonArray a = caseInList.getAsJsonArray();
                int q_cnt = 0, wa = 0, ac = 0;
                for (JsonElement o : a) {
                    q_cnt++;
                    JsonObject person = o.getAsJsonObject();
                    JsonArray arg_lvl = person.getAsJsonArray("level");
                    JsonArray arg_rng = person.getAsJsonArray("range");
                    JsonArray arg_ans = person.getAsJsonArray("answer");
                    int LVL[] = new int[arg_lvl.size()];
                    int RNG[] = new int[arg_lvl.size()];
                    int Answer[] = new int[arg_ans.size()];
                    int Answer_W[] = new int[arg_ans.size()];
                    for (int i = 0; i < arg_ans.size(); i++) {
                        Answer[i] = (arg_ans.get(i).getAsInt());
                        if (i < arg_lvl.size()) {
                            LVL[i] = (arg_lvl.get(i).getAsInt());
                            RNG[i] = (arg_rng.get(i).getAsInt());
                        }
                    }
                    Answer_W = sol.result(LVL, RNG);
                    for (int i = 0; i < arg_ans.size(); i++) {
                        if (Answer_W[i] == Answer[i]) {
                            if (i == arg_ans.size() - 1) {
                                System.out.println(q_cnt + ": AC");
                            }
                        } else {
                            wa++;
                            System.out.println(q_cnt + ": WA");
                            break;
                        }
                    }

                }
                System.out.println("Score: " + (q_cnt - wa) + "/" + q_cnt);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class member {
    int Level;
    int Range;
    int Index;

    member(int _level, int _range, int i) {
        Level = _level;
        Range = _range;
        Index = i;
    }
}

class DefenseSystem {

    // version 3 自己寫 stack
    public class MyStack {

        int size = 65535;
        int[] stack = new int[size];
        int top = -1;

        private void resize(int newSize) {
            int[] oldStack = stack;
            stack = new int[newSize]; // 創建更大的 stack 陣列

            // 複製舊數據
            for (int i = 0; i < oldStack.length; i++) {
                stack[i] = oldStack[i];
            }

            size = newSize; // 更新 stack 大小
        }

        private boolean isEmpty() {
            return (top == -1);
        }

        private void ensureCapacity() {
            if (top == size - 1) {
                resize(size * 2);
            }
        }

        public int peek() {

            // 檢查不為零
            if (isEmpty()) {
                throw new RuntimeException("peek when stack is empty");
            }
            return stack[top];
        }

        public void push(int i) {
            ensureCapacity();
            stack[++top] = i;
        }

        public int pop() {
            if (isEmpty()) {
                // 印出錯誤
                throw new RuntimeException("pop when stack is empty");
            }

            return stack[top--];

        }

    }

    public int[] result(int[] levels, int[] ranges) { // levels: 攻擊力 ranges: 攻擊範圍
        int n = levels.length;
        int[] result = new int[2 * n];

        // 預處理左邊界
        // stack 存放的是所有左邊的局部最大值 相當於 stack 內越左邊 攻擊力一定越高
        // 不會存放中間值的原理是依靠 pop ，把所有現在塔攻擊力低的清掉。
        // 最後把自己加到 stack 最上方
        int[] left = new int[n];
        MyStack stack = new MyStack();
        for (int i = 0; i < n; i++) {
            // 如果stack不是空的，且目前塔的攻擊力比stack最上面塔的攻擊力還要高，就pop掉，檢查stack下一個塔的攻擊力。
            while (!stack.isEmpty() && levels[stack.peek()] < levels[i]) {
                stack.pop();
            } // 直到 stack 清空 或是 目前塔的攻擊力比stack最上面塔的攻擊力還要低

            // 如果stack是空的，代表左邊界是 -1
            // 如果stack不是空的，代表stack最上方的塔攻擊力更高。所以將左邊界設為stack最上方的塔。
            left[i] = stack.isEmpty() ? -1 : stack.peek();

            // 將目前塔的index存入stack 最上方
            stack.push(i);
        }

        // 預處理右邊界
        int[] right = new int[n];
        stack = new MyStack();
        for (int i = n - 1; i >= 0; i--) {
            while (!stack.isEmpty() && levels[stack.peek()] < levels[i]) {
                stack.pop();
            }

            right[i] = stack.isEmpty() ? n : stack.peek();
            stack.push(i);
        }

        // 計算攻擊範圍
        for (int i = 0; i < n; i++) {
            int a = Math.max(i - ranges[i], left[i] + 1);
            int b = Math.min(i + ranges[i], right[i] - 1);
            result[2 * i] = a;
            result[2 * i + 1] = b;
        }

        return result;
    }

    // // version 2 用單調棧 monotonic stack
    // // 較佳解法 複雜度O(3n)
    // // 想法: 一開始先額外創建兩個陣列 left 和 right。
    // // 用 monotonic stack 的方式，算出每個塔只考慮攻擊力的左右極限，並將結果存到兩個陣列當中。 O(2n)
    // // 最後再用攻擊範圍直接crop出最後答案。 O(n)
    // public int[] result(int[] levels, int[] ranges) { // levels: 攻擊力 ranges: 攻擊範圍
    // int n = levels.length;
    // int[] result = new int[2 * n];

    // // 預處理左邊界
    // // stack 存放的是所有左邊的局部最大值 相當於 stack 內越左邊 攻擊力一定越高
    // // 不會存放中間值的原理是依靠 pop ，把所有現在塔攻擊力低的清掉。
    // // 最後把自己加到 stack 最上方
    // int[] left = new int[n];
    // Stack<Integer> stack = new Stack<>();
    // for (int i = 0; i < n; i++) {
    // // 如果stack不是空的，且目前塔的攻擊力比stack最上面塔的攻擊力還要高，就pop掉，檢查stack下一個塔的攻擊力。
    // while (!stack.isEmpty() && levels[stack.peek()] < levels[i]) {
    // stack.pop();
    // } // 直到 stack 清空 或是 目前塔的攻擊力比stack最上面塔的攻擊力還要低

    // // 如果stack是空的，代表左邊界是 -1
    // // 如果stack不是空的，代表stack最上方的塔攻擊力更高。所以將左邊界設為stack最上方的塔。
    // left[i] = stack.isEmpty() ? -1 : stack.peek();

    // // 將目前塔的index存入stack 最上方
    // stack.push(i);
    // }

    // // 預處理右邊界
    // int[] right = new int[n];
    // stack.clear();
    // for (int i = n - 1; i >= 0; i--) {
    // while (!stack.isEmpty() && levels[stack.peek()] < levels[i]) {
    // stack.pop();
    // }
    // right[i] = stack.isEmpty() ? n : stack.peek();
    // stack.push(i);
    // }

    // // 計算攻擊範圍
    // for (int i = 0; i < n; i++) {
    // int a = Math.max(i - ranges[i], left[i] + 1);
    // int b = Math.min(i + ranges[i], right[i] - 1);
    // result[2 * i] = a;
    // result[2 * i + 1] = b;
    // }

    // return result;
    // }

    // version 1 for loop 暴力解
    // // 暴力解 複雜度最差是O(n^2)
    // public int[] result(int[] levels, int[] ranges) {
    // // Given the traits of each member levels,nd output
    // // the leftmost and rightmost index of member
    // // can be attacked by each member.

    // // complete the code by returning an int[]
    // // flatten the results since we only need an 1-dimentional array.

    // int[] result = new int[2 * levels.length];

    // // 遍歷每個塔
    // for (int i = 0; i < levels.length; i++) {

    // // 初始化左右攻擊範圍 最短是自己
    // result[2 * i] = i;
    // result[2 * i + 1] = i;

    // // 計算左右攻擊範圍
    // int left = -1;
    // int right = -1;

    // left = (i - ranges[i] < 0) ? 0 : i - ranges[i];
    // right = ((i + ranges[i]) > (levels.length - 1)) ? levels.length - 1 : i +
    // ranges[i];

    // // 往左檢查
    // for (int j = i - 1; j >= left; j--) {

    // if (levels[j] < levels[i]) {
    // result[2 * i] = j;
    // } else {
    // break;
    // }
    // }

    // // 往右檢查
    // for (int j = i + 1; j <= right; j++) {

    // if (levels[j] < levels[i]) {
    // result[2 * i + 1] = j;
    // } else {
    // break;
    // }

    // }

    // }

    // return result;
    // }

    public static void main(String[] args) {
        DefenseSystem sol = new DefenseSystem();
        System.out.println(Arrays.toString(
                sol.result(new int[] { 11, 13, 11, 7, 15 },
                        new int[] { 1, 8, 1, 7, 2 })));
        // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
        // => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
    }
}