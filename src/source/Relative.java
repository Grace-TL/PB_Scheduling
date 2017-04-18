package source;

import java.util.LinkedList;
import java.util.List;

public class Relative {
	
	public int id; 
	public Node h ; //U-handle
	public Node H ; //handle node of U
	public List<Node> KT = new LinkedList<Node>();
	public List<Node> SubF = new LinkedList<Node>();
	public List<Node> U = new LinkedList<Node>();  //list node of U
	public List<Node> D = new LinkedList<Node>();  //list node of D
	public List<ArcNode> ArcNodeList = new LinkedList<ArcNode>();
	public int statue = 0;
	public int layer;  //layer of U

}


