package AO;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import source.DAG;
import source.Layer;
import source.Node;
import source.Relative;

public class TreeExplor {

    public List<Layer> LayerList = new LinkedList<Layer>();
    public DAG dag;
    public Relative relative;
    public List<Relative> relativeList;
    public DAG T = new DAG();
    public DAG tree = new DAG();
    
    public boolean flag = true;
    public List<Node> orphanList = new LinkedList<Node>();
    public void explor(int level){
        //****************************************************************************
        //System.out.println("Now start to explor the "+level+" level!");

        Layer layer = LayerList.get(level);  
        this.relativeList = layer.RelativeList;
        for(Relative relat : layer.RelativeList) // reset
            for(Node runode : relat.U)
                runode.VertexStatue = 0;
        while(!Alldone(this.relativeList)){
            for(Relative relative : this.relativeList){
                if(relative.statue == 0){
                    //****************************************************************************
                    //	System.out.println("--- relative --- "+layer.RelativeList.indexOf(relative));
                    //	System.out.print("U: ");
                    //	for(Node unode : relative.U)
                    //		System.out.print(unode.data+" ");
                    //	System.out.println();
                    //	System.out.print("D: ");
                    //	for(Node unode : relative.D)
                    //		System.out.print(unode.data+" ");
                    //	System.out.println();
                    this.relative = relative;
                    this.relative.layer = level;
                    explorU(this.relative);
                    if(flag)  //if flag=truem, then break, and start to search relatives
                        break;
                    relative.statue = 1; 
                    for(Node  relnode : relative.U)
                        relnode.VertexStatue = 0;;

                }				
            }
        }

        int p = relative.SubF.size();  
        while(p>0){
            Node node = relative.SubF.get(0);
            for(Node snode : relative.SubF){
                if(node.layer < snode.layer){
                    relative.SubF.remove(snode);
                    p--;
                    break;
                }else if(node.layer > snode.layer){
                    relative.SubF.remove(node);
                    node = snode;				
                    p--;
                    break;
                }else{
                    p--;
                }
            }
        }
        CapterOrphan(level);
    }


    private boolean Alldone(List<Relative> relativeList) {
        for(Relative rel : relativeList)
            if(rel.statue != 1)
                return false;
        return true;
    }


    public void explorU(Relative relative){
        List<Node> exploration = new LinkedList<Node>();
        for(Node node : relative.U)
            exploration.add(node);
        this.flag = false; 
        while(exploration.size()!=1){
            Node maxnode = getMaxNode(exploration);
            eliminate1(maxnode,exploration);
        }
        relative.h = exploration.get(0);
        List<Node> KT = Cons_KT(relative);
        for(Node node : KT)
            if(!IsContain(exploration,node)){
                exploration.add(node);
                //****************************************************************************
                //System.out.println("--- KT node "+node.data);
            }
        while(exploration.size()!=1){
            Node maxnode = getMaxNode(exploration);
            eliminate2(maxnode,exploration);
        }
        relative.H = exploration.get(0);

    }


    public Node getMaxNode(List<Node> exploration){

        Node maxnode = exploration.get(0);
        for(Node node : exploration){
            if(maxnode.layer < node.layer)
                maxnode = node;
        }
        return maxnode;
    }



    public void eliminate1(Node maxnode, List<Node> exploration){
        if(!IsContain(relative.SubF, maxnode))
            relative.SubF.add(maxnode); 
        maxnode.VertexStatue = 1; 
        for(Node node : maxnode.previous)
            if(!IsContain(exploration,node)){
                exploration.add(node);
            }
        exploration.remove(maxnode);
    }

