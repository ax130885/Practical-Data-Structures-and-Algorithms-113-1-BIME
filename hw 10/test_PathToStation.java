
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.*;

import com.google.gson.*;

class OutputFormat {
    int[][] map;
    int[] init_pos;
    int[] target_pos;
    int answer;
}

class PathToStation {
    private int[][] map; // 0=cliff懸崖, 2=plain平原, 3=hills山丘 // 0 不可走
    private int[] initPos; // 起點座標 map[initPos[0]][initPos[1]]
    private int[] targetPos; // 終點座標 map[targetPos[0]][targetPos[1]]
    private int rows, cols; // 地圖的行數和列數
    private int[][] gScore; // 從起點到當前節點的實際成本(最短路徑)
    private int[][] fScore; // gScore + 啟發式估價
    private int[][][] cameFrom; // 路徑重建

    // 定義移動方向: 上、下、左、右
    private static final int[][] DIRECTIONS = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

    // 構造函數，初始化地圖、起點和終點
    public PathToStation(int[][] map, int[] init_pos, int[] target_pos) {
        this.map = map;
        this.initPos = init_pos;
        this.targetPos = target_pos;
        this.rows = map.length;
        this.cols = map[0].length;
        this.gScore = new int[rows][cols];
        this.fScore = new int[rows][cols];
        this.cameFrom = new int[rows][cols][2];

        // 初始化所有節點的gScore和fScore為無限大
        for (int i = 0; i < rows; i++) {
            Arrays.fill(gScore[i], Integer.MAX_VALUE);
            Arrays.fill(fScore[i], Integer.MAX_VALUE);
        }
        // 起點的gScore和fScore初始化為0
        gScore[initPos[0]][initPos[1]] = 0;
        fScore[initPos[0]][initPos[1]] = heuristic(initPos[0], initPos[1]);
    }

    // 使用曼哈頓距離定義估價函數（保證可容許性(保證全局最佳解)）
    private int heuristic(int y, int x) {
        return Math.abs(y - targetPos[0]) + Math.abs(x - targetPos[1]);
    }

    // 計算移動成本
    private int calculateMoveCost(int fromY, int fromX, int toY, int toX) {
        if (map[toY][toX] == 3) { // 移動到山丘
            return 5;
        } else if (map[fromY][fromX] == 3) { // 從山丘移動到平原
            return 1;
        } else { // 平原間移動
            return 1;
        }
    }

    // 返回最短路徑
    public List<int[]> shortest_path() {
        aStarSearch(); // 執行A*搜索算法
        // 如果目標點不可達，返回空列表
        return reconstructPath(); // 重建路徑
    }

    // 返回最短路徑長度
    public int shortest_path_len() {
        aStarSearch();
        return gScore[targetPos[0]][targetPos[1]];
    }

    // A*算法
    private void aStarSearch() {
        // 優先隊列按fScore排序，(fScore, y, x)
        PriorityQueue<int[]> openSet = new PriorityQueue<>(Comparator.comparingInt(a -> a[0]));

        // 把起點加到pq
        openSet.add(new int[] { fScore[initPos[0]][initPos[1]], initPos[0], initPos[1] });

        // dijkstra主迴圈 跑到pq沒人，每次執行，把鄰居全加到pq中
        while (!openSet.isEmpty()) {
            int[] current = openSet.poll();
            int currentY = current[1];
            int currentX = current[2];

            // 到達目標點
            if (currentY == targetPos[0] && currentX == targetPos[1]) {
                return;
            }

            for (int[] dir : DIRECTIONS) {
                int neighborY = currentY + dir[0];
                int neighborX = currentX + dir[1];

                // 檢查邊界和懸崖
                if (neighborY < 0 || neighborY >= rows || neighborX < 0 || neighborX >= cols
                        || map[neighborY][neighborX] == 0) {
                    continue;
                }

                // 計算暫存gScore
                int tentativeGScore = gScore[currentY][currentX] +
                        calculateMoveCost(currentY, currentX, neighborY, neighborX);

                if (tentativeGScore < gScore[neighborY][neighborX]) {
                    // 找到更優路徑
                    cameFrom[neighborY][neighborX] = new int[] { currentY, currentX };
                    gScore[neighborY][neighborX] = tentativeGScore;
                    fScore[neighborY][neighborX] = tentativeGScore + heuristic(neighborY, neighborX);

                    // 檢查是否已在openSet中
                    boolean inOpenSet = false;
                    for (int[] node : openSet) {
                        if (node[1] == neighborY && node[2] == neighborX) {
                            inOpenSet = true;
                            break;
                        }
                    }

                    if (!inOpenSet) {
                        openSet.add(new int[] { fScore[neighborY][neighborX], neighborY, neighborX });
                    }
                }
            }
        }
    }

