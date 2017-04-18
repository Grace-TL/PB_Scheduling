package FIFO;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import source.DAG;
import source.Node;

public class FIFO {

    public DAG dag;
    public  Queue<Node> queue = new LinkedList<Node>();
    public List<Node> schedule = new LinkedList<Node>();

    public void Fifo(){
        schedule.clear();
        DAG dag_copy = new DAG();  
        for(Node node : dag.NodeList)
            dag_copy.NodeList.add((Node) node.clone());
        List<Node> eligible = new LinkedList<Node>();
        Random rand = new Random();
        while(dag_copy.NodeList.size()>0){
            for(Node node : dag_copy.GetEntryNodes()){  //construct eligible list
                if(!queue.contains(node))
                    eligible.add(node);
            }

            while(eligible.size()>0){    //add node in eligible to queue randomly
                if(eligible.size()>1){
                    int i = rand.nextInt(eligible.size());
                    queue.offer(eligible.get(i));
                    eligible.remove(i);
                }else{

                    queue.offer(eligible.get(0));
                    eligible.remove(0);
                }
            }
            Node node = queue.poll();
            schedule.add(node);
            dag_copy.DelNode(node);

        }
        //		for(Node node : schedule)
        //			System.out.print(node.data+" ");
        //		System.out.println();
    }

    public int getEligible(DAG dag , List<Node> schedule){

        List<Integer> eligi = new LinkedList<Integer>();
        List<Integer> total = new LinkedList<Integer>();
        DAG co_dag = new DAG();
        int area = 0;
        for(Node node : dag.NodeList)
            co_dag.NodeList.add((Node)node.clone());
        for(Node node :schedule){
            int e1 = co_dag.GetEntryNodes().size() - 1; 
            co_dag.DelNode(node);
            int e2 = co_dag.GetEntryNodes().size();
            total.add(e2);
            eligi.add(e2 - e1)  ;  
        }
        System.out.print("eligible jobs rendered by  : ");
        for(Integer i : eligi)
            System.out.print(" "+i);
        System.out.println();
        System.out.print("the total eligible jobs after t's execution : ");
        for(Integer i : total){
            System.out.print(" "+i);
            area += i;
        }
        System.out.println();
        System.out.println("The area is "+area+", "+"the average area is "+(double)area/dag.NodeList.size());
        return area;
    }


    //	public static void main(String agrs[]) throws FileNotFoundException{
    //		
    //		FIFO  fi = new FIFO();
    //		fi.dag = new DAG();
    //		fi.dag.InitDAG();
    //		int Area=0;
    //		for(int i=0;i<50;i++){
    //			fi.Fifo();
    //			Area += fi.getEligible(fi.dag, fi.schedule);
    //		}
    //		System.out.println("The total average area is "+Area/50);
    ////		for(Node node : fi.schedule)
    ////			System.out.print(node.data+" ");
    ////		System.out.println();
    //		
    //	}


}
