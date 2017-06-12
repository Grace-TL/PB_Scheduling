package AO;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import source.Block;
import source.DAG;
import source.Dnode;
import source.Node;



public class AO {

    public DAG dag;
    public DAG T_dc;
    public DAG dag_ori;  //backup of the original dag
    public List<Node> schedule = new LinkedList<Node>();

    public void Ao( Dnode root){
        if(root.VertexStatue != -1 && root.VertexStatue != -2){  //-1 means serial nodes, -2 means paralell nodes, otherwise normal nodes.
            root.schedule.add(root);
            Node node = (Node) root.clone();
            root.G.NodeList.add(node);
            root.VertexStatue = 1;
        }

        else{
            if(root.next.get(0).VertexStatue != 1){				
                //System.out.println(">>>>Left tree--  "+root.next.get(0).data+"----");				
                Ao((Dnode) root.next.get(0));			
            }

            if(root.next.get(1).VertexStatue != 1){
                //System.out.println(">>>>Right tree-- "+root.next.get(1).data+" ----");	
                Ao((Dnode) root.next.get(1));
            }

            if(root.VertexStatue == -1){				
                //construct schedule
                for(Node node : ((Dnode)root.next.get(0)).schedule)					
                    root.schedule.add(node);			
                if(root.next.get(1).next.size()>0){				
                    for(Node node : ((Dnode)root.next.get(1)).schedule)				
                        if(((Dnode)root.next.get(1)).schedule.indexOf(node)!=0){					
                            root.schedule.add(node);						
                        }
                }else			
                    root.schedule.add(((Dnode)root.next.get(1)).schedule.get(0));
                //System.out.println("------------------------serial node "+root.data+"  schedule is: ");
                //for(Node node : root.schedule)
                //	System.out.print(" "+node.data);
                //System.out.println();
                if(root.next.get(0).next.size()==0 && root.next.get(1).next.size()==0){				
                    Node entrynode = ((Dnode)root.next.get(0)).G.NodeList.get(0);
                    Node exitnode = ((Dnode)root.next.get(1)).G.NodeList.get(0);
                    entrynode.next.add(exitnode);
                    root.G.NodeList.add(entrynode);
                    root.G.NodeList.add(exitnode);			
                }else{
                    //Construct G
                    DAG G1 = ((Dnode)root.next.get(0)).G;//Left tree
                    DAG G2 = ((Dnode)root.next.get(1)).G;//Right tree	
                    Node breaknode1 = G1.GetExitNode();
                    for(Node node : G1.NodeList){
                        root.G.NodeList.add(node);
                    }
                    for(Node node : G2.NodeList){
                        if(node.data == breaknode1.data){			
                            for(Node next : node.next){
                                breaknode1.next.add(next);
                                next.previous.clear();
                                next.previous.add(breaknode1);
                            }
                        }
                        else{		
                            root.G.NodeList.add(node);
                        }
                    }
                }
                //System.out.println("------------------------G is: ");
                //for(Node node : root.G.NodeList)
                //	for(Node previous : node.previous)
                //		System.out.println("( "+previous.data+", "+node.data+" )");
                root.VertexStatue = 1;
            }	
            else if(root.VertexStatue == -2){		
                Dnode lchild = ((Dnode)root.next.get(0));		
                Dnode rchild = ((Dnode)root.next.get(1));		
                DAG G1 = lchild.G;		
                DAG G2 = rchild.G;
                List<Block> blockList = new LinkedList<Block>();	
                List<Block> blockList_l = GetBreaks(G1,  lchild.schedule );
                List<Block> blockList_r = GetBreaks(G2,  rchild.schedule);	
                for(Block block : blockList_l)				
                    blockList.add(block);			
                for(Block block : blockList_r)				
                    blockList.add(block); 	
                root.schedule.add(G1.GetEntryNode());		
                while(blockList.size()!=0){				
                    Block maxBlock = new Block();				
                    maxBlock = blockList.get(0);				
                    for(Block block : blockList) //Find block with bigest AEV				
                        if(block.AEV > maxBlock.AEV)  
                            maxBlock = block;				
                    for(Node node : maxBlock.NodeList)				
                        root.schedule.add(node);			
                    blockList.remove(maxBlock);
                }		
                root.schedule.add(G2.GetExitNode());	

                //System.out.println("------------------------parallel node "+root.data+"  schedule is: ");
                //for(Node node : root.schedule)
                //	System.out.print(" "+node.data);
                //System.out.println();

                //construct G
                Node entrynode =G1.GetEntryNode();
                Node exitnode = G1.GetExitNode();
                Node exitnode2 = G2.GetExitNode();
                for(Node node :G1.NodeList){
                    root.G.NodeList.add(node);
                }
                for(Node node : G2.NodeList){
                    if(node.data == entrynode.data){ 
                        for(Node next : node.next){
                            next.previous.clear();
                            next.previous.add(entrynode);
                            entrynode.next.add(next);

                        }
                    }
                    else if(node.next.contains(exitnode2)){ 
                        node.next.clear();
                        node.next.add(exitnode);
                        exitnode.previous.add(node);
                        root.G.NodeList.add(node);
                    }
                    else if(node.data != exitnode.data)

                        root.G.NodeList.add(node);
                }
                //System.out.println("------------------------G is: ");
                //for(Node node : root.G.NodeList)
                //	for(Node next : node.next)
                //		System.out.println("( "+node.data+", "+next.data+" )");
                root.VertexStatue = 1;
            }
        }	
    }

