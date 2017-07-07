package PB;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import source.DAG;
import source.Node;

public class EPB {

    public DAG dag ;

    public List<Node> L = new LinkedList<Node>();   //Ready List
    
    public List<Node> SchedulList = new LinkedList<Node>();
    

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


    /**
     * EPB
     * */
    public void EPBSchedul( DAG mydag ) {
        this.dag = mydag;
        SchedulList.clear();
        InitQuoVet initQ = new InitQuoVet();		
        initQ.dag = this.dag;	
        //Compute the initial DQ LQ EQ IQ for each node in G	
        //initQ.Init();
        initQ.Init_JT();
        // Add the entry node into the Ready list L
        for(Node node : this.dag.GetEntryNodes()) 
            L.add(node);	
        SortList sort = new SortList();	
        //While L is not empty
        while(L.size()!=0){		

            /** Test*/
            //			System.out.print("Node in L: ");
            //			for(Node lnode : L){
            //				System.out.print(lnode.data+" <");
            //				for(int i=0;i<4;i++)
            //					System.out.print(lnode.Quo_V[i]+",");
            //				System.out.print(" >");	
            //				System.out.println();
            //			}

            //Schedule the node v in L with the highest priority P.
            sort.SetL(L);			
            Node node = sort.MaxPriNode_LQ();

            /** Test*/
            //			System.out.println("Node with highest priority: "+node.data);
            //			System.out.println();

            this.SchedulList.add(node);	
            //Remove v from L
            L.remove(node);	
            //Remove v form G
            this.dag.DelNode(node);			
            initQ.dag = this.dag;
            //For each child x of v in G Update Priority.
            for(Node nnode : node.next)
                initQ.UpdatePriority(this.dag.FindNode(nnode.data));			
            //			UpdateL();	
            //Add new ready tasks into L.
            this.AddReady( );

        }

        System.out.print("The final EPB schedule is : ");
        for(Node node : SchedulList){
            System.out.print(" "+node.data);
        }
        System.out.println();
    }
}
