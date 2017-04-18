package Tools;

import java.io.FileNotFoundException;

import AO.ReShortcuts;
import source.ArcNode;
import source.DAG;
import source.Node;

public class DAG_test {

	/**
	 * ����Ƿ���ȷ�����ļ�
	 * */
	public void test_scanf() throws FileNotFoundException{
		DAG dag = new DAG();
		dag.InitDAG();
		System.out.print("N:");
		System.out.println(dag.N);
		System.out.println("dag�б߽�������");
		for(ArcNode arc : dag.ArcNodeList)
			System.out.println(arc.firstNode+"  "+arc.nextNode);
		System.out.println("����е�����");
		for(Node node : dag.NodeList)
			for(Node next : node.next)
				System.out.println(node.data+"  "+next.data);
		System.out.println("ǰ���е�����");
		for(Node node : dag.NodeList)
			for(Node previous : node.previous)
				System.out.println(previous.data+"  "+node.data);
		
	}
	/**
	 * �ɺ����Ϣ��ӡdag
	 * */
	public void print_dag(DAG dag){
		System.out.print("N:");
		System.out.println(dag.N);
		for(Node node : dag.NodeList)
			for(Node next : node.next)
				System.out.println(node.data+"  "+next.data);	
	}
	
	/**
	 * ����ɾ��ָ����㹦��
	 * */
	public void test_delNode() throws FileNotFoundException{
		
		DAG dag = new DAG();
		dag.InitDAG();
		System.out.println("ɾ��ǰ��dagΪ��");
		print_dag(dag);
		dag.DelNode(new Node(13));
		System.out.println("ɾ�������dagΪ");
		print_dag(dag);
		System.out.println("ǰ���е�����");
		for(Node node : dag.NodeList)
			for(Node previous : node.previous)
				System.out.println(previous.data+"  "+node.data);
		System.out.println("dag�б߽�������");
		for(ArcNode arc : dag.ArcNodeList)
			System.out.println(arc.firstNode+"  "+arc.nextNode);
	}
	
	public void test_reshcut() throws FileNotFoundException{
		
		DAG dag = new DAG();
		dag.InitDAG();
		System.out.println("�����dagΪ��");
		print_dag(dag);
		ReShortcuts rsc = new ReShortcuts();
		rsc.dag = dag;
		rsc.InitAdj();
		rsc.P_closure();
		rsc.GenAdj();
		System.out.println("ȥ���ݾ��ߵ�dagΪ��");
		print_dag(rsc.dag);
	}
	
	public static void main(String agrs[]) throws FileNotFoundException{
		DAG_test test = new DAG_test();
		//test.test_scanf();
		//test.test_delNode();
		test.test_reshcut();
		
	}
}
