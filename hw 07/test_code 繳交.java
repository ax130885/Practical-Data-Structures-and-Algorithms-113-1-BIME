import java.util.*;
import edu.princeton.cs.algs4.Point2D;

class GalacticClustering {

    // 定義 Cluster 群組物件，每個包含：質心座標 centroid、點數 size、是否還活著 alive
    private static class Cluster {
        Point2D centroid; // 群的質心
        int size; // 群中點的數量
        boolean alive; // 是否還是有效群（被合併後就設為 false）

        Cluster(Point2D c, int size) {
            this.centroid = c;
            this.size = size;
            this.alive = true;
        }

        // 合併兩個 cluster 並產生新的 centroid（加權平均）
        static Cluster merge(Cluster a, Cluster b) {
            double x = (a.centroid.x() * a.size + b.centroid.x() * b.size) / (a.size + b.size);
            double y = (a.centroid.y() * a.size + b.centroid.y() * b.size) / (a.size + b.size);
            return new Cluster(new Point2D(x, y), a.size + b.size);
        }

        // 使用平方距離 計算兩個 cluster 之間的距離 避免開根號
        double distanceTo(Cluster other) {
            double dx = this.centroid.x() - other.centroid.x();
            double dy = this.centroid.y() - other.centroid.y();
            return dx * dx + dy * dy; // 返回平方距離
        }

    }

    // 主演算法函式：執行中心點階層式分群
    // coords: 原始所有外星種族座標 (List of [x, y])
    // targetClusterCount: 希望分成的群數
    // 分群後的所有群心座標（已排序）

    public List<double[]> analyzeSpecies(List<int[]> coords, int targetClusterCount) {
        int n = coords.size(); // 初始點數量
        List<Cluster> clusters = new ArrayList<>();

        // Step 0: 建立初始 cluster，每個點都是獨立的群
        for (int[] p : coords) {
            clusters.add(new Cluster(new Point2D(p[0], p[1]), 1));
        }

        // 用來記錄每個 cluster 當前最近鄰的 index
        int[] nearest = new int[2 * n]; // 為了後續新加入 cluster 保留足夠空間
        Arrays.fill(nearest, -1); // 初始化為 -1 表示無設定

        // Step 1: 每輪合併最近的一對 cluster，直到剩下 targetClusterCount 為止
        while (countAlive(clusters) > targetClusterCount) {
            double bestDist = Double.MAX_VALUE; // 記錄最短距離
            int mergeA = -1, mergeB = -1; // 記錄要合併的兩群 index

            for (int i = 0; i < clusters.size(); i++) {
                if (!clusters.get(i).alive)
                    continue; // 只看還活著的群

                // 如果之前記錄的最近鄰 j 不存在或已經被合併了，就重新計算
                int j = nearest[i];
                if (j == -1 || !clusters.get(j).alive) {
                    double minDist = Double.MAX_VALUE;
                    int best = -1;
                    for (int k = 0; k < clusters.size(); k++) {
                        if (i == k || !clusters.get(k).alive)
                            continue;
                        double d = clusters.get(i).distanceTo(clusters.get(k));
                        if (d < minDist) {
                            minDist = d;
                            best = k;
                        }
                    }
                    nearest[i] = best; // 更新最近鄰
                    j = best;
                }

                // 若找到了有效的最近鄰 j，就計算距離並看是否為最短距離
                if (j != -1 && clusters.get(j).alive) {
                    double d = clusters.get(i).distanceTo(clusters.get(j));
                    if (d < bestDist) {
                        bestDist = d;
                        mergeA = i;
                        mergeB = j;
                    }
                }
            }

            // 找不到可合併的群，提前結束（防呆）
            if (mergeA == -1 || mergeB == -1)
                break;

            // Step 2: 合併這兩群並新增一個新群
            Cluster merged = Cluster.merge(clusters.get(mergeA), clusters.get(mergeB));
            clusters.get(mergeA).alive = false; // 原本的兩群標記為死亡
            clusters.get(mergeB).alive = false;
            clusters.add(merged); // 新群加入到 cluster 列表中
        }

        // Step 3: 收集所有還活著的群體的質心，加入結果清單中
        List<double[]> result = new ArrayList<>();
        for (Cluster c : clusters) {
            if (c.alive) {
                result.add(new double[] { c.centroid.x(), c.centroid.y() });
            }
        }

        // Step 4: 結果排序，先 x 再 y 遞增
        result.sort((a, b) -> {
            if (Math.abs(a[0] - b[0]) > 1e-6)
                return Double.compare(a[0], b[0]);
            return Double.compare(a[1], b[1]);
        });

        return result;
    }

    // 計算目前還活著的 cluster 數量
    private int countAlive(List<Cluster> clusters) {
        int cnt = 0;
        for (Cluster c : clusters)
            if (c.alive)
                cnt++;
        return cnt;
    }

    // 範例主程式（可改為 Scanner 輸入）
    public static void main(String[] args) {
        List<int[]> test = new ArrayList<>(List.of(
                new int[] { 0, 1 }, new int[] { 0, 2 }, new int[] { 3, 1 }, new int[] { 3, 2 }));
        int target = 2;

        List<double[]> res = new GalacticClustering().analyzeSpecies(test, target);
        for (double[] d : res) {
            System.out.printf("[%.3f, %.3f]\n", d[0], d[1]);
        }
    }
}
