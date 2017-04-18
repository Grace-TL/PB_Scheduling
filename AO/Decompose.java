package AO;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import source.DAG;
import source.Dnode;
import source.Node;

public class Decompose {
    public DAG dag = new DAG();
    public DAG T_dc = new DAG(); //decomposation tree
    public static int counter = 0;  //dfs counter
    public void decompose(){
        Dnode  root_dc = new Dnode(dag.NodeList.size()+T_dc.NodeList.size()+1);		
        T_dc.NodeList.add(root_dc);
        //****************************************************************************
        //System.out.println();
        //System.out.println("Binary decomposition tree, root node is:"+root_dc.data);
        decomp(dag, root_dc);
    }

    public void decomp(DAG T, Dnode root_dc){

        int startid = dag.NodeList.size();
        //****************************************************************************
        //System.out.println("-------------------------------------------------------------------------");
        if(T.NodeList.size()==2){   // If there are only two nodes in dag, this is a dag with only one edge.			
            root_dc.VertexStatue = -1;			
            Dnode lchild = (Dnode) new Dnode(T.GetEntryNode().data);  //Conver node to Dnode			
            lchild.previous = T.GetEntryNode().previous;			
            Dnode rchild = (Dnode) new Dnode(T.GetExitNode().data);			
            rchild.previous = T.GetExitNode().previous;			
            root_dc.next.add((Dnode) lchild.clone());  //left node
            root_dc.next.add((Dnode)rchild.clone());   //right node
        }		
        else{
            Node exitNode = T.GetExitNode();
            Node entryNode = T.GetEntryNode();
            DAG T_temp = new DAG();			
            T_temp = (DAG) T.clone(); 
            //****************************************************************************
            //for(Node test: T_temp.NodeList)
            //	for(Node tn : test.next)
            //		System.out.println("("+test.data+","+tn.data+")");
            T_temp.DelNode(entryNode);
            T_temp.DelNode(exitNode);
            Node entrynode = T_temp.GetEntryNode();
            DAG T1 = Seprate_dfs(T_temp,entrynode);  
            //****************************************************************************
            //System.out.println("T root  "+entryNode.data +"     Number of nodes in T1: "+T1.NodeList.size()+"--------Remaining number of nodes in T_temp: "+T_temp.NodeList.size());
            if(T_temp.NodeList.size()==0){  
                //****************************************************************************
                //System.out.println("----------Serial Tree-----------");			
                root_dc.VertexStatue = -1 ;  // -1 means parallel	
                fixDag(T1,entryNode,exitNode);  
                Node breakNode = GetBreakNode(T1);   //find breakpoint	
                //****************************************************************************
                //System.out.println("BreakPonit Node is :  "+ breakNode.data);
                List<DAG> subtree = Seprate_dag2(breakNode, T1);  //decompose dag into two dags with breakpoint
                Dnode l_child = new Dnode(startid+T_dc.NodeList.size()+1) ;				
                l_child.previous.add(root_dc);				
                T_dc.NodeList.add(l_child);				
                root_dc.next.add(l_child);
                //****************************************************************************
                //System.out.println("Decompose serial tree left tree");				
                decomp(subtree.get(0),l_child);			
                Dnode r_child = new Dnode(startid+T_dc.NodeList.size()+1) ;				
                l_child.previous.add(root_dc);				
                T_dc.NodeList.add(r_child);				
                root_dc.next.add(r_child);	
                //****************************************************************************
                //System.out.println("Decompose serial tree right tree");		
                //for(Node snode : subtree.get(1).NodeList)
                //	for(Node nsnode : snode.next)
                //		System.out.println("(("+snode.data+","+nsnode.data+"))");
                decomp(subtree.get(1),r_child);		
            }else{
                //****************************************************************************
                //System.out.println("----------Parallel Tree-----------");
                root_dc.VertexStatue = -2 ;  				
                fixDag(T1,entryNode,exitNode);				
                fixDag(T_temp,entryNode,exitNode);			
                Dnode lnode = new Dnode(startid+T_dc.NodeList.size()+1);				
                lnode.previous.add(root_dc);				
                root_dc.next.add(lnode);			
                T_dc.NodeList.add(lnode);			
                Dnode rnode = new Dnode(startid+T_dc.NodeList.size()+1);				
                rnode.previous.add(root_dc);			
                root_dc.next.add(rnode);		
                T_dc.NodeList.add(rnode);	
                //****************************************************************************
                decomp(T1,lnode);	
                //****************************************************************************
                decomp(T_temp,rnode);
            }
        }
    }

