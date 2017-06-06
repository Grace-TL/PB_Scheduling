package ICO;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import source.Block;
import source.DAG;
import source.Dnode;
import source.Node;

import AO.ReShortcuts;


public class ICO {

    public DAG dag;
    //store super dag node, each node in super dag is also a dag
    //the first dag is the index dag
    public List<DAG> superDag = new ArrayList<DAG>();  
    //public DAG dag_ori;  //backup of the original dag
    public List<Node> schedule = new LinkedList<Node>();
    public Map<Integer, List<Node>> scheduleMap = new HashMap<Integer, List<Node>>();
    
    
    public void getSuperdag( ){
        //topo information table
        int[] table = new int[this.dag.N+1];
        DAG G_copy = new DAG();
        for(Node node : this.dag.NodeList)
            G_copy.NodeList.add((Node)node.clone());
        DAG indexdag = new DAG();
        int index = 0;
        superDag.add(index, indexdag);
        index += 1;
        List <Node> dellist = new ArrayList<Node>();
        List <Node> delarc = new ArrayList<Node>(); 
        while(G_copy.NodeList.size() != 0){
            Node node = G_copy.GetEntryNode();
            DAG dagnode = GetSupernode(node, G_copy);
            superDag.add(index, dagnode);
            index += 1;
    /*
            //dump
            System.out.println("----------------- the " + (index-1) + "-th dag -------------------");
            dagnode.dump();
    */
            //Delete dagnode from the original dag
            for(Node snode : dagnode.NodeList){
                Node csnode = G_copy.FindNode(snode.data);
                if((csnode.previous.size() == 0 ) || (csnode.next.size() == 0)){
                    dellist.add(csnode);
                }
                //dag contains snode has child dag (index - 1)
                else if((csnode.previous.size() > 0) && snode.previous.size() == 0){
                    table[snode.data] = index - 1;
                    //also need to handle the edges
                    for(Node tmpnode : snode.next){
                        if(G_copy.FindNode(tmpnode.data).next.size()>0){
                            delarc.add(csnode);
                            delarc.add(tmpnode);
                        }
                    }
                }
                //dag contains snode has parent dag (index - 1)
                else if((csnode.next.size() > 0) && snode.next.size() == 0){
                    table[snode.data] = -(index - 1);
                    //handle edges
                    for(Node tmpnode : snode.previous){
                        if(G_copy.FindNode(tmpnode.data).previous.size()>0){
                            delarc.add(tmpnode);
                            delarc.add(snode);
                        }                                                }
                }
            }
            //System.out.println("dellist size = " + dellist.size());
            for(Node delnode : dellist){
                G_copy.DelNode(delnode);
                //System.out.println("Delete node " + delnode.data);
            }
            for(int i = 0 ; i < delarc.size(); i+=2){
                G_copy.delarc(delarc.get(i).data, delarc.get(i+1).data);
            }
            dellist.clear();
            delarc.clear();
            //G_copy.dump();
        }
        for(int i = 1; i < superDag.size(); i++){
            Node inode = new Node(i);
            indexdag.NodeList.add(inode);
        }
        //Construct the topo of superdag
        for(int i = 1; i < superDag.size(); i++){
            for(Node node : superDag.get(i).NodeList){
                if(table[node.data] > 0 && i != table[node.data]){
                    Node node1 = indexdag.FindNode(i);
                    Node node2 = indexdag.FindNode(table[node.data]);
                    if(!indexdag.contain(node1.next, node2))
                        node1.next.add(node2);
                    if(!indexdag.contain(node2.previous, node1))
                        node2.previous.add(node1);
                }
                else if(table[node.data] < 0 && i != -table[node.data]){
                    Node node1 = indexdag.FindNode(i);
                    Node node2 = indexdag.FindNode(-table[node.data]);
                    if(!indexdag.contain(node1.previous, node2))
                        node1.previous.add(node2);
                    if(!indexdag.contain(node2.next, node1))
                        node2.next.add(node1);
                }
            }
        }

        /*
        System.out.println("Finish superDag construction, size is " + (superDag.size()-1));
        System.out.println("The index dag is ");
        indexdag.dump();
        */
    }

    /**
     *Construct a dag for a given source node n
     the children nodes of n are in dag
     if the node is in dag and it is not a source node
     all its parents nodes are in dag
     * */
    private DAG GetSupernode(Node node, DAG G_copy){
        Node copynode = (Node) node.clone(); 
        DAG sudag = new DAG();
        List <Node> sourcenodes = new LinkedList<Node>();
        sourcenodes.add(copynode);
        while(sourcenodes.size() != 0){
            Node tmpnode = sourcenodes.get(0);
            sourcenodes.remove(0);
            if(!sudag.contain(sudag.NodeList, tmpnode))
                sudag.NodeList.add(tmpnode);
            //Add children nodes of tmpnode to supernode
            for(Node nnode : tmpnode.next){
                Node cnnode = (Node) G_copy.FindNode(nnode.data).clone();
                cnnode.next.clear();
                if(!sudag.contain(sudag.NodeList, cnnode)){
                    sudag.NodeList.add(cnnode);
                    //for each non-source node in supernode, its parents must also be in supernode
                    for(Node pnode : cnnode.previous){
                        Node cpnode = (Node) G_copy.FindNode(pnode.data).clone();
                        //if parents node are not added in supernode, add it 
                        if(!sudag.contain(sudag.NodeList, cpnode)){
                            cpnode.previous.clear();
                            sudag.NodeList.add(cpnode);
                            //if this parent node is a source node in the original dag, add it to sourcenodes
                            if(cpnode.previous.size() == 0){
                                sourcenodes.add(cpnode);
                            }
                        }

                    }
                }
            }
        }
        //sudag.dump();
        return sudag;
    }

