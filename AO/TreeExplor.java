package AO;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import source.DAG;
import source.Layer;
import source.Node;
import source.Relative;

public class TreeExplor {

    public List<Layer> LayerList = new LinkedList<Layer>();

    public DAG dag;

    public DAG tree;

    public Relative relative;

    public List<Relative> relativeList;

    //to store the tree that has been explored

    public List<Node> orphanList = new LinkedList<Node>();

    //tree table, used to find LCA (Lowerest common ancestor)
    public int[][] P;

    //tree ancestor table, used to find LCA
    public int[] T;

    //tree node level table, used to find LCA
    public int[] L;

    public int log2(int n){
        if(n == 0 || n == 1)
            return n;
        int ret = 0;
        while( n > 1 ){
            n = n/2;
            ret++;
        }
        return ret;
    }

    public void preprocess_LCA(int maxno){

        int n = this.dag.NodeList.size();
        int m = log2(n);
        P = new int[maxno+1][m];
        T = new int[maxno+1];
        L = new int[maxno+1];
        int i, j;

        //initialize every element in P with -1
        for(i = 0; i < maxno+1; i++){
            T[i] = -1;
            L[i] = -1;
            for(j = 0; j < m; j++)
                P[i][j] = -1;
        }


        //construct T,L,i is the index in NodeList
        for(Node node : this.tree.NodeList){
            if(node.previous.size() == 0){
                T[node.data] = node.data;
            }else{
                int tmp = node.previous.get(0).data;
                T[node.data] = tmp;
            }
            L[node.data] = node.layer;
        }

        //dump
        /*
           System.out.print("T: ");
           for(i = 0; i < maxno+1; i++)
           System.out.print(T[i]+" ");
           System.out.println();
           */
        //dump
        /*
           System.out.print("L: ");
           for(i = 0; i < maxno+1; i++)
           System.out.print(L[i]+" ");
           System.out.println();
           */
        //the first ancestor of every node i is T[i]
        for(i = 0; i < maxno+1; i++)
            P[i][0] = T[i];

        //bottom up dynamic programing
        for(j = 1; j < m; j++)
            for(i = 0; i < maxno+1; i++)
                if(P[i][j-1] != -1)
                    P[i][j] = P[P[i][j-1]][j-1];

        //dump
        /*
           for(i = 0; i < maxno+1; i++){
           for(j = 0; j < m; j++)
           System.out.print( P[i][j]+" ");
           System.out.println();
           }
           */
    }


    //public int query_LCA(Node p, Node q){
    public int query_LCA(int idp, int idq){

        //System.out.println("[Test] Query " + idp + " --- " + idq);
        int i, j; 

        //if p is situated on a higher level than q then we swap them 
        //System.out.println("L["+idp+"]"+"="+L[idp]+" --- " + "L["+idq+"]"+"="+L[idq]);
        if(L[idp] < L[idq]){
            int tmp = idp;
            idp = idq;
            idq = tmp;
            //System.out.println("[Test] L[p]>L[q], Swap!");
        }

        //compute the value of log(p.level)
        //int log = log2(L[idp]);
        int log;
        for(log = 1; 1 << log <= L[idp]; log++);
        log--;
        //System.out.println("[Test] log = "+log);

        //find the ancestor of node p situated on the same level
        //with q using the values in P
        for(i = log; i >= 0; i--){

            //System.out.println("[Test] L[p]="+L[idp]+"  (1<<i)="+(1<<i) +"   L[q]="+L[idq]);
            if(L[idp] - (1 << i) >= L[idq]){
                idp = P[idp][i]; 
                //System.out.println("[Test] L[p]-"+(1<<i)+">=L[q], p=P[p][i]="+P[idp][i]);
            }
        }

        if(idp == idq)
            return idp;

        //compute LCA(,q) using the values in P
        for(i = log; i >= 0; i--){
            //System.out.println("i="+i+" log="+log+" idp="+idp+"    idq="+idq);
            if(P[idp][i] != -1 && P[idp][i] != P[idq][i]){
                idp = P[idp][i];
                idq = P[idq][i];
                //System.out.println("i = "+i+"  idp="+idp+"  idq="+idq);
            }
        }

        return T[idp];
    }



