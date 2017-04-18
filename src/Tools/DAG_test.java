package Tools;

import java.io.FileNotFoundException;

import AO.ReShortcuts;
import source.ArcNode;
import source.DAG;
import source.Node;

public class DAG_test {

	/**
	 * 检查是否正确读入文件
	 * */
	public void test_scanf() throws FileNotFoundException{
		DAG dag = new DAG();
		dag.InitDAG();
		System.out.print("N:");
		System.out.println(dag.N);
		System.out.println("dag中边结点的数据");
		for(ArcNode arc : dag.ArcNodeList)
			System.out.println(arc.firstNode+"  "+arc.nextNode);
		System.out.println("后继中的数据");
		for(Node node : dag.NodeList)
			for(Node next : node.next)
				System.out.println(node.data+"  "+next.data);
		System.out.println("前驱中的数据");
		for(Node node : dag.NodeList)
			for(Node previous : node.previous)
				System.out.println(previous.data+"  "+node.data);
		
	}
	/**
	 * 由后继信息打印dag
	 * */
	public void print_dag(DAG dag){
		System.out.print("N:");
		System.out.println(dag.N);
		for(Node node : dag.NodeList)
			for(Node next : node.next)
				System.out.println(node.data+"  "+next.data);	
	}
	
	/**
	 * 测试删除指定结点功能
	 * */
	public void test_delNode() throws FileNotFoundException{
		
		DAG dag = new DAG();
		dag.InitDAG();
		System.out.println("删除前的dag为：");
		print_dag(dag);
		dag.DelNode(new Node(13));
		System.out.println("删除结点后的dag为");
		print_dag(dag);
		System.out.println("前驱中的数据");
		for(Node node : dag.NodeList)
			for(Node previous : node.previous)
				System.out.println(previous.data+"  "+node.data);
		System.out.println("dag中边结点的数据");
		for(ArcNode arc : dag.ArcNodeList)
			System.out.println(arc.firstNode+"  "+arc.nextNode);
	}
	
	public void test_reshcut() throws FileNotFoundException{
		
		DAG dag = new DAG();
		dag.InitDAG();
		System.out.println("读入的dag为：");
		print_dag(dag);
		ReShortcuts rsc = new ReShortcuts();
		rsc.dag = dag;
		rsc.InitAdj();
		rsc.P_closure();
		rsc.GenAdj();
		System.out.println("去掉捷径边的dag为：");
		print_dag(rsc.dag);
	}
	
	public static void main(String agrs[]) throws FileNotFoundException{
		DAG_test test = new DAG_test();
		//test.test_scanf();
		//test.test_delNode();
		test.test_reshcut();
		
	}
}
