/**
 * 本实验用来比较六种不同调度算法的makespan结果
 * 用法： 在DAG.java中指定DAG的路径后，运行本实验 
 *     本实验默认统计不可准确预测时间的结果，若要统计可准确预测时间结果，请注释掉 代码中******Please Notice 2 ******* 部分的代码
 *     根据DAG类型的不同，选择适当的AO算法部分代码，详细见 代码中****************Please Notice 1 ******** 注释部分
 ***/
package experiment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import source.DAG;
import source.Node;
import AO.AO;
                                                                     
import Heft.Heft;
import PB.PB;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Compare_6schedule_toExcel {
	public static final int SIZE = 50000;
	private  double CURRENT_TIME = 0.0;
	int[] batchSize = new int[SIZE];
	double[] interTime = new double[SIZE];
	double[] jobTime = new double[SIZE];
	DAG dag = new DAG();
	List<Node> schedule_AO = new LinkedList<Node>();
	List<Node> schedule_PB = new LinkedList<Node>();
	private List<Node> schedule;

	/**
	 * 初始化各随机数组
	 * */
	public void init(int batchsize){

		//每个批次的主机数量，服从指数分布
		  for(int i=0; i<SIZE; i++)  
	            batchSize[i] = (int) Exponent_rand( 1.0/batchsize);  // lamda = 1.0/2;  //[1.0/2 ~ 1.0/32]  (2, 4, 8, 16, 32)
		  
		  // batch interarrival time
		  for(int j=1; j<SIZE; j++)     //batch0 到来的时刻为0.0  即interTime[0] = 0.0 
	            interTime[j] = Exponent_rand(1.0/5);  //[ 1.0/1000 ~ 1000.0/1]		  
	}
	
	
	/**
	 * 根据结点的jobtime方差，随机给其一个正太jobtime值
	 * */
	public void initJobtime( ){
		int u=0; //正态分布期望
		double v=0; //正态分布方差
		for(Node node : dag.NodeList){
			u = (int) node.jobTime;
			v = u/3.0;
			node.jobTime = Norm_rand(u,v*v); //服从均值为u ，方差为v的正态分布
		}			  
	}
	
	/**
	 * 得到两个算法的调度结果
	 * */
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
			  
		   }while(x<=0);          //在此我把小于0的数排除掉了
		  return x;
		 }

	public double Exponent_rand(double lamda){
		
		double z =  Math.random();		
		double x;
		
		x = (-(1 / lamda) * Math.log(z)); 
		
		return x;
		
	}
	
	
	
	public void makespan( ){
		dag.ResetStatue();
		CURRENT_TIME = 0.0;
		DAG dag_copy = new DAG();
		
		for(Node node : dag.NodeList)
			dag_copy.NodeList.add((Node) node.clone());   //复制dag
		
		int i = 0, p = 0; //p为schedule的指针
		List<Node> eligibleList = new LinkedList<Node>();  //可执行结点列表
		eligibleList = dag_copy.GetEntryNodes(); //将entrynode加入可执行结点列表
		List<Node> allocated = new LinkedList<Node>();  //已分配结点列表
		double lastFinish = 0.0;
		while(dag_copy.NodeList.size() != 0){
			List<Node> deln = new LinkedList<Node>();

			for(Node anode : allocated){   //对于每一个被分配过的结点
				
				if(anode.jobTime <= interTime[i] + CURRENT_TIME){
					
					dag_copy.DelNode(anode);  //执行该点
					deln.add(anode);
					lastFinish = Math.max(lastFinish, anode.jobTime);
//					System.out.println(" "+anode.jobTime+" : node "+anode.data+" is done");  //如何按照升序排列
										
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
				for(int j = 0; j< num&& p<schedule.size() ; j++){  //分配结点给请求				
					  //分配结点给请求,将eligibleList中的节点按照schedule中的优先级进行排序分配					
					Node node = eligibleList.get(0);				
					for(Node enode : eligibleList){  //找出优先级最大的节点					
						if(this.Getpri(schedule, node) > this.Getpri(schedule, enode)){  //schedule中越靠前，优先级越大						
							node = enode;
						}
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
		
//		System.out.println("-----------------------------------------------------");
//		System.out.println("The final makeSpan is "+lastFinish);
	}
	
	
	public void makespan_FIFO(){
		
		dag.ResetStatue();
		CURRENT_TIME = 0.0;
		DAG dag_copy = new DAG();
		
		for(Node node : dag.NodeList)
			dag_copy.NodeList.add((Node) node.clone());   //复制dag
		
		int i = 0;
		Queue<Node> eligibleList = new LinkedList<Node>();//可执行结点列表
		List<Node> entryNodes = new LinkedList<Node>();
		entryNodes = dag_copy.GetEntryNodes();
		while(entryNodes.size()!=0){
			Node enode = entryNodes.get(new Random().nextInt(entryNodes.size()));
			eligibleList.offer(enode);
			entryNodes.remove(enode);
		}
		List<Node> allocated = new LinkedList<Node>();  //已分配结点列表
		double lastFinish = 0.0;
		while(dag_copy.NodeList.size() != 0){
			
			List<Node> deln = new LinkedList<Node>();

			for(Node anode : allocated){   //对于每一个被分配过的结点
				
				if(anode.jobTime <= interTime[i] + CURRENT_TIME){
					
					dag_copy.DelNode(anode);  //执行该点
					deln.add(anode);
					lastFinish = Math.max(lastFinish, anode.jobTime);
//					System.out.println(" "+anode.jobTime+" : node "+anode.data+" is done");  //如何按照升序排列
										
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
				for(int j = 0; j< num ; j++){  //分配结点给请求				
					  //分配结点给请求,将eligibleList中的节点按照schedule中的优先级进行排序分配					
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
			dag_copy.NodeList.add((Node) node.clone());   //复制dag
		
		int i = 0;
		List<Node> eligibleList = new LinkedList<Node>();  //可执行结点列表
		eligibleList = dag_copy.GetEntryNodes(); //将entrynode加入可执行结点列表
		List<Node> allocated = new LinkedList<Node>();  //已分配结点列表
		double lastFinish = 0.0;
		while(dag_copy.NodeList.size() != 0){
			List<Node> deln = new LinkedList<Node>();

			for(Node anode : allocated){   //对于每一个被分配过的结点
				
				if(anode.jobTime <= interTime[i] + CURRENT_TIME){
					
					dag_copy.DelNode(anode);  //执行该点
					deln.add(anode);
					lastFinish = Math.max(lastFinish, anode.jobTime);
//					System.out.println(" "+anode.jobTime+" : node "+anode.data+" is done");  //如何按照升序排列
										
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
				for(int j = 0; j< num ; j++){  //分配结点给请求				
					  //分配结点给请求,将eligibleList中的节点按照schedule中的优先级进行排序分配					
					Node node = eligibleList.get(0);				
					for(Node enode : eligibleList){  //找出优先级最大的节点	
						if(kind == 0){
							if(enode.jobTime > node.jobTime)  //时间花销越大，优先级越大						
								node = enode;
						}else if(kind == 1)
							if(enode.jobTime < node.jobTime)  //时间花销越小，优先级越大						
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
		
		List<Integer> eligi = new LinkedList<Integer>();		
		List<Integer> total = new LinkedList<Integer>();		
		DAG co_dag = new DAG();		
		int area = 0;		
		for(Node node : dag.NodeList)		
			co_dag.NodeList.add((Node)node.clone());	
		for(Node node :schedule){		
			int e1 = co_dag.GetEntryNodes().size() - 1; //不能算node本身
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
//		PrintStream out=System.out; //保存原输出流
//        PrintStream ps=new PrintStream("result/pb0_pb.txt"); //创建文件输出流
//        System.setOut(ps); //设置使用新的输出流
		
		WritableWorkbook wwb = null;
		// 创建可写入的Excel工作簿
        String fileName = "D://book_CBBBC_502_no.xls";
        File file=new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        //以fileName为文件名来创建一个Workbook
        wwb = Workbook.createWorkbook(file);

        // 创建工作表
        WritableSheet ws = wwb.createSheet("Test Shee 1", 0);
        //要插入到的Excel表格的行号，默认从0开始
        Label labelnpb= new Label(0, 0, "nPB");
        Label labelpb= new Label(1, 0, "PB");
        Label labelheft= new Label(2, 0, "Heft");
        Label labelao= new Label(3, 0, "AO");
        Label labelfifo= new Label(4, 0, "FIFO");
        Label labelltf= new Label(5, 0, "LTF");
        Label labelstf= new Label(6, 0, "STF");

        
        ws.addCell(labelnpb);
        ws.addCell(labelpb);
        ws.addCell(labelheft);
        ws.addCell(labelao);
        ws.addCell(labelfifo);
        ws.addCell(labelltf);
        ws.addCell(labelstf);
        
        
		int runtimes=100;
		Compare_6schedule_toExcel ms = new Compare_6schedule_toExcel();
		//初始化dag		
			ms.dag = new DAG();		
			ms.dag.InitDAG();
			ms.dag.initJobtime(100);
			//原版pb
			PB pb = new PB();
			pb.PBSchedul();
			ms.getEligible(ms.dag , pb.SchedulList);
			
			PB pb0 = new PB();
			pb0.PBSchedul(ms.dag.CopyDag());
			ms.getEligible(ms.dag , pb0.SchedulList);
			
			AO ao = new AO();
			/***************************Please notice 1 ************************************************************/
			ao.AOSchedule();   //若DAG为非SP DAG 请执行本步代码，注释掉下一行
//			ao.AOSchedule_sp();   //若DAG为SP DAG 请执行本步代码，注释掉上一行
			/***************************end notice  ****************************************************************/
			ms.getEligible(ms.dag, ao.schedule);
			
			
			Heft heft = new Heft();
			heft.HeftSchedule();
			ms.getEligible(ms.dag, heft.SchedulList);
			
			/***************************Please notice 2 ************************************************************/
			//根据期望，随机分配一个作业执行时间
			ms.initJobtime( );	  //若运行该步，则实验为不可准确预测时间；注释掉该步，实验为可准确预测时间
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
				int  count_2=0;
				int countpb_2=0;
				batchsize *=2;
				System.out.println("----------------------Batch size is---------------------- "+batchsize);
				for(int i = 0; i<runtimes; i++){
					
					//初始化batch size 和 inter time
					ms.init(batchsize);	
				//计算PB0的makespan
					ms.schedule = pb0.SchedulList;
					ms.makespan();				
					npbTime_2 +=ms.CURRENT_TIME;					
					double t1_2 = ms.CURRENT_TIME;
//					System.out.println("----------------------PB0---------------------- ");	
				//计算PB的makespan
					ms.schedule = pb.SchedulList;			
					ms.makespan();					
					pbTime_2 += ms.CURRENT_TIME;
					double t2_2 = ms.CURRENT_TIME;
//					System.out.println("----------------------PB---------------------- ");	
				//计算AO的makespan
					ms.schedule = ao.schedule;			
					ms.makespan();					
					aoTime_2 += ms.CURRENT_TIME;
//					System.out.println("----------------------AO---------------------- ");	
				//计算Heft的makespan
					ms.schedule = heft.SchedulList;			
					ms.makespan();					
					heftTime_2 += ms.CURRENT_TIME;
//					System.out.println("----------------------Heft---------------------- ");	
					if(t1_2 > t2_2)
						countpb_2++;
					else if(t1_2 < t2_2)
						count_2++;
					
					//计算FIFO的makespan
					ms.makespan_FIFO();
					fifoTime_2 += ms.CURRENT_TIME;
//					System.out.println("----------------------FIFO---------------------- ");	
					//计算LTF和STF的makespan
					ms.makespan_LSTF(0);
					ltfTime_2 += ms.CURRENT_TIME;
//					System.out.println("----------------------LTF---------------------- ");	
					ms.makespan_LSTF(1);
//					System.out.println("----------------------STF---------------------- ");	
					stfTime_2 += ms.CURRENT_TIME;
				}
				  Label Labelnpb= new Label(0, loop+1, npbTime_2/runtimes+"");
                  Label Labelpb= new Label(1, loop+1, pbTime_2/runtimes+"");
                  Label Labelheft= new Label(2, loop+1, heftTime_2/runtimes+"");
                  Label Labelao= new Label(3, loop+1, aoTime_2/runtimes+"");
                  Label Labelfifo= new Label(4, loop+1, fifoTime_2/runtimes+"");
                  Label Labelltf= new Label(5, loop+1, ltfTime_2/runtimes+"");
                  Label Labelstf= new Label(6, loop+1, stfTime_2/runtimes+"");
                  ws.addCell(Labelnpb);
                  ws.addCell(Labelpb);
                  ws.addCell(Labelheft);
                  ws.addCell(Labelao);
                  ws.addCell(Labelfifo);
                  ws.addCell(Labelltf);
                  ws.addCell(Labelstf);
				System.out.println("The average MakeSpan of PB0 is "+npbTime_2/runtimes);
				System.out.println("The average MakeSpan of PB is "+pbTime_2/runtimes);
				System.out.println("The average MakeSpan of FIFO is "+fifoTime_2/runtimes);
				System.out.println("The average MakeSpan of AO is "+aoTime_2/runtimes);
				System.out.println("The average MakeSpan of Heft is "+heftTime_2/runtimes);
				System.out.println("The average MakeSpan of LTF is "+ltfTime_2/runtimes);
				System.out.println("The average MakeSpan of STF is "+stfTime_2/runtimes);
				System.out.println("PB0 better time "+count_2+"  PB better time "+countpb_2);
			}
			System.out.println("Finish!");
			
			//写进文档
            wwb.write();
           // 关闭Excel工作簿对象
            wwb.close();
	}
}
