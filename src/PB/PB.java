package PB;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import source.DAG;
import source.Node;

public class PB {

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


    public void PBSchedul( ) throws FileNotFoundException  {

        dag = new DAG();
        dag.InitDAG();
        SchedulList.clear();
        InitQuoVet initQ = new InitQuoVet();		
        initQ.dag = this.dag;	
        //Compute the initial DQ LQ EQ IQ for each node in G	
        initQ.Init();
        // Add the entry node into the Ready list L
        for(Node node : dag.GetEntryNodes()) 
            L.add(node);	
        SortList sort = new SortList();	
        //While L is not empty
        while(L.size()!=0){


            //			/** Test*/
            //			System.out.print("Nodes in L: ");
            //			for(Node lnode : L){
            //				System.out.print(lnode.data+" <");
            //				for(int i=0;i<4;i++)
            //					System.out.print(lnode.Quo_V[i]+",");
            //				System.out.print(" >");	
            //				System.out.println();
            //			}

            //Schedule the node v in L with the highest priority P.
            sort.SetL(L);			
            Node node = sort.MaxPriNode();
            //			/** Test*/
            //			System.out.println("Node with highest priority: "+node.data);
            //			System.out.println();

            this.SchedulList.add(node);	
            //Remove v from L
            L.remove(node);	
            //Remove v form G
            dag.DelNode(node);			
            initQ.dag = dag;
            //For each child x of v in G Update Priority.
            for(Node nnode : node.next)
                initQ.UpdatePriority(nnode);			
            //			UpdateL();	
            //Add new ready tasks into L.
            this.AddReady( );

        }
        System.out.print("The final PB schedule is : ");
        for(Node node : SchedulList){
            System.out.print(" "+node.data);
        }
        System.out.println();

    }


    /**
     * PB0
     * */
    public void PBSchedul(DAG dag1 ) throws FileNotFoundException  {
        dag = dag1;
        SchedulList.clear();
        InitQuoVet initQ = new InitQuoVet();		
        initQ.dag = dag1;	
        //Compute the initial DQ LQ EQ IQ for each node in G	

        initQ.Init_JT();
        // Add the entry node into the Ready list L
        for(Node node : dag1.GetEntryNodes()) 
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
            Node node = sort.MaxPriNode();

            /** Test*/
            //			System.out.println("Node with highest priority: "+node.data);
            //			System.out.println();

            this.SchedulList.add(node);	
            //Remove v from L
            L.remove(node);	
            //Remove v form G
            dag1.DelNode(node);			
            initQ.dag = dag1;
            //For each child x of v in G Update Priority.
            for(Node nnode : node.next)
                initQ.UpdatePriority(dag1.FindNode(nnode.data));			
            //			UpdateL();	
            //Add new ready tasks into L.
            this.AddReady( );

        }

        System.out.print("The final PB_0 schedule is : ");
        for(Node node : SchedulList){
            System.out.print(" "+node.data);
        }
        System.out.println();
    }
}
