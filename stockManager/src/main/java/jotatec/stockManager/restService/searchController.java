package jotatec.stockManager.restService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import jotatec.stockManager.DigikeyApiConnection;
import jotatec.stockManager.MouserApiConnection;
import jotatec.stockManager.SQLconnection.MySQLConnection;
import jotatec.stockManager.SearchResult;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class searchController extends Thread {


    private final Semaphore semaphore = new Semaphore(1);


    public Map<String,String> search(String codeId,String manufacturer,Integer qty) throws IOException {

        try {
            SearchResult searchResult = manufacturer.equalsIgnoreCase("mouser") ? MouserApiConnection.search(codeId): DigikeyApiConnection.search(codeId);
            ImmutableTriple<String,String,Integer> fullBox = manufacturer.equalsIgnoreCase("mouser") ?MouserApiConnection.getFullBox(codeId):DigikeyApiConnection.getFullBox(codeId);
            Integer stock = searchResult.getStock();
            Map<Integer,Double> breakPointprices = searchResult.getPriceBreaks();
            ArrayList<Integer> breakPointQty = new ArrayList<>(breakPointprices.keySet());
            Integer currentBreakPointQty = breakPointQty.get(0);
            int i = 0;
            while (i < breakPointQty.size() && qty >= breakPointQty.get(i)) {
                currentBreakPointQty = breakPointQty.get(i);
                i++;
            }
            Integer nextBreakPointQty = i < breakPointQty.size() ? breakPointQty.get(i) : currentBreakPointQty;
            double unitPriceCurrent = breakPointprices.get(currentBreakPointQty);
            double unitPriceNext = breakPointprices.get(nextBreakPointQty);
            double finalPriceCurrent = qty*unitPriceCurrent;
            double finalPriceNext = nextBreakPointQty*unitPriceNext;
            finalPriceCurrent = round(finalPriceCurrent,2);
            finalPriceNext = round(finalPriceNext,2);

            Map<String,String> response = new HashMap<>();

            response.put("Stock", stock.toString());
            response.put("UnitPriceCurrent","$ "+unitPriceCurrent);
            response.put("UnitPriceNext","$ "+unitPriceNext);
            response.put("FinalPriceCurrent","$ "+finalPriceCurrent);
            response.put("FinalPriceNext","$ "+finalPriceNext);
            response.put("CurrentBreakPoint",currentBreakPointQty.toString());
            response.put("NextBreakPoint",nextBreakPointQty.toString());
            if (fullBox != null){
            response.put("FullBoxUnitPrice", fullBox.left);
            response.put("FullBoxFinalPrice",fullBox.middle);
            response.put("FullBoxUnitQty",fullBox.right.toString());
            }
            else {
                response.put("FullBoxUnitPrice", "Null");
                response.put("FullBoxFinalPrice","Null");
                response.put("FullBoxUnitQty","Null");
            }
            System.out.println(response);
            return  response;
        } catch (Exception e) {
            return null;
        }

    }


    @GetMapping("/search")
    public  List<Map<String,String>> totalSearch(@RequestParam(value = "codigoJotatec")String codigoJotatec,@RequestParam(value = "qty")Integer qty) throws IOException, InterruptedException {
        Map<String,String> codigos = MySQLConnection.query(codigoJotatec);
        assert codigos != null;

        String codigoMouser = codigos.get("CodigoMouser");
        String codigoDigikey = codigos.get("CodigoDigikey");
        Map<String,String> resultadoMouser = null;
        Map<String,String> resultadoDigikey = null;
        if(codigoMouser != null) {
            semaphore.acquire();
            resultadoMouser = search(codigoMouser, "mouser", qty);
            semaphore.release();
        }
        if(codigoDigikey != null){
            semaphore.acquire();
            resultadoDigikey = search(codigoDigikey,"digikey",qty);
            semaphore.release();
        }
        List<Map<String,String>> result = new ArrayList<>();
        if(resultadoMouser != null)  {
            resultadoMouser.put("manufacturer","mouser");
            result.add(resultadoMouser);
        }
        if(resultadoDigikey != null) {
            resultadoDigikey.put("manufacturer","digikey");
            result.add(resultadoDigikey);
        }



        return result;

    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }




}