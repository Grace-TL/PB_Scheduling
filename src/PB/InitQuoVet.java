package PB;

import java.util.LinkedList;
import java.util.List;

import source.DAG;
import source.Node;

public class InitQuoVet {
	
	public DAG dag;
	
	/**
	 * Init quotient vector (priority vector)
	 * */
	public void Init(){	
		List<Node> exitlist = dag.GetExitNodes();     //exitnodes Que_V{0,0,0,0}
		for(int i = 0; i < exitlist.size(); i++){	
			Node node = dag.FindNode(exitlist.get(i).data);			
			node.Quo_V[1] = 0;			
			node.Quo_V[2] = 0;			
			node.Quo_V[3] = 0;			
			SetLQ(node);
		}
		SetDQs();		
		SetEQ_IQs( );
	}
	
	
	/**
	 * Init LQ by jobtime
	 * */
	public void Init_JT(){	
		List<Node> exitlist = dag.GetExitNodes();     
		for(int i = 0; i < exitlist.size(); i++){	
			Node node = dag.FindNode(exitlist.get(i).data);			
			node.Quo_V[1] = 0;			
			node.Quo_V[2] = 0;			
			node.Quo_V[3] = 0;			
			SetLQ(node);
		}   
		SetDQs();		
		SetEQ_IQs( );
		for(int i = 0; i < exitlist.size(); i++){	
			Node node = dag.FindNode(exitlist.get(i).data);			
			node.Quo_V[1] += Math.ceil(exitlist.get(i).jobTime/5);			
			//node.Quo_V[2] = 0;			
			//node.Quo_V[3] = 0;			
			SetJT(node);
		}
		
		
	}
	
	/**
	 * Init LQ by node layer
	 * */
	public void SetLQ(Node node){  //If there are more than one leaf nodes in sp-DAG, then the lQ of them are all 0	
		List<Node> previous = new LinkedList<Node>();			
		previous = node.previous;		
		for(int i = 0; i < previous.size(); i++){		
			Node pnode = dag.FindNode(previous.get(i).data);				
			if(pnode.Quo_V[1] < node.Quo_V[1] + 1 ){					
				pnode.Quo_V[1] = node.Quo_V[1] + 1;					
				SetLQ(pnode);
			}
		}			
		node.SetPrevious(previous);
	}
	
	
	/**
	 *Function in Init_JT 
	 */
	public void SetJT(Node node){
		List<Node> previous = new LinkedList<Node>();
		previous = node.previous;
		for(Node pnode : previous){
			pnode = dag.FindNode(pnode.data);
			if(pnode.Quo_V[1] < node.Quo_V[1] + Math.ceil(pnode.jobTime/5)){
				pnode.Quo_V[1] = node.Quo_V[1] + Math.ceil(pnode.jobTime/5); 
//				System.out.println("Node : "+node.data+"        LQ:"+node.Quo_V[1]);
				SetJT(pnode);
			}
		}
		node.SetPrevious(previous);
	}
	
	public void SetDQs( ){
		for(Node node : dag.NodeList)
			SetDQ(node);
	}
	
	
	/**
	 * Set the value of node DQ
	 * */
	public void SetDQ(Node node){
		
		int DQ = 0;		
		List<Node> next = new LinkedList<Node>();		
		next = dag.FindNode(node.data).next;
		for(Node nnode : next){   //previous of next, if the length of previous = 1 and is current node, then DQ+1
			nnode = dag.FindNode(nnode.data);
			if(nnode.previous.size() == 1 && nnode.previous.get(0).data == node.data)				
				DQ++;
		}			
		dag.FindNode(node.data).Quo_V[0] = DQ;		
	}
	
	public void SetEQ_IQs( ){
		
		List<Node> levelNode = new LinkedList<Node>();		
		int level = 1;		
		levelNode = getLevelList(level);		
		do{	
			for(Node node : levelNode){
				SetEQ(node);
				SetIQ(node);
			}		
			level++;			
			levelNode = getLevelList(level);			
		}while(levelNode.size()!=0);
	
	}
	
	public void SetEQ(Node node){
				
		double EQ = 0;
		for(Node nnode : node.next){
			nnode = dag.FindNode(nnode.data);
			EQ += nnode.Quo_V[3];	
		}
		dag.FindNode(node.data).Quo_V[2] = EQ;
	
	}
	
	public void SetIQ(Node node){
		
		double IQ = 0;
		int indegree = node.previous.size();	
		if(indegree != 0)		
			IQ = (node.Quo_V[2] + 1.0) / indegree ;		
		dag.FindNode(node.data).Quo_V[3] = IQ;		
	}
	

	
	/*
	 * return the number of source nodes
	 * 
	 * */
	public int getSourceNumber(){
		
		int count = 0;
		for(Node node : dag.NodeList)
			if(node.previous.size()==0)
				count++;
		return count;
	}
	
	/*
	 * return the list of nodes  at layer level
	 * 
	 * */
	public List<Node> getLevelList(int level){
		
		List<Node> levelList = new LinkedList<Node>();	
		for(Node node : dag.NodeList)
			if(node.Quo_V[1] == level)
				levelList.add(node);		
		return levelList;
	}
	
	public void UpdatePriority(Node node){
		
//		if(node.previous.size()!=0 && node.Quo_V[1] != 0)	
		if(node.previous.size()!=0 && node.next.size()!=0)
			node.Quo_V[3] = (node.Quo_V[2]+1.0) / node.previous.size();		
		else			
			node.Quo_V[3] = 0;
		for(Node pnode : node.previous){
			pnode = dag.FindNode(pnode.data);
			SetEQ(pnode);
			SetDQ(pnode);
			UpdatePriority(pnode);
			
		}
	}
	
//	public static void main(String agrs[]) throws FileNotFoundException{
//		
//		DAG dag = new DAG();
//		
//		InitQuoVet initQ = new InitQuoVet();
//		
//		initQ.dag = dag;
//		
//		initQ.Init();
//		
//		System.out.println("Node id---DQ---LQ---EQ---IQ");
//		
//		for(int i = 0; i < dag.NodeList.size(); i++){
//			
//			System.out.print((int)dag.NodeList.get(i).data+"      ");
//			
//			for(int j = 0; j < 4; j++)
//			
//				System.out.print(dag.NodeList.get(i).Quo_V[j]+"---");
//			
//			System.out.println();
//		}
//		
//		List<Node> L = new LinkedList<Node>();
//		
//		L.add(dag.NodeList.get(dag.GetEntryNode())); // Add the entry node into the Ready list L
//		
//		SortList sort = new SortList();
//		
//		sort.SetL(L);
//		
//	    Node maxnode = sort.MaxPriNode();
//	    
//	    System.out.println("The current max node is "+(int)maxnode.data);
//		
//	}

}