    /**
     *get the schedule for each super node
     * */
    public void getSMap(){
        for(int i = 1; i < superDag.size(); i++){
//            System.out.println("--------------  " + i + "-th superdag ----------------");
            List<Node> schedulelist = new ArrayList<Node>();
            conSchedule(superDag.get(i), schedulelist);
            scheduleMap.put(i,schedulelist);
        } 
    }

    /***
     *Get the schedule of the given dag
     * */
    private void conSchedule(DAG dagnode, List<Node> schedulelist){
        DAG G_copy = new DAG();
        for(Node node : dagnode.NodeList)
            G_copy.NodeList.add((Node)node.clone());
        List<Node> eligible = new LinkedList<Node>();
        eligible = G_copy.GetEntryNodes();
        while(eligible.size() != 0){
        /*
            //dump
            System.out.print("eligible:");
            for(Node node : eligible)
                System.out.print(node.data+" ");
            System.out.println();
        */
            Node mnode = eligible.get(0);
            for(Node node : eligible){
                if(mnode.next.size() < node.next.size())
                    mnode = node;
            }
            schedulelist.add(mnode);
            G_copy.DelNode(mnode);
            eligible = G_copy.GetEntryNodes();
       }
/*
       //dump
        System.out.print("schedule:");
        for(Node node : schedule)
            System.out.print(node.data+" ");
        System.out.println();
  */
    }
   
    /***
     *return the priority of two dags
     * */
    private double getPriority(int index1, int index2){
        DAG dag1 = superDag.get(index1);
        DAG dag2 = superDag.get(index2);
        int x,y;
        x = dag1.NodeList.size() - dag1.GetExitNodes().size();
        y = dag2.NodeList.size() - dag2.GetExitNodes().size();
        int i = 0, j = 0;
        double r = 1.0, rtmp = 0;
        int e1,e2,e3;
        for(i = 0; i <= x; i++){
            for(j = 0; j <= y; j++){
                //get three number
                e1 = getEligible(dag1, this.scheduleMap.get(index1), i);
                e2 = getEligible(dag2, this.scheduleMap.get(index2), j);
                e3 = getEligible(dag1, this.scheduleMap.get(index1), Math.min(x,i+j))
                    + getEligible(dag2, this.scheduleMap.get(index2), (i+j)-Math.min(x,i+j));
                rtmp = e3*1.0/(e1+e2);
            }
            if(r > rtmp)
                r = rtmp;
        }

        return r;

    }

    /**
     *return the number of eligible nodes
     for edag, execute x nodes according to schedule
     * */
    private int getEligible(DAG edag, List<Node> schedulelist, int x){
        DAG G_dag = new DAG();
        for(Node node : edag.NodeList)
            G_dag.NodeList.add((Node)node.clone());
        for(int i = 0; i < x; i++)
            G_dag.DelNode(schedulelist.get(i));
        return G_dag.GetEntryNodes().size();
    }
   
    public void ICOSchedule() throws FileNotFoundException{
        this.dag = new DAG();
        dag.InitDAG();
        this.superDag.clear();
        this.schedule.clear();
        this.scheduleMap.clear();
        //setp 1. Find G`s transitive skeleton
        ReShortcuts res = new ReShortcuts();
        res.dag = dag;
        res.Removeshortcut();
        //Step 2. Covert G to super dag
        getSuperdag();
        //Setp3. Find schedule for each node in super dag
        getSMap();
        //Step4. Choosing the source that maxmize the priority
        ico();

        //For source nodes, decide the priority relationship of each pair
        //then choose the maxnum priority
        //this.schedule = fi.schedule;
        

        /*
        //dump final schedule
        System.out.print("The final ICO Schedule is : ");
        for(Node node : this.schedule)
            System.out.print(" "+ node.data);
        System.out.println();
        */
    }


    public void ico(){
        List<Node> sources = superDag.get(0).GetEntryNodes();
        while(sources.size() != 0){
            int len = sources.size();
            double[][] pri_matrix = new double[len][len];
            //construct the priority matrix, find minimum in each row, then find the maximum
            double minmax = 0, min = 1;
            int max = 0;
            for(int i = 0; i < len; i++){
                for(int j = 0; j < len; j++){
                    if(j != i){
                        //node.data is the index of dag in superDag
                        pri_matrix[i][j] = getPriority(sources.get(i).data, sources.get(j).data);
                        if(pri_matrix[i][j] < min)
                            min = pri_matrix[i][j];
                    }
                }
                if(minmax < min){
                    minmax = min;
                    max = i;
                }
            }
            //schedule the nodes in sources[max]
            for(Node snode : this.scheduleMap.get(sources.get(max).data)){
                if(superDag.get(sources.get(max).data).FindNode(snode.data).next.size()!=0){
                    this.schedule.add(snode);
                    this.dag.DelNode(snode);
                }
            }
            superDag.get(0).DelNode(sources.get(max));
            sources = superDag.get(0).GetEntryNodes();
        }

        for(Node rnode : this.dag.NodeList)
            this.schedule.add(rnode);
    }






       public static void main(String agrs[]) throws FileNotFoundException{
    	
    	ICO ico = new ICO();
        ico.dag = new DAG();
        ico.dag.InitDAG();
    	ico.getSuperdag();
        ico.getSMap();
        ico.ico();
    }
}
