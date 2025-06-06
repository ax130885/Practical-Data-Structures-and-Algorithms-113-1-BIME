import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.UF;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

class OutputFormat {
    // 設備類型總共有 4 種: Server, Router, Printer, Computer，透過查表知道每台設備是甚麼類型，例如:
    // Map<Integer, String> deviceTypes = Map.of(
    // 0, "Router",
    // 1, "Server",
    // 2, "Printer",
    // 3, "Printer",
    // 4, "Computer",
    // 5, "Computer",
    // 6, "Computer"
    // );
    Map<Integer, String> deviceTypes;

    // 紀錄兩個設備(兩個頂點)與連線的長度(邊的權重)
    List<int[]> links; // {vertex1, vertex2, length}
    int cablingCost; // 總電線長度
    int serverToRouter; // 所有伺服器到路由器的電線總長
    int mostPopularPrinter; // 連接最多Computer的印表機ID
}

class TestCase {
    int Case;
    int score;
    ArrayList<OutputFormat> data;
}

class KruskalMST {
    private Queue<Edge> mst = new LinkedList<Edge>(); // 用於儲存 MST 的邊

    public KruskalMST(EdgeWeightedGraph G) {
        // 1. 將輸入圖的所有邊放入最小優先級佇列，會自己根據邊的權重排序
        MinPQ<Edge> pq = new MinPQ<Edge>();
        for (Edge e : G.edges()) {
            pq.insert(e);
        }
        // 2. 初始化 Union-find
        UF uf = new UF(G.V()); // 建立 頂點個數 V 的 Union-find 結構，初始每個頂點都是獨立的集合

        // 3. 貪心選擇邊 & 4. 終止條件
        // 當優先級佇列不為空且 MST 的邊數未達到 V-1 時繼續循環
        while (!pq.isEmpty() && mst.size() < G.V() - 1) {

            // 取得最小邊與對應的兩個頂點
            Edge e = pq.delMin(); // 取出當前權重最小的邊
            int v = e.either(); // 獲取邊的一個頂點
            int w = e.other(v); // 獲取邊的另一個頂點

            // 如果兩個頂點屬於不同的Union才合併並且加入MST（避免環）
            // 如果不滿足則跳過
            if (uf.find(v) != uf.find(w)) { // find() 用於找出所屬集合的root
                uf.union(v, w); // 合併兩個Union
                mst.add(e); // 將邊加入 MST
            }
        }
    }

    // 返回 MST 的所有邊
    public Iterable<Edge> edges() {
        return mst;
    }
}

class InnovationStudioCabling {

    // 紀錄輸入的種類表
    private Map<Integer, String> deviceTypes; // 設備ID與對應種類表
    // 記錄所有設備的最短接線方式(MST)
    private KruskalMST mst;
    // 將MST轉換為鄰接表
    private Map<Integer, List<int[]>> adjacencyList = new HashMap<>();
    // 紀錄所有printer與computers的idx
    private Set<Integer> printers; // 所有印表機的索引集合
    private Set<Integer> computers; // 所有學生電腦的索引集合
    private int routerIndex; // 路由器的索引 只會有一個
    private int serverIndex; // 伺服器的索引 只會有一個

