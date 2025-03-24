import java.util.ArrayList;
import java.util.Arrays;
import edu.princeton.cs.algs4.Point2D;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Comparator;
import com.google.gson.*;

class DroneDeliveryHubNetwork {

    private ArrayList<Point2D> hubs; // 儲存所有站點
    private Point2D[] hullArray; // 儲存凸包點的陣列形式(緩存)

    // 構造函數
    public DroneDeliveryHubNetwork(ArrayList<Point2D> hubs) {
        this.hubs = hubs;
        // updateConvexHull(); // 初始化時計算凸包
    }

    // 更新凸包 Andrew's Monotone Chain Algorithm
    private void updateConvexHull() {
        if (hubs.size() < 3) {
            // 特殊情況處理：少於 3 個點時，直接返回所有點作為凸包
            hullArray = hubs.toArray(new Point2D[0]);
            return;
        }

        // 按 x 座標排序，如果 x 相同則按 y 座標排序
        hubs.sort(Comparator.comparingDouble(Point2D::x).thenComparingDouble(Point2D::y));

        ArrayList<Point2D> lower = new ArrayList<>();
        ArrayList<Point2D> upper = new ArrayList<>();

        // 構建下凸包
        for (Point2D p : hubs) {
            while (lower.size() >= 2 && cross(lower.get(lower.size() - 2), lower.get(lower.size() - 1), p) <= 0) {
                lower.remove(lower.size() - 1);
            }
            lower.add(p);
        }

        // 構建上凸包
        for (int i = hubs.size() - 1; i >= 0; i--) {
            Point2D p = hubs.get(i);
            while (upper.size() >= 2 && cross(upper.get(upper.size() - 2), upper.get(upper.size() - 1), p) <= 0) {
                upper.remove(upper.size() - 1);
            }
            upper.add(p);
        }

        // 移除重複的最後一個點，並合併上下凸包
        lower.remove(lower.size() - 1);
        upper.remove(upper.size() - 1);
        lower.addAll(upper);

        // 更新凸包陣列
        hullArray = lower.toArray(new Point2D[0]);
    }

    // 計算向量叉積，用於判斷點的方向
    private double cross(Point2D o, Point2D a, Point2D b) {
        return (a.x() - o.x()) * (b.y() - o.y()) - (a.y() - o.y()) * (b.x() - o.x());
    }

    // 找出最遠的兩個點 (旋轉卡尺法)
    public Point2D[] findFarthesthubs() {
        updateConvexHull(); // 更新凸包
        if (this.hullArray.length < 2) {
            throw new IllegalArgumentException("At least 2 points are required.");
        }

        // 特殊情況處理：只有2個點
        if (hullArray.length == 2) {
            Point2D[] result = { hullArray[0], hullArray[1] };
            sortByPolarRadius(result); // 按極座標半徑排序
            return result;
        }

        // 旋轉卡尺法找最遠點對
        int n = hullArray.length;
        Point2D[] farthest = new Point2D[2];
        double maxDistSquared = -1; // 使用距離平方避免開根號

        int j = 1; // 初始化第二個指針
        for (int i = 0; i < n; i++) {
            // 確保 j 是下一個點，並且不超過 n
            while (true) {
                int nextJ = (j + 1) % n;
                double currentDist = hullArray[i].distanceSquaredTo(hullArray[j]);
                double nextDist = hullArray[i].distanceSquaredTo(hullArray[nextJ]);

                // 如果下一個點的距離更大，移動 j
                if (nextDist > currentDist) {
                    j = nextJ;
                } else {
                    break; // 否則停止移動 j
                }
            }

            // 更新最大距離
            double distSquared = hullArray[i].distanceSquaredTo(hullArray[j]);
            if (distSquared > maxDistSquared) {
                maxDistSquared = distSquared;
                farthest[0] = hullArray[i];
                farthest[1] = hullArray[j];
            }
        }

        // 按題目要求排序
        sortByPolarRadius(farthest);
        return farthest;
    }

    // 按極座標半徑排序（保留原始註解）
    private void sortByPolarRadius(Point2D[] points) {
        // it should be sorted (ascendingly) by polar radius; please sort (ascendingly)
        // by y coordinate if there are ties in polar radius.
        Arrays.sort(points, Comparator
                .comparingDouble((Point2D p) -> p.x() * p.x() + p.y() * p.y()) // 極座標半徑平方
                .thenComparingDouble(Point2D::y)); // 如果極座標半徑相同，按 y 座標排序
    }

