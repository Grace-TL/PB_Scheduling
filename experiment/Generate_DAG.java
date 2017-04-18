/**
 * 本实验用来产生随机DAG和CBBBC DAG
 * 
 * CreateDAG(n, lev, serial);   //随机创建dag n为结点个数，lev为DAG层次，serial为文件名序号
   GetCBBB_DAG(ge.CreateCBBBs(p),serial);  //创建CBBBC DAG  p为结点个数，serial为文件名序号
 * 
 * **/
package experiment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import source.DAG;
import source.Node;


public class Generate_DAG {

	public int N;
	
	public int levels;
	
	public void CreateDAG( int n, int lev, int number ) throws FileNotFoundException{

		DAG dag = new DAG();		
		Random rand = new Random();		
		int N = rand.nextInt(n)+1;  //随机产生10 ~ 60之间的数字		
		int levels = rand.nextInt(lev)+1;   //随机产生1 ~ 12之间的数字		
		System.out.println("N "+N+" levels "+levels);		
		String path = "Random_DAG/dag_"+number+".txt";
		PrintStream ps=new PrintStream(new FileOutputStream(path));  		 
	    System.setOut(ps);   //重定向输出流  		
		for(int i = 1 ; i <= N ; i++){  //结点数统一从1开始，除了st的时候才有0结点		
			Node node = new Node(i);			
			dag.NodeList.add(node);			
		}	
		for(int j = 0 ; j <= levels; ){  //为每层首先分配一个结点，确保每层不空		
			int index = rand.nextInt(N-1);		
			if(dag.NodeList.get(index).VertexStatue != 1){			
				dag.NodeList.get(index).layer = j;	
				dag.NodeList.get(index).VertexStatue = 1;	
				j++;
			}
		}	
		for(int i = 0 ; i < N ; i++){  //随机分配其余结点到各层	
			int level = rand.nextInt(levels);	
			if(dag.NodeList.get(i).VertexStatue != 1){	
				dag.NodeList.get(i).layer = level;
				dag.NodeList.get(i).VertexStatue = 1;
			}
		}
		
		for(int i = 0 ; i < levels; i++){  //除了最底层结点，为每个节点随机指定子节点
			List<Node> upList = new LinkedList<Node>();
			List<Node> downList = new LinkedList<Node>();
			for(Node node : dag.NodeList)	
				if(node.layer == i)	
					upList.add(node);
				else if(node.layer == i+1)	
					downList.add(node);	
			for(Node upnode : upList){
				int child_num = rand.nextInt(downList.size());   //该处是child_num 是否可为0呢		
				if(child_num == 0)	
					child_num = 1;
				for(int j = 0; j < child_num ; ){	
					int id ;	
					if(downList.size()==1)		
						id = 0;	
					else			
						id = rand.nextInt(downList.size()-1);
					if(!upnode.next.contains(downList.get(id))){
						upnode.next.add(downList.get(id));
						downList.get(id).previous.add(upnode);						
						j++;
					}
				}
			}
			
			for(Node dnode : downList){				
				if(dnode.previous.size()==0){  //处理非entrynode但是没有父节点的情况					
					int parent_num = rand.nextInt(upList.size());					
					if(parent_num == 0)						
						parent_num = 1;					
					for(int j = 0 ; j < parent_num; ){						
						int id;						
						if(upList.size() == 1)							
							id = 0;						
						else							
							id = rand.nextInt(upList.size()-1);						
						if(!dnode.previous.contains(upList.get(id))){							
							dnode.previous.add(upList.get(id));							
							upList.get(id).next.add(dnode);							
							j++;
						}
					}
				}
			}
		}
		
		System.out.println(N);
		for(Node node : dag.NodeList){			
			for(Node next : node.next)		
				System.out.println(node.data+" "+next.data);
		}
	}
	
