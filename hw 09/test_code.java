import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

class OutputFormat {
    Map<Integer, String> deviceTypes;
    List<int[]> links;
    int cablingCost;
    int serverToRouter;
    int mostPopularPrinter;
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}

class InnovationStudioCabling {
    private List<Edge> mstEdges;
    private int cablingCost;
    private int serverRouterLinkLength;
    private int mostBusyPrinter;

    public InnovationStudioCabling(Map<Integer, String> deviceTypes, List<int[]> links) {
        buildMST(deviceTypes, links);
        calculateCablingCost();
        serverRouterLinkLength = findServerRouterLink(deviceTypes);
        mostBusyPrinter = findMostBusyPrinter(deviceTypes);
    }

    // 使用Kruskal算法構建MST，修正設備索引壓縮順序
    private void buildMST(Map<Integer, String> deviceTypes, List<int[]> links) {
        Set<Integer> devices = deviceTypes.keySet();
        List<Integer> deviceList = new ArrayList<>(devices);
        Collections.sort(deviceList); // 確保設備索引排序，避免壓縮錯誤
        int n = deviceList.size();
        Map<Integer, Integer> originalToCompressed = new HashMap<>();
        for (int i = 0; i < n; i++) {
            originalToCompressed.put(deviceList.get(i), i);
        }

        List<Edge> edges = new ArrayList<>();
        for (int[] link : links) {
            int u = link[0];
            int v = link[1];
            int length = link[2];
            edges.add(new Edge(u, v, length));
        }
        Collections.sort(edges);

        UnionFind uf = new UnionFind(n);
        mstEdges = new ArrayList<>();

        for (Edge edge : edges) {
            int uCompressed = originalToCompressed.get(edge.u);
            int vCompressed = originalToCompressed.get(edge.v);
            if (uf.find(uCompressed) != uf.find(vCompressed)) {
                uf.union(uCompressed, vCompressed);
                mstEdges.add(edge);
                if (mstEdges.size() == n - 1)
                    break;
            }
        }
    }

    // 計算總電纜長度
    private void calculateCablingCost() {
        cablingCost = 0;
        for (Edge edge : mstEdges) {
            cablingCost += edge.length;
        }
    }

    // 查找服務器與路由器之間的直接連接長度
    private int findServerRouterLink(Map<Integer, String> deviceTypes) {
        int serverIndex = -1, routerIndex = -1;
        for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
            if ("Server".equals(entry.getValue()))
                serverIndex = entry.getKey();
            else if ("Router".equals(entry.getValue()))
                routerIndex = entry.getKey();
        }
        if (serverIndex == -1 || routerIndex == -1)
            return 0; // 處理未找到的情況

