import os
from optparse import OptionParser

#python propertiesCreator.py -f C:\Users\Anuar\workspace\Experiments\Multiple_Machines_Week\ -n 30 -c teikoku_1_ -s {(1;7)} -m 2

def main():
    usage = "usage: %prog [options] arg1 arg2"
    parser = OptionParser(usage=usage)
    parser.add_option("-f", "--filename", type="string",
                  dest="filename", help="the default output file name base",
                  metavar="FILE")
    parser.add_option("-n", "--numberexp", type="int",
                      dest="numberexp", help="the number of experiments wanted",
                      metavar="NUMBEREXP")
    parser.add_option("-c", "--chargename", type="string",
                      dest="chargename", help="the default charge file name base",
                      metavar="CHARGE")
    parser.add_option("-s", "--slastring", type="string",
                      dest="slastring", help="the sla string on the file",
                      metavar="SLASTRING")
    parser.add_option("-t", "--test", 
                      action="store_true", dest="testmode", default=False,
                      help="enter to test mode to verify the simulator")
    parser.add_option("-m", "--numberm", type="int",
                      dest="numbermachines", help="the number of machines",
                      metavar="NUMBERM")
    #parser.add_option("-e", "--experiment", help="the experiment type to simulate",
    #                  metavar="EXPERIMENTT")
    
    (options, args) = parser.parse_args()

    #Constant Definitions

    workloadsPath = 'C:\\\\Users\\\\Anuar\\\\workspace\\\\Experiments\\\\charges\\\\'
    

    site0confsection = "sites.site0.gridInformationBroker.class = mx.cicese.dcc.teikoku.information.broker.GridInformationBrokerImpl \n\
sites.site0.activitybroker.distributionpolicy.class = de.irf.it.rmg.core.teikoku.grid.distribution.StandardDistributionPolicy \n\
sites.site0.activitybroker.transferpolicy.class = de.irf.it.rmg.core.teikoku.grid.transfer.FittingTransferPolicy \n\
sites.site0.activitybroker.acceptancepolicy.class = de.irf.it.rmg.core.teikoku.grid.acceptance.StandardAcceptancePolicy \n\
sites.site0.activitybroker.locationpolicy.class = de.irf.it.rmg.core.teikoku.grid.location.MetricLocationPolicy \n\
sites.site0.activitybroker.locationpolicy.metric = awrtdel \n\
sites.site0.activitybroker.public = size \n\
sites.site0.resourcebroker.class = de.irf.it.rmg.core.teikoku.grid.resource.TestResourceBrokerGive \n\
sites.site0.resourcebroker.public = size \n\
sites.site0.scheduler.class = de.irf.it.rmg.core.teikoku.scheduler.ParallelMachineScheduler \n\
sites.site0.scheduler.localstrategy.class = de.irf.it.rmg.core.teikoku.scheduler.strategy.FCFSStrategy \n\
sites.site0.scheduler.localqueuecomparator.class = de.irf.it.rmg.core.teikoku.scheduler.queue.NaturalOrderComparator \n\
sites.site0.scheduler.localqueuecomparator.ordering = ascending \n\
sites.site0.registeredMetric.ref = parallel_site"
   
    metricString_1 = "metrics.id = awrtdel \n\
metrics.id = art \n\
metrics.id = rt \n\
metrics.id = cu \n\
metrics.id = awrt \n\
metrics.id = swf \n\
metrics.id = util \n\
metrics.id = waiting_time_indp \n\
metrics.id = parallel_site \n\
metrics.id = workflow_grid \n\
metrics.id = parallel_grid \n\n\
metrics.awrtdel.class = de.irf.it.rmg.core.teikoku.metrics.AverageWeightedResponseTimeDeliveredJobs \n\
metrics.awrtdel.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.awrtdel.writer.mayOverwrite = true \n\
metrics.awrtdel.writer.outputfile = 1 \n\
metrics.art.class = de.irf.it.rmg.core.teikoku.metrics.AverageResponseTime \n\
metrics.art.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.art.writer.mayOverwrite = true \n\
metrics.art.writer.outputfile = 1 \n\
metrics.rt.class = de.irf.it.rmg.core.teikoku.metrics.ResponseTime \n\
metrics.rt.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.rt.writer.mayOverwrite = true \n\
metrics.rt.writer.outputfile = 1 \n\
metrics.cu.class = de.irf.it.rmg.core.teikoku.metrics.CurrentUtilization \n\
metrics.cu.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.cu.writer.mayOverwrite = true \n\
metrics.cu.writer.outputfile = 1 \n\
metrics.awrt.class = de.irf.it.rmg.core.teikoku.metrics.AverageWeightedResponseTime \n\
metrics.awrt.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.awrt.writer.mayOverwrite = true \n\
metrics.awrt.notificationStyle = AFTER \n\
metrics.awrt.notificationStyle = ONJOBCOMPLETEDONFOREIGNSITE \n\
metrics.awrt.writer.outputfile = 1 \n\
metrics.awrt.limit = 100000 \n\
metrics.swf.class = de.irf.it.rmg.core.teikoku.metrics.SWFTrace \n\
metrics.swf.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.swf.writer.mayOverwrite = true \n\
metrics.swf.writer.outputfile = 1 \n\
metrics.util.class = de.irf.it.rmg.core.teikoku.metrics.OverallUtilization \n\
metrics.util.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.util.writer.mayOverwrite = true \n\
metrics.util.notificationStyle = AFTER \n\
metrics.util.writer.outputfile = 1 \n\
metrics.waiting_time_indp.class = de.irf.it.rmg.core.teikoku.metrics.WaitTime \n\
metrics.waiting_time_indp.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.waiting_time_indp.writer.mayOverwrite = true \n\
metrics.waiting_time_indp.notificationStyle = AFTER \n\
metrics.waiting_time_indp.writer.outputfile = 1 \n\
metrics.parallel_site.class = mx.cicese.mcc.teikoku.scheduler.SLA.SLAMetrics \n\
metrics.parallel_site.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.parallel_site.writer.mayOverwrite = true \n"
    metricString_2 = "metrics.workflow_grid.class = mx.cicese.dcc.teikoku.metrics.Grid.WorkflowJob_Grid \n\
metrics.workflow_grid.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.workflow_grid.writer.mayOverwrite = true \n\
metrics.workflow_grid.writer.outputfile = 1 \n\
metrics.parallel_grid.class = mx.cicese.dcc.teikoku.metrics.Grid.ParallelJob_Grid \n\
metrics.parallel_grid.writer.class = de.irf.it.rmg.core.util.persistence.CSVFilePersistentStore \n\
metrics.parallel_grid.writer.mayOverwrite = true \n\
metrics.parallel_grid.writer.outputfile = 1\n"

    gridConf = 'sites.site0.numberOfProvidedResources = 1 \n\
sites.site0.gridActivityBroker.class = mx.cicese.dcc.teikoku.broker.CentralizedGridActivityBroker \n\
sites.site0.gridActivityBroker.composite.strategy.class = mx.cicese.dcc.teikoku.scheduler.strategy.composite.DAS \n\
sites.site0.gridActivityBroker.dss.policy.class = mx.cicese.dcc.teikoku.scheduler.priority.DownwardRank \n\
sites.site0.gridActivityBroker.dss.strategy.class = mx.cicese.dcc.teikoku.scheduler.strategy.rigid.LBal_S \n\
sites.site0.gridStrategyRigid.class = mx.cicese.mcc.teikoku.scheduler.SLA.strategy.RandomStrategy \n'

    for nexp in range(1,options.numberexp+1):
        
        #We check if the input directory exits if don't we create it
        if not os.path.exists(options.filename):
            os.makedirs(options.filename)
        
        
        #We create the output file using the base name and the current experiment number
        expfilename = options.filename + 'teikoku_1_' + str(nexp) + '.properties'

        #We open the file in writting mode and if there is any other file with the same name it will be erased        
        f = open(expfilename, "w")

        #For each machine in the experiment we write the lines that identify it inside the simulator
        f.write('#Sites identifiers\n')
        for x in range(0,options.numbermachines+1):
            #We write the identifier lines
            f.write('sites.id = site' + str(x) + '\n')

        #Once finish we write an empty space for dividing thes section
        f.write('\n')

        #Now we write the Runtime information
        f.write('#Runtime information\n')
        for x in range(0,options.numbermachines+1):
            #We write the runtime information for each site
            f.write('runtime.workloadSource.id = site' + str(x) + '\n')

        #Once finish we write an empty space for dividing thes section
        f.write('\n')
        
        #Now we print the associated Runtime information
        f.write('#Associated runtime information\n')
        for x in range(0,options.numbermachines+1):
            #We write the assiciated runtime information for each site
            f.write('runtime.site' + str(x)+'.associatedSite.ref = site' + str(x) + '\n')

        #Once finish we write an empty space for dividing thes section
        f.write('\n')

        #Now we write the file charges
        f.write('#Site workload files\n')
        f.write('runtime.site0.url = file:' + workloadsPath + 'charge_' + str(nexp) + '.swf \n')
        for x in range(1,options.numbermachines+1):
            #We write the assiciated runtime information for each site
            f.write('runtime.site' + str(x) + '.url = file:' + workloadsPath + 'emptyWorkload.swf \n')
        f.write('\n')

        #Now we print the the SLA parameters
        f.write('#SLA Parameters\n')
        f.write('sla.generator.distribution.class = mx.cicese.mcc.teikoku.scheduler.SLA.generator.distributions.MultipleConstantDistribution\n')
        f.write('sla.generator.distribution.parameters =' + options.slastring + '\n')
        if (options.testmode):
            f.write('sla.generator.distribution.writer.mayOverwrite = false \n')
        else:
            f.write('sla.generator.distribution.writer.mayOverwrite = true \n')
        f.write('\n')

        #Now we print the grid strategy
        f.write('#Grid configuration strategies\n')
        f.write(gridConf)
        f.write('\n\n')

        f.write('#Sites Configuration sections\n')
        f.write(site0confsection)
        f.write('\n')

        psiteS = 'sites.site'
        for x in range(1,options.numbermachines+1):
            f.write('\n#Site ' + str(x) + ' : configuration section\n')
            f.write(psiteS + str(x) + '.numberOfProvidedResources = 1\n')
            f.write(psiteS + str(x) + '.informationBroker.class = mx.cicese.dcc.teikoku.information.broker.SiteInformationBrokerImpl \n')
            f.write(psiteS + str(x) + '.informationBroker.refreshRate = -1 \n')
            f.write(psiteS + str(x) + '.activitybroker.class = de.irf.it.rmg.core.teikoku.grid.activity.StandardActivityBroker \n')
            f.write(psiteS + str(x) + '.activitybroker.distributionpolicy.class = de.irf.it.rmg.core.teikoku.grid.distribution.StandardDistributionPolicy \n')
            f.write(psiteS + str(x) + '.activitybroker.transferpolicy.class = de.irf.it.rmg.core.teikoku.grid.transfer.FittingTransferPolicy \n')
            f.write(psiteS + str(x) + '.activitybroker.acceptancepolicy.class = de.irf.it.rmg.core.teikoku.grid.acceptance.StandardAcceptancePolicy \n')
            f.write(psiteS + str(x) + '.activitybroker.locationpolicy.class = de.irf.it.rmg.core.teikoku.grid.location.MetricLocationPolicy \n')
            f.write(psiteS + str(x) + '.activitybroker.locationpolicy.metric = awrtdel \n')
            f.write(psiteS + str(x) + '.activitybroker.public = size \n')
            f.write(psiteS + str(x) + '.scheduler.class = de.irf.it.rmg.core.teikoku.scheduler.ParallelMachineScheduler \n')
            f.write(psiteS + str(x) + '.scheduler.localstrategy.class = mx.cicese.mcc.teikoku.scheduler.SLA.machine.strategy.EDFStrategy \n')
            f.write(psiteS + str(x) + '.scheduler.localslaaccepter.class = mx.cicese.mcc.teikoku.scheduler.SLA.acceptance.EDFAccepter \n')
            f.write(psiteS + str(x) + '.scheduler.localqueuecomparator.class = de.irf.it.rmg.core.teikoku.scheduler.queue.EDFComparator \n')
            f.write(psiteS + str(x) + '.scheduler.localqueuecomparator.ordering = ascending \n')
            f.write('\n')
        
        f.write(metricString_1)
        f.write("metrics.parallel_site.writer.outputfile = "+ str(nexp) +" \n")
        f.write(metricString_2)

if __name__ == "__main__":
    main()