    // 構造函數
    public InnovationStudioCabling(Map<Integer, String> deviceTypes, List<int[]> links) { // 設備ID與對應種類表, 設備的連接情況與權重
        this.deviceTypes = new HashMap<>(deviceTypes);
        this.printers = new HashSet<>();
        this.computers = new HashSet<>();

        // 識別各類設備
        for (Map.Entry<Integer, String> entry : deviceTypes.entrySet()) {
            int index = entry.getKey();
            String type = entry.getValue();
            if (type.equals("Printer")) {
                printers.add(index); // 加入印表機集合
            } else if (type.equals("Computer")) {
                computers.add(index); // 加入電腦集合
            } else if (type.equals("Router")) {
                routerIndex = index; // 記錄路由器索引
            } else if (type.equals("Server")) {
                serverIndex = index; // 記錄伺服器索引
            }
        }

        // 建立邊權重圖
        EdgeWeightedGraph G = new EdgeWeightedGraph(deviceTypes.size());
        for (int[] link : links) { // 遍歷所有連接
            int v = link[0]; // 第一個設備的索引
            int w = link[1]; // 第二個設備的索引
            int length = link[2]; // 連接的長度
            G.addEdge(new Edge(v, w, length)); // 添加邊到圖中
        }

        // 使用Kruskal演算法計算最小生成樹
        this.mst = new KruskalMST(G);

        // 根據MST構建鄰接表
        adjacencyList = new HashMap<>(); // 用dictionary來存儲鄰接表
        for (Edge edge : mst.edges()) {
            int v = edge.either();
            int w = edge.other(v);
            int weight = (int) edge.weight();

            // V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction)
            // 第一個參數是 key，第二個參數是 mappingFunction。
            // ? super K 表示 mappingFunction 的參數類型可以是 K 或其父類型。
            // ? extends V 表示 mappingFunction 的返回類型可以是 V 或其子類型。
            // 如果 key 不存在，則使用 mappingFunction 來建立一個新list。
            // 如果 key 存在，直接在原來的list後面添加新的節點。
            adjacencyList.computeIfAbsent(v, k -> new ArrayList<>()).add(new int[] { w, weight });
            adjacencyList.computeIfAbsent(w, k -> new ArrayList<>()).add(new int[] { v, weight });
        }

    }

    /**
     * 計算總佈線長度
     * 即MST中所有邊的權重總和
     */
    public int cablingCost() {
        int cost = 0;
        for (Edge edge : mst.edges()) {
            cost += (int) edge.weight();
        }
        return cost;
    }

    /**
     * 計算MST當中，伺服器到路由器的電線長度(只會各有一個)
     */
    public int serverToRouter() {
        // BFS or DFS 尋找 serverIndex 到 routerIndex 的路徑長度
        Set<Integer> visited = new HashSet<>();
        return dfsServerToRouter(serverIndex, -1, routerIndex, 0, visited);
    }

    /**
     * 用 DFS 遞迴搜尋 MST 鄰接表，找出從 current（server）到 target（router）的唯一路徑長度。
     * 
     * @param current 目前所在節點（初始為 serverIndex）
     * @param parent  來的上一個節點（防止回頭，初始為 -1）
     * @param target  目標節點（routerIndex）
     * @param accLen  目前累積的路徑長度（初始為 0）
     * @param visited 已經拜訪過的節點集合（避免重複走訪）
     * @return 如果找到路徑，回傳累積長度；找不到回傳 -1
     */
    private int dfsServerToRouter(int current, int parent, int target, int accLen, Set<Integer> visited) {
        // 如果目前節點就是目標節點，代表已經找到 server 到 router 的路徑，回傳累積長度
        if (current == target)
            return accLen;

        // 標記目前節點已拜訪，避免重複走訪
        visited.add(current);

        // 遍歷目前節點在 MST 鄰接表中的所有鄰居
        for (int[] neighbor : adjacencyList.getOrDefault(current, Collections.emptyList())) {
            int next = neighbor[0]; // 鄰居節點編號
            int weight = neighbor[1]; // 這條邊的權重（電線長度）

            // 如果鄰居是父節點，代表是回頭路，跳過
            if (next == parent)
                continue;

            // 如果鄰居已經拜訪過，也跳過（避免環）
            if (visited.contains(next))
                continue;

            // 遞迴往下一個鄰居走，累積長度
            int res = dfsServerToRouter(next, current, target, accLen + weight, visited);

            // 如果在這條路徑下有找到目標，直接回傳結果
            if (res != -1)
                return res;
        }

        // 所有鄰居都走過還是沒找到目標，回傳 -1
        return -1;
    }

