//app name: KPI Calculator
//date: 2016 06 11
//author: Valdas
//email: zaidimas7@gmail.com
//version: v1.2;
package kpicalc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class KPICalc {

    /**
     * @param args the command line arguments
     */
    static int groupCount = 5; //this number sets if orders will be grouped by 3 or 5 //use 3-for debbuging; 5-for real cases
    static int iterationCount = 10; //this number sets the count of Iterations

    public static Map<String, Double> sortByValue(Map<String, Double> map) {
        List list = new LinkedList(map.entrySet());
        Collections.sort(list, new Comparator() {

            @Override
            public int compare(Object o1, Object o2) {
                return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
            }
        });

        Map result = new LinkedHashMap();
        for (Iterator it = list.iterator(); it.hasNext();) {
            Map.Entry entry = (Map.Entry) it.next();
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here

        int numberOfRows;
        String file= "C:\\\\Users\\\\Valdas\\\\Desktop\\\\test1.txt"; //path to your .txt document with Orders and Products;
        LineNumberReader lnr = new LineNumberReader(new FileReader(new File(file))); 
        lnr.skip(Long.MAX_VALUE);
        lnr.close();
        numberOfRows = lnr.getLineNumber();
        //  System.out.println(numberOfRows + 1); for debugging

        Scanner read = new Scanner(new File(file)); 

        String[] data1 = new String[numberOfRows + 1];
        String[] data2 = new String[numberOfRows + 1];
        int i = 0, j = 0;

        while (read.hasNext()) {
            data1[i] = read.next();
            data2[j] = read.next();
            i++;
            j++;

        }
        read.close();

        int totalMatches = data1.length;

        //for debugging
//        for (int k = 0; k < data1.length; k++) {
//            System.out.println(data1[k] + " " + data2[k]);
//        }
//find unique orders, total count
        Set<String> uniqOrders = new TreeSet<String>();
        uniqOrders.addAll(Arrays.asList(data1));
        System.out.println("uniqOrders: " + uniqOrders);
        int uniqueOrdersCount = uniqOrders.size();
        System.out.println("Unique orders count: " + uniqueOrdersCount);
        System.out.println();

//find unique products, total count
        Set<String> uniqProducts = new TreeSet<String>();
        uniqProducts.addAll(Arrays.asList(data2));
        System.out.println("uniqProducts: " + uniqProducts);
        int uniqueProductsCount = uniqProducts.size();
        System.out.println("Unique products count: " + uniqueProductsCount);
        System.out.println();

        String[] uniquedata1 = uniqOrders.toArray(new String[uniqOrders.size()]);
        String[] uniquedata2 = uniqProducts.toArray(new String[uniqProducts.size()]);

       // System.out.println("uniquedata1: " + Arrays.deepToString(uniquedata1)); //for debbuging2
        //     System.out.println("data1" + Arrays.deepToString(data1)); //for debbuging
        //    System.out.println("data2" + Arrays.deepToString(data2)); //for debbuging
        int[][] Multi = new int[uniqueProductsCount][uniqueOrdersCount];
        for (int m = 0; m < uniqueOrdersCount; m++) {
            for (int n = 0; n < uniqueProductsCount; n++) {
                for (int k = 0; k < totalMatches; k++) {
                    if (uniquedata1[m].equals(data1[k]) && uniquedata2[n].equals(data2[k])) {
                        Multi[n][m] = 1;
                    } else {

                    }

                }
            }
        }
       // System.out.println("Matrix" + Arrays.deepToString(Multi)); //for debbugging

        //for debbuging prints whole main matrix
//        for (int k = 0; k < uniqueProductsCount; k++) {
//            for (int l = 0; l < uniqueOrdersCount; l++) {
//                System.out.print(Multi[k][l]);
//            }
//            System.out.println();
//        }
        
      double [] ArrBestIteration = new double [iterationCount];
        for (int iteration = 0; iteration<iterationCount; iteration++){
         System.out.println("Iteration: "+(iteration+1));
        
        // makes order in numeric easier to calculate; every String order gets unique numeric value; "order1" = 0 "order2" = 1 ...
        Integer[] numericOrders = new Integer[uniqueOrdersCount];
        for (int k = 0; k < uniqueOrdersCount; k++) {
            numericOrders[k] = k;
        }
        //System.out.println("Numeric Orders: " + Arrays.toString(numericOrders));
        System.out.println("\n------------------Finding Orders--------------\n");

        String[] SuperFinalOrders = new String[uniqueOrdersCount / groupCount];
        double[] SuperFinalOrdersKPI = new double[uniqueOrdersCount / groupCount];
        double[] SuperFinalOrdersTasks = new double[uniqueOrdersCount / groupCount];

        Integer[] ArrDecreasingOrders = new Integer[uniqueOrdersCount];
        ArrDecreasingOrders = numericOrders.clone(); //takes all initial Orders values in numeric type, this array will decrease over and over after deleting group of orders

        //------------here resize searcable array size and delete orders and start again everything----------------------------------------------------------------
        int[] tempArrWorkingOrder = new int[groupCount];
        for (int h = 0; h < uniqueOrdersCount / groupCount; h++) {

            Collections.shuffle(Arrays.asList(ArrDecreasingOrders));
            // System.out.println("Mixed searchable array: " + Arrays.deepToString(ArrDecreasingOrders)); //for debugging
            // System.out.println("\n--------Start of " + (h + 1) + " group iteration-----------------------\n"); //for debbuging
            int[] ArrFinalOrder = new int[groupCount];
//initial value
            for (int k = 0; k < groupCount; k++) {
                ArrFinalOrder[k] = ArrDecreasingOrders[k];
            }
            double[] returnInfo;
            //System.out.print("Initial: "); //for debbuging2
            returnInfo = CalculateKPI(Multi, ArrFinalOrder, uniqueProductsCount); // function returns kpi, tasks, picks of orders 
            double FinalOrderKPI = returnInfo[0];         
            double FinalPicks = 0;
            double FinalTasks = 0;   
            FinalPicks = returnInfo[1];
            FinalTasks = returnInfo[2];
            //System.out.println(""); //for debbuging

            double tempFinalOrderKPI = 0;
            int[] ArrWorkingOrder;
            int[] tempArr = new int[groupCount];
            int temp = 0;

            ArrWorkingOrder = ArrFinalOrder.clone();
            for (int k = groupCount; k < ArrDecreasingOrders.length; k++) {

                for (int m = 0; m < groupCount; m++) {
                    ArrWorkingOrder[m] = ArrDecreasingOrders[k];
                    returnInfo = CalculateKPI(Multi, ArrWorkingOrder, uniqueProductsCount);
                    tempFinalOrderKPI = returnInfo[0];

                    if (tempFinalOrderKPI > FinalOrderKPI) {
                        FinalOrderKPI = tempFinalOrderKPI;
                        FinalPicks = returnInfo[1];
                        FinalTasks = returnInfo[2];
                        tempArr = ArrWorkingOrder.clone();
                        temp++;
                    }

                    ArrWorkingOrder = ArrFinalOrder.clone();

                }
                if (temp > 0) {
                    temp = 0;
                    ArrFinalOrder = tempArr.clone();
                    ArrWorkingOrder = ArrFinalOrder.clone();
                }
            }
            //System.out.println("----------");//for debbuging
            if (groupCount == 5) {
                //  System.out.printf("Best Order: " + uniquedata1[ArrWorkingOrder[0]] + " " + uniquedata1[ArrWorkingOrder[1]] + " " + uniquedata1[ArrWorkingOrder[2]] + " " + uniquedata1[ArrWorkingOrder[3]] + " " + uniquedata1[ArrWorkingOrder[4]] + " Picks = " + FinalPicks + " Tasks = " + FinalTasks + " KPI = %1.3f \n", FinalOrderKPI); //for debbuging
                //System.out.printf("Best Order (numeric):  " + ArrWorkingOrder[0] + " " + ArrWorkingOrder[1] + " " + ArrWorkingOrder[2] + " " + ArrWorkingOrder[3] + " " + ArrWorkingOrder[4] + " Picks = " + FinalPicks + " Tasks = " + FinalTasks + " KPI = %1.3f \n", FinalOrderKPI); //for debbuging
            }

            if (groupCount == 3) {
                // System.out.printf("Best Order: " + uniquedata1[ArrWorkingOrder[0]] + " " + uniquedata1[ArrWorkingOrder[1]] + " " + uniquedata1[ArrWorkingOrder[2]] + " Picks = " + FinalPicks + " Tasks = " + FinalTasks + " KPI = %1.3f \n", FinalOrderKPI); //for debbuging
                //System.out.printf("Best Order (numeric):  " + ArrWorkingOrder[0] + " " + ArrWorkingOrder[1] + " " + ArrWorkingOrder[2] + " Picks = " + FinalPicks + " Tasks = " + FinalTasks + " KPI = %1.3f \n", FinalOrderKPI); //for debbuging
            }
            //-------deletes finded best order from searchable array and freshly fill it with remaining orders
            for (int g = 0; g < ArrWorkingOrder.length; g++) {
                for (int b = 0; b < ArrDecreasingOrders.length; b++) {
                    if (ArrDecreasingOrders[b] == ArrWorkingOrder[g]) {
                        tempArrWorkingOrder[g] = b;                    //writes indexs
                    }
                }
            }
            List<Integer> list = new ArrayList<Integer>(Arrays.asList(ArrDecreasingOrders));
            //System.out.println(list); //for debugging
            Arrays.sort(tempArrWorkingOrder);

            // System.out.println("Sorted tempArrWorkingOrder: " + Arrays.toString(tempArrWorkingOrder)); //for debugging
            for (int c = ArrWorkingOrder.length - 1; c >= 0; c--) {
                // System.out.println(tempArrWorkingOrder[c]); //for debugging
                list.remove(tempArrWorkingOrder[c]);
            }

            Integer[] arr = list.toArray(new Integer[list.size()]);
            // System.out.println("Remaining orders in the list: " + Arrays.toString(arr)); //for debugging
            ArrDecreasingOrders = arr.clone();
            //-------
            // System.out.println("\n--------End of " + (h + 1) + " group iteration-----------------------\n"); //for debbuging
            double roundOff = (double) Math.round(FinalOrderKPI * 1000) / 1000;
            if (groupCount == 5) {
                SuperFinalOrders[h] = uniquedata1[ArrWorkingOrder[0]] + " " + uniquedata1[ArrWorkingOrder[1]] + " " + uniquedata1[ArrWorkingOrder[2]] + " " + uniquedata1[ArrWorkingOrder[3]] + " " + uniquedata1[ArrWorkingOrder[4]] + " Picks = " + (int) FinalPicks + " Tasks = " + (int) FinalTasks;
            }
            if (groupCount == 3) {
                SuperFinalOrders[h] = uniquedata1[ArrWorkingOrder[0]] + " " + uniquedata1[ArrWorkingOrder[1]] + " " + uniquedata1[ArrWorkingOrder[2]] + " Picks = " + (int) FinalPicks + " Tasks = " + (int) FinalTasks;

            }
            SuperFinalOrdersKPI[h] = roundOff;
            SuperFinalOrdersTasks[h] = FinalTasks;
            
        }//end of finding groups of orders
        System.out.println("Uncomment 'for debbuging' lines to see the calculations \n");
        System.out.println("------------Results----------------------------------------------");
        System.out.println("Total orders: " + uniqueOrdersCount + "\n");
        System.out.println("Orders with highest KPI:");

        HashMap<String, Double> map = new HashMap<String, Double>();

        for (int k = 0; k < SuperFinalOrders.length; k++) {
            map.put(SuperFinalOrders[k], SuperFinalOrdersKPI[k]);
        }

        Map<String, Double> sortedMap = KPICalc.sortByValue(map);
        int iii = 1;
        
        for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {
            if (iii < 10) {
                System.out.println("Order  " + iii++ + ": " + entry.getKey() + " KPI = " + entry.getValue());
            } else {
                System.out.println("Order " + iii++ + ": " + entry.getKey() + " KPI = " + entry.getValue());
            }
        }

        Integer remainingArr[] = ArrDecreasingOrders.clone();
        int count = ArrDecreasingOrders.length;
       double[] infoRemaining = CalculateKPI2(Multi, remainingArr, uniqueProductsCount, count);
        
       
        System.out.println("\nRemaining orders: ");
        if (count <5 && count >0) {
            for (int k = 0; k < ArrDecreasingOrders.length; k++) {
            System.out.print(uniquedata1[ArrDecreasingOrders[k]]+" ");
        }
        System.out.print("Picks = "+infoRemaining[1]+" Tasks = "+infoRemaining[2]+" KPI = "+infoRemaining[0]+"\n");
        }
        else {System.out.println("None");}
        
          //calcules sum of Tasks
        
        int sumOfOneIterationKPI = 0;
        for (int k = 0; k < SuperFinalOrdersTasks.length; k++) {
            sumOfOneIterationKPI = sumOfOneIterationKPI+(int)SuperFinalOrdersTasks[k];
        }
       //double roundOff2 = (double) Math.round(sumOfOneIterationKPI * 1000) / 1000;
      // System.out.println("Total sum of KPIs: "+roundOff2);
      // ArrBestIteration[iteration] = roundOff2;
        System.out.println();
        System.out.println("Total sum of Tasks: "+(sumOfOneIterationKPI+(int)infoRemaining[2]));
        ArrBestIteration[iteration] = sumOfOneIterationKPI+(int)infoRemaining[2];
        
        
        System.out.println("\n------------End of Results----------------------------------------------");
        System.out.println();
        
        
        } //end of iteration
        //for best of iteration Tasks fiding
        //System.out.println("ArrBestIteration: " + Arrays.toString(ArrBestIteration));
        
        
        int bestIterationIndex = -1;
        int bestIterationTasksSum =1000000000;
        for (int k = 0; k < ArrBestIteration.length; k++) {
            if( ArrBestIteration[k]<bestIterationTasksSum){
            bestIterationTasksSum = (int) ArrBestIteration[k];
            bestIterationIndex = k;
            }
            System.out.println("Iteration "+(k+1)+" tasks = "+(int)ArrBestIteration[k]);
        }
        System.out.println("---------Best Iteration--------------------------------------------");
        System.out.println("The best one is iteration: "+(bestIterationIndex+1)+"; Tasks = "+bestIterationTasksSum);
        
        //for checking best iteration
//        Arrays.sort(ArrBestIteration);
//        System.out.println("after sort ArrBestIteration: " + Arrays.toString(ArrBestIteration));
//        
//        System.out.print("Iteration: "+(int)ArrBestIteration[0]+"\n");
    }

    public static double[] CalculateKPI(int[][] Multi, int[] ArrFinalOrderCalc, int uniqueProductsCount) {
        double tempKPI = 0;
        double[] info = new double[3];
        int tempPicks = 0;
        int tempTasks = 0;
        int tempPickstemp = 0;
        int[][] selectedMulti = new int[uniqueProductsCount][groupCount];

        //selects chosen orders and writes to new arr
        for (int k = 0; k < uniqueProductsCount; k++) {
            for (int l = 0; l < groupCount; l++) {
                selectedMulti[k][l] = Multi[k][ArrFinalOrderCalc[l]];
                //System.out.print(selectedMulti[k][l]); //for debugging2
            }
            //System.out.println(); //for debugging2
        }
        //counts Tasks and Picks
        for (int k = 0; k < uniqueProductsCount; k++) {
            for (int l = 0; l < groupCount; l++) {
                if (selectedMulti[k][l] == 1) {
                    tempPicks++;

                    tempPickstemp = tempPickstemp + selectedMulti[k][l];
                }
            }
            if (tempPickstemp > 0) {
                tempTasks++;
                tempPickstemp = 0;
            }
        }

        tempKPI = (double)  tempPicks / tempTasks;
        if (groupCount == 5) {
            //System.out.printf("Orders: " + ArrFinalOrderCalc[0] + " " + ArrFinalOrderCalc[1] + " " + ArrFinalOrderCalc[2] + " " + ArrFinalOrderCalc[3] + " " + ArrFinalOrderCalc[4] + " Picks = " + tempPicks + " Tasks = " + tempTasks + " KPI = %1.3f \n", tempKPI); //for debugging
        }
        if (groupCount == 3) {
            //System.out.printf("Orders: " + ArrFinalOrderCalc[0] + " " + ArrFinalOrderCalc[1] + " " + ArrFinalOrderCalc[2] + " Picks = " + tempPicks + " Tasks = " + tempTasks + " KPI = %1.3f \n", tempKPI); //for debuging
        }
        info[0] = tempKPI;
        info[1] = (int) tempPicks;
        info[2] = (int) tempTasks;
        return info;
    }

    public static double[] CalculateKPI2(int[][] Multi, Integer[] ArrFinalOrderCalc, int uniqueProductsCount, int count) {
        double tempKPI = 0;
        double[] info = new double[3];
        int tempPicks = 0;
        int tempTasks = 0;
        int tempPickstemp = 0;
        int[][] selectedMulti = new int[uniqueProductsCount][count];

        //selects chosen orders and writes to new arr
        for (int k = 0; k < uniqueProductsCount; k++) {
            for (int l = 0; l < count; l++) {
                selectedMulti[k][l] = Multi[k][ArrFinalOrderCalc[l]];
                //System.out.print(selectedMulti[k][l]); //for debugging2
            }
            //System.out.println(); //for debugging2
        }
        //counts Tasks and Picks
        for (int k = 0; k < uniqueProductsCount; k++) {
            for (int l = 0; l < count; l++) {
                if (selectedMulti[k][l] == 1) {
                    tempPicks++;

                    tempPickstemp = tempPickstemp + selectedMulti[k][l];
                }
            }
            if (tempPickstemp > 0) {
                tempTasks++;
                tempPickstemp = 0;
            }
        }

        tempKPI = (double)  tempPicks / tempTasks;
        if (groupCount == 5) {
            //System.out.printf("Orders: " + ArrFinalOrderCalc[0] + " " + ArrFinalOrderCalc[1] + " " + ArrFinalOrderCalc[2] + " " + ArrFinalOrderCalc[3] + " " + ArrFinalOrderCalc[4] + " Picks = " + tempPicks + " Tasks = " + tempTasks + " KPI = %1.3f \n", tempKPI); //for debugging
        }
        if (groupCount == 3) {
            //System.out.printf("Orders: " + ArrFinalOrderCalc[0] + " " + ArrFinalOrderCalc[1] + " " + ArrFinalOrderCalc[2] + " Picks = " + tempPicks + " Tasks = " + tempTasks + " KPI = %1.3f \n", tempKPI); //for debuging
        }
        info[0] = tempKPI;
        info[1] = (int) tempPicks;
        info[2] = (int) tempTasks;
        return info;
    }
}
