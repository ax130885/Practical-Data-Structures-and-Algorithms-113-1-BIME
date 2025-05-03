import java.util.*;
import edu.princeton.cs.algs4.Point2D;

class GalacticClustering {

    /**
     * 內部類別：表示一個聚類(Cluster)
     * 優化點：不再保存所有點，改用質心(centroid)和點數量(size)計算合併
     * 這樣可以大幅減少記憶體使用和合併時間
     */
    private static class Cluster {
        double cx, cy; // 質心座標
        int size; // 點的數量

        // 用單個點初始化聚類
        Cluster(double x, double y) {
            this.cx = x;
            this.cy = y;
            this.size = 1;
        }

        // 合併兩個聚類，返回新的聚類
        Cluster mergeWith(Cluster other) {
            // 計算合併後的質心與大小
            double totalSize = this.size + other.size;
            double newCx = (this.cx * this.size + other.cx * other.size) / totalSize;
            double newCy = (this.cy * this.size + other.cy * other.size) / totalSize;

            Cluster merged = new Cluster(newCx, newCy);
            merged.size = (int) totalSize; // 設置合併後的點數量
            return merged;
        }
    }

    /**
     * 內部類別：表示一對聚類及其距離
     * 優化點：使用平方距離代替標準歐幾里得距離，避免開根號運算
     * 這能大幅減少計算時間但不影響比較結果
     */
    private static class ClusterPair implements Comparable<ClusterPair> {
        Cluster c1, c2; // 一對聚類
        double distance; // 這對聚類之間的平方距離

        ClusterPair(Cluster c1, Cluster c2) {
            this.c1 = c1;
            this.c2 = c2;
            // 計算平方距離（避免 Math.sqrt）
            double dx = c1.cx - c2.cx;
            double dy = c1.cy - c2.cy;
            this.distance = dx * dx + dy * dy;
        }

        @Override
        public int compareTo(ClusterPair other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    /**
     * 主要方法：執行層次聚類演算法
     * 優化策略：
     * 1. 使用更輕量的 Cluster 與 ClusterPair 類別
     * 2. 使用平方距離代替標準距離計算
     * 3. 避免保存所有點，改用質心與大小計算合併
     * 4. 使用 HashSet 跟蹤活躍聚類（O(1) 查找與刪除）
     * 
     * @param speciesCoordinates 物種座標列表
     * @param targetClusterCount 目標聚類數量
     * @return 最終聚類的質心座標列表
     */
    public List<double[]> analyzeSpecies(List<int[]> speciesCoordinates, int targetClusterCount) {
        // 步驟0：初始化聚類，每個點作為一個獨立聚類
        List<Cluster> clusters = new ArrayList<>();
        for (int[] coord : speciesCoordinates) {
            clusters.add(new Cluster(coord[0], coord[1]));
        }

        // 特殊情況處理：若初始點數 <= 目標聚類數，直接返回
        if (clusters.size() <= targetClusterCount) {
            List<double[]> result = new ArrayList<>();
            for (Cluster c : clusters) {
                result.add(new double[] { c.cx, c.cy });
            }
            sortResult(result); // 排序結果
            return result;
        }

        // 使用 HashSet 維護活躍聚類（O(1) 查找）
        Set<Cluster> activeClusters = new HashSet<>(clusters);

        // 使用優先隊列找最近的聚類對
        PriorityQueue<ClusterPair> pq = new PriorityQueue<>();

        // 初始化距離計算（O(N^2) 時間但更簡潔）
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                pq.add(new ClusterPair(clusters.get(i), clusters.get(j)));
            }
        }

        // 層次聚類主循環
        while (activeClusters.size() > targetClusterCount) {
            ClusterPair closest = pq.poll();

            // 如果這對聚類已不再活躍，跳過
            if (!activeClusters.contains(closest.c1) || !activeClusters.contains(closest.c2)) {
                continue;
            }

            // 合併這兩個聚類
            Cluster merged = closest.c1.mergeWith(closest.c2);

            // 更新聚類集合
            activeClusters.remove(closest.c1);
            activeClusters.remove(closest.c2);
            activeClusters.add(merged);

            // 計算新聚類與所有其他活躍聚類的距離
            for (Cluster c : activeClusters) {
                if (c != merged) {
                    pq.add(new ClusterPair(merged, c));
                }
            }
        }

        // 準備結果
        List<double[]> result = new ArrayList<>();
        for (Cluster c : activeClusters) {
            result.add(new double[] { c.cx, c.cy });
        }
        sortResult(result); // 最終排序

        return result;
    }

    /**
     * 對結果進行排序（x -> y）
     * 
     * @param result 要排序的結果列表
     */
    private void sortResult(List<double[]> result) {
        result.sort((a, b) -> {
            if (a[0] != b[0])
                return Double.compare(a[0], b[0]);
            return Double.compare(a[1], b[1]);
        });
    }

    /**
     * 主函數：測試程式
     */
    public static void main(String[] args) {
        List<int[]> testCoordinates = new ArrayList<int[]>() {
            {
                add(new int[] { 0, 1 });
                add(new int[] { 0, 2 });
                add(new int[] { 3, 1 });
                add(new int[] { 3, 2 });
            }
        };
        int targetClusters = 2;

        List<double[]> clusters = new GalacticClustering()
                .analyzeSpecies(testCoordinates, targetClusters);

        for (double[] centroid : clusters) {
            System.out.println(Arrays.toString(centroid));
        }
    }
}