    public List<Block> GetBreaks(DAG G , List<Node> schedule){

        DAG G_copy = new DAG();
        for(Node node : G.NodeList)
            G_copy.NodeList.add((Node)node.clone());
        Node entryNode = G_copy.GetEntryNode();
        Node exitNode = G_copy.GetExitNode();
        G_copy.DelNode(entryNode);
        G_copy.DelNode(exitNode);
        schedule.remove(0);
        schedule.remove(schedule.size()-1);
        List<Block> blockList = new LinkedList<Block>();
        while(G_copy.NodeList.size()!=0){
            double[] AVG = GetEligibility(G_copy,schedule);
            int cursor = 0;
            Block block = new Block();
            for(int i = 0; i < AVG.length; i++){
                if(AVG[i] >= AVG[cursor])
                    cursor = i;	
            }
            List<Node> del = new LinkedList<Node>( ); 
            for(int j = 0 ; j<= cursor; j++){
                block.NodeList.add(schedule.get(j));
                G_copy.DelNode(G_copy.FindNode(schedule.get(j).data));
                del.add(schedule.get(j));
            }
            for(Node node : del)
                schedule.remove(node);
            block.AEV = AVG[cursor];
            blockList.add(block);
        }
        return blockList;
    }

    public double[] GetEligibility(DAG G_copy, List<Node> schedule){

        DAG G = new DAG();
        for(Node node : G_copy.NodeList)
            G.NodeList.add((Node)node.clone());
        int[] EV1 = new int[schedule.size()]; 
        double[] AVG = new double[schedule.size()];
        for(Node node :schedule){
            int e1 = G.GetEntryNodes().size() - 1; 
            G.DelNode(node);
            int e2 = G.GetEntryNodes().size();
            EV1[schedule.indexOf(node)] = e2 - e1 ;  
        }

        int sum = 0;
        for(int i = 0; i < EV1.length ; i++){
            sum += EV1[i];
            AVG[i] = sum /(i+1.0);
        }

        //System.out.println("-------Current AVG is: ");
        //for(int i = 0; i <AVG.length;i++)
        //	System.out.print(" "+AVG[i]);
        //System.out.println();
        return AVG;
    }

    public void AOSchedule(String dagpath) throws FileNotFoundException{
        DAG dag = new DAG();
        dag.InitDAG(dagpath); 
        //setp 1. Find G`s transitive skeleton
        ReShortcuts res = new ReShortcuts();
        res.dag = dag;
        res.Removeshortcut();
        //Step 2. Covert G` to an SP-DAG
        SP sp = new SP();
        sp.dag = res.dag;
        //		sp.dag = dag;
        sp.SP_ization(dagpath);
        //sp.dag.dump();
        Filter fi = new Filter();
        for(Node node : sp.dag.NodeList)
            fi.spdag.NodeList.add((Node)node.clone());   
       /*
        for(Node node : sp.dag.NodeList)			
        	for(Node next : node.next)		
        		System.out.println("( "+node.data+", "+next.data+" )" );
        System.out.println("---------------------------------------------------------------------");
      */
        //Setp3. Find.......
        Decompose dc = new Decompose();
        dc.dag = (DAG)sp.dag.clone();
        dc.dag.ResetStatue();
        dc.decompose();
        //for(Node node : dc.T_dc.NodeList)
        //	for(Node next : node.next)
        //	 System.out.println("( "+node.data+", "+next.data+" ) -- " + node.VertexStatue);
        Dnode root = (Dnode) dc.T_dc.GetEntryNode();
        Ao(root);

        //Step4. "Filter" the AREA-max schedule
        fi.schedule = root.schedule;
        /*
        System.out.print("Schedule (before filter):" );
        for(Node node : root.schedule)
            System.out.println(" "+node.data);
        System.out.println();
        */
        fi.filter(dagpath);
        this.schedule = fi.schedule;
/*
        //dump final schedule
        System.out.print("The final AO Schedule is : ");
        for(Node node : schedule)
            System.out.print(" "+ node.data);
        System.out.println();
*/  
    }

    public void AOSchedule_sp(String dagpath) throws FileNotFoundException{
        DAG dag = new DAG();
        dag.InitDAG(dagpath); 
        long startTime_AO = System.currentTimeMillis(); 
        //Setp3. Find.......
        Decompose dc = new Decompose();
        dc.dag = (DAG) dag.clone();
        dc.dag.ResetStatue();
        dc.decompose();
        //for(Node node : dc.T_dc.NodeList)
        //	for(Node next : node.next)
        //	 System.out.println("( "+node.data+", "+next.data+" ) -- " + node.VertexStatue);
        Dnode root = (Dnode) dc.T_dc.GetEntryNode();
        Ao(root);

        //Step4. "Filter" the AREA-max schedule
        Filter fi = new Filter();
        fi.schedule = root.schedule;
        fi.filter(dagpath);
        long endTime_AO = System.currentTimeMillis(); 
        this.schedule = fi.schedule;

        /*
         * dump schedule
        System.out.print("The final AO Schedule is : ");
        for(Node node : schedule)
            System.out.print(" "+ node.data);
        System.out.println();
        System.out.println("Inner AO   "+(endTime_AO-startTime_AO));
        */
    }

    public static void main(String agrs[]) throws FileNotFoundException{
    	
    	AO ao = new AO();
        String path = "DAG_SP/sp_30_0.txt";
    	ao.AOSchedule(path);
    //			ao.AOSchedule_sp();
    }
}