    public void eliminate2(Node maxnode, List<Node> exploration){

        //****************************************************************************
        //System.out.println("current eliminate node "+maxnode.data);
        if(!IsContain(relative.SubF, maxnode))
            relative.SubF.add(maxnode); 
        if(maxnode.data != this.relative.h.data){  
            Queue<Node> q = new LinkedList<Node>();
            q.offer(maxnode);
            List<Node> leafList = new LinkedList<Node>();
            while(!q.isEmpty()){
                Node first = q.poll();
                for(Node nnode : first.next){
                    if(nnode.layer < this.relative.layer)
                        q.offer(nnode);
                    else if(nnode.layer == this.relative.layer)
                        leafList.add(nnode);
                }
            }	
            //**************************************************************************** 
            //for(Node lnode : leafList){
            //	System.out.print(" "+lnode.data);
            //}					
            //System.out.println();
            for(Node nnode : leafList){  
                if(nnode.VertexStatue == 0){  
                    Relative urel = null;
                    for(Relative rel : relativeList){
                        if(IsContain(rel.U,nnode)){
                            urel = rel;
                            break;
                        }
                    }
                    if(urel!=null){
                        UnioRelative(relative,urel);
                        flag = true;
                        relativeList.remove(urel);
                    }
                }
            }
        }
        maxnode.VertexStatue = 1; 
        for(Node node : maxnode.previous)
            if(!IsContain(exploration,node)){
                exploration.add(node);
            }
        exploration.remove(maxnode);
    }

    public void eliminate(Node maxnode , List<Node> exploration){

        //****************************************************************************
        //System.out.println("current eliminate node "+maxnode.data);
        if(maxnode.layer > this.relative.layer){  
            Queue<Node> q = new LinkedList<Node>();
            q.offer(maxnode);
            List<Node> leafList = new LinkedList<Node>();
            while(!q.isEmpty()){
                Node first = q.poll();
                for(Node nnode : first.next){
                    if(nnode.layer < this.relative.layer)
                        q.offer(nnode);
                    else if(nnode.layer == this.relative.layer)
                        leafList.add(nnode);
                }
            }	
            //****************************************************************************
            //for(Node lnode : leafList){
            //	System.out.print(" "+lnode.data);
            //}					
            //System.out.println();
            for(Node nnode : leafList){  
                if(nnode.VertexStatue == 0){  
                    Relative urel = null;
                    for(Relative rel : relativeList){
                        if(IsContain(rel.U,nnode)){
                            urel = rel;
                            break;
                        }
                    }
                    if(urel!=null){
                        UnioRelative(relative,urel);
                        flag = true;
                        relativeList.remove(urel);
                    }
                }
            }
        }
        maxnode.VertexStatue = 1; 
        for(Node node : maxnode.previous)
            if(!IsContain(exploration,node)){
                exploration.add(node);
            }
        exploration.remove(maxnode);
    }

