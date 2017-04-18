package AO;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

import source.DAG;
import source.Node;

public class Filter {

    public DAG dag;
    public DAG spdag = new DAG();
    public List<Node> schedule = new LinkedList<Node>();
    public void filter() throws FileNotFoundException{
        dag = new DAG();
        dag.InitDAG();
        List<Node> delList = new LinkedList<Node>();  
        List<Node> schedule_new = new LinkedList<Node>(); 
        //int sn_idx = 0;
        for(Node node : schedule){
            schedule_new.add(node);  
            boolean flag = false;

            for(Node dnode : dag.NodeList)

                if(node.data == dnode.data){

                    flag = true;

                    break;
                }

            if( ! flag){ //If node is new added, add it to delList

                delList.add(node);
                //sn_idx = schedule_new.size();  
                Node sp_node = spdag.FindNode(node.data);
                if(sp_node.previous.size()!=0 && sp_node.next.size()!=0){  
                    List<Node> equallist = new LinkedList<Node>();
                    List<Node> order = new LinkedList<Node>();
                    //Node snode = spdag.NodeList.get(spdag.FindNode(node.data));
                    for(Node previous : sp_node.previous)
                        equallist.add(previous);   
                    //for(Node pnode :equallist)
                    //	System.out.print(pnode.data+" ");
                    System.out.println();
                    DAG dag_temp = new DAG();  
                    for(Node onode : dag.NodeList)
                        dag_temp.NodeList.add((Node)onode.clone());
                    boolean delete = true;
                    while(delete){
                        delete = false;
                        List<Node> entrylist = new LinkedList<Node>();
                        entrylist = dag_temp.GetEntryNodes();
                        for(Node eenode : entrylist)
                            if(!Iscontain(equallist,eenode)){
                                dag_temp.DelNode(eenode);
                                //System.out.println("delete node is "+eenode.data);
                                delete = true;
                            }
                    }
                    //for(Node enode : equallist){
                    //	Node enode_ori = (Node) dag.NodeList.get(dag.FindNode(enode.data)).clone();
                    //	enode_ori.previous.clear();
                    //	dag_temp.NodeList.add(enode_ori);
                    //	for(Node enext:enode_ori.next)
                    //  	dag_temp.NodeList.add(enext);
                    //}
                    while(equallist.size()>0){   
                        Node maxnode = equallist.get(0);
                        int maxeligible = 0;
                        for(Node enode : equallist){
                            DAG dcopy = new DAG();
                            for(Node tenode : dag_temp.NodeList )
                                dcopy.NodeList.add((Node)tenode.clone());
                            int eligibefor = dcopy.GetEntryNodes().size();
                            dcopy.DelNode(enode);
                            int eligiafter = dcopy.GetEntryNodes().size();
                            //System.out.println("the node is: "+enode.data+"   eligibefor: "+eligibefor+"  eligiafter: "+eligiafter+"  maxeligible:"+maxeligible);
                            if(eligiafter - eligibefor+1 > maxeligible){			
                                maxeligible = eligiafter - eligibefor+1;
                                maxnode = enode;
                            }
                        }
                        order.add(maxnode);  
                        equallist.remove(maxnode);
                        dag_temp.DelNode(maxnode);
                    }
                    int a[] = new int[order.size()];
                    int p=0;
                    for(Node sch_node :schedule_new){
                        if(Iscontain(order,sch_node))
                            a[p++]=schedule_new.indexOf(sch_node);
                    }
                    for(int m=0;m<p;m++){
                        schedule_new.set(a[m], order.get(m));
                    }
                }
            }	
        }
        for(Node node : delList)
            schedule_new.remove(node);
        schedule = schedule_new;
    }

    public boolean Iscontain(List<Node> list, Node node){
        for(Node enode : list)
            if(enode.data == node.data)
                return true;
        return false;
    }
}