    public void explor(int level){
        //****************************************************************************
        //System.out.println("Now start to explor the "+level+" level!");

        Layer layer = LayerList.get(level);  
        this.relativeList = layer.RelativeList;

        //Construct P T L of the graph
        preprocess_LCA(this.dag.NodeList.size()+1);

        //1. find the h'(U), KT(U), h(U), subF for all relatives
        //The most important is subF
        for(Relative relative : this.relativeList){
            //****************************************************************************
            /*
               System.out.println("--- relative --- "+layer.RelativeList.indexOf(relative));
               System.out.print("U: ");
               for(Node unode : relative.U)
               System.out.print(unode.data+" ");
               System.out.println();
               System.out.print("D: ");
               for(Node unode : relative.D)
               System.out.print(unode.data+" ");
               System.out.println();
               */
            explorU(relative);

        }	

        //2. Merge Relatives according to subF
        merge();
        //catch guer node
        boolean guer = true;
        while(guer){
            guer = false;
            for(int k = 0; k < this.relativeList.size(); k++){
                Relative relative = this.relativeList.get(k);
                if(relative.D.size() == 0){
                    //choose max L[lca] to merge
                    int maxlev = 0, p = 0;
                    for(int l = 0; l < this.relativeList.size(); l++){
                        if(l != k){
                            int lev = query_LCA(this.relativeList.get(l).H, relative.H);
                            if(maxlev <= L[lev]){
                                maxlev = L[lev];
                                p = l;
                            }
                        }
                    }

                    UnioRelative(this.relativeList.get(p), relative);
                    this.relativeList.remove(relative);
                    guer = true;
                    break;
                }
            }

            if(guer)
                merge();
        }
        /*
           for(Relative relative : this.relativeList){
        //****************************************************************************

        System.out.println("--- relative (after mergafter mergee)--- "+layer.RelativeList.indexOf(relative));
        System.out.print("U: ");
        for(Node unode : relative.U)
        System.out.print(unode.data+" ");
        System.out.println();
        System.out.print("D: ");
        for(Node unode : relative.D)
        System.out.print(unode.data+" ");
        System.out.println();
           }	
           */

    }


    private void merge(){
         boolean flag = true;
        int count = 0;
        while(count < this.relativeList.size()){
            flag = true;
            while(flag){
                flag = false;
                Relative relative = this.relativeList.get(count);
                int i = 0;
                for(i = count+1; i < this.relativeList.size(); i++){
                    //System.out.println(" Now compare "+i+" ---- "+count);
                    if(needUnio(relative, this.relativeList.get(i))){
                        int tmp = query_LCA(relative.H, this.relativeList.get(i).H);
                        if(tmp == relative.H){
                            UnioRelative(relative, this.relativeList.get(i));
                            this.relativeList.remove(i);
                        }else if(tmp == this.relativeList.get(i).H){
                            UnioRelative(this.relativeList.get(i), relative);
                            this.relativeList.remove(count);
                        }else{
                            System.out.println("Error in Union relative!!");
                            return; 
                        }
                        flag = true;
                        break;
                    }
                }
            }
            count++;
        }


    
    }



