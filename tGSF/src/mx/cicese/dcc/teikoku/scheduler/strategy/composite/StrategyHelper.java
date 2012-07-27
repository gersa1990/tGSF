package mx.cicese.dcc.teikoku.scheduler.strategy.composite;

import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import mx.cicese.dcc.teikoku.workload.job.Vertex;
import mx.cicese.dcc.teikoku.workload.job.Edge;


/**
 *	Set of static functions used to perform operations on list or other structures
 *	used by workflow scheduling strategies. 
 *	<p>
 *
 *	StrategyHelper currently implements the following helper functions:
 *	<ul>
 *	<li> sortIncreasing 
 *						Sorts the elements in map in increasing order, the 
 *						sorted keys are stored in a <code> List </code>.
 *	<li> sortDecreasing 
 *						Sorts the elements in map in decreasing order, the 
 *						sorted keys are stored in a <code> List </code>.
 *	</ul>
 *	<p> 
 * 
 * 	Sorting is performed based on the weight associated to each key.
 * 	<p>
 * 
 * @author </a>href="mailto:ahirales@uabc.mx">Adan Hirales Carbajal</a>
 *         (last edited by $Author$)
 * @version $Version$, $Date$
 */

public final class StrategyHelper<V extends Vertex>{
	
	
	private static class Value{
		private Object v;
		private Number n;
		
		public Value(Object v, Number n){
			this.v = v;
			this.n = n;
		}
		
		public Number getValue() {
			return this.n;
		}
		
		public Object getKey() {
			return this.v;
		}
	} //End Value

	
	
	static class CostComparator implements Comparator {
		public int compare(Object obj1, Object obj2) {
			if(!(obj1 instanceof Value) || !(obj2 instanceof Value))
				throw new ClassCastException();
			long value = ((Value)obj1).getValue().longValue() - ((Value)obj2).getValue().longValue();
			return (new Double(Math.floor(value))).intValue();
		}
	}
	
	/**
	 *	Sorts the elements of a given map in increasing order.
     *
     * @param map 	a map containing <V,ranking> values. 
     * 				A jobs rank is computed using a Priority asigment strategy.
     * @return 		an list sorted in increasing order.
     * @see Priority
     */
	@SuppressWarnings(value = "unchecked")
	public static <V extends Vertex, E extends Edge>
	List<V> sortIncreasing(Map<V,Number> map) {
		List<Value> list = new LinkedList<Value>();
		List<V> vList = new LinkedList<V>();
		
		/* Transfor Map into a list with */
		for(V v : map.keySet())
			list.add(new Value(v,map.get(v)));
		
		/* Sort the list */
		Collections.sort(list, new StrategyHelper.CostComparator());
		
		/* Retreive only the values of the sorted list */
		for(Iterator it = list.iterator(); it.hasNext();) 
			vList.add((V)((Value)it.next()).getKey());
		
		return vList; 
	}// End sort
	
	
	/**
	 *	Sorts the elements of a given map in decreasing order.
     *
     * @param map 	a map containing <V,ranking> values. 
     * 				A jobs rank is computed using a Priority asigment strategy.
     * @return 		an list sorted in decreasing order.
     * @see Priority
     */
	@SuppressWarnings(value = "unchecked")
	public static <V extends Vertex, E extends Edge>
	List<V> sortDecreasing(Map<V,Number> map) {
		List<Value> list = new LinkedList<Value>();
		List<V> vList = new LinkedList<V>();
		
		/* Transfor Map into a list with */
		for(V v : map.keySet())
			list.add(new Value(v,map.get(v)));
		
		/* Sort the list */
		Comparator<Value> revOrdComp = Collections.reverseOrder(new StrategyHelper.CostComparator());
		Collections.sort(list, revOrdComp );
		
		/* Retreive only the values of the sorted list */
		for(Iterator it = list.iterator(); it.hasNext();) 
			vList.add((V)((Value)it.next()).getKey());
		
		return vList; 
	}// End sort

	
	
	/**
	 *	Retreives a list of sorted independent jobs 
     *
     * @param map 	a map containing <V,ranking> values. 
     * 				A jobs rank is computed using a Priority asigment strategy.
     * @return 		an list sorted in decreasing order.
     * @see Priority
     */
	@SuppressWarnings(value = "unchecked")
	public static <V extends Vertex, E extends Edge>
	List<V> getSortedIndpJobs(List<V> sortedL, List<V> unSortedL) {
		List<V> sortedIndJobs = new LinkedList<V>();
				
		/* Transfor Map into a list with */
		for(Iterator it = sortedL.iterator(); it.hasNext();) {
			V job = (V) it.next();
			if(unSortedL.contains(job))
				sortedIndJobs.add(job);
		}// End for
		
		return sortedIndJobs; 
	}// End getSortedIndpJobs

	
}// End StrategyHelper
