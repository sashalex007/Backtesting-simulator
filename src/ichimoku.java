import java.util.ArrayList;

public class ichimoku {

    public static String getResult(ArrayList<double[]> array, double price, boolean inMarket, double entryPrice, double ratio) {

        if (array.size() >= 300) {

            //9, 26, 52
            int c = 9;
            int b = 26;
            int B = 52;
            int lead = 26;


            ArrayList<double[]> array1 = new ArrayList<>(array.subList(array.size() - c, array.size()));
            ArrayList<double[]> array2 = new ArrayList<>(array.subList(array.size() - b, array.size()));
            ArrayList<double[]> array3 = new ArrayList<>(array.subList(array.size() - B - lead, array.size() - lead));

            ArrayList<double[]> array4 = new ArrayList<>(array.subList(array.size() - c - lead, array.size() - lead));
            ArrayList<double[]> array5 = new ArrayList<>(array.subList(array.size() - b - lead, array.size() - lead));

            double conversion = (utils.getHigh(utils.convertTo1D(array1, 2)) + utils.getLow(utils.convertTo1D(array1, 3))) / 2; //9 period
            double base = (utils.getHigh(utils.convertTo1D(array2, 2)) + utils.getLow(utils.convertTo1D(array2, 3))) / 2; //26 period

            double conversionPast = (utils.getHigh(utils.convertTo1D(array4, 2)) + utils.getLow(utils.convertTo1D(array4, 3))) / 2; //9 period
            double basePast = (utils.getHigh(utils.convertTo1D(array5, 2)) + utils.getLow(utils.convertTo1D(array5, 3))) / 2; //26 period

            double leadingA = (conversionPast + basePast) / 2;
            double leadingB = (utils.getHigh(utils.convertTo1D(array3, 2)) + utils.getLow(utils.convertTo1D(array3, 3))) / 2; //52 period
            double lagging = array.get(array.size() - lead)[1];

            double[] latestCandle = array.get(array.size() - 1);
            double[] previousCandle = array.get(array.size() - 2);
            double time = latestCandle[4];

            //1.069
            //1.0465 backtested for 5m ===> 0.4 btc
            if (leadingA / price > ratio && leadingB > leadingA) {
                //buy
                double mcadHist = utils.calculateMACDhist(utils.convertTo1D(array, 1));
                double currentATR = utils.calculateATR(array);
                array.remove(array.size() - 1);
                double mcadHistPrev = utils.calculateMACDhist(utils.convertTo1D(array, 1));


                if (!inMarket && mcadHist > -31 && mcadHist < 1) {
                    return "buy" + "_" + price + "_" + time;
                }
                else if (inMarket && price < entryPrice - (currentATR * 3.1)) {
                    return "sell" + "_" + price + "_" + time;
                }
                else {
                    return "null" + "_" + price + "_" + time;
                }

            } else if (price > leadingA && inMarket) {
                //sell
                return "sell" + "_" + price + "_" + time;

            }

            else {
                return "null" + "_" + price + "_" + time;
            }

        } else {
            return "null" + "_" + price + "_" + "0";
        }

    }

}
