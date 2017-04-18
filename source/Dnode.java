package source;

import java.util.LinkedList;
import java.util.List;

public class Dnode extends Node{
	
	public Dnode(int d) {
		super(d);
		// TODO Auto-generated constructor stub
	}

	public List<Node> schedule = new LinkedList<Node>();
	int EV[];
	double AVG[];
	public DAG G = new DAG(); 

}
