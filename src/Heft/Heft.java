package Heft;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import source.DAG;
import source.Node;

public class Heft {

    public DAG dag;
    public List<Node> L = new LinkedList<Node>();   //Ready List
    public List<Node> SchedulList = new LinkedList<Node>();
    public void Init(){	
        List<Node> exitlist = dag.GetExitNodes();     //exitnodes Que_V{0,0,0,0}
    for(int i = 0; i < exitlist.size(); i++){	
        Node node = dag.FindNode(exitlist.get(i).data);			
        node.Quo_V[1] = 0;			
        node.Quo_V[2] = 0;			
        node.Quo_V[3] = 0;			
        SetLQ(node);
    }
    }

    /**
     * Init LQ by jobtime
     * */
    public void Init_JT(){	
        List<Node> exitlist = dag.GetExitNodes();     //exitnodes Que_V{0,0,0,0}
    for(int i = 0; i < exitlist.size(); i++){	
        Node node = dag.FindNode(exitlist.get(i).data);			
        node.Quo_V[1] = 0;			
        node.Quo_V[2] = 0;			
        node.Quo_V[3] = 0;			
        SetLQ(node);
    }   
    for(int i = 0; i < exitlist.size(); i++){	
        Node node = dag.FindNode(exitlist.get(i).data);			
        node.Quo_V[1] += Math.ceil(exitlist.get(i).jobTime/5);						
        SetJT(node);
    }	
    }

    public void SetJT(Node node){
        List<Node> previous = new LinkedList<Node>();
        previous = node.previous;
        for(Node pnode : previous){
            pnode = dag.FindNode(pnode.data);
            if(pnode.Quo_V[1] < node.Quo_V[1] + Math.ceil(pnode.jobTime)/5){
                pnode.Quo_V[1] = node.Quo_V[1] + Math.ceil(pnode.jobTime/5); 
                //				System.out.println("Node : "+node.data+"        LQ:"+node.Quo_V[1]);
                SetJT(pnode);
            }
        }
        node.SetPrevious(previous);
    }

    public void SetLQ(Node node){  	
        List<Node> previous = new LinkedList<Node>();			
        previous = node.previous;		
        for(int i = 0; i < previous.size(); i++){		
            Node pnode = dag.FindNode(previous.get(i).data);				
            if(pnode.Quo_V[1] < node.Quo_V[1] + 1 ){					
                pnode.Quo_V[1] = node.Quo_V[1] + 1;					
                SetLQ(pnode);
            }
        }			
        node.SetPrevious(previous);
    }


    public void AddReady( ){  

        for(Node node : dag.NodeList){
            if(node.previous.size()==0){				
                boolean flag = false;				
                for(int j = 0; j < L.size(); j++){					
                    if(L.get(j).data == node.data)						
                        flag = true;
                }				
                if(!flag)
                    L.add(node); 
            }
        }
    }

    public Node MaxPriNode(List<Node> L){
        Node MaxNode = L.get(0);
        for(Node node : L)
            if(node.Quo_V[1]>MaxNode.Quo_V[1])
                MaxNode = node;
        return MaxNode;
    }

    public void HeftSchedule() throws FileNotFoundException{

        dag = new DAG();
        dag.InitDAG();
        SchedulList.clear();
        Init();  
        Init_JT();  
        for(Node node : dag.GetEntryNodes()) 
            L.add(node);	
        while(L.size()!=0){		
            Node node = MaxPriNode(L);
            this.SchedulList.add(node);	
            //Remove v from L
            L.remove(node);	
            //Remove v form G
            dag.DelNode(node);			
            this.AddReady( );

        }
        System.out.print("The final Heft schedule is : ");
        for(Node node : SchedulList){

            System.out.print(" "+node.data);
        }
        System.out.println();

    }
}
