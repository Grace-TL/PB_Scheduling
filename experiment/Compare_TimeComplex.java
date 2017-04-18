/**
 * ��ʵ�������Ƚ�PB��AO�㷨������ʱ��
 * �÷�����DAG.java �ļ���ָ��DAG��·�������б�ʵ�飬����̨���PB��AO�㷨������ʱ��
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
	 * ��ʼ�����������
	 * */
	public void init(int batchsize){

		//ÿ�����ε���������������ָ���ֲ�
		  for(int i=0; i<SIZE; i++)  
	            batchSize[i] = (int) Exponent_rand( 1.0/batchsize);  // lamda = 1.0/2;  //[1.0/2 ~ 1.0/32]  (2, 4, 8, 16, 32)
		  
		  // batch interarrival time
		  for(int j=1; j<SIZE; j++)     //batch0 ������ʱ��Ϊ0.0  ��interTime[0] = 0.0 
	            interTime[j] = Exponent_rand(1.0/10);  //[ 1.0/1000 ~ 1000.0/1]

		  
		  
	}
	
	public void initJobtime(int u, int d){

		//ÿ����ҵ��ִ��ʱ�䣬������̬�ֲ�
		  jobTime = new double[dag.NodeList.size()];
		  for(int k = 0 ; k < dag.NodeList.size() ; k++)  
			  jobTime[k] = Norm_rand(u,d); //���Ӿ�ֵΪ1 ������Ϊ0.1����̬�ֲ�  
		  
		  for(int i = 0; i < dag.NodeList.size();i++){	 
				 dag.NodeList.get(i).jobTime = jobTime[i]; //���Ӿ�ֵΪ1 ������Ϊ0.1����̬�ֲ�  
//					System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!! "+dag.NodeList.get(i).jobTime);
			  }
	}
	
	/**
	 * �õ������㷨�ĵ��Ƚ��
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
			  
		   }while(x<=0);          //�ڴ��Ұ�С��0�����ų�����
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
			
			dag_copy.NodeList.add((Node) node.clone());   //����dag
		
		int i = 0, p = 0; //pΪschedule��ָ��
		
		List<Node> eligibleList = new LinkedList<Node>();  //��ִ�н���б�
		
		eligibleList = dag_copy.GetEntryNodes(); //��entrynode�����ִ�н���б�
		
		while(dag_copy.NodeList.size() != 0){
			
			int cnum = batchSize[i];
			
			CURRENT_TIME += interTime[i];
			
			int num = Math.min(cnum, eligibleList.size());
			
			List<Node> allocated = new LinkedList<Node>();  //�ѷ������б�
			
			for(int j = 0; j< num ; j++){  //�����������,��eligibleList�еĽڵ㰴��schedule�е����ȼ������������
				
				Node node = eligibleList.get(0);
				
				for(Node enode : eligibleList){  //�ҳ����ȼ����Ľڵ�
					
					if(this.Getpri(schedule, node) > this.Getpri(schedule, enode)){  //schedule��Խ��ǰ�����ȼ�Խ��
						
						node = enode;

					}
				}
				
				allocated.add(node);
				eligibleList.remove(node);
				node.VertexStatue = 1;

			}
			
			for(Node anode : allocated){   //����ÿһ����������Ľ��

				dag_copy.DelNode(anode);  //ִ�иõ�
				
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
			dag_copy.NodeList.add((Node) node.clone());   //����dag
		
		int i = 0, p = 0; //pΪschedule��ָ��
		List<Node> eligibleList = new LinkedList<Node>();  //��ִ�н���б�
		eligibleList = dag_copy.GetEntryNodes(); //��entrynode�����ִ�н���б�
		List<Node> allocated = new LinkedList<Node>();  //�ѷ������б�
		double lastFinish = 0.0;
		while(dag_copy.NodeList.size() != 0){
			List<Node> deln = new LinkedList<Node>();

			for(Node anode : allocated){   //����ÿһ����������Ľ��
				
				if(anode.jobTime <= interTime[i] + CURRENT_TIME){
					
					dag_copy.DelNode(anode);  //ִ�иõ�
					deln.add(anode);
					lastFinish = Math.max(lastFinish, anode.jobTime);
//					System.out.println(" "+anode.jobTime+" : node "+anode.data+" is done");  //��ΰ�����������
					
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
				
				for(int j = 0; j< num&& p<schedule.size() ; j++){  //�����������
					
					  //�����������,��eligibleList�еĽڵ㰴��schedule�е����ȼ������������
					
					Node node = eligibleList.get(0);
					
					for(Node enode : eligibleList){  //�ҳ����ȼ����Ľڵ�
						
						if(this.Getpri(schedule, node) > this.Getpri(schedule, enode)){  //schedule��Խ��ǰ�����ȼ�Խ��
							
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
			
			int e1 = co_dag.GetEntryNodes().size() - 1; //������node����

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
//		PrintStream out=System.out; //����ԭ�����
//		PrintStream ps=new PrintStream("E:/result/[new]result_sp_0.txt"); //�����ļ������
//        System.setOut(ps); //����ʹ���µ������
		
		Compare_TimeComplex ms = new Compare_TimeComplex();
		//��ʼ��dag	
			ms.dag = new DAG();
			
			ms.dag.InitDAG();
			
			AO ao = new AO();

			 long startTime_AO = System.currentTimeMillis(); //��ȡϵͳʱ��
			 for(int p=0;p<100;p++)
			    ao.AOSchedule();  
//			 ao.AOSchedule_sp();
			 long endTime_AO = System.currentTimeMillis(); //��ȡʱ��
			
			
			PB pb = new PB();
			
			long startTime_PB = System.currentTimeMillis(); //��ȡϵͳʱ��
			for(int q=0;q<100;q++)
			    pb.PBSchedul();
			long endTime_PB = System.currentTimeMillis(); //��ȡʱ��
			System.out.println("PB�㷨100�ε�ƽ��ִ��ʱ��Ϊ��"+(endTime_PB-startTime_PB)/100.00);
			System.out.println("AO�㷨100�ε�ƽ��ִ��ʱ��Ϊ��"+(endTime_AO-startTime_AO)/100.00);
	}
	
}
