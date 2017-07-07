/**
 * Random DAG and CBBBC DAG generator
 * 
 * CreateDAG(n, lev, serial);   //generate random dag, n is the number of nodes, lev is the height of dag, serial is the file NO   GetCBBB_DAG(ge.
 * CreateCBBBs(p),serial);  //generate Cbbbs dag, p is the number of nodes, serial is the file NO.
 * 
 * **/
package experiment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.lang.String;

import source.DAG;
import source.Node;


public class Generate_DAG {

	public int N;
	
	public int levels;

    public int num_node;

	public void CreateDAG( int n, int lev, int number ) throws FileNotFoundException{

		DAG dag = new DAG();		
		Random rand = new Random();		
		int N = rand.nextInt(n)+1;  //random number
		int levels = rand.nextInt(lev)+1;   	
		//System.out.println("N "+N+" levels "+levels);		
		String path = "Random_DAG/dag_"+number+".txt";
		PrintStream ps=new PrintStream(new FileOutputStream(path));  		 
	    //System.setOut(ps);   
		for(int i = 1 ; i <= N ; i++){  //node id begin from 1, sp dag begin from 0 
			Node node = new Node(i);			
			dag.NodeList.add(node);			
		}	
		for(int j = 0 ; j <= levels; ){  //asgin a node to each level to ensure non-empty of each level
			int index = rand.nextInt(N-1);		
			if(dag.NodeList.get(index).VertexStatue != 1){			
				dag.NodeList.get(index).layer = j;	
				dag.NodeList.get(index).VertexStatue = 1;	
				j++;
			}
		}	
		for(int i = 0 ; i < N ; i++){  //randomly assign the remaining nodes to each level	
			int level = rand.nextInt(levels);	
			if(dag.NodeList.get(i).VertexStatue != 1){	
				dag.NodeList.get(i).layer = level;
				dag.NodeList.get(i).VertexStatue = 1;
			}
		}
		
		for(int i = 0 ; i < levels; i++){  //assign children nodes of each node except the sinks
			List<Node> upList = new LinkedList<Node>();
			List<Node> downList = new LinkedList<Node>();
			for(Node node : dag.NodeList)	
				if(node.layer == i)	
					upList.add(node);
				else if(node.layer == i+1)	
					downList.add(node);	
			for(Node upnode : upList){
				int child_num = rand.nextInt(downList.size());   		
				if(child_num == 0)	
					child_num = 1;
				for(int j = 0; j < child_num ; ){	
					int id ;	
					if(downList.size()==1)		
						id = 0;	
					else			
						id = rand.nextInt(downList.size()-1);
					if(!upnode.next.contains(downList.get(id))){
						upnode.next.add(downList.get(id));
						downList.get(id).previous.add(upnode);						
						j++;
					}
				}
			}
			
			for(Node dnode : downList){				
				if(dnode.previous.size()==0){  //handle non-entrynode without parents node					
					int parent_num = rand.nextInt(upList.size());					
					if(parent_num == 0)						
						parent_num = 1;					
					for(int j = 0 ; j < parent_num; ){						
						int id;						
						if(upList.size() == 1)							
							id = 0;						
						else							
							id = rand.nextInt(upList.size()-1);						
						if(!dnode.previous.contains(upList.get(id))){							
							dnode.previous.add(upList.get(id));							
							upList.get(id).next.add(dnode);							
							j++;
						}
					}
				}
			}
        }
        /*
           System.out.println(N);
           for(Node node : dag.NodeList){			
           for(Node next : node.next)		
           System.out.println(node.data+" "+next.data);
           }
           */
    }
	
	public List<DAG> CreateCBBBs(int p){
		
		Random rand = new Random();
		N = rand.nextInt(p)+1; 
        //System.out.println("N = "+N);
		int current_N = 0;
		List<DAG> CBBBs = new LinkedList<DAG>();
		
		while(current_N < N){
			
		int cid = rand.nextInt(4)+1;  //1. W 2. M 3. N 4. C 5.Q
		//System.out.println("w m n q ----" + cid);
		switch(cid){
		case 1: {
			
			int n = N - current_N;
			int x =  Math.max(rand.nextInt(Math.max((int)Math.sqrt(n), 1)), 1);
			int y =  Math.max(rand.nextInt(Math.max((int)Math.sqrt(n), 1)), 2);
			CBBBs.add(CreateW(x, y, current_N));
			
			current_N += (x*y+1);
			break;
		}
		
		case 2:{
			
			int n = N - current_N;
			int x = Math.max(rand.nextInt(Math.max((int)Math.sqrt(n), 1)), 1);
			int y = Math.max(rand.nextInt(Math.max((int)Math.sqrt(n), 1)), 2);
			CBBBs.add(CreateM(x,y,current_N));
			
			current_N += (x*y+1);
			break;
		} 
		
		case 3: {
			
			int n = N - current_N;
			int x = Math.max(rand.nextInt(Math.max(1, n/2)), 2);
			CBBBs.add(CreateN(x,current_N));
			
			current_N += 2*x;
			break;
		}
		
		case 4:{
			
			int n = N - current_N;
			int x = Math.max(rand.nextInt(Math.max(1, n/2)), 2);
			CBBBs.add(CreateC(x,current_N));
			
			current_N += 2*x;
			break;
		}
			
		case 5:{
			
			int n = N - current_N;
			int x = Math.max(rand.nextInt(Math.max(1, n/2)), 2);
			CBBBs.add(CreateQ(x,current_N));
			
			current_N += 2*x;
			break;
		}
		}
		}	
		
		return CBBBs;
	}
	
