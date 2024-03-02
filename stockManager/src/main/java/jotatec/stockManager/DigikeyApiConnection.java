package jotatec.stockManager;


import org.apache.commons.lang3.tuple.ImmutableTriple;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import static jotatec.stockManager.restService.searchController.round;

public class DigikeyApiConnection {

    private static final String ClientIdDigiKey = "YOUR CLIENT ID";
    private static final String ClientSecretDigiKey = "YOUR CLIENT SECRET";
    private static final String TOKEN_ENDPOINT = "https://api.digikey.com/v1/oauth2/token";
    private static final String charset = StandardCharsets.UTF_8.name();



    private static HttpURLConnection connect() throws Exception {
        URL url = new URL(DigikeyApiConnection.TOKEN_ENDPOINT);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set the request method and headers

        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" +charset);

        return connection;
    }



    public static SearchResult search( String code) throws IOException {
        String accessCode = authenticate();
        SearchResult UsefulInformation = getJson(code.trim(),accessCode);
        int stock = UsefulInformation.getStock();
        Map<Integer,Double> valueBreaks = UsefulInformation.getPriceBreaks();
        return new SearchResult(stock,valueBreaks);
    }
    private static String authenticate() {
        try {
            HttpURLConnection connection = connect();

            // Set up the request data
            String postData = "client_id=" + ClientIdDigiKey +
                    "&client_secret=" + ClientSecretDigiKey +
                    "&grant_type=client_credentials";

            // trigers POST.
            connection.setDoOutput(true);

            // Write the POST data
            try (OutputStream output = connection.getOutputStream()) {
                output.write(postData.getBytes(charset));
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                String response = getResponse(connection);

                // Parse and return the access token
                JSONObject jsonObject = new JSONObject(response);
                return  jsonObject.getString("access_token");

            } else {
                System.out.println("Authentication failed! Status code: " + responseCode);
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private static String getItem(String accessCode, String itemCode) throws IOException {
        String url = "https://api.digikey.com/products/v4/search/"+ URLEncoder.encode(itemCode, StandardCharsets.UTF_8)+"%20/productdetails";
        String localeSite = "US";
        String localeLanguage = "en";
        String localeCurrency = "USD";
        String customerId = "2900474";

        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessCode);
        connection.setRequestProperty("accept", "application/x-www-urlencoded");
        connection.setRequestProperty("X-DIGIKEY-Client-Id", ClientIdDigiKey);
        connection.setRequestProperty("X-DIGIKEY-Locale-Site", localeSite);
        connection.setRequestProperty("X-DIGIKEY-Locale-Language", localeLanguage);
        connection.setRequestProperty("X-DIGIKEY-Locale-Currency", localeCurrency);
        connection.setRequestProperty("X-DIGIKEY-Customer-Id", customerId);

        String response = getResponse(connection);
        return response;
    }

    private static SearchResult getJson(String itemId, String accescode) throws IOException {
        JSONObject jsonObject = new JSONObject(getItem(accescode,itemId));
        TreeMap<Integer,Double> priceBreaks = new TreeMap<>();

        try {
            int stock = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations").getJSONObject(1).getInt("QuantityAvailableforPackageType");

            int size = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations").getJSONObject(1).getJSONArray("StandardPricing").length();
            for (int i = 0; i < size; i++) {
                Double unitPrice = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations").getJSONObject(1).getJSONArray("StandardPricing").getJSONObject(i).getDouble("UnitPrice");
                Integer priceBreak = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations").getJSONObject(1).getJSONArray("StandardPricing").getJSONObject(i).getInt("BreakQuantity");
                priceBreaks.put(priceBreak, unitPrice);
            }
            if (priceBreaks.isEmpty()) throw new JSONException("No PriceBreaks");
            return new SearchResult(stock,priceBreaks);
        }
        catch (JSONException e){
            try {
                int stock = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations").getJSONObject(0).getInt("QuantityAvailableforPackageType");

                int size = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations").getJSONObject(0).getJSONArray("StandardPricing").length();
                for (int i = 0; i < size; i++) {
                    Double unitPrice = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations").getJSONObject(0).getJSONArray("StandardPricing").getJSONObject(i).getDouble("UnitPrice");
                    Integer priceBreak = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations").getJSONObject(0).getJSONArray("StandardPricing").getJSONObject(i).getInt("BreakQuantity");
                    priceBreaks.put(priceBreak, unitPrice);
                }
                if (priceBreaks.isEmpty()) throw new JSONException("No PriceBreaks");
                return new SearchResult(stock, priceBreaks);
            }
            catch (JSONException j){
                return null;
            }
        }


    }



    static StringBuilder getStringBuilder(InputStream inputStream) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response;
    }




    private static String getResponse(HttpURLConnection con) throws IOException {
        try(BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }

    public static ImmutableTriple<String,String,Integer> getFullBox(String itemId) throws IOException {
        try {
            String accessCode = authenticate();
            JSONObject jsonObject = new JSONObject(getItem(accessCode, itemId.trim()));
            JSONArray productVariations = jsonObject.getJSONObject("Product").getJSONArray("ProductVariations");

            // Buscar el "full package"
            JSONObject fullPackage = null;
            boolean found = false;
            for (int i = 0; i < productVariations.length(); i++) {
                JSONObject variation = productVariations.getJSONObject(i);
                if (variation.getJSONObject("PackageType").getString("Name").equals("Tape & Reel (TR)")) {
                    fullPackage = variation;
                    found = true;
                    break;
                }
                else {
                    fullPackage = variation;
                }
            }

            String a = "";
            String b = "";

            if (found){
                a = "$ " + fullPackage.getJSONArray("StandardPricing").getJSONObject(0).getDouble("UnitPrice");
                b = "$ " + fullPackage.getJSONArray("StandardPricing").getJSONObject(0).getDouble("UnitPrice")*fullPackage.getInt("StandardPackage");
            }


            for (int i = 0; i < fullPackage.getJSONArray("StandardPricing").length(); i++) {

                a = "$ " + fullPackage.getJSONArray("StandardPricing").getJSONObject(i).getDouble("UnitPrice");
                b = "$ " + round(fullPackage.getJSONArray("StandardPricing").getJSONObject(i).getDouble("UnitPrice")*fullPackage.getInt("StandardPackage"),2);
                if (fullPackage.getJSONArray("StandardPricing").getJSONObject(i).getInt("BreakQuantity") == fullPackage.getInt("StandardPackage")) {
                    break; // Sale del bucle cuando BreakQuantity es igual a StandardPackage
                }
            }

            return new ImmutableTriple<>(
                        a,
                        b,
                        fullPackage.getInt("StandardPackage")
                );
        }
        catch (Exception e){
            return null;
        }

    }
    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }






}