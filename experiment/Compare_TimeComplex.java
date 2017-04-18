/**
 * 该实验用来比较PB和AO算法的运行时间
 * 用法：在DAG.java 文件中指定DAG的路径后，运行本实验，控制台输出PB和AO算法的运行时间
 * **/


package experiment;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import AO.AO;
import FIFO.FIFO;
import PB.PB;
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

	/**
	 * 初始化各随机数组
	 * */
	public void init(int batchsize){

		//每个批次的主机数量，服从指数分布
		  for(int i=0; i<SIZE; i++)  
	            batchSize[i] = (int) Exponent_rand( 1.0/batchsize);  // lamda = 1.0/2;  //[1.0/2 ~ 1.0/32]  (2, 4, 8, 16, 32)
		  
		  // batch interarrival time
		  for(int j=1; j<SIZE; j++)     //batch0 到来的时刻为0.0  即interTime[0] = 0.0 
	            interTime[j] = Exponent_rand(1.0/10);  //[ 1.0/1000 ~ 1000.0/1]

		  
		  
	}
	
	public void initJobtime(int u, int d){

		//每个作业的执行时间，服从正态分布
		  jobTime = new double[dag.NodeList.size()];
		  for(int k = 0 ; k < dag.NodeList.size() ; k++)  
			  jobTime[k] = Norm_rand(u,d); //服从均值为1 ，方差为0.1的正态分布  
		  
		  for(int i = 0; i < dag.NodeList.size();i++){	 
				 dag.NodeList.get(i).jobTime = jobTime[i]; //服从均值为1 ，方差为0.1的正态分布  
//					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!! "+dag.NodeList.get(i).jobTime);
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
	
	
	public void makespan_1(){
		
		dag.ResetStatue();
		
		CURRENT_TIME = 0.0;
		
		DAG dag_copy = new DAG();
		
		for(Node node : dag.NodeList)
			
			dag_copy.NodeList.add((Node) node.clone());   //复制dag
		
		int i = 0, p = 0; //p为schedule的指针
		
		List<Node> eligibleList = new LinkedList<Node>();  //可执行结点列表
		
		eligibleList = dag_copy.GetEntryNodes(); //将entrynode加入可执行结点列表
		
		while(dag_copy.NodeList.size() != 0){
			
			int cnum = batchSize[i];
			
			CURRENT_TIME += interTime[i];
			
			int num = Math.min(cnum, eligibleList.size());
			
			List<Node> allocated = new LinkedList<Node>();  //已分配结点列表
			
			for(int j = 0; j< num ; j++){  //分配结点给请求,将eligibleList中的节点按照schedule中的优先级进行排序分配
				
				Node node = eligibleList.get(0);
				
				for(Node enode : eligibleList){  //找出优先级最大的节点
					
					if(this.Getpri(schedule, node) > this.Getpri(schedule, enode)){  //schedule中越靠前，优先级越大
						
						node = enode;

					}
				}
				
				allocated.add(node);
				eligibleList.remove(node);
				node.VertexStatue = 1;

			}
			
			for(Node anode : allocated){   //对于每一个被分配过的结点

				dag_copy.DelNode(anode);  //执行该点
				
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
	public void makespan_2( ){
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
	
	
	public static void main(String agrs[]) throws FileNotFoundException{
//		PrintStream out=System.out; //保存原输出流
//		PrintStream ps=new PrintStream("E:/result/[new]result_sp_0.txt"); //创建文件输出流
//        System.setOut(ps); //设置使用新的输出流
		
		Compare_TimeComplex ms = new Compare_TimeComplex();
		//初始化dag	
			ms.dag = new DAG();
			
			ms.dag.InitDAG();
			
			AO ao = new AO();

			 long startTime_AO = System.currentTimeMillis(); //获取系统时间
			 for(int p=0;p<100;p++)
			    ao.AOSchedule();  
//			 ao.AOSchedule_sp();
			 long endTime_AO = System.currentTimeMillis(); //获取时间
			
			
			PB pb = new PB();
			
			long startTime_PB = System.currentTimeMillis(); //获取系统时间
			for(int q=0;q<100;q++)
			    pb.PBSchedul();
			long endTime_PB = System.currentTimeMillis(); //获取时间
			System.out.println("PB算法100次的平均执行时间为："+(endTime_PB-startTime_PB)/100.00);
			System.out.println("AO算法100次的平均执行时间为："+(endTime_AO-startTime_AO)/100.00);
	}
	
}