    // 計算凸包的面積 鞋帶公式: 1/2 * |Σ(xi * yi+1 - xi+1 * yi)| (i=1 to n-1)
    public double coverageArea() {
        updateConvexHull(); // 更新凸包

        // calculate the area surrounded by the existing stations
        if (hullArray.length < 3) {
            throw new IllegalArgumentException("At least 3 points are required.");
        }

        double area = 0.0;
        int n = hullArray.length;

        // 使用鞋帶公式計算面積
        for (int i = 0; i < n; i++) {
            Point2D p1 = hullArray[i];
            Point2D p2 = hullArray[(i + 1) % n]; // 環狀處理最後一個點
            area += (p1.x() * p2.y() - p2.x() * p1.y()); // 叉積累加
        }

        return Math.abs(area) / 2.0; // 取絕對值並除以2
    }

    // 新增點
    public void addNewhub(Point2D newhub) {
        hubs.add(newhub);
    }

    public static void main(String[] args) throws Exception {
        ArrayList<Point2D> hubCoordinates = new ArrayList<>();
        // Example coordinates (you may replace them with your actual coordinates)
        hubCoordinates.add(new Point2D(0, 0));
        hubCoordinates.add(new Point2D(2, 0));
        hubCoordinates.add(new Point2D(3, 2));
        hubCoordinates.add(new Point2D(2, 6));
        hubCoordinates.add(new Point2D(0, 4));
        hubCoordinates.add(new Point2D(1, 1));
        hubCoordinates.add(new Point2D(2, 2));

        DroneDeliveryHubNetwork analysis = new DroneDeliveryHubNetwork(hubCoordinates);
        Point2D[] farthesthubs = analysis.findFarthesthubs();
        System.out.println("Farthest hub A: " + farthesthubs[0]);
        System.out.println("Farthest hub B: " + farthesthubs[1]);
        System.out.println("Coverage Area: " + analysis.coverageArea());

        System.out.println("Add hub (10,3): ");
        analysis.addNewhub(new Point2D(10, 3));
        farthesthubs = analysis.findFarthesthubs();
        System.out.println("Farthest hub A: " + farthesthubs[0]);
        System.out.println("Farthest hub B: " + farthesthubs[1]);
        System.out.println("Coverage Area: " + analysis.coverageArea());
    }
}

class OutputFormat {
    ArrayList<Point2D> hubs;
    DroneDeliveryHubNetwork DDHN;
    Point2D[] farthest;
    double area;
    Point2D[] farthestNew;
    double areaNew;
    ArrayList<Point2D> newhubs;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}

class test_DroneDeliveryHubNetwork {
    public static void main(String[] args) {
        Gson gson = new Gson();
        int num_ac = 0;
        int i = 1;

        try {
            // TestCase[] testCases = gson.fromJson(new FileReader(args[0]),
            // TestCase[].class);
            FileReader reader = new FileReader("test.json");
            TestCase[] testCases = gson.fromJson(reader, TestCase[].class);
            for (TestCase testCase : testCases) {
                System.out.println("Sample" + i + ": ");
                i++;
                for (OutputFormat data : testCase.data) {
                    DroneDeliveryHubNetwork DDHN = new DroneDeliveryHubNetwork(data.hubs);
                    Point2D[] farthest;
                    double area;
                    Point2D[] farthestNew;
                    double areaNew;

                    farthest = DDHN.findFarthesthubs();
                    area = DDHN.coverageArea();

                    if (data.newhubs != null) {
                        for (Point2D newhub : data.newhubs) {
                            DDHN.addNewhub(newhub);
                        }
                        farthestNew = DDHN.findFarthesthubs();
                        areaNew = DDHN.coverageArea();
                    } else {
                        farthestNew = farthest;
                        areaNew = area;
                    }

                    if (farthest[0].equals(data.farthest[0]) && farthest[1].equals(data.farthest[1])
                            && Math.abs(area - data.area) < 0.0001
                            && farthestNew[0].equals(data.farthestNew[0]) && farthestNew[1].equals(data.farthestNew[1])
                            && Math.abs(areaNew - data.areaNew) < 0.0001) {
                        System.out.println("AC");
                        num_ac++;
                    } else {
                        System.out.println("WA");
                        System.out.println("Ans-farthest: " + Arrays.toString(data.farthest));
                        System.out.println("Your-farthest:  " + Arrays.toString(farthest));
                        System.out.println("Ans-area:  " + data.area);
                        System.out.println("Your-area:  " + area);

                        System.out.println("Ans-farthestNew: " + Arrays.toString(data.farthestNew));
                        System.out.println("Your-farthestNew:  " + Arrays.toString(farthestNew));
                        System.out.println("Ans-areaNew: " + data.areaNew);
                        System.out.println("Your-areaNew: " + areaNew);
                        System.out.println("");
                    }
                }
                System.out.println("Score: " + num_ac + "/ 8");
            }

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