    public void explorU(Relative relative){

        //construct h'(U)
        int p = relative.U.get(0).data;
        for(Node node : relative.U){
            p = query_LCA(p, node.data);
        }
        relative.h = p;
        //relative.h = exploration.get(0);

        //construct KTU : collection of father nodes of D, remove transitive of h'(U)
        List<Node> flist = new ArrayList<Node>();
        for(Node node : relative.D){
            for(Node fnode : node.previous){
                //TODO maybe we can remove the relationship between U and D when construct relatives
                if(!IsContain(relative.U, fnode) && !IsContain(flist, fnode))
                    flist.add(fnode);
            }
        }

        List<Node> dele = new ArrayList<Node>();
        for(Node node : flist){
            int q = query_LCA(relative.h, node.data);
            if(q == relative.h || q == node.data)
                dele.add(node);
        }
        flist.removeAll(dele);
        Node hhnode = new Node(relative.h);
        if(!IsContain(flist, hhnode))
        flist.add(hhnode);

        //construct h(U) LCA of KTU
        p = flist.get(0).data;
        for(Node node : flist){
            p = query_LCA(p, node.data);
        }
        relative.H = p;


        //construct subF(U)
        if(relative.H == relative.h){
            Node hnode = this.tree.FindNode(relative.H);
            for(Node node : relative.U){
                for(Node nnode: hnode.next){
                    int lca = query_LCA(node.data, nnode.data);
                    if(lca == nnode.data && !IsContain(relative.SubF, nnode))
                        relative.SubF.add(nnode);
                }
            }
            if(relative.SubF.size() == 0)
                for(Node node : relative.U)
                    relative.SubF.add(node);
        }else{
            Node hnode = this.tree.FindNode(relative.H);
            for(Node node : hnode.next){
                for(Node snode : flist){
                    int lca = query_LCA(node.data, snode.data);
                    //System.out.println("p="+node.data+"  q="+snode.data+"  lca="+lca);
                    if((lca == node.data || lca == snode.data)){
                        if(!IsContain(relative.SubF, node))
                            relative.SubF.add(node);
                        break;
                    }
                }
            }
        }
        /*
        //dump
        System.out.println("------ h'(U)="+relative.h);
        System.out.println("------ h(U)="+relative.H);
        System.out.print("------ KT(U) --");
        for(Node node : flist)
            System.out.print(" "+node.data);
        System.out.println();

        System.out.print("------ SubF");
        for(Node node : relative.SubF)
            System.out.print(" "+node.data);
        System.out.println();
        System.out.println("1111111111111111111111111");
    */
    }

    boolean needUnio(Relative re1, Relative re2){
       
        //no overlapping in SubF(U)
        if(re1.SubF.size()==1 && re2.SubF.size()==1 && 
                re1.SubF.get(0).data == re2.SubF.get(0).data)
            return false;
        
        
        int lca;
        /*
        //capture opharn node
        if((re1.U.size() == 1 && re1.D.size() == 0) || (re2.U.size() == 1 && re2.D.size() == 0)){
            lca = query_LCA(re1.H, re2.H);            
            if(lca == re1.H || lca == re2.H)
                return true;
        
        }
        */
        for(Node node1 : re1.SubF){
            for(Node node2 : re2.SubF){
                lca = query_LCA(node1.data, node2.data); 
                //System.out.println("LCA: ("+node1.data+" "+node2.data+")"+" ---- "+lca);
                if(lca == node1.data || lca == node2.data)
                    return true;
            }
        }
        return false;

    }

    public boolean IsContain(List<Node> list, Node node){
        for(int i = 0; i < list.size(); i++){
            if(list.get(i).data == node.data)
                return true;
        }
        return false;
    }

    public void UnioRelative(Relative relative1, Relative relative2){

        for(Node node : relative2.D){	
            relative1.D.add(node);
        }
        for(Node node : relative2.U){
            relative1.U.add(node);
        }
        //****************************************************************************
     /*
        System.out.println("relative "+relativeList.indexOf(relative2)+" is union in relative "+ relativeList.indexOf(relative1));
        System.out.println("the union node is");
        for(Node rnode : relative2.U)
        	System.out.print(rnode.data+" ");
        System.out.println();
       */
        explorU(relative1);
    }