    /**
     * fix dag
     * 
     * */	
    public void fixDag(DAG T, Node entryNode, Node exitNode){

        List<Node> exitList = T.GetExitNodes();		
        List<Node> entryList = T.GetEntryNodes();		
        Node entryNodenew = (Node)entryNode.clone();		
        Node exitNodenew = (Node)exitNode.clone();		
        entryNodenew.previous.clear();			
        exitNodenew.next.clear();		
        entryNodenew.next.clear();	
        exitNodenew.previous.clear();
        for(int i = 0; i < entryList.size(); i++){			
            entryList.get(i).previous.add(entryNodenew);			
            entryNodenew.next.add(entryList.get(i));
        }				
        for(int j = 0; j < exitList.size(); j++){		
            exitList.get(j).next.add(exitNodenew);			
            exitNodenew.previous.add(exitList.get(j));			
        }
        T.NodeList.add(entryNodenew);		
        T.NodeList.add(exitNodenew);
        //for(Node node : T.NodeList)
        //	for(Node next : node.next)
        //		System.out.println("("+node.data+","+next.data+")");
    }



    public DAG Seprate_dfs(DAG T_temp, Node entryNode){

        T_temp.ResetStatue();
        DAG T1 = new DAG();
        T1.NodeList.add((Node)entryNode.clone());
        S_dfs(entryNode, T1, T_temp);
        for(Node node1 : T1.NodeList)
            T_temp.DelNode(node1);
        return T1;	
    }

    public void S_dfs(Node node, DAG T1, DAG T_temp){

        node.VertexStatue = 1;
        List<Node> adjList = new LinkedList<Node>();
        for(Node nnode : node.next)
            adjList.add(nnode);
        for(Node pnode : node.previous)
            adjList.add(pnode);   
        for(Node nex : adjList){
            Node next = T_temp.FindNode(nex.data);
            if(next.VertexStatue == 0){
                T1.NodeList.add((Node)next.clone());
                S_dfs(next,T1,T_temp);
            }
        }
    }


    public List<DAG> Seprate_dag2(Node breakNode, DAG T){
        List<DAG> subtreeList = new LinkedList<DAG>();
        DAG T2 = (DAG)T.clone();		
        DAG T1 = new DAG();
        Node entryNode = T2.GetEntryNode();
        T2.DelNode(breakNode);
        Node tempbreak = (Node)breakNode.clone();   
        tempbreak.previous.clear();
        tempbreak.next.clear();
        T1 = this.Seprate_dfs(T2,entryNode);
        List<Node> exitList = new LinkedList<Node>();
        exitList = T1.GetExitNodes();
        for(Node exnode : exitList){
            exnode.next.add(breakNode);
            tempbreak.previous.add(exnode);
        }
        T1.NodeList.add(tempbreak);
        List<Node> entryList = new LinkedList<Node>();
        entryList = T2.GetEntryNodes();
        Node tempbreak2 = (Node)breakNode.clone();   
        tempbreak2.previous.clear();
        tempbreak2.next.clear();
        for(Node ennode : entryList){
            ennode.previous.add(tempbreak2);
            tempbreak2.next.add(ennode);
        }
        T2.NodeList.add(tempbreak2);
        //for(Node node : T2.NodeList)
        //	for(Node next : node.next)
        //		System.out.println("((("+node.data+","+next.data+")))");
        subtreeList.add(T1);
        subtreeList.add(T2);
        return subtreeList;
    }


    public Node GetBreakNode(DAG T1){
        //for(Node node : T1.NodeList)
        //	for(Node next : node.previous)
        //		System.out.println("("+next.data+","+node.data+")");
        T1.ResetStatue();
        List<Node> breakList = new LinkedList<Node>();
        dfs(T1.GetEntryNode() , breakList,T1);
        //for(Node node : breakList)
        //	System.out.println("The breakpoint is:"+node.data);
        T1.ResetStatue();
        counter = 0;
        return breakList.get(0);
    }

    public void dfs(Node node, List<Node> breakList, DAG T1){
        node = T1.FindNode(node.data);
        node.VertexStatue = 1;
        node.dfn = node.low = ++counter;
        List<Node> adjList = new LinkedList<Node>();
        for(Node nnext : node.next)
            adjList.add(nnext);
        for(Node pnode : node.previous)
            adjList.add(pnode);

        for(Node next : adjList){
            next = T1.FindNode(next.data);
            if(next.VertexStatue == 0){
                next.parent = node;
                dfs(next, breakList,T1);
                node.low = Math.min(node.low, next.low);
                if(node.parent != null && next.low >= node.dfn)
                    breakList.add(node);
            }
            else if(node.parent == null || next.data != node.parent.data)
                node.low = Math.min(node.low, next.dfn);
        }
    }

    //	public static void main(String agrs[]) throws FileNotFoundException{
    //		String path = "debug/decompose.txt";
    //		PrintStream ps=new PrintStream(new FileOutputStream(path));  		 
    //	    System.setOut(ps);     
    //
    //		SP sp = new SP();		
    //		sp.dag = new DAG();		
    //		sp.dag.InitDAG();	
    //		sp.SP_ization();		
    //		Decompose dc = new Decompose();		
    //		dc.dag = (DAG)sp.dag.clone();
    //		dc.dag.ResetStatue();
    //		dc.decompose();
    //		for(Node node : dc.T_dc.NodeList)
    //			for(Node next : node.next)
    //				System.out.println("( "+node.data+", "+next.data+" )");		
    //	}
}
