import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.*;

class Cities {
    int citizens; // 城市人口數
    int overrunDay; // 被感染的日期
    int recoveryDay; // 恢復的日期

    public Cities(int citizens, int overrunDay, int recoveryDay) {
        this.citizens = citizens;
        this.overrunDay = overrunDay;
        this.recoveryDay = recoveryDay;
    }

    public boolean isHealthy(int day) {
        if (day >= recoveryDay) {
            return true; // 恢復
        } else if (overrunDay == 0) {
            return true; // 未被感染
        } else {
            return false; // 被感染
        }
    }
}

abstract class Event implements Comparable<Event> {
    int day; // 事件發生的日期
    int type; // 事件類型（殭屍攻擊或旅行）

    int attCity; // 被攻擊的城市索引 （僅適用於攻擊事件）

    int departureDay; // 出發日期（僅適用於旅行事件）
    int arrivalDay; // 抵達日期（僅適用於旅行事件）
    int fromCity; // 出發城市索引（僅適用於旅行事件）
    int toCity; // 目的城市索引（僅適用於旅行事件）
    int numberOfTraveller; // 旅行者的人數（僅適用於旅行事件)
    boolean fromIsInfected; // 出發時旅行者是否已經感染 (僅適用於旅行事件)

    // 定義 Constants
    // 事件類型
    public static final int ATTACK_EVENT = 1; // 攻擊事件:1
    public static final int TRAVEL_START_EVENT = 2; // 旅行出發:2
    public static final int TRAVEL_ARRIVAL_EVENT = 3; // 旅行抵達:3
    // public static final int RECOVERY_EVENT = 4; // 恢復事件:4

    // 攻擊事件
    public Event(int day, int type, int attCity) {
        this.day = day;
        this.type = type;
        this.attCity = attCity;
    }

    // 旅行事件 (出發+抵達)
    public Event(int departureDay, int arrivalDay, int type, int fromCity, int toCity, int numberOfTraveller) {
        this.departureDay = departureDay;
        this.arrivalDay = arrivalDay;
        this.type = type;
        this.fromCity = fromCity;
        this.toCity = toCity;
        this.numberOfTraveller = numberOfTraveller;
    }

    // 抽象方法 事件具體要做的事情
    public abstract void process();

    @Override
    // 比較事件順序: 先比日期，如果同一天的話，則按事件類型排序
    public int compareTo(Event other) {
        if (this.day != other.day) {
            return Integer.compare(this.day, other.day);
        }
        // 確保相同日期的事件按正確順序處理
        // 攻擊 > 旅行出發 > 旅行到達
        return Integer.compare(this.type, other.type);
    }
}

class ZombieSimulation {
    private int numCities; // 城市數量
    private Cities[] cities; // 城市列表
    private PriorityQueue<Event> eventQueue; // PriorityQueue<class> 的class一定要實現 implements
                                             // Comparable<Event> 接口。 可以使用add()方法添加事件 使用take()方法獲取事件

    // 以下兩個是最終優化遍歷速度用 沒實際意義
    // 答案的快取，如果已經計算過，直接返回結果 保存天數和最多殭屍的程式
    private Map<Integer, Integer> zombieCache = new HashMap<>();
    // 保存曾經感染過的城市索引
    private Set<Integer> infectedCities = new HashSet<>();

    /**
     * 建構子：初始化模擬
     *
     * @param initialCitizens 每個城市的初始人口數
     */
    public ZombieSimulation(int[] initialCitizens) {
        numCities = initialCitizens.length;
        cities = new Cities[numCities]; // 初始化城市數量
        for (int i = 0; i < numCities; i++) {
            cities[i] = new Cities(initialCitizens[i], 0, 0); // 初始化每個城市(索引, 人口數, 被感染的日期, 恢復的日期)
        }
        eventQueue = new PriorityQueue<Event>();
    }

    // 攻擊事件
    private class AttackEvent extends Event {
        public AttackEvent(int day, int type, int attCity) {
            super(day, type, attCity); // 呼叫父類別的建構子
        }

