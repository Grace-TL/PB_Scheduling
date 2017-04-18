package AO;

import source.DAG;
import source.Node;

public class ReShortcuts {
	
	public DAG dag;
	public int[][] Adj;
	public int[][] P;
	
	public void InitAdj( ){

		int N = dag.NodeList.size();
		Adj = new int[N+1][N+1];
		for(Node node : dag.NodeList){
			for(Node next : node.next)
				Adj[node.data][next.data] = 1;
		}
	}
	/**
	 *get the closure of dag
	 * */
	public void P_closure( ){

		int N = Adj.length;
		P = new int[N][N];
		for(int k = 1;k < N;k++)
			for(int j = 1;j < N;j++)
				P[k][j]=Adj[k][j];
		for(int k = 1; k < N;k++)
			for(int i = 1;i < N;i++)
				for(int j=1;j<N;j++){
					if(P[i][k]+P[k][j]==2)
						P[i][j]=1;
				}

		for(int i=1;i<N ;i++)
			P[i][i]=0;
	}
	
	public void GenAdj(){
		int N = Adj.length;
		for(int i=1;i<N;i++)
			for(int j=1;j<N;j++)
				if(P[i][j]==1){
					for(int k=0;k<N;k++)
						if(P[j][k]==1&&Adj[i][k]==1){
							reDag(i,k);
							P[i][k]=0;
						}	
				}		
	}

	
	/**
	 * remove edge(j,k) from dag
	 * */
	private void reDag(int j, int k) {
		for(Node node : dag.NodeList)
			if(node.data == j)
				for(Node next : node.next)
					if(next.data == k){
						node.next.remove(next);
						break;
					}
		for(Node node : dag.NodeList)
			if(node.data == k)
				for(Node previous : node.previous)
					if(previous.data == j){
						node.previous.remove(previous);
						break;
					}	
	}
	
	public void Removeshortcut(){
		this.InitAdj();
		this.P_closure();
		this.GenAdj();
	}

}
