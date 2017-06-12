package AO;
import java.util.LinkedList;
import java.util.List;

import source.ArcNode;
import source.DAG;
import source.Layer;
import source.Node;
import source.Relative;

/*
 * Transform the input DAG into
 * an StDAG
 * author TL
 * */

public class InitSP {

    public DAG dag;
    public List<Layer> LayerList = new LinkedList<Layer>();
    public List<Layer> GetLayerList(){
        return this.LayerList;
    }

    /**
     * Convert to StDAG
     */	
    public void TranStDAG(){

        List<Node> exitList = dag.GetExitNodes();	
        List<Node> entryList = dag.GetEntryNodes();		
        if(exitList.size() == 1 && entryList.size() == 1)  
            return;		
        Node source = new Node(0); //inputDAG index start from 1	
        Node target = new Node(dag.NodeList.size()+1);	
        for(Node node : entryList){
            source.next.add(node);
            node.previous.add(source);
            ArcNode arc = new ArcNode(0, node.data);
            dag.ArcNodeList.add(arc);
        }
        for(Node lnode : exitList){
            target.previous.add(lnode);
            lnode.next.add(target);
            ArcNode arc = new ArcNode(lnode.data, dag.NodeList.size()+1);
            dag.ArcNodeList.add(arc);
        }
        dag.NodeList.add(source);		
        dag.NodeList.add(target);
    }

    /**
     * Set the layer of each node, function in CrearLayers
     */
    public void Initlayers(DAG dag){

        for(Node node : dag.NodeList)
            node.layer = 0;
        Node entrynode = dag.GetEntryNode();
        entrynode.layer = 0;
        initlayer(entrynode, dag);
    }

    /**
     * function in Initlayers(DAG dag)
     * */
    private void initlayer(Node currentNode , DAG dag){

        for(Node node : currentNode.next){			
            if(node.layer < currentNode.layer + 1){			
                node.layer = currentNode.layer + 1;				
                initlayer(node, dag);
            }
        }
    }

    /**
     * Init layers
     */
    public void CreatLayers(DAG dag){

        Initlayers(dag);		
        double maxLayer = 0;	
        for(Node node : dag.NodeList)
            if(node.layer > maxLayer)
                maxLayer = node.layer;
        for(int i = 0;i <= maxLayer;i++){			
            Layer layer = new Layer(i);
            for(Node node : dag.NodeList)
                if(node.layer == i)
                    layer.NodeList.add(node);		
            LayerList.add(layer);
        }

    }

    /**
     * Construct Relative, function in SplitLayer(int i)
     */
    private  void ConsRelative(Layer layer_u, Layer layer_d, Node currentnode, Relative relative){

        currentnode.VertexStatue = 1; 		
        relative.U.add(currentnode);
        for(Node cnnode : currentnode.next){
            if(layer_d.Iscontain(cnnode)){
                Node cnode = dag.FindNode(cnnode.data);
                if(!relative.D.contains(cnode)) 						
                    relative.D.add(cnode);
                relative.ArcNodeList.add(new ArcNode(currentnode.data,cnode.data));					
                for(Node cppnode : cnode.previous){						
                    Node cpnode = dag.FindNode(cppnode.data); 						
                    if(cpnode.VertexStatue != 1 && layer_u.Iscontain(cpnode))	
                        ConsRelative(layer_u,layer_d,cpnode,relative);
                }					
            }
        }		
    }


    /**
     * Split layer into  Relative
     */
    public void SplitLayer(int i){

        Layer layer_u = LayerList.get(i);			
        Layer layer_d = LayerList.get(i+1);
        int next = 0;		
        while(next!= -1){
            Node node = dag.FindNode(layer_u.NodeList.get(next).data);
            Relative relative = new Relative();						
            ConsRelative(layer_u, layer_d, node, relative);				
            relative.id = LayerList.get(i).RelativeList.size();  
            LayerList.get(i).RelativeList.add(relative);
            next = layer_u.GetNextNode();

        }			
    }
}
