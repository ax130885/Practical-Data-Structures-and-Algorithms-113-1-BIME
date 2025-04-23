import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class ZombieSimulation {

    public ZombieSimulation(int[] Num_Of_Citizen) {
        // Initialization code here
    }

    public int CityWithTheMostZombies(int date){
        // Implementation here
        return 0;
    }

    public void zombieAttackPlan(int city, int date){
        // Implementation here
    }

    public void TravelPlan(int NumberOfTraveller, int FromCity, int ToCity, int DateOfDeparture, int DateOfArrival){
        // Implementation here
    }

    public static void main(String[] args){
        new Test(args);
    }
}

class Test {
    public Test(String[] args) {
        ZombieSimulation g;
        try (FileReader reader = new FileReader(args[0])) {
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
                                System.out.println(count + ": AC");
                            } else {
                                waSize++;
                                System.out.println(count + ": WA");
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
