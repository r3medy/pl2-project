package sales;

import java.util.Map;
import enums.*;

public class SalesReporter {
    private int totalSalesCount;
    private double totalRevenue;
    private Map<Category, Double> revenuePerCategory;

    public SalesReporter(int totalSalesCount, double totalRevenue, Map<Category, Double> revenuePerCategory) {
        this.totalSalesCount = totalSalesCount;
        this.totalRevenue = totalRevenue;
        this.revenuePerCategory = revenuePerCategory;
    }

    public Category findBestSellingCategory() {
        Category bestCategory = null;
        double maxRevenue = -1;

        for (Map.Entry<Category, Double> entry : revenuePerCategory.entrySet()) {
            if (entry.getValue() > maxRevenue) {
                maxRevenue = entry.getValue();
                bestCategory = entry.getKey();
            }
        }

        return bestCategory;
    }

    public int getTotalSalesCount() { return totalSalesCount; }
    public double getTotalRevenue() { return totalRevenue; }
    public Map<Category, Double> getRevenuePerCategory() { return revenuePerCategory; }
    public double getAverageSalesValue() {
        if (totalSalesCount == 0) return 0;
        return totalRevenue / totalSalesCount;
    }
}
