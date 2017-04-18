package PB;

import java.util.LinkedList;
import java.util.List;

import source.Node;

public class SortList {

    public List<Node> L = new LinkedList<Node>();

    public void SetL(List<Node> list){

        this.L = list;
    }

    /**
     * Compare the priority between node1 and node2
     * return 1: if node1 bigger than node2
     * return 0: if node1 equals to node2
     * return -1, if node1 smaller than node2
     */

    public int ComparePriority(Node node1, Node node2){

        switch(CompareQ(0, node1, node2)){
            case 1:{				
                       switch(CompareQ(1,node1,node2)){				
                           case -1:{					
                                       if(CompareQ(2,node1,node2) == -1)						
                                           return -1;					
                                       else						
                                           return 1;					
                           }				
                           default: return 1;				
                       }			
            }
            case 0:{				
                       switch(CompareQ(1,node1,node2)){				
                           case 1: return 1;				
                           case 0:{					
                                      switch(CompareQ(2,node1,node2)){					
                                          case 1: return 1;					
                                          case 0: return 0;					
                                          default: return -1;
                                      }
                           }
                           default: return -1;
                       }
            }
            default:{				
                        switch(CompareQ(1, node1, node2)){				
                            case 1: {					
                                        if(CompareQ(2, node1, node2) == 1)						
                                            return 1;					
                                        else						
                                            return -1;
                            }				
                            default: return -1;
                        }
            }
        }

    }

    public int CompareQ(int i,Node node1, Node node2){

        if(node1.Quo_V[i] > node2.Quo_V[i])		
            return 1;		
        else if(node1.Quo_V[i] == node2.Quo_V[i])			
            return 0;		
        else			
            return -1;
    }

    public Node MaxPriNode(){

        int max = 0;		
        for(int i = 1; i < L.size(); i++){			
            if( i < L.size() && ComparePriority(L.get(max), L.get(i)) == -1)				
                max = i;
        }
        return L.get(max);
    }

    public void setq(Node node,double i ,double j,double k,double m){
        node.Quo_V[0] = i;	
        node.Quo_V[1] = j;	
        node.Quo_V[2] = k;	
        node.Quo_V[3] = m;
    }

//	
//	public static void main(String agrs[]){
//	
//		SortList sl = new SortList();
//		
//		Node node1 = new Node(3);
//		
//		Node node2 = new Node(6);
//		
//		Node node3 = new Node(8);
//		
//		Node node4 = new Node(4);
//		
//		Node node5 = new Node(12);
//		
//		Node node6 = new Node(7);
//		
//		sl.setq(node1, 3.0, 2.0, 1.0, 0.0);
//		
//		sl.setq(node2, 0.0, 1.0, 0.0, 0.0);
//		sl.setq(node3, 0.0, 0.0, 0.0, 0.0);
//		sl.setq(node4, 0.0, 0.0, 0.0, 0.0);
//		sl.setq(node5, 0.0, 1.0, 0.0, 0.0);
//		sl.setq(node6, 0.0, 0.0, 0.0, 0.0);
//		
//		sl.L.add(node1);
//		
//		sl.L.add(node2);
//		sl.L.add(node3);
//		sl.L.add(node4);
//		sl.L.add(node5);
//		sl.L.add(node6);
//		
//		
//		System.out.println((int)sl.MaxPriNode().data);
//		
//	}
	
}
