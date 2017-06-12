/*
 *Compare Time complexity of PB AO ICO
 *
 * */

package experiment;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import AO.AO;
import PB.PB;
import ICO.ICO;
import source.DAG;
import source.Node;

public class TimeComplex {

    public static void main(String args[]) throws FileNotFoundException{

        if(args.length != 1){
            System.out.println("java TimeComplex [path like DAG_SP/sp_100]");
            return;
        }

        TimeComplex ms = new TimeComplex();

        AO ao = new AO();
        PB pb = new PB();
        ICO ico = new ICO();
        double time_PB=0.0, time_AO=0.0, time_ICO=0.0;
        double startTime, endTime;
        for(int i = 0; i < 100; i++){
            String dagpath = args[0] + "_" + i + ".txt";
            //String dagpath = args[0] + "_" + (99-i) + ".txt";
            System.out.println(dagpath);

            startTime = System.currentTimeMillis(); 
                //ao.AOSchedule(dagpath);  
                ao.AOSchedule_sp(dagpath);
            endTime = System.currentTimeMillis(); 			
            time_AO += endTime - startTime;
    
            //System.out.println("AO done!");

            startTime = System.currentTimeMillis(); 			
                pb.PBSchedul(dagpath);
            endTime = System.currentTimeMillis();
            time_PB += endTime - startTime;
            //System.out.println("PB done");

            startTime = System.currentTimeMillis();
                ico.ICOSchedule(dagpath);
            endTime = System.currentTimeMillis();
            time_ICO += endTime - startTime;
        }

        System.out.println("PB average runtime: "+ time_PB/100.00);
        System.out.println("AO average runtime: "+ time_AO/100.00);
        System.out.println("ICO average runtime: " + time_ICO/100.00);
    }

}