        @Override
        public void process() {
            // 如果已經處於被感染狀態，則不進行處理
            if (cities[attCity].isHealthy(day) == false) {
                return; // 已經被感染，無需再次處理
            }

            cities[attCity].overrunDay = day; // 設定被感染的日期
            cities[attCity].recoveryDay = day + 4; // 設定恢復日期
            infectedCities.add(attCity); // 添加到感染城市集合
        }

    }

    // 旅行出發事件
    private class TravelStartEvent extends Event {
        TravelArrivalEvent linkedArrivalEvent; // 旅行抵達事件的鏈接

        public TravelStartEvent(int departureDay, int arrivalDay, int type, int fromCity, int toCity,
                int numberOfTraveller,
                TravelArrivalEvent linkedArrivalEvent) {
            super(departureDay, arrivalDay, type, fromCity, toCity, numberOfTraveller); // 呼叫父類別的建構子
            this.day = departureDay; // 設定出發日期
            this.linkedArrivalEvent = linkedArrivalEvent;
        }

        @Override
        public void process() {
            cities[fromCity].citizens -= numberOfTraveller; // 減少出發城市的人口數

            // 計算出發時是否感染
            this.fromIsInfected = !cities[fromCity].isHealthy(day);
            // 計算抵達時是否痊癒 如果提早抵達則還沒恢復 如果晚抵達則恢復
            boolean isRecovered = (arrivalDay >= cities[fromCity].recoveryDay);

            // 如果出發城市已經被感染，則將旅行者的感染狀態設置為true
            if (this.fromIsInfected && !isRecovered) {
                linkedArrivalEvent.fromIsInfected = true;
            } else {
                linkedArrivalEvent.fromIsInfected = false;
            }
        }
    }

    // 旅行抵達事件
    private class TravelArrivalEvent extends Event {

        public TravelArrivalEvent(int departureDay, int arrivalDay, int type, int fromCity, int toCity,
                int numberOfTraveller) {
            super(departureDay, arrivalDay, type, fromCity, toCity, numberOfTraveller); // 呼叫父類別的建構子
            this.day = arrivalDay; // 設定抵達日期
        }

        @Override
        public void process() {
            cities[toCity].citizens += numberOfTraveller; // 增加目的城市的人口數

            // 檢查出發時旅行者是否已經感染 並且抵達時尚未痊癒
            if (this.fromIsInfected) {

                // 檢查目的地城市是否已感染
                // 如果已感染，恢復時間 +3 (最多只能是感染日期 +7)
                if (cities[toCity].isHealthy(day) == false) {
                    // a = b > c ? a : b; // 如果a大於b，則返回a，否則返回b
                    int upperBound = (cities[toCity].recoveryDay + 3 >= cities[toCity].overrunDay + 7)
                            ? cities[toCity].overrunDay + 7 // 如果超過上限，則設為上限
                            : cities[toCity].recoveryDay + 3; // 如果還沒超過上限就 +3
                    cities[toCity].recoveryDay = upperBound; // 設定恢復日期
                }
                // 如果目的地城市尚未感染，直接感染抵達城市
                else {
                    cities[toCity].overrunDay = day; // 設定被感染的日期
                    cities[toCity].recoveryDay = day + 4; // 設定恢復日期
                    infectedCities.add(toCity); // 添加到感染城市集合
                }
            }

        }
    }

    /**
     * 模擬殭屍在指定日期攻擊指定城市的計畫。
     *
     * @param city 被攻擊的城市索引。
     * @param day  攻擊發生的日期。
     */
    public void zombieAttackPlan(int city, int day) {
        AttackEvent attackEvent = new AttackEvent(day, Event.ATTACK_EVENT, city);
        eventQueue.add(attackEvent); // 將攻擊事件加入事件隊列
    }