    /**
     * 找出最忙碌的印表機
     * 1. 以所有印表機為起點，對 MST 鄰接表執行多源 Dijkstra，
     * 一次性計算每個節點到最近印表機的最短距離與是哪一台印表機（距離相同時選 index 較小者）。
     * 2. 統計每台印表機服務的電腦數量。
     * 3. 返回服務最多電腦的印表機索引（平手時返回較小索引）。
     */
    // 舉例: 假設有三台印表機（A、B、C），每台都同時出發，像水波一樣往外擴散。
    // 每個節點只會被最短距離、index 較小的印表機「染色」一次。
    public int mostPopularPrinter() {
        // 如果沒有印表機，直接回傳 -1
        if (printers.isEmpty())
            return -1;
        // 如果沒有電腦，回傳 index 最小的印表機
        if (computers.isEmpty())
            return printers.stream().min(Integer::compare).orElse(-1);

        // 初始化變數
        Map<Integer, Integer> nearestPrinter = new HashMap<>(); // 記錄每個節點最近的印表機 index
        Map<Integer, Integer> minDist = new HashMap<>(); // 記錄每個節點到最近印表機的距離

        // 初始化pq用來記錄多源擴散的過程
        // pq的每個節點(a)包含三個參數
        // a[0]=目前機器的編號, a[1]=目前機器到起始印表機的累積距離（dist）, a[2]=這條路徑所屬的印表機
        // a, b -> 為 lambda 表達式，用來定義優先隊列的排序規則。
        // 根據距離排序，距離相同時印表機 index 較小的優先
        PriorityQueue<int[]> pq = new PriorityQueue<>(
                (a, b) -> a[1] != b[1] ? Integer.compare(a[1], b[1]) : Integer.compare(a[2], b[2]));

        // 初始化所有節點的距離為無限大
        for (int node : deviceTypes.keySet()) { // Map.keySet() 獲取所有設備的索引
            minDist.put(node, Integer.MAX_VALUE);
        }
        // 將所有印表機作為起點加入優先隊列，距離設為0
        for (int printer : printers) {
            pq.offer(new int[] { printer, 0, printer }); // 目前節點, 距離, 屬於哪條印表機的擴散
            minDist.put(printer, 0); // 印表機到自己的距離是0
            nearestPrinter.put(printer, printer); // 印表機到自己的最近印表機是自己
        }

        // 多源 Dijkstra 主迴圈 (同時用多個起點的 Dijkstra)
        while (!pq.isEmpty()) {
            int[] cur = pq.poll(); // 取出目前距離最小的節點
            int node = cur[0], dist = cur[1], printerIdx = cur[2];
            // 如果目前距離比已知最短距離還大，跳過
            if (dist > minDist.get(node))
                continue;
            // 如果距離一樣但印表機 index 較大，也跳過（確保 index 較小的優先）
            if (dist == minDist.get(node) && printerIdx > nearestPrinter.get(node))
                continue;
            // 更新目前節點最近的印表機
            nearestPrinter.put(node, printerIdx);
            // 遍歷所有鄰居，嘗試更新鄰居的最短距離與最近印表機
            for (int[] neighbor : adjacencyList.getOrDefault(node, Collections.emptyList())) {
                int next = neighbor[0], weight = neighbor[1];
                int newDist = dist + weight;
                // 如果新距離更短，或距離一樣但印表機 index 較小，則更新
                if (newDist < minDist.get(next) || (newDist == minDist.get(next)
                        && printerIdx < nearestPrinter.getOrDefault(next, Integer.MAX_VALUE))) {
                    minDist.put(next, newDist);
                    nearestPrinter.put(next, printerIdx);
                    pq.offer(new int[] { next, newDist, printerIdx });
                }
            }
        }

        // 統計每台印表機服務的電腦數量
        Map<Integer, Integer> printerCounts = new HashMap<>();
        for (int computer : computers) {
            int p = nearestPrinter.get(computer); // 這台電腦最近的印表機
            printerCounts.put(p, printerCounts.getOrDefault(p, 0) + 1);
        }

        // 找出服務最多電腦的印表機（平手時選 index 較小者）
        return printerCounts.entrySet().stream() // entrySet() 取得整個Map的集合, stream() 轉為流才能用後面的幾種method。
                // 先依照服務電腦數量由大到小排序，若數量相同則印表機 index 較小的排前面
                .sorted((a, b) -> { // -> 是 lambda 表達式，用於把{}的內容傳給sotred()的比較器。
                    if (b.getValue().equals(a.getValue())) { // 如果兩個印表機服務的電腦數量相同
                        // 返回 index 較小的印表機
                        return a.getKey().compareTo(b.getKey()); // getKey() 取得印表機 index
                    }
                    // 如果印表機服務的電腦數量不同，返回服務比較多台電腦的印表機。
                    return b.getValue().compareTo(a.getValue());
                })
                // 取排序後的第一個（最多電腦的印表機）
                .findFirst()
                // 只取印表機 index
                .map(Map.Entry::getKey)
                // 如果沒有任何印表機服務電腦，則回傳 index 最小的印表機（題目規定）
                .orElse(printers.stream().min(Integer::compare).orElse(-1));
    }