	public void GetCBBB_DAG(List<DAG> CBBBs, String number) throws FileNotFoundException{//////
		
	
		Random rand = new Random();
		
		while(CBBBs.size() > 1){
			List<DAG> cbbbs = new LinkedList<DAG>();
			for(DAG dag : CBBBs)
				cbbbs.add(dag);
			int id1 = rand.nextInt(cbbbs.size()-1);
			cbbbs.remove(id1);
			DAG dag2 = new DAG();
            int id2 = 0;
			if(cbbbs.size()>1){
                dag2 = cbbbs.get(rand.nextInt(cbbbs.size()-1));
                id2 = CBBBs.indexOf(dag2);
            }else{
				dag2 = cbbbs.get(0);
                id2 = CBBBs.indexOf(dag2);
            }
			 CBBBs.set(id1, Union(CBBBs.get(id1),dag2));
			 CBBBs.remove(id2);
			
		}

        //CBBBs.get(0).dump();
		
	
        this.num_node = CBBBs.get(0).NodeList.size();
		//System.out.println("The final CBBBS DAG is "+CBBBs.get(0).NodeList.size());
		
		String path = "/Users/tanglu/Workspace/research/PB_Scheduling/DAG_Cbbbs/cbbb_"+number+".txt";
    
	    
        PrintStream stdout = System.out;
        PrintStream ps=new PrintStream(new FileOutputStream(path));  
		 
	    System.setOut(ps);   
	    for(int i = 0; i < CBBBs.get(0).NodeList.size();i++)
	    	CBBBs.get(0).NodeList.get(i).data = i+1;
	    System.out.println(CBBBs.get(0).NodeList.size());
		for(Node node : CBBBs.get(0).NodeList)
			for(Node next : node.next)
			System.out.println(node.data+" "+next.data);
        System.setOut(stdout);

	}

	public DAG Union(DAG dag1, DAG dag2){
		Random rand = new Random();
		List<Node> sourceList = dag2.GetEntryNodes();
		
		List<Node> sinkList = dag1.GetExitNodes();
		int s = Math.min(sourceList.size(), sinkList.size());
		int num = new Random().nextInt(s);
		if(num == 0)
			num = 1;
		while(num !=0){
			Node snode ;
			Node tnode;
			if(sourceList.size()>1)
				snode = sourceList.get(rand.nextInt(sourceList.size()-1));
			else
				snode = sourceList.get(0);
			if(sinkList.size()>1)
				tnode = sinkList.get(rand.nextInt(sinkList.size()-1));
			else
				tnode = sinkList.get(0);
		    //System.out.println("Merge node " + tnode.data + " node " + snode.data);
            for(Node snnode : snode.next){  
				tnode.next.add(snnode);
				snnode.previous.set(snnode.previous.indexOf(snode), tnode);
			}
			dag2.DelNode(snode);
			sourceList.remove(snode);
			sinkList.remove(tnode);
			num --;
		}
		
		for(Node node : dag2.NodeList)
			dag1.NodeList.add(node);
	    //dag1.dump();	
		return dag1;

	}
	public DAG CreateQ(int x, int id) {
		DAG Q_dag = new DAG();
		for(int i = 0; i < 2*x; i++){  			
			Node node = new Node(++id);
			Q_dag.NodeList.add(node);
		}
		
		for(int j = 0; j < x; j++){
			
			for(int k = x; k < 2*x; k++){
				
					Q_dag.NodeList.get(j).next.add(Q_dag.NodeList.get(k));
					Q_dag.NodeList.get(k).previous.add(Q_dag.NodeList.get(j));
				
			}
		}
        /*
           System.out.println("Q_DAG " + Q_dag.NodeList.size());		
           for(Node node : Q_dag.NodeList)
           for(Node previous : node.previous);
           System.out.println("( "+ previous.data+", "+node.data+" )");
           */
        return Q_dag;
    }

