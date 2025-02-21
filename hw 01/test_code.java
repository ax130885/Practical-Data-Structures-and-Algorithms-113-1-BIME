import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class TestSocialNetwork {

    public static void test(String filename) {
        SocialNetwork sn;
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filename)) {
            JSONArray all = (JSONArray) jsonParser.parse(reader);
            int count = 0;
            int testSize = 0, waSize = 0;

            for (Object CaseInList : all) {
                count++;
                JSONObject aCase = (JSONObject) CaseInList;
                JSONArray dataArray = (JSONArray) aCase.get("data");

                System.out.println("Case " + count + ":");

                for (Object dataObject : dataArray) {
                    JSONObject dataDetails = (JSONObject) dataObject;
                    int N = ((Long) dataDetails.get("N")).intValue();
                    JSONArray likesArray = (JSONArray) dataDetails.get("likes");

                    HashMap<Integer, HashSet<Integer>> likesMap = new HashMap<>();
                    for (int i = 0; i < N; i++) {
                        likesMap.put(i, new HashSet<>());
                    }

                    for (Object obj : likesArray) {
                        JSONArray pair = (JSONArray) obj;
                        int a = ((Long) pair.get(0)).intValue();
                        int b = ((Long) pair.get(1)).intValue();
                        likesMap.get(a).add(b);
                    }

                    sn = new SocialNetwork(N);
                    for (int i = 0; i < N; i++) {
                        for (int j : likesMap.get(i)) {
                            if (likesMap.get(j).contains(i)) {
                                sn.addLike(i, j);
                            }
                        }
                    }

                    int expectedCircles = ((Long) dataDetails.get("DistinctFriendCircles")).intValue();
                    int expectedLargestCircle = ((Long) dataDetails.get("LargestFriendCircleSize")).intValue();
                    JSONArray checkPairs = (JSONArray) dataDetails.get("CheckConnections");

                    int ans1 = sn.countFriendCircles();
                    int ans2 = sn.getLargestFriendCircleSize();
                    boolean connectionsCorrect = true;
                    boolean connectionMismatch = false;

                    for (Object checkPair : checkPairs) {
                        JSONArray pair = (JSONArray) checkPair;
                        int u = ((Long) pair.get(0)).intValue();
                        int v = ((Long) pair.get(1)).intValue();
                        boolean expectedConnected = (Boolean) pair.get(2);
                        boolean computedConnected = sn.areConnected(u, v);
                        if (computedConnected != expectedConnected) {
                            connectionsCorrect = false;
                            connectionMismatch = true;
                            System.out.println("  - Connection mismatch: (" + u + ", " + v + ")");
                            System.out
                                    .println("    Expected: " + expectedConnected + ", Computed: " + computedConnected);
                        }
                    }

                    testSize++;
                    if (ans1 == expectedCircles && ans2 == expectedLargestCircle && connectionsCorrect) {
                        System.out.println("  AC");
                    } else {
                        waSize++;
                        System.out.println("  WA");
                        if (ans1 != expectedCircles) {
                            System.out.println("  - DistinctFriendCircles mismatch:");
                            System.out.println("    Expected: " + expectedCircles + ", Computed: " + ans1);
                        }
                        if (ans2 != expectedLargestCircle) {
                            System.out.println("  - LargestFriendCircleSize mismatch:");
                            System.out.println("    Expected: " + expectedLargestCircle + ", Computed: " + ans2);
                        }
                        if (connectionMismatch) {
                            System.out.println("  - Some connections mismatched.");
                        }
                    }
                }
            }
            System.out.println("Score: " + (testSize - waSize) + " / " + testSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        test("test_cases.json");
    }
}

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

    public static void main(String[] args) {
        int n1 = 5;
        int[][] likes1 = {
                { 0, 1 },
                { 1, 0 },
                { 2, 3 },
                { 3, 4 }
        };

        System.out.println("Example 1:");
        SocialNetwork sn1 = new SocialNetwork(n1);

        for (int[] like : likes1) {
            sn1.addLike(like[0], like[1]);
        }

        System.out.println("Are 0 and 1 connected? " + sn1.areConnected(0, 1)); // true
        System.out.println("Are 2 and 4 connected? " + sn1.areConnected(2, 4)); // false
        System.out.println("Friend circles count: " + sn1.countFriendCircles()); // 4
        System.out.println("Largest friend circle size: " + sn1.getLargestFriendCircleSize() + "\n"); // 2

        sn1.addLike(3, 2);
        sn1.addLike(4, 3);

        System.out.println("After add other relationship:");
        System.out.println("Are 0 and 1 connected? " + sn1.areConnected(0, 1)); // true
        System.out.println("Are 2 and 4 connected? " + sn1.areConnected(2, 4)); // true
        System.out.println("Friend circles count: " + sn1.countFriendCircles()); // 2
        System.out.println("Largest friend circle size: " + sn1.getLargestFriendCircleSize()); // 3

        System.out.println("\n" + "Example 2:");
        int n2 = 5;
        SocialNetwork sn2 = new SocialNetwork(n2);
        int[][] likes2 = {

        };

        for (int[] like : likes2) {
            sn2.addLike(like[0], like[1]);
        }

        System.out.println("Are 0 and 1 connected? " + sn2.areConnected(0, 1)); // false
        System.out.println("Are 2 and 4 connected? " + sn2.areConnected(2, 4)); // false
        System.out.println("Friend circles count: " + sn2.countFriendCircles()); // 5
        System.out.println("Largest friend circle size: " + sn2.getLargestFriendCircleSize() + "\n"); // 1

    }
}
