package Tools;

import java.io.FileNotFoundException;

import AO.InitSP;
import source.DAG;
import source.Layer;
import source.Node;
import source.Relative;

public class SP_test {
	
	public DAG dag;
	
	/**
	 * 测试dag图节点层次
	 * */
	public void test_layers(){
		
		InitSP initsp = new InitSP();
		initsp.Initlayers(dag);
		for(Node node : dag.NodeList)
			System.out.println("node "+node.data+"的层次为："+node.layer);
		initsp.CreatLayers(dag);
		System.out.println("各层结点如下：");
		for(Layer lay : initsp.LayerList){
			System.out.print("第"+initsp.LayerList.indexOf(lay)+"层结点：");
			for(Node node : lay.NodeList)
				System.out.print(" "+node.data);
			System.out.println();
		}
	}
	
	
	/**
	 * 测试dag图relative划分
	 * */
	public void test_SplitLayer(){
		
		InitSP initsp = new InitSP();
		initsp.dag = dag;
		initsp.TranStDAG();
		//initsp.Initlayers(dag);  //首先划分层次
		initsp.CreatLayers(dag);
		for(int i = 0; i < initsp.LayerList.size()-1;i++){
			initsp.SplitLayer(i);
			System.out.println("---------完成第"+i+"层划分----------");
			for(Relative relative : initsp.LayerList.get(i).RelativeList){
				System.out.print("relative-"+relative.id+"中U中节点为：：：：：：：");
				for(int k=0;k<relative.U.size();k++)
					System.out.print((int)relative.U.get(k).data+" ");
				System.out.println();
				System.out.print("            D中节点为：：：：：：：");
				for(int k=0;k<relative.D.size();k++)
					System.out.print((int)relative.D.get(k).data+" ");
				System.out.println();
			}	
		}		
	}
	
	public static void main(String agrs[]) throws FileNotFoundException{
		
		DAG dag = new DAG();
		dag.InitDAG();
		SP_test sptest = new SP_test();
		sptest.dag = dag;
		//sptest.test_layers();
		sptest.test_SplitLayer();
		
	}
}

