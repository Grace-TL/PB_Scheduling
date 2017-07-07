/**
 *Generate SP dag 
 * **/
package experiment;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import source.DAG;
import source.Node;

public class Generate_SPDAG {
	
	public DAG T = new DAG();
	public int SIZE = 7000;
	public int idx = SIZE;
	public Map<Integer, DAG> map = new HashMap<Integer, DAG>(); 
	public void Generate_tree(){
		Node root = new Node(0);
		T.NodeList.add(root);
		List<Node> leaflist = new LinkedList<Node>();
		Random rand = new Random();
		while(T.NodeList.size()<SIZE){
			leaflist = T.GetExitNodes();
			int p = rand.nextInt(leaflist.size());
			Node choose = leaflist.get(p);
			Node leftNode = new Node(T.NodeList.size());
			Node rightNode = new Node(T.NodeList.size()+1);
			choose.next.add(leftNode);
			choose.next.add(rightNode);
			leftNode.previous.add(choose);
			rightNode.previous.add(choose);
			T.NodeList.add(leftNode);
			T.NodeList.add(rightNode);
		}	
	}
	
	public void GetSPDag(){
		List<Node> leaves = new LinkedList<Node>();
		leaves = T.GetExitNodes();
		for(Node node : leaves){  
			Node node_new = (Node) node.clone();
			node_new.previous.clear();
			node_new.next.clear();
			DAG dag = new DAG();
			Node node2 = new Node(idx++);
			node2.previous.add(node_new);
			node_new.next.add(node2);
			dag.NodeList.add(node_new);
			dag.NodeList.add(node2);
			map.put(node.data,dag );
		}
			
		consDag(T.GetEntryNode());
		
	}
	
	public void consDag(Node node){
		node.VertexStatue = 1;
		if(node.next.size() == 0)
			return;
		if(node.next.get(0).VertexStatue==0)
			consDag(node.next.get(0));
		if(node.next.get(1).VertexStatue==0)
			consDag(node.next.get(1));
		DAG dag1 = map.get(node.next.get(0).data);
		DAG dag2 = map.get(node.next.get(1).data);
		DAG dag = new DAG();
		if(Math.random()>0.5){
			dag = unionPara(dag1,dag2);
//			System.out.println("Current node is "+node.data+" the operation is Para");
		}
		else{
			dag = unionSeri(dag1,dag2);
//			System.out.println("Current node is "+node.data+" the operation is Seri");
		}
		
		map.remove(node.next.get(0).data);
		map.remove(node.next.get(1).data);
		map.put(node.data, dag);
	}
	
	public DAG unionSeri(DAG dag1, DAG dag2){
		
		Node breakNode1 = dag1.GetExitNode();
		Node breakNode2 = dag2.GetEntryNode();

		for(Node node : breakNode2.next){
			breakNode1.next.add(node);
			node.previous.clear();
			node.previous.add(breakNode1);
		}
		dag2.DelNode(breakNode2);
		for(Node node : dag2.NodeList)
			dag1.NodeList.add(node);
		return dag1;
		
	}
	
	public DAG unionPara(DAG dag1, DAG dag2){
		Node entryNode1 = dag1.GetEntryNode();
		Node entryNode2 = dag2.GetEntryNode();
		Node exitNode1 = dag1.GetExitNode();
		Node exitNode2 = dag2.GetExitNode();
		if(dag1.NodeList.size()==2 && dag2.NodeList.size() ==2){
			dag2.DelNode(entryNode2);
			entryNode1.next.add(exitNode2);
			exitNode2.previous.add(entryNode1);
			Node node = new Node(idx++);
			exitNode2.next.add(node);
			exitNode1.next.add(node);
			node.previous.add(exitNode2);
			node.previous.add(exitNode1);
			dag1.NodeList.add(exitNode2);
			dag1.NodeList.add(node);
		}else if(dag1.NodeList.size() == 2){
			dag1.DelNode(entryNode1);
			entryNode2.next.add(exitNode1);
			exitNode1.previous.add(entryNode2);
			exitNode1.next.add(exitNode2);
			exitNode2.previous.add(exitNode1);
			dag2.NodeList.add(exitNode1);
			dag1 = dag2;
		}else if(dag2.NodeList.size() == 2){
			dag2.DelNode(entryNode2);
			entryNode1.next.add(exitNode2);
			exitNode2.previous.add(entryNode1);
			exitNode2.next.add(exitNode1);
			exitNode1.previous.add(exitNode2);
			dag1.NodeList.add(exitNode2);
		}else{
			for(Node node : entryNode2.next){
				node.previous.clear();
				entryNode1.next.add(node);
				node.previous.add(entryNode1);
			}
			for(Node node: exitNode2.previous){
				node.next.clear();
				node.next.add(exitNode1);
				exitNode1.previous.add(node);
			}
			dag2.DelNode(entryNode2);
			dag2.DelNode(exitNode2);
			for(Node node : dag2.NodeList){
				dag1.NodeList.add(node);
			}
		}
	
		return dag1;
	}

	public static void main(String args[]) throws FileNotFoundException{
		if(args.length != 2){
            System.out.println("java Generate_SPDAG [DAG_SIZE] [Number of DAG you want generate]");
            return;
        }
        int size = Integer.parseInt(args[0]);
        int num = Integer.parseInt(args[1]);
        //		for(Node node : gs.T.NodeList)
        //			for(Node next : node.next)
        //				System.out.println("( "+node.data+", "+next.data+" )");
        //		System.out.println("----------------------------------------------------------------------");
        for(int j = 0; j < num; j++){
            int num_node = 0;
            String path = "DAG_SP/sp_" + size + "_" + j + ".txt";
            DAG dag = new DAG();
            while((num_node > size+5) || (num_node < size-5) ){
                Generate_SPDAG gs = new Generate_SPDAG();
                gs.SIZE = size*2;
                gs.Generate_tree();
                gs.GetSPDag();
                dag = new DAG();
                Node root = gs.T.GetEntryNodes().get(0);
                dag = gs.map.get(root.data);
                int i = 1;
                for(Node node : dag.NodeList){
                    node.data = i++;
                }
                num_node = dag.NodeList.size();
//                System.out.println(dag.NodeList.size());
            }
            PrintStream out=System.out; 
            PrintStream ps=new PrintStream(path); 
            System.setOut(ps); 
            System.out.println(dag.NodeList.size());
            for(Node node : dag.NodeList)
                for(Node next : node.next)
                    System.out.println(node.data+" "+next.data);

        }
    }
}
