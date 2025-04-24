import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

class ZombieSimulation {
    private int currentDay = 1;

    public ZombieSimulation(int[] initialCitizens) {
        // The initial number of citizens in each city is set here.
    }

    public void zombieAttackPlan(int city, int day) {
        // A zombie horde attacks the specified city on the given day.
    }

    public void TravelPlan(int numberOfTraveller, int fromCity, int toCity, int departureDay, int arrivalDay) {
        // Record the travel plans between cities.
        // Travelers may carry the overrun condition from one city to another.
    }

    public int CityWithTheMostZombies(int day) {
        // Return the index of the city with the highest number of overrun citizens.
        // If more than one city has the same number, return the one with the larger
        // index.
        // Return -1 if no city is overrun.
        return -1;
    }

    public static void main(String[] args) {
        ZombieSimulation sim = new ZombieSimulation(new int[] { 10, 100, 15, 25, 10, 13 });

        sim.zombieAttackPlan(0, 1);
        sim.zombieAttackPlan(4, 3);
        sim.TravelPlan(3, 0, 3, 3, 4);
        sim.TravelPlan(3, 4, 0, 3, 4);

        System.out.println(sim.CityWithTheMostZombies(2));
        // Expected output = 0

        sim.zombieAttackPlan(5, 5);
        sim.TravelPlan(1, 5, 0, 5, 6);

        System.out.println(sim.CityWithTheMostZombies(4));
        // Expected output = 3
        System.out.println(sim.CityWithTheMostZombies(8));
        // Expected output = 5

        // For clarity, here is an example of how the simulation might evolve:
        // Day 1: Cities: {10, 100, 15, 25, 10, 13}
        // Overrun status: {1, 0, 0, 0, 0, 0}
        // Day 2: Cities: {10, 100, 15, 25, 10, 13}
        // Overrun status: {1, 0, 0, 0, 0, 0}
        // Day 3: Cities: {7, 100, 15, 25, 7, 13} // Citizens deducted due to clearance
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
    }

    // public static void main(String[] args){
    // new Test(args);
    // }
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