    /**
     * 記錄城市之間的旅行計畫，旅行者可能會將殭屍感染狀態從一個城市帶到另一個城市。
     *
     * @param numberOfTraveller 旅行者的人數。
     * @param fromCity          出發城市的索引。
     * @param toCity            目的城市的索引。
     * @param departureDay      旅行開始的日期。
     * @param arrivalDay        旅行結束的日期。
     */
    public void TravelPlan(int numberOfTraveller, int fromCity, int toCity, int departureDay, int arrivalDay) {

        // 建立抵達事件
        TravelArrivalEvent travelArrivalEvent = new TravelArrivalEvent(departureDay, arrivalDay,
                Event.TRAVEL_ARRIVAL_EVENT, fromCity,
                toCity, numberOfTraveller);
        // 建立出發事件
        TravelStartEvent travelStartEvent = new TravelStartEvent(departureDay, arrivalDay, Event.TRAVEL_START_EVENT,
                fromCity,
                toCity, numberOfTraveller, travelArrivalEvent);

        // 將旅行出發事件加入事件隊列
        eventQueue.add(travelStartEvent);
        // 將旅行抵達事件加入事件隊列
        eventQueue.add(travelArrivalEvent);

    }

    /**
     * 返回在指定日期殭屍感染人數最多的城市索引。
     *
     * @param day 查詢的日期。
     * @return 殭屍感染人數最多的城市索引。
     *         如果多個城市有相同的感染人數，返回索引較大的城市。
     *         如果沒有城市被感染，返回 -1。
     */
    public int CityWithTheMostZombies(int day) {
        // 返回在指定日期殭屍感染人數最多的城市索引。
        // 如果多個城市有相同的感染人數，返回索引較大的城市。

        // 如果已計算過，直接返回結果
        if (zombieCache.containsKey(day)) {
            return zombieCache.get(day);
        }

        // 執行所有早於或等於查詢日期的事件
        while (!eventQueue.isEmpty() && eventQueue.peek().day <= day) {
            Event event = eventQueue.poll(); // 取出事件
            event.process(); // 處理事件

            // // 印出 event queue 的執行順序 和 event 的參數
            // System.out.println("Day " + event.day
            // + "-> Event Type: " + event.type + ", " + (event.type == Event.ATTACK_EVENT ?
            // "Attack" : "Travel")
            // + " from city " + (event.type == Event.ATTACK_EVENT ? event.attCity :
            // event.fromCity)
            // + (event.type == Event.ATTACK_EVENT ? "" : " to city " + event.toCity)
            // + ", number of traveller: " + event.numberOfTraveller
            // + (event.type == Event.TRAVEL_ARRIVAL_EVENT || event.type ==
            // Event.TRAVEL_START_EVENT
            // ? ", fromIsInfected: " +
            // event.fromIsInfected
            // : ""));
        }

        // // 檢查所有城市的狀態
        // System.out.println("=====================================");
        // for (int j = 0; j < this.numCities; j++) {
        // System.out.println("Day " + day + ", City " + j
        // + "-> 人數: " + cities[j].citizens
        // + ", 是否健康: " + cities[j].isHealthy(day)
        // + ", 感染日期: " + cities[j].overrunDay
        // + ", 恢復日期: " + cities[j].recoveryDay);
        // }
        // System.out.println("=====================================");

        // 初始化 沒有城市被感染，返回 -1。
        int max = -1; // 記錄最多殭屍的城市索引
        int maxCitizens = -1; // 記錄最多殭屍的城市人口數

        // 只遍歷曾經感染過的城市，而不是所有城市
        for (int cityIndex : infectedCities) {
            // 檢查當前城市在查詢日期是否被感染
            if (!cities[cityIndex].isHealthy(day)) {
                // 比較人口數
                if (cities[cityIndex].citizens > maxCitizens ||
                        (cities[cityIndex].citizens == maxCitizens && cityIndex > max)) {
                    max = cityIndex;
                    maxCitizens = cities[cityIndex].citizens;
                }
            }
        }

        zombieCache.put(day, max); // 將結果存入快取
        return max;
    }

