package mx.cicese.dcc.teikoku.utilities;

import edu.uci.ics.jung.graph.Hypergraph;
import java.util.List;
import java.util.LinkedList;

/**
 *	Topological sort of a <b> directed acyclic graph </b> G=(V,E)
 *	is a linear ordering of vertices such that if there exists an
 * 	edge (u,v), then u appears before v in the linear ordering. 
 *	<p> 
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public class TopologicalSort<V,E> {
	/**
	 * A DFS distance labeler.
	 * @see DFSDistanceLabeler
	 */
	private DFSDistanceLabeler<V,E> dfs;
	
	/**
	 * The topological sor of vertices. 
	 */
	private List<V> ordListByFinishingTime;
	
	/**
	 * Class constructor
	 */
	public TopologicalSort() {
		dfs = new DFSDistanceLabeler<V,E>();
		ordListByFinishingTime = new LinkedList<V>();
	}// End TopologicalSort
	
	/**
	 * Computes the topological sort of the given graph.
	 * 
	 * @param g		a directed graph.
	 */
	public void sort(Hypergraph<V,E> g) {
		dfs.clear();
		dfs.labelDistances(g);
		this.ordListByFinishingTime = dfs.getVerticesOrdByFinishTime();
	}// End sort
	
	/**
	 * Returns a list of topologically ordered vertices
	 * 
	 * @return		the topological sorted vertices.
	 */
	public List<V> getTopologicalSort() {
		return this.ordListByFinishingTime;
	} // End getTopologicalSort
	
	
	 /**    
     *	Removes all mappings. 
     * 	
     */
    public void clear() {
    	this.ordListByFinishingTime.clear();
    } // clear
	
}// End TopologicalSort
