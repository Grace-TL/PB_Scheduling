package source;

import java.util.LinkedList;
import java.util.List;

public class Layer  {
	
	public int id;
	public List<Node> NodeList = new LinkedList<Node>();
	public List<Relative> RelativeList = new LinkedList<Relative>();
	public Layer(int id){
		this.id = id;
	}
	
	/*
	 * If node is node is in NodeList, reutrn true,
     * otherwise, return false.
	 * */
	public boolean Iscontain(Node node){
		
		for(int i = 0; i < this.NodeList.size(); i++){
			
			if(this.NodeList.get(i).data == node.data)
				
				return true;
		}
		
		return false;
	}
	
//	public int UpdateNode(Relative relative){
////		System.out.println("The length of layer_u NodeList is "+this.NodeList.size());
//		int num = 0;
//		
//		for(int i = 0; i < relative.U.size(); i++){
//			
//			for(int j = 0; j < this.NodeList.size(); j++)
//				
//				if(relative.U.get(i).data == this.NodeList.get(j).data)
//					
//					num++;			
//		}
//		
//		return num;
//	}
	
	/**
	 * Find node that has not been executed.
	 * */
	public int GetNextNode(){
		
		int j ;
		for( j = 0; j < this.NodeList.size(); j++)
			if(this.NodeList.get(j).VertexStatue != 1)
				return j;
		return -1;
	}
}


