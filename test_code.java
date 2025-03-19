import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

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
                    JsonArray arg_lvl = person.getAsJsonArray("power");
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

class Tower {
    int Power;
    int Range;
    int Index;

    Tower(int _power, int _range, int i) {
        Power = _power;
        Range = _range;
        Index = i;
    }
}

class DefenseSystem {

    // version 1 for loop 暴力解
    // 暴力解 複雜度最差是O(n^2)
    public int[] result(int[] levels, int[] ranges) {
        // Given the traits of each member levels,nd output
        // the leftmost and rightmost index of member
        // can be attacked by each member.

        // complete the code by returning an int[]
        // flatten the results since we only need an 1-dimentional array.

        int[] result = new int[2 * levels.length];

        // 遍歷每個塔
        for (int i = 0; i < levels.length; i++) {

            // 初始化左右攻擊範圍 最短是自己
            result[2 * i] = i;
            result[2 * i + 1] = i;

            // 計算左右攻擊範圍
            int left = -1;
            int right = -1;

            left = (i - ranges[i] < 0) ? 0 : i - ranges[i];
            right = ((i + ranges[i]) > (levels.length - 1)) ? levels.length - 1
                    : i +
                            ranges[i];

            // 往左檢查
            for (int j = i - 1; j >= left; j--) {

                if (levels[j] < levels[i]) {
                    result[2 * i] = j;
                } else {
                    break;
                }
            }

            // 往右檢查
            for (int j = i + 1; j <= right; j++) {

                if (levels[j] < levels[i]) {
                    result[2 * i + 1] = j;
                } else {
                    break;
                }

            }

        }

        return result;
    }

    public static void main(String[] args) {
        DefenseSystem sol = new DefenseSystem();
        System.out.println(Arrays.toString(
                sol.result(new int[] { 11, 13, 11, 7, 15 },
                        new int[] { 1, 8, 1, 7, 2 })));
        // Output: [0, 0, 0, 3, 2, 3, 3, 3, 2, 4]
        // => [a0, b0, a1, b1, a2, b2, a3, b3, a4, b4]
    }
}