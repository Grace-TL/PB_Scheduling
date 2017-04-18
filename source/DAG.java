package source;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DAG implements Cloneable{

    public int N;   //number of nodes	
    public List<Node> NodeList = new LinkedList<Node>();  //node list

    public List<ArcNode> ArcNodeList = new LinkedList<ArcNode>(); //edge list	

    public String Path="Cbbbs_DAG/cbbb_502.txt";   //path 
    //	public String Path="SP_DAG/sp_1000.txt";   
    private double[] Jobtime;


    @SuppressWarnings("unchecked")
    @Override  
    public Object clone() {  
        DAG dag = null;  
        try{  
            dag = (DAG)super.clone();   //easy clone  
        }catch(CloneNotSupportedException e) {  
            e.printStackTrace();  
        }    
        dag.NodeList = (List<Node>) ((LinkedList<Node>) NodeList).clone();   //deep clone  
        dag.ArcNodeList = (List<ArcNode>) ((LinkedList<ArcNode>) ArcNodeList).clone();   //deep clone          
        return dag;  
    }  


    //read data from file and init DAG
    public void InitDAG() throws FileNotFoundException{

        System.out.println("Loading data......");
        FileInputStream fis=new FileInputStream(Path);
        System.setIn(fis);	     
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        N =  scanner.nextInt();   //read the total number of nodes	
        Jobtime = new double[N+1];  
        for(int i = 1; i <= N; i++){		
            Node node = new Node(i);			
            NodeList.add(node);				
        }
        //construct the edge list	
        while(scanner.hasNext()){			
            int first = scanner.nextInt();			
            int second = scanner.nextInt();					
            ArcNode arcNode = new ArcNode(first,second);			
            ArcNodeList.add(arcNode);		
            UpdateNode(first, second, false);
        }	
    }	


    public void InitNodeJobtime() throws FileNotFoundException{
        System.out.println("Loading data......");
        FileInputStream fis=new FileInputStream("Cbbbs_DAG/temp.txt");
        System.setIn(fis);	     
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        int i=0;
        while(scanner.hasNext()){
            Jobtime[++i] = scanner.nextInt();
            NodeList.get(i).jobTime = Jobtime[i];
        }
    }
    /**
     * Update nodes according to edges
     * IsRemove ture:  remove the edge
     * IsRemove false : add edge
     * 
     * */	
    public boolean UpdateNode(int first, int second, boolean IsRemove){

        Node FirstNode = FindNode(first);		
        Node SecondNode = FindNode(second);		
        if(FirstNode == null || SecondNode == null)			
            return false;			
        if(!IsRemove){	
            FirstNode.next.add(SecondNode);			
            SecondNode.previous.add(FirstNode);			
            return true;			
        }else{			
            FirstNode.next.remove(SecondNode);			
            SecondNode.previous.remove(FirstNode);			
            return true;
        }
    }

    /**
     * return Node 
     * 
     * */
    public Node FindNode(int data){

        for(Node node : NodeList)
            if(node.data == data)
                return node;
        return null;
    }

    /**
     * If edge (st, ed) exists, return the index of ArcList,
     * otherwise, return -1.	
     *  
     * */	
    public ArcNode FindArc(int st, int ed){

        for(ArcNode arc : ArcNodeList)
            if(arc.firstNode == st && arc.nextNode == ed)
                return arc;	
        return null;
    }

    /**
     * Reset statue
     * 
     * */	
    public void ResetStatue(){	

        for(Node node : NodeList)
            node.VertexStatue = 0;
        for(ArcNode arc : ArcNodeList)
            arc.ArcStatue = 0;		
    }

    /**
     * Delete Node node
     * */
    public boolean DelNode(Node node ){  		
        this.N--;		
        Node onode = this.FindNode(node.data);		
        for(Node next: onode.next){            
            Node onext = this.FindNode(next.data);
            DelArc(onode.data, next.data);  
            for(Node npnode : onext.previous)  
                if(npnode.data == node.data){
                    onext.previous.remove(npnode);
                    break;
                }				
        }		
        for(Node previous : onode.previous){  
            Node oprevious = this.FindNode(previous.data);
            DelArc(previous.data, onode.data);
            for(Node pnnode : oprevious.next )
                if(pnnode.data == node.data){
                    oprevious.next.remove(pnnode);
                    break;
                }			
        }					
        this.NodeList.remove(onode);		
        return true;
    }

    /**
     * Delete edge(st, ed)
     * */
    public boolean DelArc(int st, int ed){

        for(ArcNode arc : ArcNodeList)
            if(arc.firstNode == st && arc.nextNode == ed){
                ArcNodeList.remove(arc);
                return true;
            }
        return false;
    }

    /**
     * For SP DAG, find the exit_Node 
     * there is only one exit node in SP DAG
     * */
    public Node GetExitNode(){

        for(Node node : NodeList)
            if(node.next.size() == 0)
                return node;		
        return null;
    }

    /**
     * Find the list of exit_Nodes
     *
     * */	
    public List<Node> GetExitNodes(){	
        List<Node> exitlist = new LinkedList<Node>();
        for(Node node : NodeList)
            if(node.next.size() == 0)
                exitlist.add(node);	
        return exitlist;		
    }

    /**
     * For SP DAG, find the entry node
     * 
     * */
    public Node GetEntryNode(){

        for(Node node : NodeList)
            if(node.previous.size() == 0)
                return node;	
        return null;
    }

    /**
     *Find entry node list
     * */
    public List<Node> GetEntryNodes(){

        List<Node> entrylist = new LinkedList<Node>();		
        for(Node node : NodeList)
            if(node.previous.size() == 0)			
                entrylist.add(node);	
        return entrylist;
    }

    /**
     * If node is in list, reutrn ture,
     * otherwise, return false
     * 
     * */
    public boolean contain(List<Node> list, Node node){		

        for(Node lnode : list)
            if(lnode.data == node.data)
                return true;	
        return false;
    }

    public DAG CopyDag(){
        DAG dag = new DAG();
        for(Node node : this.NodeList)
            dag.NodeList.add((Node)node.clone());
        return dag;
    }


    /**
     * Init Job time of each node
     * */
    public void initJobtime(int t){

        //Normal distribution
        double[] jobTime = new double[NodeList.size()];
        for(int k = 0 ; k < NodeList.size() ; k++)  
            jobTime[k] = new Random().nextInt(t) + 1; //generate integer of 1--t+1
        for(int i = 0; i < NodeList.size();i++){	 
            NodeList.get(i).jobTime = jobTime[i]; 
            System.out.println("Node "+NodeList.get(i).data+" jobtime is "+jobTime[i]);
        }
    }

    /**
     * Init job time from file
     * @throws FileNotFoundException 
     * */
    public void initJobtime() throws FileNotFoundException{

        System.out.println("Loading data......");
        FileInputStream fis=new FileInputStream("SP_DAG/q_data.txt");
        System.setIn(fis);	     
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        int i=0;
        while(scanner.hasNext()){
            Jobtime[i+1] = scanner.nextInt();
            NodeList.get(i).jobTime = Jobtime[i+1];
            System.out.println("Node "+NodeList.get(i).data+" jobtime is "+NodeList.get(i).jobTime);
            i++;	
        }

    }			 



}
