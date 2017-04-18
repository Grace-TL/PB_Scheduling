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
	 * ����dagͼ�ڵ���
	 * */
	public void test_layers(){
		
		InitSP initsp = new InitSP();
		initsp.Initlayers(dag);
		for(Node node : dag.NodeList)
			System.out.println("node "+node.data+"�Ĳ��Ϊ��"+node.layer);
		initsp.CreatLayers(dag);
		System.out.println("���������£�");
		for(Layer lay : initsp.LayerList){
			System.out.print("��"+initsp.LayerList.indexOf(lay)+"���㣺");
			for(Node node : lay.NodeList)
				System.out.print(" "+node.data);
			System.out.println();
		}
	}
	
	
	/**
	 * ����dagͼrelative����
	 * */
	public void test_SplitLayer(){
		
		InitSP initsp = new InitSP();
		initsp.dag = dag;
		initsp.TranStDAG();
		//initsp.Initlayers(dag);  //���Ȼ��ֲ��
		initsp.CreatLayers(dag);
		for(int i = 0; i < initsp.LayerList.size()-1;i++){
			initsp.SplitLayer(i);
			System.out.println("---------��ɵ�"+i+"�㻮��----------");
			for(Relative relative : initsp.LayerList.get(i).RelativeList){
				System.out.print("relative-"+relative.id+"��U�нڵ�Ϊ��������������");
				for(int k=0;k<relative.U.size();k++)
					System.out.print((int)relative.U.get(k).data+" ");
				System.out.println();
				System.out.print("            D�нڵ�Ϊ��������������");
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

