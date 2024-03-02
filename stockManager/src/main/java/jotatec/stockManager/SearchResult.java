package jotatec.stockManager;

import java.util.Map;

public class SearchResult {
    private final int stock;
    private final Map<Integer, Double> priceBreaks;

    public SearchResult(Integer stock, Map<Integer, Double> priceBreaks) {
        this.stock = stock;
        this.priceBreaks = priceBreaks;
    }

    public int getStock() {
        return stock;
    }

    public Map<Integer, Double> getPriceBreaks() {
        return priceBreaks;
    }
}