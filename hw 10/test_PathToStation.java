
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.*;

class OutputFormat{
    int[][] map;
    int[] init_pos;
    int[] target_pos;
    int answer;
}

class test{
    static boolean are4Connected(int[] p1, int[] p2) {
        return (Math.abs(p1[0] - p2[0]) == 1 && p1[1] == p2[1]) || (Math.abs(p1[1] - p2[1]) == 1 && p1[0] == p2[0]);
    }
    static boolean isShortestPath(int[][] map, int path_len, List<int[]> path)
    {
        // check if the path is valid, (if the two node is actually neighbour, and if the path is not wall)
        int path_len2 = 0;
        for(int i = 1; i<path.size(); ++i){
            int[] pos_prev = path.get(i-1);
            int[] pos_now = path.get(i);
            int type = map[pos_now[0]][pos_now[1]];
            if(!are4Connected(pos_prev,pos_now) || type == 0) //type == 0 means that it is a cliff.
                return false;
            path_len2 += (type == 3) ? 5 : 1;
        }
        return (path_len == path_len2);
    }
    public static void main(String[] args)
    {
        Gson gson = new Gson();
        OutputFormat[] datas;
        OutputFormat data;
        int num_ac = 0;

        List<int[]> SHP;
        PathToStation sol;

        try {
            datas = gson.fromJson(new FileReader(args[0]), OutputFormat[].class);
            for(int i = 0; i<datas.length;++i)
            {
                data = datas[i];
                sol = new PathToStation(data.map, data.init_pos, data.target_pos);
                SHP = sol.shortest_path();

                System.out.print("Sample"+i+": ");
                if(sol.shortest_path_len() != data.answer)
                {
                    System.out.println("WA: incorrect path length");
                    System.out.println("Test_ans:  " + data.answer);
                    System.out.println("User_ans:  " + sol.shortest_path_len());
                    System.out.println("");
                }
                else if(!Arrays.equals(SHP.get(0),data.init_pos))
                {
                    System.out.println("WA: incorrect starting position");
                    System.out.println("Test_ans:  " + Arrays.toString(data.init_pos));
                    System.out.println("User_ans:  " + Arrays.toString(SHP.get(0)));
                    System.out.println("");
                }
                else if(!Arrays.equals(SHP.get(SHP.size()-1),data.target_pos))
                {
                    System.out.println("WA: incorrect goal position");
                    System.out.println("Test_ans:  " + Arrays.toString(data.target_pos));
                    System.out.println("User_ans:  " + Arrays.toString(SHP.get(SHP.size()-1)));
                    System.out.println("");
                }
                else if(!isShortestPath(data.map, data.answer,SHP))
                {
                    System.out.println("WA: Path Error, either not shortest Path or path not connected");
                    System.out.println("Map:      " + Arrays.deepToString(data.map));
                    System.out.println("User_Path:  " + Arrays.deepToString(SHP.toArray()));
                    System.out.println("Test_path_len:  " + data.answer);
                    System.out.println("User_path_len:  " + sol.shortest_path_len());
                    System.out.println("");

                }
                else
                {
                    System.out.println("AC");
                    num_ac++;
                }
            }
            System.out.println("Score: "+num_ac+"/"+datas.length);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}