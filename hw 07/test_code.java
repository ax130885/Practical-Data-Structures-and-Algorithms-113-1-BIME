import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Collections;
import edu.princeton.cs.algs4.Point2D;

import java.io.FileReader; // 修正：導入 FileReader
import java.io.FileNotFoundException; // 修正：導入 FileNotFoundException
import java.io.IOException; // 修正：導入 IOException

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

class GalacticClustering {

    /**
     * 內部類別：表示一個聚類(Cluster)
     * 包含該聚類中的所有點和其質心(centroid)
     */
    private static class Cluster {
        List<Point2D> points; // 屬於該聚類的所有點
        Point2D centroid; // 該聚類的質心座標

        // 構造函數：用單個點初始化聚類
        Cluster(Point2D point) {
            this.points = new ArrayList<>();
            this.points.add(point);
            this.centroid = point; // 單點聚類的質心就是它自己
        }

        // 構造函數：用多個點初始化聚類
        Cluster(List<Point2D> points) {
            this.points = new ArrayList<>(points);
            calculateCentroid(); // 計算這些點的質心
        }

        // 計算聚類的質心（所有點座標的平均值）
        void calculateCentroid() {
            double x = 0, y = 0;
            for (Point2D p : points) {
                x += p.x(); // 累加所有x座標
                y += p.y(); // 累加所有y座標
            }
            // 計算平均值得到質心
            centroid = new Point2D(x / points.size(), y / points.size());
        }

        // 計算與另一個聚類之間的距離（質心之間的歐幾里得距離）
        double distanceTo(Cluster other) {
            return centroid.distanceTo(other.centroid);
        }

        // 與另一個聚類合併，返回新的聚類
        Cluster mergeWith(Cluster other) {
            List<Point2D> combined = new ArrayList<>(this.points);
            combined.addAll(other.points); // 合併兩個聚類的所有點
            return new Cluster(combined); // 創建新聚類
        }
    }

    /**
     * 內部類別：表示一對聚類及其距離
     * 用於優先隊列(PriorityQueue)的排序
     */
    private static class ClusterPair implements Comparable<ClusterPair> {
        Cluster c1, c2; // 一對聚類
        double distance; // 這對聚類之間的距離

        ClusterPair(Cluster c1, Cluster c2) {
            this.c1 = c1;
            this.c2 = c2;
            this.distance = c1.distanceTo(c2); // 計算距離
        }

        // 實現Comparable接口，用於優先隊列排序
        @Override
        public int compareTo(ClusterPair other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    /**
     * 主要方法：執行層次聚類演算法
     * 
     * @param speciesCoordinates 物種座標列表
     * @param targetClusterCount 目標聚類數量
     * @return 最終聚類的質心座標列表
     */
    public List<double[]> analyzeSpecies(List<int[]> speciesCoordinates, int targetClusterCount) {
        // 步驟0：初始化聚類，每個點作為一個獨立聚類
        List<Cluster> clusters = new ArrayList<>();
        for (int[] coord : speciesCoordinates) {
            clusters.add(new Cluster(new Point2D(coord[0], coord[1])));
        }

        // 初始化優先隊列，儲存所有可能的聚類對及其距離
        PriorityQueue<ClusterPair> queue = new PriorityQueue<>();
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = i + 1; j < clusters.size(); j++) {
                // 計算每對聚類的距離並加入隊列
                queue.add(new ClusterPair(clusters.get(i), clusters.get(j)));
            }
        }

        // 層次聚類主循環
        while (clusters.size() > targetClusterCount && !queue.isEmpty()) {
            // 取出距離最近的聚類對
            ClusterPair closest = queue.poll();
            Cluster c1 = closest.c1;
            Cluster c2 = closest.c2;

            // 檢查這對聚類是否已被合併過（處理隊列中的過期項目）
            if (!clusters.contains(c1) || !clusters.contains(c2)) {
                continue; // 跳過已處理的聚類對
            }

            // 合併這兩個聚類
            Cluster merged = c1.mergeWith(c2);

            // 移除舊聚類，添加新合併的聚類
            clusters.remove(c1);
            clusters.remove(c2);
            clusters.add(merged);

            // 計算新聚類與其他所有聚類的距離，並加入隊列
            for (Cluster c : clusters) {
                if (c != merged) { // 不與自己比較
                    queue.add(new ClusterPair(merged, c));
                }
            }
        }

        // 按x座標排序，若x相同則按y座標排序
        Collections.sort(clusters, (a, b) -> {
            if (a.centroid.x() != b.centroid.x()) {
                return Double.compare(a.centroid.x(), b.centroid.x());
            }
            return Double.compare(a.centroid.y(), b.centroid.y());
        });

        // 準備結果：將聚類質心轉換為double陣列
        List<double[]> result = new ArrayList<>();
        for (Cluster c : clusters) {
            result.add(new double[] { c.centroid.x(), c.centroid.y() });
        }

        return result;
    }

    /**
     * 主函數：測試程式
     */
    public static void main(String[] args) {
        GalacticClustering sol = new GalacticClustering();
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("test_case.json")) {
            JSONArray all = (JSONArray) jsonParser.parse(reader);
            for (Object CaseInList : all) {
                JSONArray a = (JSONArray) CaseInList;
                int q_cnt = 0, wa = 0;
                for (Object o : a) {
                    q_cnt++;
                    JSONObject tc = (JSONObject) o;
                    JSONArray point = (JSONArray) tc.get("points");
                    Long clusterNumber = (Long) tc.get("cluster_num");
                    JSONArray arg_ans = (JSONArray) tc.get("answer");

                    double Answer_x[] = new double[arg_ans.size()];
                    double Answer_y[] = new double[arg_ans.size()];
                    for (int i = 0; i < clusterNumber; i++) {
                        String ansStr = arg_ans.get(i).toString()
                                .replace("[", "").replace("]", "");
                        String[] parts = ansStr.split(",");
                        Answer_x[i] = Double.parseDouble(parts[0]);
                        Answer_y[i] = Double.parseDouble(parts[1]);
                    }

                    List<int[]> pointList = new ArrayList<>();
                    for (int i = 0; i < point.size(); i++) {
                        String ptStr = point.get(i).toString()
                                .replace("[", "").replace("]", "");
                        String[] parts = ptStr.split(",");
                        pointList.add(new int[] {
                                Integer.parseInt(parts[0]),
                                Integer.parseInt(parts[1])
                        });
                    }

                    List<double[]> ansClus = sol.analyzeSpecies(
                            pointList,
                            clusterNumber.intValue());

                    if (ansClus.size() != clusterNumber) {
                        wa++;
                        System.out.println(q_cnt + ": WA");
                    } else {
                        boolean ok = true;
                        for (int i = 0; i < clusterNumber; i++) {
                            double[] c = ansClus.get(i);
                            if (Math.abs(c[0] - Answer_x[i]) > 1e-3 ||
                                    Math.abs(c[1] - Answer_y[i]) > 1e-3) {
                                ok = false;
                                break;
                            }
                        }
                        if (ok) {
                            System.out.println(q_cnt + ": AC");
                        } else {
                            wa++;
                            System.out.println(q_cnt + ": WA");
                        }
                    }
                }
                System.out.println("Score: " + (q_cnt - wa) + "/" + q_cnt);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}