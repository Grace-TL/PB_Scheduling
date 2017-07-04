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


public class Generate_Random {

	public int N;
	
	public int levels;

    public int num_node;

    public int[] res;
	public void CreateDAG( int n, int lev, String number ) throws FileNotFoundException{

		DAG dag = new DAG();		
		Random rand = new Random();		
		int N = n; //rand.nextInt(n)+1;  //random number
		int levels = lev; //rand.nextInt(lev)+1;   	
		System.out.println("N "+N+" levels "+levels);		
		String path = "/Users/tanglu/Workspace/research/PB_Scheduling/DAG_Random/random_"+number+".txt";
        PrintStream ps=new PrintStream(new FileOutputStream(path));  		 
	    System.setOut(ps);   
		for(int i = 1 ; i <= N ; i++){  //node id begin from 1, sp dag begin from 0 
			Node node = new Node(i);			
			dag.NodeList.add(node);		
            //System.out.println("node.statu"+node.VertexStatue);
		}
        //asgin a node to each level to ensure non-empty of each level
        Rsample(N-1, levels+1);
        for(int i = 0; i < levels+1; i++) {
            dag.NodeList.get(res[i]).layer = i;
            dag.NodeList.get(res[i]).VertexStatue = 1;
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
        
           System.out.println(N);
           for(Node node : dag.NodeList){			
           for(Node next : node.next)		
           System.out.println(node.data+" "+next.data);
           }
           
    }

    public void Rsample(int n, int m) {
        res = new int[m];
        Random random = new Random();
        for(int i= 0; i < n; i++){
            if (i < m) {
                res[i] = i+1;
            }else{
                int r = random.nextInt(Integer.SIZE-1)%i;
                if (r < m) {
                    res[r] = i+1;
                }
            }
        }
    }

    public static void main(String args[]) throws FileNotFoundException{

        if(args.length != 3){
            System.out.println("java Generate_Random [DAG_SIZE] [Level] [Number of DAG you want generate]");
            return;
        }
        int size = Integer.parseInt(args[0]);
        int level = Integer.parseInt(args[1]);
        int num = Integer.parseInt(args[2]);
        for(int i = 0; i < num; i++){
            String serial = size + "_" + i;
            Generate_Random ge = new Generate_Random();
            ge.CreateDAG(size, level, serial);
        }

    }
}

