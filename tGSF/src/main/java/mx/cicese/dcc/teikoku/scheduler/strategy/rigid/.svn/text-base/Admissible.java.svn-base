package mx.cicese.dcc.teikoku.scheduler.strategy.rigid;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import de.irf.it.rmg.core.teikoku.site.Site;

import mx.cicese.dcc.teikoku.information.broker.GridInformationBroker;
import mx.cicese.dcc.teikoku.information.broker.GridInformationBrokerImpl;

public abstract class Admissible {
	
	/**
	 * Used to initialize all known site min waiting time (minWt) 
	 */
	private boolean initialized;
	
	
	/**
	 * The grid information broker
	 */
	private GridInformationBroker gInfoBroker;
	
	
	/**
	 * List of known sitess
	 */
	private List<UUID> knownSites;
	
	
	/**
	 * Class constructor
	 */
	public Admissible() {
		this.initialized = false;
	}
	
	
	/**
	 * Initializes the Grid information broker, the know sites are ordered
	 */
	public void initialize(Site site) {
		if(!this.initialized) {
			this.gInfoBroker = site.getGridInformationBroker();
			this.gInfoBroker.bind();
			
			knownSites = ((GridInformationBrokerImpl)gInfoBroker).getKnownSites();
			
			this.initialized = true;
		}
	}
	

	/**
	 * Retrieves the admissible set of machines for the given job
	 * 
	 * @param jobSize, the job size
	 * @return the admissible set of machines
	 */
	public List<UUID> getAdmissableSet(double jobSize) {
		LinkedList<UUID> admissibleSet = new LinkedList<UUID>(); 
		
		for(UUID s : this.knownSites)
			if( jobSize <= gInfoBroker.getKnownSite(s).getSiteInformation().getProvidedResources().size())
				admissibleSet.add(s);
		
		return admissibleSet;
	}
	
}