    public void Synchron(int level){

        for(Relative rel : this.LayerList.get(level).RelativeList){ 
            /*
            if(rel.U.size()==1){
                
                for(Node node : rel.D){
                    for(Node p_node : node.previous){   
                        p_node.next.remove(node);
                        System.out.println("Remove "+p_node.data+"  "+node.data);
                    }
                    node.previous.clear();  
                    for(Node pnode : rel.U){
                        if(!IsContain(node.previous, pnode)){
                        
                        System.out.println("Add "+pnode.data+"  "+node.data);
                            node.previous.add(pnode);
                        }
                        if(!IsContain(pnode.next, node)){
                         
                        System.out.println("Add "+pnode.data+"  "+node.data);
                            pnode.next.add(node);		
                        }
                    }
                }


                for(Node node : rel.D){
                    Node tnode = this.tree.FindNode(node.data);
                    for(Node p_node : tnode.previous){   
                        p_node.next.remove(tnode);

                        System.out.println("Tree Remove "+p_node.data+"  "+node.data);
                    }
                    tnode.previous.clear();  
                    for(Node pnode : rel.U){
                        Node tpnode = this.tree.FindNode(pnode.data);
                        if(!IsContain(tnode.previous, tpnode)){
                        tnode.previous.add(tpnode);
                        }
                        if(!IsContain(tpnode.next, tnode)){
                            tpnode.next.add(tnode);		
                        }
                    }
                }
                
            }
            else{
            */
                //handle dag
                Node bu = new Node(this.dag.NodeList.size()+1); 							
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
                        if(!IsContain(nextnode.previous, bu))
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
                this.dag.NodeList.add(bu);
                //handle tree node
                Node sy = new Node(this.dag.NodeList.size());
                sy.layer = this.tree.FindNode(rel.U.get(0).data).layer;
                for(Node node : rel.U){
                    Node trnode = this.tree.FindNode(node.data);
                    for(Node pnode: trnode.previous){
                        pnode.next.remove(trnode);
                        pnode.next.add(sy);
                        if(!IsContain(sy.previous, pnode))
                            sy.previous.add(pnode);
                    }
                    for(Node nnode: trnode.next){
                        nnode.previous.remove(trnode);
                        if(!IsContain(nnode.previous, sy))
                        nnode.previous.add(sy);
                        if(sy.next.size() ==0 || !IsContain(sy.next,nnode)) { 
                            sy.next.add(nnode);	
                            //****************************************************************************
                            //System.out.println("Tree ArcNode:( "+sy.data+" , "+nnode.data+" )");
                        }
                 
                    }
                    this.tree.NodeList.remove(trnode);
                }
                for(Node node : rel.D){

                    Node tnode = this.tree.FindNode(node.data);
                    for(Node pdnode : tnode.previous)
                        pdnode.next.remove(tnode);
                    tnode.previous.clear();
                    tnode.previous.add(sy);
                    if(!IsContain(sy.next, tnode))
                        sy.next.add(tnode);

                }
                this.tree.NodeList.add(sy);


           // }
        }
    }
    public static void main(String args[]) throws FileNotFoundException{
        /*
        if(args.length!=2){
            System.out.println("Usage: [Node1] [Node2]");
            return;
        }
        int p = Integer.parseInt(args[0]);
        int q = Integer.parseInt(args[1]);
        */
        DAG tree = new DAG();
        tree.InitDAG("DAG_SP/sp_exp.txt");
        //Step1 Transform the input DAG into an StDAG a
        InitSP initSP = new InitSP();
        TreeExplor tre = new TreeExplor();

        tre.tree = new DAG();
        tre.tree.InitDAG("DAG_SP/sp_exp.txt");
        initSP.dag = tre.tree;
        initSP.TranStDAG();
        initSP.Initlayers(tre.tree);



        initSP.dag = tree;

        initSP.TranStDAG();
        //Step2 Layering of the graph.
        initSP.CreatLayers(initSP.dag);

        tre.dag = initSP.dag;
        tre.dag.dump();

        tre.LayerList = initSP.LayerList;
        for(int m=0;m<initSP.LayerList.size()-1;m++){
            //System.out.println("--------------Sync the "+m+" layer--------------------");
            Layer layer = tre.LayerList.get(m);
            int level = tre.LayerList.indexOf(layer);
            //for(Node tnode : layer.NodeList)
            //	System.out.println("Nodes in this layer: "+tnode.data);
            //a. Split layer in classes of relatives	
            initSP.SplitLayer(level);	
            //System.out.println("level--"+level+"     layer.RelativeList.size()---"+layer.RelativeList.size());
            //b.  Tree exploration to detect handles for classes of relatives	
            //c. Merge classes with overlapping forests
            //d. Capture orphan nodes
            //e. Class barrier synchronization
            //initSP.Initlayers(tre.dag);
            tre.dag.dump();
            tre.explor(level);
            tre.Synchron(level);
        }	
        
        tre.dag.dump();

    } 
}
