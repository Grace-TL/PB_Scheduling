package source;
/**
 * ArcNode, Created by TL
 * */
public class ArcNode implements Cloneable{
	
	public int firstNode;  
	public int nextNode;   	
	public int ArcStatue;  
	
	@Override  
	public Object clone() {  
		ArcNode arcnode = null;  
		try{  
			arcnode = (ArcNode)super.clone();  
		}catch(CloneNotSupportedException e) {  
			e.printStackTrace();  
		}  
		return arcnode;  
	}  
	
	public ArcNode(int first, int next){

		firstNode = first;	
		nextNode = next;		
		ArcStatue = 0;
	}
}