    // 從終點開始回溯a*的紀錄，直到起點，得到最短路徑的實際路徑。
    private List<int[]> reconstructPath() {
        // 建立一個保存int[0,1]=x,y的list
        List<int[]> path = new ArrayList<>();
        // 當前座標初始化為終點
        int currentY = targetPos[0];
        int currentX = targetPos[1];

        // 把終點加到路徑中
        path.add(new int[] { currentY, currentX });

        // 當已經回朔到起點時，停止回朔
        while (currentY != initPos[0] || currentX != initPos[1]) {
            // 回朔找到現在座標是從哪來的
            int[] prev = cameFrom[currentY][currentX];
            // 更新當前座標
            currentY = prev[0];
            currentX = prev[1];
            // 把當前座標加到最終路徑的最前端(index:0)
            path.add(0, new int[] { currentY, currentX });
        }
        // 返回從起點到終點的路徑
        return path;
    }

    public static void main(String[] args) {
        PathToStation sol = new PathToStation(new int[][] {
                { 0, 0, 0, 0, 0 },
                { 0, 2, 3, 2, 0 }, // map[1][2]=3
                { 0, 2, 0, 2, 0 },
                { 0, 2, 0, 2, 0 },
                { 0, 2, 2, 2, 0 },
                { 0, 0, 0, 0, 0 }
        },
                // 最佳路徑: [1,1] → [1,2] → [1,3]
                // 成本計算:
                // [1,1](平原) → [1,2](山丘): 5
                // [1,2](山丘) → [1,3](平原): 1
                // 總成本: 0 + 5 + 1 = 6
                new int[] { 1, 1 },
                new int[] { 1, 3 });
        System.out.println(sol.shortest_path_len());
        List<int[]> path = sol.shortest_path();
        for (int[] coor : path) {
            System.out.println("y: " + coor[0] + " x: " + coor[1]);
        }
    }
}

class test {
    static boolean are4Connected(int[] p1, int[] p2) {
        return (Math.abs(p1[0] - p2[0]) == 1 && p1[1] == p2[1]) || (Math.abs(p1[1] - p2[1]) == 1 && p1[0] == p2[0]);
    }

    static boolean isShortestPath(int[][] map, int path_len, List<int[]> path) {
        // check if the path is valid, (if the two node is actually neighbour, and if
        // the path is not wall)
        int path_len2 = 0;
        for (int i = 1; i < path.size(); ++i) {
            int[] pos_prev = path.get(i - 1);
            int[] pos_now = path.get(i);
            int type = map[pos_now[0]][pos_now[1]];
            if (!are4Connected(pos_prev, pos_now) || type == 0) // type == 0 means that it is a cliff.
                return false;
            path_len2 += (type == 3) ? 5 : 1;
        }
        return (path_len == path_len2);
    }

    public static void main(String[] args) {
        Gson gson = new Gson();
        OutputFormat[] datas;
        OutputFormat data;
        int num_ac = 0;

        List<int[]> SHP;
        PathToStation sol;

        try {
            datas = gson.fromJson(new FileReader("test_PathToStation.json"), OutputFormat[].class);
            for (int i = 0; i < datas.length; ++i) {
                data = datas[i];
                sol = new PathToStation(data.map, data.init_pos, data.target_pos);
                SHP = sol.shortest_path();

                System.out.print("Sample" + i + ": ");
                if (sol.shortest_path_len() != data.answer) {
                    System.out.println("WA: incorrect path length");
                    System.out.println("Test_ans:  " + data.answer);
                    System.out.println("User_ans:  " + sol.shortest_path_len());
                    System.out.println("");
                } else if (!Arrays.equals(SHP.get(0), data.init_pos)) {
                    System.out.println("WA: incorrect starting position");
                    System.out.println("Test_ans:  " + Arrays.toString(data.init_pos));
                    System.out.println("User_ans:  " + Arrays.toString(SHP.get(0)));
                    System.out.println("");
                } else if (!Arrays.equals(SHP.get(SHP.size() - 1), data.target_pos)) {
                    System.out.println("WA: incorrect goal position");
                    System.out.println("Test_ans:  " + Arrays.toString(data.target_pos));
                    System.out.println("User_ans:  " + Arrays.toString(SHP.get(SHP.size() - 1)));
                    System.out.println("");
                } else if (!isShortestPath(data.map, data.answer, SHP)) {
                    System.out.println("WA: Path Error, either not shortest Path or path not connected");
                    System.out.println("Map:      " + Arrays.deepToString(data.map));
                    System.out.println("User_Path:  " + Arrays.deepToString(SHP.toArray()));
                    System.out.println("Test_path_len:  " + data.answer);
                    System.out.println("User_path_len:  " + sol.shortest_path_len());
                    System.out.println("");

                } else {
                    System.out.println("AC");
                    num_ac++;
                }
            }
            System.out.println("Score: " + num_ac + "/" + datas.length);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}