    public boolean IsContain(List<Node> list, Node node){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).data == node.data)
                return true;
        }
        return false;
    }

    public void UnioRelative(Relative relative1, Relative relative2){

        for(Node node :relative2.D)	
            relative1.D.add(node);
        for(Node node :relative2.U){
            node.VertexStatue = 1; 
            relative1.U.add(node);
        }
        //****************************************************************************
        //System.out.println("relative "+relativeList.indexOf(relative2)+" is union in relative "+ relativeList.indexOf(relative1));
        //System.out.println("the union node is");
        //for(Node rnode : relative2.U)
        //	System.out.print(rnode.data+" ");
        //System.out.println();
    }

    public List<Node> Cons_KT(Relative relative){  

        List<Node> KT = new LinkedList<Node>();		
        for(Node node : relative.D){
            for(Node dpnode : node.previous)	
                if(!IsContain(relative.U,dpnode)&&!IsContain(KT,dpnode) &&!HavePath(dpnode, relative.h, dag)  )	
                    KT.add(dpnode);				
        }		
        return KT;
    }

    public boolean HavePath(Node node1, Node node2, DAG dag){ 
        Node root = dag.GetEntryNode();				
        if(node2.layer <= node1.layer) 		
            return false;	
        while(node2.data != root.data){			
            node2 = node2.previous.get(0);			
            if(node1.data == node2.data)				
                return true;			
        }
        return false;
    }


    public void CapterOrphan(int level){

        Layer lay = LayerList.get(level);  														
        for(Relative relative : lay.RelativeList)					
            if( relative.D.size()==0)  
                this.orphanList.add(relative.U.get(0));
        List<Node> rmlist = new LinkedList<Node>();
        for(Node unode : this.orphanList){  
            for(Relative rel : lay.RelativeList){		
                //****************************************************************************
                //System.out.println("rel.SubF  :"+ rel.SubF.size()+"--------------   orphan.data "+unode.data);
                for(Node h : rel.SubF){
                    if(HavePath(h,unode,dag)&&!IsContain(rmlist,unode)||h.data == unode.data){  	
                        //System.out.println("Orhpan node  :"+ unode.data+" is union in relative "+lay.RelativeList.indexOf(rel)+"     layer is "+unode.layer);									
                        rel.U.add(unode);
                        rmlist.add(unode);
                        break;
                    }
                }
            }			
        }
        for(Node rnode : rmlist){  
            orphanList.remove(rnode);
            Layer tmp_lay = LayerList.get(rnode.layer);
            for(Relative relt : tmp_lay.RelativeList){
                if(relt.D.size()==0 && relt.U.get(0).data == rnode.data){
                    tmp_lay.RelativeList.remove(relt);
                    break;
                }						
            }
        }
    }

    public void Synchron(int level){

        for(Relative rel : this.LayerList.get(level).RelativeList){ 
            if(rel.U.size()==1 || rel.D.size()==1){
                for(Node node : rel.D){
                    for(Node p_node : node.previous){   
                        p_node.next.remove(node);
                    }
                    node.previous.clear();  
                    for(Node pnode : rel.U){
                        node.previous.add(pnode);
                        if(!IsContain(pnode.next, node))
                            pnode.next.add(node);					
                    }
                }				
            }
            else{
                Node bu = new Node(dag.NodeList.size()+1); 							
                bu.layer = rel.U.get(0).layer;  
                //****************************************************************************
                for(Node node : rel.U){ 			
                    bu.previous.add(node);   
                    for(Node nextnode : node.next){ 	
                        nextnode.previous.remove(node);
                        if(bu.next.size() ==0 || !IsContain(bu.next,nextnode)) { 
                            bu.next.add(nextnode);	
                            //****************************************************************************
                            //System.out.println("ArcNode:( "+bu.data+" , "+nextnode.data+" )");
                        }
                        nextnode.previous.add(bu);
                    }
                    node.next.clear();
                    node.next.add(bu); 
                }

                for(Node dnode : rel.D){
                    for(Node pdnode : dnode.previous)
                        pdnode.next.remove(dnode);
                    dnode.previous.clear();
                    dnode.previous.add(bu);
                    if(!IsContain(bu.next, dnode))
                        bu.next.add(dnode);
                }
                dag.NodeList.add(bu);
            }
        }
    }

    public static void main(String agrs[]) throws FileNotFoundException{
        InitSP initSP = new InitSP();
        initSP.dag = new DAG();
        initSP.dag.InitDAG("DAG_SP/sp_100.txt");
        //Step1 Transform the input DAG into an StDAG a
        initSP.TranStDAG();
        //Step2 Layering of the graph.
        initSP.CreatLayers(initSP.dag);
        //Step3 Initialize an ancillary tree T to L0
        TreeExplor tre = new TreeExplor();
        tre.dag = initSP.dag;
        tre.LayerList = initSP.LayerList;
        for(int m=0;m<initSP.LayerList.size()-1;m++){
            System.out.println("--------------Sync the "+m+" layer--------------------");
            Layer layer = tre.LayerList.get(m);
            int level = tre.LayerList.indexOf(layer);
            //for(Node tnode : layer.NodeList)
            //	System.out.println("Nodes in this layer: "+tnode.data);
            //a. Split layer in classes of relatives	
            initSP.SplitLayer(level);	
            System.out.println("level--"+level+"     layer.RelativeList.size()---"+layer.RelativeList.size());
            //b.  Tree exploration to detect handles for classes of relatives	
            //c. Merge classes with overlapping forests
            //d. Capture orphan nodes
            //e. Class barrier synchronization
            tre.explor(level);
            tre.Synchron(level);
        }	
        System.out.println(tre.dag.NodeList.size());		
        for(Node node : tre.dag.NodeList){
            for(Node next : node.next)
                System.out.println("("+node.data+" , "+next.data+")");
        }
    }
}
