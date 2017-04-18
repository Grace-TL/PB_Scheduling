package Tools;

import java.io.FileNotFoundException;

import source.DAG;
import source.Node;
import PB.InitQuoVet;

public class PB_QuoVet_Test {
	
	public void QVInit_test() throws FileNotFoundException{
		
		InitQuoVet init_QV = new InitQuoVet();
		init_QV.dag = new DAG();
		init_QV.dag.InitDAG();
		init_QV.Init();
		for(Node node : init_QV.dag.NodeList){
			System.out.println(node.data+": <"+node.Quo_V[0]+", "+node.Quo_V[1]+", "+node.Quo_V[2]+", "+node.Quo_V[3]+">");
		}
	}
	
	public static void main(String agrs[]) throws FileNotFoundException{
		PB_QuoVet_Test qtest = new PB_QuoVet_Test();
		qtest.QVInit_test();
	}

}
