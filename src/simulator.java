import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Stream;


public class simulator {


    private static double[][] bitmexHist = new double[521515][5];

    private static double iterations;
    private static double iterationCount = 0;
    private static int threadCount;

    public static void main(String args[]) throws InterruptedException, ExecutionException {

        System.out.println("Simulation starting...");

        bitmexHist = getArray(bitmexHist, "bitmex5m_521515.txt");

        List<Callable<Double[]>> threads = new ArrayList<>();
        long stopwatch = System.currentTimeMillis();

        threadCount = 12;
        iterations = 1000;
        double ratio = 1.03;

        int i;
        for (i = 0; i < iterations; i++) {
            threads.add(new simulate(ratio + 0.0001*i));
        }


        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        List<Future<Double[]>> tasks = executorService.invokeAll(threads);


        System.out.println(tasks.size() + " results\n");

        List<Double[]> resultArray = new ArrayList<>();
        for (Future<Double[]> task : tasks) {
            Double[] result = task.get();
            resultArray.add(result);
        }

        Double best = 0.0;
        Double[] bestResult = {0.0, 0.0, 0.0, 0.0};
        for (Double[] result : resultArray) {
            if (result[0] > best) {
                best = result[0];
                bestResult = result;
            }
        }

        long stopwatch2 = System.currentTimeMillis();
        long elapsed = (stopwatch2 - stopwatch) / 1000;
        System.out.println(elapsed + "s");

        System.out.println("BTC: " + bestResult[0] + "  Ratio: " + bestResult[1]);
        executorService.shutdown();

    }

    public static class simulate implements Callable<Double[]> {

        private double ratio;

        private boolean inMarket = false;
        private int tradeCount = 0;
        private double entryPrice = 0;
        private double balanceBTC = 0.1;

        public simulate(double providedRatio) {
            ratio = providedRatio;
        }

        public Double[] call() {

            int i;
            for (i = 0; i < bitmexHist.length; i++) {
                if (balanceBTC <= 0 || Double.isNaN(balanceBTC) || balanceBTC < 0.01) {
                    balanceBTC = 0;
                    break;
                }
                double[] latestCandle = bitmexHist[i];
                double price = latestCandle[1];
                double time = latestCandle[4];


                ArrayList<double[]> array = new ArrayList<>();
                if (i > 300) {
                    int temp = 300;
                    for (int start = i - temp; start < i; start++) {
                        array.add(bitmexHist[start + 1]);
                    }
                }

                String actionIchimoku = ichimoku.getResult(array, price, inMarket, entryPrice, ratio);
                //String actionFibonacci = fibonacci.getResult(array, price, inMarket, entryPrice);
                tradingAlgo(actionIchimoku);
            }

            iterationCount++;
            System.out.println(Math.round((iterationCount / iterations) * 100000.0) / 1000.0 + "%   BTC: " + balanceBTC + "  Candle: " + i + "  Tradecount: " + tradeCount + "  Ratio: " + ratio);
            Double[] result = {balanceBTC, ratio};
            return result;
        }

        private void tradingAlgo(String actionFib) {
            String action = utils.splitString(actionFib)[0];
            double price = Double.parseDouble(utils.splitString(actionFib)[1]);
            double time = Double.parseDouble(utils.splitString(actionFib)[2]);


            if (action.equals("buy") && !inMarket) {
                submitOrder(true, price, time);
            }

            if (action.equals("sell") && inMarket) {

                submitOrder(false, price, time);
            }

        }

        public void submitOrder(boolean status, double price, double time) {

            String date = utils.convertDate(time);

            if (status && !inMarket) {
                //buy
                tradeCount++;
                inMarket = true;
                entryPrice = price; //long with leverage

                double fee = balanceBTC  * 0.00085;
                balanceBTC = (balanceBTC - fee);


                if (threadCount == 1) {
                    System.out.println(tradeCount + ". BTC " + balanceBTC + "       <----------BUY: " + price + "   " + date + "   " );
                }

                entryPrice = price;

            }
            if (!status && inMarket) {
                //sell
                tradeCount++;
                inMarket = false;

                balanceBTC = balanceBTC + (balanceBTC) * (entryPrice) * ((1 / entryPrice) - (1 / price));
                double fee = balanceBTC * 0.00085;
                balanceBTC = balanceBTC - fee;
                entryPrice = 0;

                if (threadCount == 1) {
                    System.out.println(tradeCount + ". BTC " + balanceBTC + "       <----------SELL : " + price + "   " + date);
                }

            }


        }


    }

    private static double[][] getArray(double[][] array, String path) {

        File file1 = new File("/Users/alexandrepokhodoun/Documents/Coding/Java/simulator/src/" + path);
        try {
            Scanner scanner = new Scanner(file1);
            for (int row = 0; scanner.hasNextLine() && row < array.length; row++) {

                String line = scanner.nextLine();

                double[] arr = Stream.of(line.split(" "))
                        .mapToDouble(Double::parseDouble)
                        .toArray();

                array[row] = arr;
            }
        } catch (FileNotFoundException ex) {
            System.out.println(ex);

        }

        return array;

    }

}