    // 最熱門印表機的優化方向: 用「超級節點（supernode）」連接所有印表機

    // 在 MST 上額外假想一個「超級節點 S」，並且從 S 到每一臺原本的印表機 p_i 連一條權重為 0 的邊。如此一來，只要從 S 執行一次
    // Dijkstra，就能同時找到每個節點到最近印表機的最短路徑和來源印表機。
    // 具體流程：
    // 在原本的鄰接表 adj 上，把 S 視為額外的唯一源頭。
    // 對每臺印表機 p，在 adj[S] 加入一條邊 (S → p, 0)；同時在 adj[p] 加入 (p → S, 0)（雖然不需要往回，但保持一致性）。
    // 初始化資料結構

    // 節點總數為 N，若把 S 設編號為 N，則陣列大小為 N+1。
    // 準備兩個長度為 N+1 的陣列：
    // dist[v]：節點 v（包括 S）到來源為 S 的最短距離；初始都設為很大值（∞）。
    // nearestPrinter[v]：節點 v 最近的真正印表機編號；初始都設為 -1。
    // 設 dist[S] = 0 並 nearestPrinter[S] = -1（S 本身不是印表機）。
    // 使用一個 Min Priority Queue 來處理 Dijkstra 中的節點取出，排序邏輯如下：
    // 先比較「dist 值」小的先取出；
    // 若 dist 相同，再比較「來源印表機編號」小的先取出。
    // 開始時，只把 (dist[S] = 0, printer = -1, node = S) 推進佇列。
    // 當優先佇列不為空時，重複以下步驟：

    // 取出 (d, p, u)

    // d：從超級節點 S 經由印表機 p 抵達節點 u 的最短距離。
    // p：來源印表機編號。
    // u：目前節點。
    // 佇列按「距離小、來源編號小」排列，確保每次取出皆是最新且最優。
    // 檢查是否過時

    // 若 d > dist[u] 或 p != nearestPrinter[u]，代表已有更短或更新的來源印表機，可跳過。
    // 更新鄰居 (u → v, w)

    // 若 u == S（超級節點）：
    // 這條邊代表「S→v」直接連到印表機 v，邊長為 0，
    // 設 nd = 0、np = v，比較 nd 與 dist[v]：
    // 若 0 < dist[v]，更新 dist[v]=0、nearestPrinter[v]=v，並 enqueue (0, v, v)；
    // 若 0 == dist[v] 且 v < nearestPrinter[v]，也更新 nearestPrinter[v]=v，enqueue (0, v,
    // v)。
    // 若 u != S：表示目前 u 已由某印表機 p 到達，且 d = dist[u]。
    // 對每條 (u → v, w) 計算 nd = d + w、np = p，再比較：
    // 若 nd < dist[v]，更新 dist[v]=nd、nearestPrinter[v]=p，enqueue (nd, p, v)；
    // 若 nd == dist[v] 且 p < nearestPrinter[v]，更新 nearestPrinter[v]=p，enqueue (nd,
    // p, v)。
    // 重複直到佇列空

    // 最終 dist[v] 記錄每個節點到最近印表機的距離，nearestPrinter[v] 記下對應印表機。
    // 只要查 nearestPrinter[c]，即可得知電腦節點 c 要使用哪臺印表機。
    // 統計印表機使用量

    // Dijkstra 完成後，對每個原本的「Computer」節點 c，其 nearestPrinter[c] 就是該電腦距離最近且編號最小的印表機。
    // 用一個 Map 或陣列，把每臺印表機的使用次數初始化為 0，遍歷所有電腦 c：
    // 取得 p = nearestPrinter[c]，將該印表機的計數 count[p]++。
    // 最後在所有印表機中，找出使用次數最多的那一臺；若多臺並列則選擇編號最小者。
    // 回傳計數最高（若平手則編號最小）的印表機編號，作為最熱門印表機。

}

class test_InnovationStudioCabling {

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