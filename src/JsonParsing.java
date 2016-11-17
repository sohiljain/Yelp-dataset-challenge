import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;

//import org.apache.commons.io.FileUtils;

public class JsonParsing {
//    public static void main(String myHelpers[]) throws JSONException, IOException {
    public static HashMap<String, String> tips() throws JSONException, IOException {

        BufferedReader read_business = new BufferedReader(new FileReader(new File("/Users/sjain/git/Yelp-IR/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json")));
        BufferedReader read_review = new BufferedReader(new FileReader(new File("/Users/sjain/git/Yelp-IR/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review.json")));
        BufferedReader read_tip = new BufferedReader(new FileReader(new File("/Users/sjain/git/Yelp-IR/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_tip.json")));

        BufferedWriter bw = new BufferedWriter(new FileWriter(new File("/Users/sjain/git/Yelp-IR/files/temp.tsv")));
        HashMap<String, String> map = null;
        try {

            HashMap<String, ArrayList<String>> categoryMap = new HashMap<String, ArrayList<String>>();
            String jsonString = "";
            while ((jsonString = read_business.readLine()) != null) {
                JSONObject obj = new JSONObject(jsonString);
                String businessId = obj.getString("business_id");
                JSONArray catArray = obj.getJSONArray("categories");
                ArrayList<String> categories = new ArrayList<>();
                for (int i = 0; i < catArray.length(); i++) {
                    categories.add(catArray.getString(i));
                }
                categoryMap.put(businessId, categories);
            }

            Set<String> distinctCategories = categoryMap.values().stream().flatMap(x -> x.stream()).distinct().collect(Collectors.toSet());

            map = new HashMap<>();

            for (String distinctCategory : distinctCategories) {
                map.put(distinctCategory, "");
            }

            HashMap<String, String> tipMap = new HashMap<String, String>();
            jsonString = "";
            while ((jsonString = read_tip.readLine()) != null) {
                JSONObject obj = new JSONObject(jsonString);
                String businessId = obj.getString("business_id");
                String tip = obj.getString("text");
                if (tipMap.containsKey(businessId))
                    tipMap.put(businessId, tipMap.get(businessId) + " " + tip);
                else
                    tipMap.put(businessId, tip);
            }

            for (String bid : categoryMap.keySet()) {
                if (tipMap.containsKey(bid)) {
                    for (String cat : categoryMap.get(bid)) {
                        if (map.containsKey(cat))
                            map.put(cat, map.get(cat) + "\n" + tipMap.get(bid));
                        else
                            map.put(cat, tipMap.get(bid));
                    }
                }
            }


//            map.forEach((k,v) -> System.out.println(k +" -> "+ v));


//            HashMap<String, String> reviewMap = new HashMap<String, String>();
//            jsonString = "";
//            while ((jsonString = read_review.readLine()) != null) {
//                JSONObject obj = new JSONObject(jsonString);
//                String businessId = obj.getString("business_id");
//                String review = obj.getString("text");
//                if(reviewMap.containsKey(businessId))
//                    reviewMap.put(businessId, reviewMap.get(businessId) + " " + review);
//                else
//                    reviewMap.put(businessId, review);
//            }
//
//            for (String bid : categoryMap.keySet()) {
//                if(reviewMap.containsKey(bid)) {
//                    for(String cat : categoryMap.get(bid)) {
//                        if(map.containsKey(cat))
//                            map.put(cat, map.get(cat) + "\n" + reviewMap.get(bid) );
//                        else
//                            map.put(cat, reviewMap.get(bid));
//                    }
//                }
//            }
//


//            HashMap<String, ArrayList<String>> toIndexMap = new HashMap<>();
//            for(String bid : categoryMap.keySet()) {
//                for(String category : categoryMap.get(bid)) {
//                    String review = "", tip = "";
//                    if(!toIndexMap.containsKey(category)) {
//                        for(String bid1 : categoryMap.keySet()){
//                            for(String cat : categoryMap.get(bid1)) {
//                                if(category.contains(cat)) {
//
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//
//            bw.write("bid\tcategories\treview\ttip");
//            for(String bid : categoryMap.keySet()) {
//                String useThis = bid + "\t" + categoryMap.get(bid) + "\t" + reviewMap.get(bid) + "\t" + tipMap.get(bid);
//                bw.append(useThis+"\n");
//            }


//            Stream<String> stream = categoryMap.keySet().stream().map(s -> s + "\t" + categoryMap.get(s) + "\t" + reviewMap.get(s) + "\t" + tipMap.get(s));
//            Set<String> finalSet = stream.collect(Collectors.toSet());

        } catch (Exception e) {

        }
        return map;
    }

}