	public List<DAG> CreateCBBBs(int p){
		
		Random rand = new Random();
		N = rand.nextInt(p)+1;  //随机产生10 ~ 60之间的数字
		System.out.println("所产生的N为"+N);
		int current_N = 0;
		List<DAG> CBBBs = new LinkedList<DAG>();
		
		while(current_N < N){
			
		int cid = rand.nextInt(4)+1;  //1. W 2. M 3. N 4. C 5.Q
//		System.out.println(cid+"  &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& "+current_N+"  0000000000 "+N);
		switch(cid){
		case 1: {
			
			int n = N - current_N;
			int x =  Math.max(rand.nextInt(Math.max((int)Math.sqrt(n), 1)), 1);
			int y =  Math.max(rand.nextInt(Math.max((int)Math.sqrt(n), 1)), 2);
			CBBBs.add(CreateW(x, y, current_N));
			
			current_N += (x*y+1);
			break;
		}
		
		case 2:{
			
			int n = N - current_N;
			int x = Math.max(rand.nextInt(Math.max((int)Math.sqrt(n), 1)), 1);
			int y = Math.max(rand.nextInt(Math.max((int)Math.sqrt(n), 1)), 2);
			CBBBs.add(CreateM(x,y,current_N));
			
			current_N += (x*y+1);
			break;
		} 
		
		case 3: {
			
			int n = N - current_N;
			int x = Math.max(rand.nextInt(Math.max(1, n/2)), 2);
			CBBBs.add(CreateN(x,current_N));
			
			current_N += 2*x;
			break;
		}
		
		case 4:{
			
			int n = N - current_N;
			int x = Math.max(rand.nextInt(Math.max(1, n/2)), 2);
			CBBBs.add(CreateC(x,current_N));
			
			current_N += 2*x;
			break;
		}
			
		case 5:{
			
			int n = N - current_N;
			int x = Math.max(rand.nextInt(Math.max(1, n/2)), 2);
			CBBBs.add(CreateQ(x,current_N));
			
			current_N += 2*x;
			break;
		}
		}
		}	
		
		return CBBBs;
	}
	
	public void GetCBBB_DAG(List<DAG> CBBBs, int number) throws FileNotFoundException{//////
		
	
		Random rand = new Random();
		
		while(CBBBs.size() > 1){
			List<DAG> cbbbs = new LinkedList<DAG>();
			for(DAG dag : CBBBs)
				cbbbs.add(dag);
			int id1 = rand.nextInt(cbbbs.size()-1);
			cbbbs.remove(id1);
			DAG dag2 = new DAG();
			if(cbbbs.size()>1)
				dag2 = cbbbs.get(rand.nextInt(cbbbs.size() -1));
			else
				dag2 = cbbbs.get(0);
			 CBBBs.set(id1, Union(CBBBs.get(id1),dag2));
			 CBBBs.remove(dag2);
			
		}
		
		
		System.out.println("The final CBBBS DAG is "+CBBBs.get(0).NodeList.size());
		
		String path = "Cbbbs_DAG/cbbb_"+number+".txt";

		PrintStream ps=new PrintStream(new FileOutputStream(path));  
		 
	    System.setOut(ps);   //重定向输出流  
	    for(int i = 0; i < CBBBs.get(0).NodeList.size();i++)
	    	CBBBs.get(0).NodeList.get(i).data = i+1;
	    System.out.println(CBBBs.get(0).NodeList.size());
		for(Node node : CBBBs.get(0).NodeList)
			for(Node next : node.next)
			System.out.println(node.data+" "+next.data);
		

	}

	public DAG Union(DAG dag1, DAG dag2){
		Random rand = new Random();
		List<Node> sourceList = dag2.GetEntryNodes();
		
		List<Node> sinkList = dag1.GetExitNodes();
		int s = Math.min(sourceList.size(), sinkList.size());
		int num = new Random().nextInt(s);
		if(num == 0)
			num = 1;
		while(num !=0){
			Node snode ;
			Node tnode;
			if(sourceList.size()>1)
				snode = sourceList.get(rand.nextInt(sourceList.size()-1));
			else
				snode = sourceList.get(0);
			if(sinkList.size()>1)
				tnode = sinkList.get(rand.nextInt(sinkList.size()-1));
			else
				tnode = sinkList.get(0);
			for(Node snnode : snode.next){  
				tnode.next.add(snnode);
				snnode.previous.set(snnode.previous.indexOf(snode), tnode);
			}
			dag2.NodeList.remove(snode);
			sourceList.remove(snode);
			sinkList.remove(tnode);
			num --;
		}
		
		for(Node node : dag2.NodeList)
			dag1.NodeList.add(node);
		
		return dag1;

	}
	public DAG CreateQ(int x, int id) {
		DAG Q_dag = new DAG();
		for(int i = 0; i < 2*x; i++){  //创建相应的结点个数
			
			Node node = new Node(++id);
			Q_dag.NodeList.add(node);
		}
		
		for(int j = 0; j < x; j++){
			
			for(int k = x; k < 2*x; k++){
				
					Q_dag.NodeList.get(j).next.add(Q_dag.NodeList.get(k));
					Q_dag.NodeList.get(k).previous.add(Q_dag.NodeList.get(j));
				
			}
		}
System.out.println("所生成的Q_DAG为：");		
		for(Node node : Q_dag.NodeList)
		for(Node previous : node.previous)
			System.out.println("( "+ previous.data+", "+node.data+" )");
		return Q_dag;
	}

