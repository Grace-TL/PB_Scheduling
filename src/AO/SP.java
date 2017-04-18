package AO;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import source.ArcNode;
import source.DAG;
import source.Layer;
import source.Node;



public class SP {

    public DAG dag;

    public List<Layer> LayerList = new LinkedList<Layer>();

    public void SP_ization() throws FileNotFoundException{

        dag = new DAG();
        dag.InitDAG();
        InitSP initSP = new InitSP();
        initSP.dag = this.dag;
        //Step1 Transform the input DAG into an StDAG a
        initSP.TranStDAG();
        //Step2 Layering of the graph.
        initSP.CreatLayers(initSP.dag);
        //Step3 Initialize an ancillary tree T to L0
        TreeExplor tre = new TreeExplor();
        tre.dag = initSP.dag;
        tre.LayerList = initSP.LayerList;
        //System.out.println("--------------Sync the "+initSP.LayerList.size()+" layer--------------------");
        for(int m=0;m<initSP.LayerList.size()-1;m++){
            //System.out.println("--------------Sync the "+m+" layer--------------------");
            Layer layer = tre.LayerList.get(m);
            int level = tre.LayerList.indexOf(layer);
            //a. Split layer in classes of relatives	
            initSP.SplitLayer(level);	
            //System.out.println("level--"+level+"     layer.RelativeList.size()---"+layer.RelativeList.size());
            //b.  Tree exploration to detect handles for classes of relatives	
            //c. Merge classes with overlapping forests
            //d. Capture orphan nodes
            //e. Class barrier synchronization
            tre.explor(level);
            tre.Synchron(level);
        }	
        Node root = dag.GetEntryNode();
        Comp_DAG(root);
    }


    public void Comp_DAG(Node root){
        dag.ArcNodeList.clear();
        Queue<Node> q = new LinkedList<Node>();
        q.offer(root);
        while(q.size()!=0){
            Node node = q.poll();
            for(Node next : node.next){
                if(!q.contains(next))
                    q.offer(next);
                dag.ArcNodeList.add(new ArcNode((int)node.data,(int)next.data));
                //System.out.println("ArcNode:( "+node.data+" , "+next.data+" )");
            }			
        }
    }

    public static void main(String agrs[]) throws FileNotFoundException{

        SP sp = new SP();
        sp.dag = new DAG();
        sp.dag.InitDAG();
        sp.SP_ization();
        for(Node node : sp.dag.NodeList)
            for(Node next : node.next)
                System.out.println("("+node.data+" , "+next.data+")");
    }
}