    public static void main(String[] args) {
        ZombieSimulation sim = new ZombieSimulation(new int[] { 10, 100, 15, 25, 10,
                13 });

        sim.zombieAttackPlan(0, 1);
        sim.zombieAttackPlan(4, 3);
        sim.TravelPlan(3, 0, 3, 3, 4);
        sim.TravelPlan(3, 4, 0, 3, 4);

        System.out.println(sim.CityWithTheMostZombies(1)); // Expected output = 0
        System.out.println(sim.CityWithTheMostZombies(2)); // Expected output = 0

        sim.zombieAttackPlan(5, 5);
        sim.TravelPlan(1, 5, 0, 5, 6);

        System.out.println(sim.CityWithTheMostZombies(3)); // Expected output = 4
        System.out.println(sim.CityWithTheMostZombies(4)); // Expected output = 3
        System.out.println(sim.CityWithTheMostZombies(5)); // Expected output = 3
        System.out.println(sim.CityWithTheMostZombies(6)); // Expected output = 3
        System.out.println(sim.CityWithTheMostZombies(7)); // Expected output = 3
        System.out.println(sim.CityWithTheMostZombies(8)); // Expected output = 5

        // For clarity, here is an example of how the simulation might evolve:
        // Day 1: Cities: {10, 100, 15, 25, 10, 13}
        // Overrun status: {1, 0, 0, 0, 0, 0}
        // Day 2: Cities: {10, 100, 15, 25, 10, 13}
        // Overrun status: {1, 0, 0, 0, 0, 0}
        // Day 3: Cities: {7, 100, 15, 25, 7, 13} // Citizens deducted due to
        // clearance
        // operations
        // Overrun status: {1, 0, 0, 0, 1, 0}
        // Day 4: Cities: {10, 100, 15, 28, 7, 13}
        // Overrun status: {1, 0, 0, 1, 1, 0}
        // Day 5: Cities: {10, 100, 15, 28, 7, 12}
        // Overrun status: {1, 0, 0, 1, 1, 1}
        // Day 6: Cities: {11, 100, 15, 28, 7, 12}
        // Overrun status: {1, 0, 0, 1, 1, 1}
        // Day 7: Cities: {11, 100, 15, 28, 7, 12}
        // Overrun status: {1, 0, 0, 1, 0, 1}
        // Day 8: Cities: {11, 100, 15, 28, 7, 12}
        // Overrun status: {0, 0, 0, 0, 0, 1}

        new Test();
    }

}

class Test {
    public Test() {
        ZombieSimulation g;
        // args[0] = "testdata.json";
        // try (FileReader reader = new FileReader(args[0])) {
        try (FileReader reader = new FileReader("testdata.json")) {
            // Parse the JSON file using Gson
            JsonElement fileElement = JsonParser.parseReader(reader);
            JsonArray all = fileElement.getAsJsonArray();

            int waSize = 0;
            int count = 0;
            for (JsonElement caseInListElement : all) {
                JsonArray a = caseInListElement.getAsJsonArray();
                // Board Setup
                JsonObject argsSetting = a.get(0).getAsJsonObject();

                JsonArray argSettingArr = argsSetting.get("args").getAsJsonArray();
                int[] citySetting = new int[argSettingArr.size()];
                for (int i = 0; i < argSettingArr.size(); i++) {
                    citySetting[i] = argSettingArr.get(i).getAsInt();
                }
                g = new ZombieSimulation(citySetting);

                // Process remaining elements starting from index 1
                for (int i = 1; i < a.size(); i++) {
                    JsonObject person = a.get(i).getAsJsonObject();
                    String func = person.get("func").getAsString();
                    JsonArray arg = person.get("args").getAsJsonArray();

                    switch (func) {
                        case "zombiePlan":
                            g.zombieAttackPlan(arg.get(0).getAsInt(), arg.get(1).getAsInt());
                            break;
                        case "TravelPlan":
                            g.TravelPlan(arg.get(0).getAsInt(),
                                    arg.get(1).getAsInt(),
                                    arg.get(2).getAsInt(),
                                    arg.get(3).getAsInt(),
                                    arg.get(4).getAsInt());
                            break;
                        case "CityMax":
                            count++;
                            int ans_sol = g.CityWithTheMostZombies(arg.get(0).getAsInt());
                            int ans = person.get("answer").getAsInt();
                            if (ans_sol == ans) {
                                System.out.println(count + ": AC: " + ans_sol);
                            } else {
                                waSize++;
                                System.out.println(count + ": WA cal: " + ans_sol + ", ans: " + ans);
                            }
                            break;
                        default:
                            System.out.println("Unknown function: " + func);
                            break;
                    }
                }
            }
            System.out.println("Score: " + (count - waSize) + " / " + count);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) { // For exceptions such as JsonSyntaxException
            e.printStackTrace();
        }
    }
}