	private DAG CreateC(int x, int id) {
		DAG C_dag = new DAG();
		for(int i = 0; i < 2*x; i++){  //创建相应的结点个数
			
			Node node = new Node(++id);
			C_dag.NodeList.add(node);
		}
		
		for(int j = 0; j < x; j++){
			
			int num ;
			if(x >1)
				num =2;
			else
				num = 1;
			for(int k = 0; k < num; k++){
				if(k+j < x){
					C_dag.NodeList.get(j).next.add(C_dag.NodeList.get(x+k+j));
					C_dag.NodeList.get(x+k+j).previous.add(C_dag.NodeList.get(j));
				}else{
				
					C_dag.NodeList.get(x-1).next.add(C_dag.NodeList.get(x));
					C_dag.NodeList.get(x).previous.add(C_dag.NodeList.get(x-1));
				}
			}
		}
		System.out.println("所生成的C_DAG为：");			
		for(Node node : C_dag.NodeList)
		for(Node next : node.next)
			System.out.println("( "+ node.data+", "+next.data+" )");
		return C_dag;
	}

	private DAG CreateN(int x, int id) {
		System.out.println("-----------------"+x);
		DAG N_dag = new DAG();
		for(int i = 0; i < 2*x; i++){  //创建相应的结点个数
			
			Node node = new Node(++id);
			N_dag.NodeList.add(node);
		}
		
		for(int j = 0; j < x; j++){
			
			for(int k = 0; k < 2; k++){
				if(k+j < x){
					N_dag.NodeList.get(j).next.add(N_dag.NodeList.get(x+k+j));
					N_dag.NodeList.get(x+k+j).previous.add(N_dag.NodeList.get(j));
				}
			}
		}
		System.out.println("所生成的N_DAG为：");			
		for(Node node : N_dag.NodeList)
		for(Node previous : node.previous)
			System.out.println("( "+ previous.data+", "+node.data+" )");
		return N_dag;
	}

	public DAG CreateW(int x, int y, int id){
		System.out.println("-----------------"+x+"---"+y);
		DAG W_dag = new DAG();
		for(int i = 0; i < x*y+1; i++){  //创建相应的结点个数
			
			Node node = new Node(++id);
			W_dag.NodeList.add(node);
		}
		x = Math.min(x,y);
		y = Math.max(x, y);
		for(int j = 0; j < x; j++){
			
			for(int k = 0; k < y; k++){
				
				W_dag.NodeList.get(j).next.add(W_dag.NodeList.get(x+k+j*(y-1)));
				W_dag.NodeList.get(x+k+j*(y-1)).previous.add(W_dag.NodeList.get(j));
			}
		}
		System.out.println("所生成的W_DAG为：");			
		for(Node node : W_dag.NodeList)
			for(Node previous : node.previous)
				System.out.println("( "+ previous.data+", "+node.data+" )");
		return W_dag;
	}
	
	public DAG CreateM(int x, int y, int id){

		DAG M_dag = new DAG();
		for(int i = 0; i < x*y+1; i++){  //创建相应的结点个数
			
			Node node = new Node(++id);
			M_dag.NodeList.add(node);
		}
		x = Math.min(x,y);
		y = Math.max(x, y);
		for(int j = 0; j < x; j++){
			
			for(int k = 0; k < y; k++){
				
				M_dag.NodeList.get(j).previous.add(M_dag.NodeList.get(x+k+j*(y-1)));
				M_dag.NodeList.get(x+k+j*(y-1)).next.add(M_dag.NodeList.get(j));
			}
		}
		System.out.println("所生成的M_DAG为：");			
		for(Node node : M_dag.NodeList)
			for(Node next : node.next)
				System.out.println("( "+ node.data+", "+next.data+" )");
		return M_dag;
	}

	public static void main(String agrs[]) throws FileNotFoundException{

		Generate_DAG ge = new Generate_DAG();
		
//		ge.CreateDAG(0);   //随机创建dag
		ge.GetCBBB_DAG(ge.CreateCBBBs(1000),502);
		
		
	}
}

