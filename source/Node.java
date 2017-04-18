package source;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by TL
 * */

public class Node implements Cloneable{
		 
	 public int data;  //id of this node
	 public List<Node> previous ;  // list of ancestor nodes
	 public List<Node> next;  //list of child nodes
	 public int VertexStatue; //statue of node, if node has been executed, VertexStatue=1 
	 public double[] Quo_V = new double[4]; //quotient vector  <DQ, LQ, EQ, IQ>
	 public int layer; //the layer of this node, it equals to the longest path from source node 
	 public double jobTime; //time need to finish execution
	 public int dfn;  //the index of depth-first-traversal	 
	 public int low;  //the minimum index of ancestor node
	 public Node parent;//the ancestor node in dfs
	 
	 public void SetPrevious(List<Node> list){
		 this.previous = list;
	 }
	 
	 
	 @SuppressWarnings("unchecked")
	 @Override  
	 public Object clone() {  
		 Node node = null;  
		 try{  
			 node = (Node)super.clone();  
		 }catch(CloneNotSupportedException e) {  
			 e.printStackTrace();  
		 } 
		 node.previous = (List<Node>) ((LinkedList<Node>) previous).clone();     
		 node.next = (List<Node>) ((LinkedList<Node>) next).clone();          
		 return node;  
	 }  
	
	
	 public Node(int d){		 
		 data = d;			 
		 previous = new LinkedList<Node>();		 
		 next = new LinkedList<Node>();		 
		 VertexStatue = 0;
		 jobTime = 0.0;
		 layer = 0;		 
	 }

}
