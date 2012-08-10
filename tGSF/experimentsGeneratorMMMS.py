import os
#We will create the properties files for the different experiments
#For multiple machines and multiple SLA this can be easily seen because we use 
#the concString this function generates the sla string as a concatenation of
#the previos sla strings used.

def concString(InputStr,i):
    if i == 0:
        return InputStr
    else:
        i = i-1 
        return concString(SLAStrings[i]+InputStr,i)
     

SLAStrings = ['(1;0.0022207)','(2;0.0016806)','(3;0.0014175)','(4;0.0012004)','(5;0.0010436)','(6;0.0009325)','(7;0.0008457)','(8;0.0007771)','(9;0.0007132)',\
'(10;0.0006633)','(11;0.0006224)','(12;0.0005845)','(13;0.0005529)','(14;0.0005249)','(15;0.0004979)','(16;0.0004738)','(17;0.0004539)','(18;0.0004367)',\
'(19;0.0004198)','(20;0.0004056)']
SLALabels = ['1_1','1_2','1_3','1_4','1_5','1_6','1_7','1_8','1_9','1_10','1_11','1_12','1_13','1_14','1_15','1_16','1_17','1_18','1_19','1_20']
#NumberMachines = [2,4,8,16,32,64]
NumberMachines = [1,2,4,8,16,32,64]
#os.system("python properiesGenerator.py -f C:\Users\sergio\workspace\Experiments\Multiple_Machines_Week\ -n 30 -c teikoku_1_ -s {(1;7)} -m 2")

for j in range(len(NumberMachines)):
    for i in range(len(SLAStrings)):
        exectString = 'python properiesGenerator.py -f ' + 'C:/users/sergio/workspace/Experiments/Multiple_Machines_Week\\' + 'Machines_' + str(NumberMachines[j]) +'\\'+ 'SLA_' + SLALabels[i] +'\\ -n 30 -c teikoku_1_ -s {' + concString(SLAStrings[i],i) + '} -m ' + str(NumberMachines[j])
        os.system(exectString)

    
