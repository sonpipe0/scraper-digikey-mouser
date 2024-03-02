package jotatec.stockManager;

import jotatec.stockManager.restService.searchController;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class MouserApiConnection {

    private static final String API_KEY_MOUSER = "YOUR API KEY";


    private static HttpURLConnection connect() throws IOException {
        String apiUrl = "https://api.mouser.com/api/docs/V2";
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method and headers


        connection.setRequestProperty("Api-Key", API_KEY_MOUSER);
        return connection;
    }

    public static SearchResult search(String code) throws IOException {
        try {
            JSONObject jsonObject = MouserApiConnection.getJson(code.trim());
            int searchResults = jsonObject.getJSONObject("SearchResults").getInt("NumberOfResult");
            int stock = Integer.parseInt(jsonObject.getJSONObject("SearchResults").getJSONArray("Parts").getJSONObject(0).getString("Availability").split("\\s+")[0]);
            int N = jsonObject.getJSONObject("SearchResults").getJSONArray("Parts").getJSONObject(0).getJSONArray("PriceBreaks").length();
            TreeMap<Integer,Double> priceBreaks = new TreeMap<>();
            for(int i = 0 ; i < N ; i++){
                String priceString = jsonObject.getJSONObject("SearchResults").getJSONArray("Parts").getJSONObject(0).getJSONArray("PriceBreaks").getJSONObject(i).getString("Price");

                Double price = Double.valueOf(priceString.substring(1));
                Integer qty = jsonObject.getJSONObject("SearchResults").getJSONArray("Parts").getJSONObject(0).getJSONArray("PriceBreaks").getJSONObject(i).getInt("Quantity");
                priceBreaks.put(qty,price);
            }


            return new SearchResult(stock, priceBreaks);
        } catch (JSONException e) {
            return new SearchResult(0,null);
        }

    }


    public static ImmutableTriple<String,String,Integer> getFullBox(String itemId) throws IOException {
        try {
            JSONObject jsonObject = getJson(itemId.trim());
            JSONObject parts = jsonObject.getJSONObject("SearchResults").getJSONArray("Parts").getJSONObject(0);
            JSONArray productAttributes = parts.getJSONArray("ProductAttributes");
            int fullboxqty = 0;
            for (int i = 0; i < productAttributes.length(); i++) {
                JSONObject attribute = productAttributes.getJSONObject(i);
                if ("Standard Pack Qty".equals(attribute.getString("AttributeName"))) {
                    fullboxqty = attribute.getInt("AttributeValue");
                    break;
                }
            }

            JSONArray priceBreaks = parts.getJSONArray("PriceBreaks");
            Double fullboxunitprice = 0.0;
            for (int i = 0; i < priceBreaks.length(); i++) {
                JSONObject priceBreak = priceBreaks.getJSONObject(i);
                if (priceBreak.getInt("Quantity") == fullboxqty) {
                    fullboxunitprice = Double.valueOf(priceBreak.getString("Price").substring(1));
                    break;
                }
            }

            double fullboxfinalprice = searchController.round(fullboxunitprice*( (double) fullboxqty),3);

            return new ImmutableTriple<>("$ "+fullboxunitprice,"$ "+fullboxfinalprice,fullboxqty);
        } catch (IOException e) {
           return null;
       }

    }





    public static JSONObject getJson(String itemId) throws IOException {
        HttpURLConnection connection = connect();
        String command = "curl -X POST \"https://api.mouser.com/api/v1.0/search/partnumber?apiKey="+API_KEY_MOUSER+"\" -H \"accept: application/json\" -H \"Content-Type: application/json\" -H \"Currency: usd\" -d \"{ \\\"SearchByPartRequest\\\": { \\\"mouserPartNumber\\\": \\\""+itemId+"\\\" , \\\"partSearchOptions\\\": \\\"string\\\" }}\"";
        Process process = Runtime.getRuntime().exec(command);
        InputStream inputStream = process.getInputStream();
        StringBuilder response2 = getStringBuilder(inputStream);
        return new JSONObject(response2.toString());
    }




    private static StringBuilder getStringBuilder(InputStream inputStream) throws IOException {
        return DigikeyApiConnection.getStringBuilder(inputStream);
    }


}

