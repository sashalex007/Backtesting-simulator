import java.util.ArrayList;
import java.util.Date;

public class utils {

    public static double getHigh (ArrayList<Double> array) {
        double high = array.get(0);
        for (int i = 0; i < array.size(); i++) {
            double close = array.get(i);
            if (close > high) {
                high = close;
            }
        }
        return high;
    }

    public static double getLow (ArrayList<Double> array) {
        double low = array.get(0);

        for (int i = 0; i < array.size(); i++) {
            double close = array.get(i);
            if (close < low) {
                low = close;
            }
        }
        return low;
    }

    public static double[] ppHighCalculate(ArrayList<double[]> ppArray, int ppPeriod) {
        boolean newPPhigh = false;
        //test ppHigh
        for (int i = 0; i < ppArray.size(); i++) {
            if (i != ppPeriod) {
                if (ppArray.get(i)[2] < ppArray.get(ppPeriod)[2]) {
                    newPPhigh = true;
                } else {
                    newPPhigh = false;
                    break;
                }
            }
        }

        if (newPPhigh == true) {
            return ppArray.get(ppPeriod);
        } else {
            return null;
        }
    }

    public static double[] ppLowCalculate(ArrayList<double[]> ppArray, int ppPeriod) {
        boolean newPPLow = false;
        //test ppHigh
        for (int i = 0; i < ppArray.size(); i++) {
            if (i != ppPeriod) {
                if (ppArray.get(i)[3] >= ppArray.get(ppPeriod)[3]) {
                    newPPLow = true;
                } else {
                    newPPLow = false;
                    break;
                }
            }
        }

        if (newPPLow == true) {
            return ppArray.get(ppPeriod);
        } else {
            return null;
        }
    }

    public static double calculateMovingAverage(ArrayList<Double> array) {
        double sum = 0;
        sum = array.stream()
                .mapToDouble(a -> a)
                .sum();
        double movingAverage = sum / (array.size());
        return movingAverage;
    }

    public double calculateStdev(ArrayList<Double> array) {
        double average = calculateMovingAverage(array);
        double squaredSum = 0;

        for (int i = 0; i < array.size(); i++) {
            double close = array.get(i);
            squaredSum = Math.pow(close - average, 2) + squaredSum;
        }

        double stdev = Math.sqrt(squaredSum);
        return stdev;

    }

    public static double[] calculateFib(double High, double Low) {
        double diff = High - Low;
        double level1 = High - (0.50 * diff);
        double level2 = High - (0.618 * diff);
        double level3 = High - (0.786 * diff);

        double[] levels = {level1, level2, level3};
        return levels;
    }

    public static ArrayList<Double> convertTo1D(ArrayList<double[]> array, int element) {
        ArrayList<Double> convertedArray = new ArrayList<Double>();

        for(int i = 0; i < array.size(); i ++) {
            convertedArray.add(array.get(i)[element]);
        }

            return convertedArray;
    }

    public static int getStopLossIndex(double entryPrice, double time, double offset, int candleMinute) {
//        int sellPriceIndex = 0;
//        double[][] bitmex1m = simulator.bitmexHist1m;
//        int index = 0;
//
//        for (int i = 0; i < 679344; i++) {
//            if (bitmex1m[i][4] == time-(((candleMinute*60)-60)*1000) ) {
//                index = i;
//                break;
//            }
//        }
//
//        for (int i = index; i < index+60; i++) {
//
//            if (i < 679344) {
//                if (bitmex1m[i][1] < entryPrice - offset) {
//                    sellPriceIndex = i;
//                    break;
//                }
//            }
//        }
//
//        //System.out.println(bitmex1m[index][0] + "  " + bitmex1m[index+59][1]);
//
        return 0;
    }

    public static String[] splitString(String action) {
        String[] parts = action.split("_");
        return parts;
    }

    public static String convertDate(double time) {
        long l = (long) time;
        Date d = new Date(l);
        String date = d.toString();

        return date;
    }

    public static double calculateEMA(ArrayList<Double> array, int period) {
        //ArrayList<Double> array = new ArrayList<>(array1.subList(array1.size()-50, array1.size()));
        double ema = 0;
        double doublePeriod = period;
        double multiplier = 2 / (doublePeriod + 1.0);
        ArrayList<Double> tempArray = new ArrayList<>();

        for (int i = 0; i < array.size(); i++) {
            if (i < period+1) {
                tempArray.add(array.get(i));

                if (tempArray.size() == period) {
                    ema = calculateMovingAverage(tempArray);
                    tempArray.clear();
                }
            }
            else {
                double currentClose = array.get(i);
                ema = ((currentClose- ema) * multiplier) + ema;
            }

        }

        return ema;
    }

    public static double calculateMACDhist(ArrayList<Double> array) {
        double macdHist = 0;
        ArrayList<Double> signalArray = new ArrayList<>();

        for (int i=0; i < 100; i++) {
            ArrayList<Double> tempArray = new ArrayList<>(array.subList(100+i, array.size()-100+i));
            double mcadLineTemp = calculateEMA(tempArray, 12) - calculateEMA(tempArray, 26);
            signalArray.add(mcadLineTemp);
        }
        double mcadLine = calculateEMA(array, 12) - calculateEMA(array, 26);
        signalArray.add(mcadLine);
        double signalLine = calculateEMA(signalArray, 9);
        macdHist = mcadLine - signalLine;

        return macdHist;
    }

    public static double calculateATR(ArrayList<double[]> array) {

        ArrayList<double[]> tempArray = new ArrayList<>();
        int period = 14;
        double priorATR = 0;
        double currentTR = 0;
        double currentATR = 0;

        for (int i = 0; i < array.size(); i++) {
            if (i < period) {
                tempArray.add(array.get(i));

                if (tempArray.size() == period) {
                    double[] firstCandle = tempArray.get(period-1);
                    currentTR = firstCandle[2] - firstCandle[3];
                    ArrayList<Double> tempArray2 = new ArrayList<Double>();
                    for (int j = 0; j < period - 1; j++) {
                        double firstATR;
                        double[] tempCandle = tempArray.get(j);
                        firstATR = tempCandle[2] - tempCandle[3];
                        tempArray2.add(firstATR);
                    }
                    priorATR = calculateMovingAverage(tempArray2);
                    tempArray.clear();
                    tempArray2.clear();

                    currentATR = (((priorATR * (period - 1)) + currentTR)) / period;
                    //System.out.println(currentATR);
                }


            } else {
                double[] currentCandle = array.get(i);
                currentTR = currentCandle[2] - currentCandle[3];
                currentATR = ((currentATR * (period - 1)) + currentTR) / period;

            }

        }

        return currentATR;

    }



}
