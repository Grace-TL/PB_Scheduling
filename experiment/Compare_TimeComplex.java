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
import FIFO.FIFO;
import PB.PB;
import ICO.ICO;
import source.DAG;
import source.Node;

public class Compare_TimeComplex {

    public static final int SIZE = 5000;
    private  double CURRENT_TIME = 0.0;
    int[] batchSize = new int[SIZE];
    double[] interTime = new double[SIZE];
    double[] jobTime = new double[SIZE];
    DAG dag = new DAG();
    List<Node> schedule_AO = new LinkedList<Node>();
    List<Node> schedule_PB = new LinkedList<Node>();
    private List<Node> schedule;

    public void init(int batchsize){

        for(int i=0; i<SIZE; i++)  
            batchSize[i] = (int) Exponent_rand( 1.0/batchsize);  // lamda = 1.0/2;  //[1.0/2 ~ 1.0/32]  (2, 4, 8, 16, 32)

        // batch interarrival time
        for(int j=1; j<SIZE; j++)     
            interTime[j] = Exponent_rand(1.0/10);  //[ 1.0/1000 ~ 1000.0/1]



    }

    public void initJobtime(int u, int d){

        jobTime = new double[dag.NodeList.size()];
        for(int k = 0 ; k < dag.NodeList.size() ; k++)  
            jobTime[k] = Norm_rand(u,d);  

        for(int i = 0; i < dag.NodeList.size();i++){	 
            dag.NodeList.get(i).jobTime = jobTime[i];  
            //					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!! "+dag.NodeList.get(i).jobTime);
        }
    }

    public void Getschedule() throws FileNotFoundException{

        AO ao = new AO();
        ao.AOSchedule();
        schedule_AO = ao.schedule;
        PB pb = new PB();
        pb.PBSchedul();
        schedule_PB = pb.SchedulList;

    }

    public double Norm_rand(double miu, double sigma2){

        double N = 12; 
        double x=0,temp=N;

        do{
            x=0;
            for(int i=0;i<N;i++)  
                x=x+(Math.random());

            x=(x-temp/2)/(Math.sqrt(temp/12)); 
            x=miu+x*Math.sqrt(sigma2);

        }while(x<=0);          
        return x;
    }

    public double Exponent_rand(double lamda){

        double z =  Math.random();		
        double x;

        x = (-(1 / lamda) * Math.log(z)); 

        return x;
    }


    public static void main(String agrs[]) throws FileNotFoundException{

        Compare_TimeComplex ms = new Compare_TimeComplex();
        ms.dag = new DAG();

        ms.dag.InitDAG();

        AO ao = new AO();

        long startTime_AO = System.currentTimeMillis(); 
        for(int p=0;p<100;p++)
            ao.AOSchedule();  
            //ao.AOSchedule_sp();
        long endTime_AO = System.currentTimeMillis(); 			

        PB pb = new PB();
        long startTime_PB = System.currentTimeMillis(); 			
        for(int q=0;q<100;q++)
            pb.PBSchedul();
        long endTime_PB = System.currentTimeMillis();

        ICO ico = new ICO();
        long startTime_ICO = System.currentTimeMillis();
        for(int i = 0; i < 100; i++)
            ico.ICOSchedule();
        long endTime_ICO = System.currentTimeMillis();
        System.out.println("PB average runtime: "+(endTime_PB-startTime_PB)/100.00);
        System.out.println("AO average runtime: "+(endTime_AO-startTime_AO)/100.00);
        System.out.println("ICO average runtime: " + (endTime_ICO-startTime_ICO)/100.00);
    }

}