        for (Edge edge : mstEdges) {
            if ((edge.u == serverIndex && edge.v == routerIndex) ||
                    (edge.v == serverIndex && edge.u == routerIndex)) {
                return edge.length;
            }
        }
        return 0;
    }

    // 查找最忙碌的打印機
    private int findMostBusyPrinter(Map<Integer, String> deviceTypes) {
        Set<Integer> devices = deviceTypes.keySet();
        List<Integer> deviceList = new ArrayList<>(devices);
        int n = deviceList.size();
        Map<Integer, Integer> originalToCompressed = new HashMap<>();
        for (int i = 0; i < n; i++) {
            originalToCompressed.put(deviceList.get(i), i);
        }

        // 構建鄰接表
        List<List<Edge>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++)
            adj.add(new ArrayList<>());
        for (Edge edge : mstEdges) {
            int uCompressed = originalToCompressed.get(edge.u);
            int vCompressed = originalToCompressed.get(edge.v);
            adj.get(uCompressed).add(new Edge(uCompressed, vCompressed, edge.length));
            adj.get(vCompressed).add(new Edge(vCompressed, uCompressed, edge.length));
        }

        // 收集打印機和計算機的壓縮索引
        List<Integer> printersCompressed = new ArrayList<>();
        List<Integer> originalPrinters = new ArrayList<>();
        List<Integer> computersCompressed = new ArrayList<>();
        for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
            int original = entry.getKey();
            String type = entry.getValue();
            int compressed = originalToCompressed.get(original);
            if ("Printer".equals(type)) {
                printersCompressed.add(compressed);
                originalPrinters.add(original);
            } else if ("Computer".equals(type)) {
                computersCompressed.add(compressed);
            }
        }

        // 預處理每個打印機到所有節點的距離
        List<int[]> printerDistances = new ArrayList<>();
        for (int p : printersCompressed) {
            int[] dist = new int[n];
            Arrays.fill(dist, -1);
            dist[p] = 0;
            Queue<Integer> queue = new LinkedList<>();
            queue.offer(p);
            while (!queue.isEmpty()) {
                int current = queue.poll();
                for (Edge e : adj.get(current)) {
                    int neighbor = e.v;
                    if (dist[neighbor] == -1) {
                        dist[neighbor] = dist[current] + e.length;
                        queue.offer(neighbor);
                    }
                }
            }
            printerDistances.add(dist);
        }

        // 統計每個計算機的最近打印機
        int[] printerCounts = new int[printersCompressed.size()];
        for (int c : computersCompressed) {
            int minDist = Integer.MAX_VALUE;
            int selectedPrinterIdx = -1;
            for (int pIdx = 0; pIdx < printersCompressed.size(); pIdx++) {
                int dist = printerDistances.get(pIdx)[c];
                if (dist < minDist) {
                    minDist = dist;
                    selectedPrinterIdx = pIdx;
                } else if (dist == minDist) {
                    int currentOriginal = originalPrinters.get(pIdx);
                    int selectedOriginal = originalPrinters.get(selectedPrinterIdx);
                    if (currentOriginal < selectedOriginal) {
                        selectedPrinterIdx = pIdx;
                    }
                }
            }
            if (selectedPrinterIdx != -1) {
                printerCounts[selectedPrinterIdx]++;
            }
        }

        // 找出最忙碌的打印機
        int maxCount = -1, result = -1;
        for (int i = 0; i < printerCounts.length; i++) {
            int count = printerCounts[i];
            int original = originalPrinters.get(i);
            if (count > maxCount || (count == maxCount && original < result)) {
                maxCount = count;
                result = original;
            }
        }
        return result;
    }

    public int cablingCost() {
        return cablingCost;
    }

    public int serverToRouter() {
        return serverRouterLinkLength;
    }

    public int mostPopularPrinter() {
        return mostBusyPrinter;
    }

    // 邊的數據結構
    static class Edge implements Comparable<Edge> {
        int u, v, length;

        Edge(int u, int v, int length) {
            this.u = u;
            this.v = v;
            this.length = length;
        }

        @Override
        public int compareTo(Edge o) {
            return Integer.compare(length, o.length);
        }
    }

    // 並查集實現
    static class UnionFind {
        int[] parent, rank;

        UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            for (int i = 0; i < n; i++)
                parent[i] = i;
        }

        int find(int x) {
            if (parent[x] != x)
                parent[x] = find(parent[x]);
            return parent[x];
        }

        void union(int x, int y) {
            int xRoot = find(x), yRoot = find(y);
            if (xRoot == yRoot)
                return;
            if (rank[xRoot] < rank[yRoot])
                parent[xRoot] = yRoot;
            else {
                parent[yRoot] = xRoot;
                if (rank[xRoot] == rank[yRoot])
                    rank[xRoot]++;
            }
        }
    }

    public static void main(String[] args) {
        Gson gson = new Gson();
        int num_ac = 0;
        try {
            // 直接指定測試檔案名稱
            TestCase[] testCases = gson.fromJson(
                    new FileReader("test_cases.json"), TestCase[].class);

            for (TestCase tc : testCases) {
                for (OutputFormat data : tc.data) {
                    InnovationStudioCabling LNC = new InnovationStudioCabling(
                            data.deviceTypes, data.links);
                    int user_cc = LNC.cablingCost();
                    int user_sr = LNC.serverToRouter();
                    int user_mpp = LNC.mostPopularPrinter();
                    if (user_cc == data.cablingCost
                            && user_sr == data.serverToRouter
                            && user_mpp == data.mostPopularPrinter) {
                        System.out.println("AC");
                        num_ac++;
                    } else {
                        System.out.println("WA");
                        System.out.println("Input deviceTypes: " + data.deviceTypes);
                        System.out.println("Input links: ");
                        for (int[] link : data.links) {
                            System.out.print(Arrays.toString(link));
                        }
                        System.out.println();
                        System.out.println("Ans cablingCost: " + data.cablingCost);
                        System.out.println("Your cablingCost: " + user_cc);
                        System.out.println("Ans serverToRouter: " + data.serverToRouter);
                        System.out.println("Your serverToRouter: " + user_sr);
                        System.out.println("Ans mostPopularPrinter: " + data.mostPopularPrinter);
                        System.out.println("Your mostPopularPrinter: " + user_mpp);
                        System.out.println();
                    }
                }
            }
            System.out.println("Score: " + num_ac + "/10");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JsonIOException | JsonSyntaxException e) {
            e.printStackTrace();
        }
    }
}