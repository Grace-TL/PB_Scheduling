package experiment;
import AO.AO;
import FIFO.FIFO;
import ICO.ICO;
import source.DAG;
import source.Node;
import Heft.Heft;
import PB.PB;
import PB.EPB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Makespan_Test_100_c1000p {
	public static final int SIZE = 50000;
	private  double CURRENT_TIME = 0.0;
	int[] batchSize = new int[SIZE];
	double[] interTime = new double[SIZE];
	double[] jobTime = new double[SIZE];
	DAG dag = new DAG();
	List<Node> schedule_AO = new LinkedList<Node>();
	List<Node> schedule_PB = new LinkedList<Node>();
	private List<Node> schedule;
    public String dagpath;
    String debug = "";
    String debug1 = "";

	public void init(int batchsize){

		  for(int i=0; i<SIZE; i++)  
	            batchSize[i] = (int) Exponent_rand( 1.0/batchsize);  // lamda = 1.0/2;  //[1.0/2 ~ 1.0/32]  (2, 4, 8, 16, 32)
		  
		  // batch interarrival time
		  for(int j=1; j<SIZE; j++)      
	            interTime[j] = Exponent_rand(1.0/5);  //[ 1.0/1000 ~ 1000.0/1]		  
	}
	
	
	public void initJobtime( ){
		int u=0; 
        double v=0; 
		for(Node node : dag.NodeList){
			u = (int) node.jobTime;
			v = u/3.0;
			node.jobTime = Norm_rand(u,v*v); 
		}			  
	}
	
	
    public void Getschedule() throws FileNotFoundException{
		
		AO ao = new AO();
		ao.AOSchedule(this.dagpath);
		schedule_AO = ao.schedule;
		PB pb = new PB();
		pb.PBSchedul(this.dagpath);
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
	
	   /**
     * With the assumtion that each task can be finished before the next batch
     * comes
     * 	 */
    public void makespan_1(){

        dag.ResetStatue();
        CURRENT_TIME = 0.0;
        DAG dag_copy = new DAG();
        for(Node node : dag.NodeList)
            dag_copy.NodeList.add((Node) node.clone());   		
        int i = 0, p = 0; 
        List<Node> eligibleList = new LinkedList<Node>();  
        eligibleList = dag_copy.GetEntryNodes(); 
        while(dag_copy.NodeList.size() != 0){
            int cnum = batchSize[i];
            CURRENT_TIME += interTime[i];
            int num = Math.min(cnum, eligibleList.size());
            List<Node> allocated = new LinkedList<Node>();  
            for(int j = 0; j< num ; j++){  				
                Node node = eligibleList.get(0);
                for(Node enode : eligibleList){  					
                    if(this.Getpri(schedule, node) > this.Getpri(schedule, enode)){  						
                        node = enode;
                    }
                }
                allocated.add(node);
                eligibleList.remove(node);
                node.VertexStatue = 1;
            }

            for(Node anode : allocated){   
                dag_copy.DelNode(anode);  				
                List<Node> neweli = new LinkedList<Node>();
                neweli = dag_copy.GetEntryNodes();
                for(Node newnode : neweli)
                    if(!dag_copy.contain(eligibleList, newnode )&&newnode.VertexStatue == 0){
                        eligibleList.add(newnode);
                    }
            }
            i++;
        }

    }


	
	public void makespan( ){
		dag.ResetStatue();
		CURRENT_TIME = 0.0;
		DAG dag_copy = new DAG();
		
		for(Node node : dag.NodeList)
			dag_copy.NodeList.add((Node) node.clone());   
	
		int i = 0, p = 0; 
		List<Node> eligibleList = new LinkedList<Node>(); 
		eligibleList = dag_copy.GetEntryNodes(); 
		List<Node> allocated = new LinkedList<Node>();  
		double lastFinish = 0.0;
		while(dag_copy.NodeList.size() != 0){
			List<Node> deln = new LinkedList<Node>();
            
           

            for(Node anode : allocated){   
				
				if(anode.jobTime <= interTime[i] + CURRENT_TIME){
					
					dag_copy.DelNode(anode);  
					deln.add(anode);
					lastFinish = Math.max(lastFinish, anode.jobTime);
										
					//Add the new eligible nodes.
					List<Node> neweli = new LinkedList<Node>();					
					neweli = dag_copy.GetEntryNodes();					
					for(Node newnode : neweli)					
						if(!dag_copy.contain(eligibleList, newnode )&&newnode.VertexStatue == 0){						
							eligibleList.add(newnode);							
                            //							System.out.println(" "+anode.jobTime+" : node "+newnode.data+" becomes eligible");
                        }	
                }		
            }	

            for(Node danode : deln)
                allocated.remove(danode);

            if(dag_copy.NodeList.size() !=0 ){			
                int cnum = batchSize[i];				
                CURRENT_TIME += interTime[i];			
                debug += System.lineSeparator() + "----------- batch "+i+" arrives, size: "+cnum+"--------------\n";
                //				System.out.println(" "+CURRENT_TIME+" : batch "+i+" arrives. size : "+cnum);				
                //debug
                debug += System.lineSeparator() + "[ Eligible List] : ";
                for(Node node: eligibleList) {
                    debug += node.data + ", ";
                }
                //debug
                debug += System.lineSeparator() + "[ Allocate List] : ";


                int num = Math.min(cnum, eligibleList.size());				
                for(int j = 0; j< num&& p<schedule.size() ; j++){  
                    Node node = eligibleList.get(0);				
                    for(Node enode : eligibleList){  
                        if(this.Getpri(schedule, node) > this.Getpri(schedule, enode)){  						
                            node = enode;
                        }
                    }
                    //					System.out.println(" "+CURRENT_TIME+" : node "+node.data+" is allocated ");
                    allocated.add(dag_copy.FindNode(node.data));
                    debug += node.data + ", ";
                    dag_copy.FindNode(node.data).jobTime += CURRENT_TIME;
                    eligibleList.remove(node);
                    node.VertexStatue = 1;


                }
            }
     //debug
            debug += System.lineSeparator() + "[ Finish list ]: ";
            for(Node node: deln) {
                debug += node.data + ", ";
            }


            i++;
        }

        //		System.out.println("-----------------------------------------------------");
        //		System.out.println("The final makeSpan is "+lastFinish);
    }


    public void makespan_FIFO(){

        dag.ResetStatue();
        CURRENT_TIME = 0.0;
        DAG dag_copy = new DAG();

        for(Node node : dag.NodeList)
            dag_copy.NodeList.add((Node) node.clone());   

        int i = 0;
        Queue<Node> eligibleList = new LinkedList<Node>();
        List<Node> entryNodes = new LinkedList<Node>();
        entryNodes = dag_copy.GetEntryNodes();
        while(entryNodes.size()!=0){
            Node enode = entryNodes.get(new Random().nextInt(entryNodes.size()));
            eligibleList.offer(enode);
            entryNodes.remove(enode);
        }
        List<Node> allocated = new LinkedList<Node>(); 
        double lastFinish = 0.0;
        while(dag_copy.NodeList.size() != 0){

            List<Node> deln = new LinkedList<Node>();

            for(Node anode : allocated){   

                if(anode.jobTime <= interTime[i] + CURRENT_TIME){

                    dag_copy.DelNode(anode);  
                    deln.add(anode);
                    lastFinish = Math.max(lastFinish, anode.jobTime);

                    //Add the new eligible nodes.
                    List<Node> neweli = new LinkedList<Node>();					
                    neweli = dag_copy.GetEntryNodes();	

                    while(neweli.size()!=0){
                        Node newnode = neweli.get(new Random().nextInt(neweli.size()));
                        if(!dag_copy.contain((List<Node>) eligibleList, newnode )&&newnode.VertexStatue == 0){						
                            eligibleList.offer(newnode);	
                        }
                        neweli.remove(newnode);
                    }		
                }	
            }
            for(Node danode : deln)
                allocated.remove(danode);

            if(dag_copy.NodeList.size() !=0 ){				
                int cnum = batchSize[i];				
                CURRENT_TIME += interTime[i];				
                //				System.out.println(" "+CURRENT_TIME+" : batch "+i+" arrives. size : "+cnum);				
                int num = Math.min(cnum, eligibleList.size());				
                for(int j = 0; j< num ; j++){  
                    Node node = eligibleList.poll();				

                    //					System.out.println(" "+CURRENT_TIME+" : node "+node.data+" is allocated ");
                    allocated.add(dag_copy.FindNode(node.data));
                    dag_copy.FindNode(node.data).jobTime += CURRENT_TIME;
                    node.VertexStatue = 1;
                }
            }
            i++;
        }	

    }



    public void makespan_LSTF(int kind){
        dag.ResetStatue();
        CURRENT_TIME = 0.0;
        DAG dag_copy = new DAG();

        for(Node node : dag.NodeList)
            dag_copy.NodeList.add((Node) node.clone());   

        int i = 0;
        List<Node> eligibleList = new LinkedList<Node>();  
        eligibleList = dag_copy.GetEntryNodes(); 
        List<Node> allocated = new LinkedList<Node>();  
        double lastFinish = 0.0;
        while(dag_copy.NodeList.size() != 0){
            List<Node> deln = new LinkedList<Node>();

            for(Node anode : allocated){   

                if(anode.jobTime <= interTime[i] + CURRENT_TIME){

                    dag_copy.DelNode(anode);  
                    deln.add(anode);
                    lastFinish = Math.max(lastFinish, anode.jobTime);

                    //Add the new eligible nodes.
                    List<Node> neweli = new LinkedList<Node>();					
                    neweli = dag_copy.GetEntryNodes();					
                    for(Node newnode : neweli)					
                        if(!dag_copy.contain(eligibleList, newnode )&&newnode.VertexStatue == 0){						
                            eligibleList.add(newnode);							
                            //							System.out.println(" "+anode.jobTime+" : node "+newnode.data+" becomes eligible");
                        }	
                }		
            }			
            for(Node danode : deln)
                allocated.remove(danode);

            if(dag_copy.NodeList.size() !=0 ){				
                int cnum = batchSize[i];				
                CURRENT_TIME += interTime[i];				
                //				System.out.println(" "+CURRENT_TIME+" : batch "+i+" arrives. size : "+cnum);				
                int num = Math.min(cnum, eligibleList.size());				
                for(int j = 0; j< num ; j++){  
                    Node node = eligibleList.get(0);				
                    for(Node enode : eligibleList){  
                        if(kind == 0){
                            if(enode.jobTime > node.jobTime)  
                                node = enode;
                        }else if(kind == 1)
                            if(enode.jobTime < node.jobTime)  
                                node = enode;
                    }
                    //					System.out.println(" "+CURRENT_TIME+" : node "+node.data+" is allocated ");
                    allocated.add(dag_copy.FindNode(node.data));
                    dag_copy.FindNode(node.data).jobTime += CURRENT_TIME;
                    eligibleList.remove(node);
                    node.VertexStatue = 1;			
                }
            }
            i++;
        }
    }

    public int Getpri(List<Node> schedule , Node node){

        for(Node snode : schedule)
            if(snode.data == node.data)
                return schedule.indexOf(snode);
        return -1;
    }

    public void getEligible(DAG dag , List<Node> schedule){

        System.out.println("Schedule :");
        for(Node node : schedule) {
            System.out.print(node.data+" ");
        }
        System.out.println();

        List<Integer> eligi = new LinkedList<Integer>();		
		List<Integer> total = new LinkedList<Integer>();		
		DAG co_dag = new DAG();		
		int area = 0;		
		for(Node node : dag.NodeList)		
			co_dag.NodeList.add((Node)node.clone());	
		for(Node node :schedule){		
			int e1 = co_dag.GetEntryNodes().size() - 1; 
			co_dag.DelNode(node);
			int e2 = co_dag.GetEntryNodes().size();
			total.add(e2);
			eligi.add(e2 - e1)  ;  
	}
		System.out.print("eligible jobs rendered by  : ");
		for(Integer i : eligi)
			System.out.print(" "+i);
		System.out.println();
		System.out.print("the total eligible jobs after t's execution : ");
		for(Integer i : total){			
			System.out.print(" "+i);
			area += i;
		}
		System.out.println();
		System.out.println("The area is "+area+", "+"the average area is "+(double)area/dag.NodeList.size());
	}
	
    public static void main(String agrs[]) throws IOException, RowsExceededException, WriteException{

        WritableWorkbook wwb = null;
        String fileName = "/Users/tanglu/Desktop/100_c1000_pre_tmp";
        File file=new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        wwb = Workbook.createWorkbook(file);

        WritableSheet ws = wwb.createSheet("Test Shee 1", 0);
        Label labeltitle = new Label(0, 0, "Batchsize");
        Label labelnpb= new Label(1, 0, "EPB");
        Label labelpb= new Label(2, 0, "PB");
        Label labelheft= new Label(3, 0, "Heft");
        Label labelao= new Label(4, 0, "AO");
        Label labelico = new Label(5, 0, "ICO");
        Label labelfifo= new Label(6, 0, "FIFO");
        Label labelltf= new Label(7, 0, "LTF");
        Label labelstf= new Label(8, 0, "STF");

        ws.addCell(labeltitle);
        ws.addCell(labelnpb);
        ws.addCell(labelpb);
        ws.addCell(labelheft);
        ws.addCell(labelao);
        ws.addCell(labelico);
        ws.addCell(labelfifo);
        ws.addCell(labelltf);
        ws.addCell(labelstf);


        int runtimes=100;
        Makespan_Test_100_c1000p ms = new Makespan_Test_100_c1000p();
        ms.dagpath = "DAG_Cbbbs/cbbb_1000_0.txt";
        //ms.dagpath = "DAG_SP/sp_100_0.txt";
        //ms.dagpath = "DAG_Random/random_500_0.txt";
        ms.dag = new DAG();		
        ms.dag.InitDAG(ms.dagpath);
        ms.dag.initJobtime(100);

        PB pb = new PB();  
        pb.PBSchedul(ms.dagpath);
        System.out.println("Finish PB!");
        ms.getEligible(ms.dag , pb.SchedulList);

        AO ao = new AO();
        /***************************Please notice 1 ************************************************************/
        ao.AOSchedule(ms.dagpath);  
        System.out.println("Finish AO!");
        /***************************end notice  ****************************************************************/
        ms.getEligible(ms.dag, ao.schedule);

        ICO ico = new ICO();
        ico.ICOSchedule(ms.dagpath);
        System.out.println("Finish ICO!");
        ms.getEligible(ms.dag, ico.schedule);

        Heft heft = new Heft();
        heft.HeftSchedule(ms.dag.CopyDag());
        System.out.println("Finish Heft!");
        ms.getEligible(ms.dag, heft.SchedulList);
       
        EPB epb = new EPB();
        epb.EPBSchedul(ms.dag.CopyDag());
        System.out.println("Finish EPB!");
        ms.getEligible(ms.dag , epb.SchedulList);



        /***************************Please notice 2 ************************************************************/
        //actual run time
        //ms.initJobtime( );	  
        /**************************** end notice ****************************************************************/

        int batchsize=1;			
        for(int loop=1;loop<9;loop++){

            double npbTime_2=0.0;
            double pbTime_2=0.0;
            double fifoTime_2=0.0;
            double aoTime_2=0.0;
            double ltfTime_2=0.0;
            double stfTime_2=0.0;
            double heftTime_2 = 0.0;
            double icoTime_2 = 0.0;
            int  count_2=0;
            int countpb_2=0;
            batchsize *=2;
            System.out.println("----------------------Batch size is---------------------- "+batchsize);
            for(int i = 0; i<runtimes; i++){

                ms.debug = " ";
                ms.init(batchsize);	
                ms.schedule = epb.SchedulList;
                ms.makespan();				
                npbTime_2 +=ms.CURRENT_TIME;
                ms.debug1 = ms.debug;
                double t1_2 = ms.CURRENT_TIME;
                //					System.out.println("----------------------PB0---------------------- ");	
                ms.schedule = pb.SchedulList;			
                ms.makespan();					
                pbTime_2 += ms.CURRENT_TIME;
                //					System.out.println("----------------------PB---------------------- ");	
                ms.schedule = ao.schedule;			
                ms.makespan();					
                aoTime_2 += ms.CURRENT_TIME;
                //					System.out.println("----------------------AO---------------------- ");	
                
                ms.debug = " ";
                ms.schedule = heft.SchedulList;			
                ms.makespan();					
                heftTime_2 += ms.CURRENT_TIME;
                double t2_2 = ms.CURRENT_TIME;
                //					System.out.println("----------------------Heft---------------------- ");	
                if(t1_2 > t2_2){
                    System.out.println("blevel------------------------\n" + ms.debug);
                    System.out.println("EPB---------------------------\n " + ms.debug1);
                }
                ms.schedule = ico.schedule;
                ms.makespan();
                icoTime_2 += ms.CURRENT_TIME;

                ms.makespan_FIFO();
                fifoTime_2 += ms.CURRENT_TIME;
                //					System.out.println("----------------------FIFO---------------------- ");	
                ms.makespan_LSTF(0);
                ltfTime_2 += ms.CURRENT_TIME;
                //					System.out.println("----------------------LTF---------------------- ");	
                ms.makespan_LSTF(1);
                //					System.out.println("----------------------STF---------------------- ");	
                stfTime_2 += ms.CURRENT_TIME;
            }
            Label Labeltitle = new Label(0, loop+1, batchsize+"");
            Label Labelnpb= new Label(1, loop+1, npbTime_2/runtimes+"");
            Label Labelpb= new Label(2, loop+1, pbTime_2/runtimes+"");
            Label Labelheft= new Label(3, loop+1, heftTime_2/runtimes+"");
            Label Labelao= new Label(4, loop+1, aoTime_2/runtimes+"");
            Label Labelico = new Label(5, loop+1, icoTime_2/runtimes+"");
            Label Labelfifo= new Label(6, loop+1, fifoTime_2/runtimes+"");
            Label Labelltf= new Label(7, loop+1, ltfTime_2/runtimes+"");
            Label Labelstf= new Label(8, loop+1, stfTime_2/runtimes+"");
            ws.addCell(Labeltitle);
            ws.addCell(Labelnpb);
            ws.addCell(Labelpb);
            ws.addCell(Labelheft);
            ws.addCell(Labelao);
            ws.addCell(Labelico);
            ws.addCell(Labelfifo);
            ws.addCell(Labelltf);
            ws.addCell(Labelstf);
            System.out.println("The average MakeSpan of EPB is "+npbTime_2/runtimes);
            System.out.println("The average MakeSpan of PB is "+pbTime_2/runtimes);
            System.out.println("The average MakeSpan of FIFO is "+fifoTime_2/runtimes);
            System.out.println("The average MakeSpan of AO is "+aoTime_2/runtimes);
            System.out.println("The average MakeSpan of Heft is "+heftTime_2/runtimes);
            System.out.println("The average MakeSpan of ICO is " + icoTime_2/runtimes);
            System.out.println("The average MakeSpan of LTF is "+ltfTime_2/runtimes);
            System.out.println("The average MakeSpan of STF is "+stfTime_2/runtimes);
            System.out.println("EPB better time "+count_2+"  PB better time "+countpb_2);
        }
        System.out.println("Finish!");
        wwb.write();
        wwb.close();
    }
}