	private DAG CreateC(int x, int id) {
		DAG C_dag = new DAG();
		for(int i = 0; i < 2*x; i++){  			
			Node node = new Node(++id);
			C_dag.NodeList.add(node);
		}
		
		for(int j = 0; j < x; j++){
			
			int num ;
			if(x >1)
				num =2;
			else
				num = 1;
			for(int k = 0; k < num; k++){
				if(k+j < x){
					C_dag.NodeList.get(j).next.add(C_dag.NodeList.get(x+k+j));
					C_dag.NodeList.get(x+k+j).previous.add(C_dag.NodeList.get(j));
				}else{
				
					C_dag.NodeList.get(x-1).next.add(C_dag.NodeList.get(x));
					C_dag.NodeList.get(x).previous.add(C_dag.NodeList.get(x-1));
				}
			}
        }
        /*
           System.out.println("C_DAG " + C_dag.NodeList.size());			
           for(Node node : C_dag.NodeList)
           for(Node next : node.next)
           System.out.println("( "+ node.data+", "+next.data+" )");
           */
        return C_dag;
    }

	private DAG CreateN(int x, int id) {
		//System.out.println("-----------------"+x);
		DAG N_dag = new DAG();
		for(int i = 0; i < 2*x; i++){  			
			Node node = new Node(++id);
			N_dag.NodeList.add(node);
		}
		
		for(int j = 0; j < x; j++){
			
			for(int k = 0; k < 2; k++){
				if(k+j < x){
					N_dag.NodeList.get(j).next.add(N_dag.NodeList.get(x+k+j));
                    N_dag.NodeList.get(x+k+j).previous.add(N_dag.NodeList.get(j));
                }
            }
        }
        /*
           System.out.println("N_DAG " + N_dag.NodeList.size());			
           for(Node node : N_dag.NodeList)
           for(Node previous : node.previous)
           System.out.println("( "+ previous.data+", "+node.data+" )");
           */
        return N_dag;
    }

	public DAG CreateW(int x, int y, int id){
		//System.out.println("-----------------"+x+"---"+y);
		DAG W_dag = new DAG();
		for(int i = 0; i < x*y+1; i++){  			
			Node node = new Node(++id);
			W_dag.NodeList.add(node);
		}
		int tmp = x;
        x = Math.min(x,y);
		y = Math.max(tmp, y);
		for(int j = 0; j < x; j++){
			
			for(int k = 0; k < y; k++){
				
				W_dag.NodeList.get(j).next.add(W_dag.NodeList.get(x+k+j*(y-1)));
				W_dag.NodeList.get(x+k+j*(y-1)).previous.add(W_dag.NodeList.get(j));
			}
		}
        /*
           System.out.println("W_DAG size " + W_dag.NodeList.size());
           for(Node node : W_dag.NodeList)
           for(Node previous : node.previous)
           System.out.println("( "+ previous.data+", "+node.data+" )");
           */
        return W_dag;
    }
	
	public DAG CreateM(int x, int y, int id){

		DAG M_dag = new DAG();
		for(int i = 0; i < x*y+1; i++){  			
			Node node = new Node(++id);
			M_dag.NodeList.add(node);
		}
        int tmp = x;
		x = Math.min(x,y);
		y = Math.max(tmp, y);
		for(int j = 0; j < x; j++){
			
			for(int k = 0; k < y; k++){
				
				M_dag.NodeList.get(j).previous.add(M_dag.NodeList.get(x+k+j*(y-1)));
				M_dag.NodeList.get(x+k+j*(y-1)).next.add(M_dag.NodeList.get(j));
			}
        }
        /*
           System.out.println("M_DAG " + M_dag.NodeList.size());			
           for(Node node : M_dag.NodeList)
           for(Node next : node.next)
           System.out.println("( "+ node.data+", "+next.data+" )");
           */
        return M_dag;
    }

	public static void main(String args[]) throws FileNotFoundException{

        if(args.length != 2){
            System.out.println("java Generate_DAG [DAG_SIZE] [Number of DAG you want generate]");
            return;
        }
        int size = Integer.parseInt(args[0]);
        int num = Integer.parseInt(args[1]);
        for(int i = 0; i < num; i++){
            //		ge.CreateDAG(0);   //Random dag
            String serial = size + "_" + i;
            Generate_DAG ge = new Generate_DAG();
            while((ge.num_node > size+5) || (ge.num_node < size-5) ){
                ge.GetCBBB_DAG(ge.CreateCBBBs(size*2),serial);
            }
        }
		
	}
}

