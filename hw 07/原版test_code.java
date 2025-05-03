import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class GalacticClusteringTest {
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
