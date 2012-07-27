package mx.uabc.lcc.teikoku.error;
/**
 * @author Aritz Barrondo
 */
public enum EventReason
{
    NULL,NOT_REPORTED, UNDETERMINES,   
    
    // INFRASTRUCTURE ERRORS
    INFRASTRUCTURE, INFRASTRUCTURE_POWER_OUT,
    INFRASTRUCTURE_POWER_SPIKE, INFRASTRUCTURE_CHILLERS,
    INFRASTRUCTURE_ENVIRONMENT, INFRASTRUCTURE_UPS,
    
    // HARDWARE ERRORS
    HARDWARE, 
    HARDWARE_COOLING_FAN,
    HARDWARE_BATCH_ASSEMBLY,
    HARDWARE_CD_ROM,
    HARDWARE_CONSOLE_INTERFACE, 
    HARDWARE_CPU,
    HARDWARE_DG_BOARD, HARDWARE_FAN,
    HARDWARE_FAN_ASSEMBLY,
    HARDWARE_GFX_POWER_SUPPLY,
    HARDWARE_GRAPHICS_ACCEL,
    HARDWARE_GRAPHICS_VIDEO,
    HARDWARE_HEATSINK,
    HARDWARE_IO6,
    HARDWARE_IOS_BUFFER_MEM,
    HARDWARE_IOS_CPU,
    HARDWARE_KGPSA,
    HARDWARE_KTOWN_BOARD,
    HARDWARE_MAINTENANCE,
    HARDWARE_MEM_DIM,
    HARDWARE_MEM_DIM_CPU,
    HARDWARE_MEM_MODULE,
    HARDWARE_MIA,
    HARDWARE_MID_PLANE,
    HARDWARE_MMB,
    HARDWARE_MODULE_ASSEMBLY,
    HARDWARE_MSC_BOARD,
    HARDWARE_OCP,
    HARDWARE_PCI_BACK_PLANE,
    HARDWARE_POWER_CORD,
    HARDWARE_POWER_SUPPLY,
    HARDWARE_RACK_POWER_UNIT,
    HARDWARE_RISER_CARD,
    HARDWARE_RM_BOARD,
    HARDWARE_SSD_LOGIC,
    HARDWARE_SSD_MEM,
    HARDWARE_SYSTEM_BOARD,
    HARDWARE_SYSTEM_CONTROLLER,
    HARDWARE_TEMP_PROBE,
    HARDWARE_TEMP_SERVER,
    HARDWARE_VHISP,
    HARDWARE_WACS_LOGIC,
    HARDWARE_WIRE_HARNESS,
    HARDWARE_XTOWN_BOARD,
    HARDWARE_NODE_BOARD,
    
    // IO ERRORS
    IO,
    IO_DISK_CABINET,
    IO_DISK_DRIVE,
    IO_DRIVE_CAGE,
    IO_FDDI,
    IO_FIBRE_CABLE,
    IO_FIBRE_CHANNEL,
    IO_FIBRE_DRIVE,
    IO_FIBRE_HBA,
    IO_FIBRE_RAID_CONTROLLER,
    IO_FIBRE_RAID_LCC,
    IO_FIBRE_RAID_MIDPLANE,
    IO_FIBRE_RAID_POWER_SUPPLY,
    IO_IDE_CABLE,
    IO_IOS_DISK_LOGIC,
    IO_PCI_FIBRE_CHANEL,
    IO_PCI_IO_MODULE,
    IO_PCI_SCSI_CONTROLLER,
    IO_PCI_SHOEBOX,
    IO_SAN_APPLIANCO,
    IO_SAN_CONTROLLER,
    IO_SAN_DISK_DRIVE,
    IO_SAN_FIBER_CABLE,
    IO_SAN_GBIC,
    IO_SAN_SHELF,
    IO_SAN_SWITCH,
    IO_SCSI_ADAPTER,
    IO_SCSI_CONTROLLER,
    IO_SCSI_DRIVE,
    IO_SCSI_BACK_PLANE,
    IO_HSV,
    IO_CONTROLLER,
    
    // NETWORK ERRORS
    NETWORK,
    NETWORK_1GB_ETHERNET_CARD,
    NETWORK_100MN_ETHERNET_CARD,
    NETWORK_ETHERNET_CABLE,
    NETWORK_ETHERNET_COPPER_CABLE,
    NETWORK_ETHERNET_FIBER_CABLE,
    NETWORK_ETHERNET_SWITCH,
    NETWORK_GE_BOARD,
    NETWORK_GE_CONNECTION,
    NETWORK_GE_SWITCH,
    NETWORK_INTERCONNECT,
    NETWORK_INTERCONNECT_CABLE,
    NETWORK_INTERCONNECT_INTERFACE,
    NETWORK_INTERCONNECT_MISC,
    NETWORK_INTERCONNECT_SOFT_ERROR,
    NETWORK_INTERCONNECT_SWITCH,
    NETWORK_PCI_ETHRTNET_BOARD,
    NETWORK_PCI_GBIT_ETHERNET_BOARD,
    NETWORK_ROUTER_BOARD,
    NETWORK_SITE_ETHERNET_SWITCH,
    NETWORK_SITE_NETWORK_INTERFACE,
    NETWORK_CONSOLE_NETWORK_DEVICE,
    NETWORK_ELAN,
    
    // SOFTWARE ERRORS
    SOFTWARE,
    SOFTWARE_DISK_IO_FIRMWARE_STORAGE,
    SOFTWARE_MPI_PVM_ARRAY_SERVICE,
    SOFTWARE_CLUSTER_FILE_SYSTEM,
    SOFTWARE_CLUSTER_SOFTWARE,
    SOFTWARE_COMPLIERS_AND_LIBRARIES,
    SOFTWARE_DST,
    SOFTWARE_DST_SCAN_ERRORS_SCRATCH,
    SOFTWARE_DST_UPGRD_INSTALL_OS,
    SOFTWARE_DST_UPGRD_INSTALL_3RD_PARTY,
    SOFTWARE_INTERCONNECT,
    SOFTWARE_IOS_SFTW,
    SOFTWARE_KERNEL_SFTW,
    SOFTWARE_MODIFY_KERNEL_PARAMETERS,
    SOFTWARE_MODIFY_SYSTEM_CONFIG,
    SOFTWARE_NETWORK,
    SOFTWARE_NFS,
    SOFTWARE_OS,
    SOFTWARE_PARALLEL_FILE_SYSTEM,
    SOFTWARE_PATCH_INSTALL,
    SOFTWARE_RESOURCE_MGMT_SYSTEM,
    SOFTWARE_SCHEDULER_SOFTWARE,
    SOFTWARE_SCRATCH_DRIVE,
    SOFTWARE_SCRATCH_FS,
    SOFTWARE_SECURITY_SOFTWARE,
    SOFTWARE_UPGRD_INSTALL_3RD_PARTY,
    SOFTWARE_UPGRD_INSTALL_OS,
    SOFTWARE_USER_CODE,
    SOFTWARE_VIZSCRATCH_FS,
    SOFTWARE_APPLICATIONS,
        
        // HUMAN ERRORS
    HUMAN_ERROR,
    USER,
    USER_MOUSE_ACTIVITY,
    USER_KEYBOARD_ACTIVITY,
    USER_SHUTDOWN;
    
    public int getCode()
    {
        switch (this)
        {
            case NULL: reasonCode=0; break;
            case NOT_REPORTED: reasonCode=0; break;
            case UNDETERMINES: reasonCode=0; break;
   
            // INFRASTRUCTURE ERRORS
            case INFRASTRUCTURE: reasonCode=1; break;
            case INFRASTRUCTURE_POWER_OUT: reasonCode=2; break;
            case INFRASTRUCTURE_POWER_SPIKE: reasonCode=3; break;
            case INFRASTRUCTURE_CHILLERS: reasonCode=4; break;
            case INFRASTRUCTURE_ENVIRONMENT: reasonCode=5; break;
            case INFRASTRUCTURE_UPS: reasonCode=6; break;
        
            // HARDWARE ERRORS
            case HARDWARE: reasonCode=1000; break;
            case HARDWARE_COOLING_FAN: reasonCode=1001; break;
            case HARDWARE_BATCH_ASSEMBLY: reasonCode=1002; break;
            case HARDWARE_CD_ROM: reasonCode=1003; break;
            case HARDWARE_CONSOLE_INTERFACE: reasonCode=1004; break;
            case HARDWARE_CPU: reasonCode=1005; break;
            case HARDWARE_DG_BOARD: reasonCode=1006; break;
            case HARDWARE_FAN: reasonCode=1007; break;
            case HARDWARE_FAN_ASSEMBLY: reasonCode=1008; break;
            case HARDWARE_GFX_POWER_SUPPLY: reasonCode=1009; break;
            case HARDWARE_GRAPHICS_ACCEL: reasonCode=1010; break;
            case HARDWARE_GRAPHICS_VIDEO: reasonCode=1011; break;
            case HARDWARE_HEATSINK: reasonCode=1012; break;
            case HARDWARE_IO6: reasonCode=1013; break;
            case HARDWARE_IOS_BUFFER_MEM: reasonCode=1014; break;
            case HARDWARE_IOS_CPU: reasonCode=1015; break;
            case HARDWARE_KGPSA: reasonCode=1016; break;
            case HARDWARE_KTOWN_BOARD: reasonCode=1017; break;
            case HARDWARE_MAINTENANCE: reasonCode=1018; break;
            case HARDWARE_MEM_DIM: reasonCode=1019; break;
            case HARDWARE_MEM_DIM_CPU: reasonCode=1020; break;
            case HARDWARE_MEM_MODULE: reasonCode=1021; break;
            case HARDWARE_MIA: reasonCode=1022; break;
            case HARDWARE_MID_PLANE: reasonCode=1023; break;
            case HARDWARE_MMB: reasonCode=1024; break;
            case HARDWARE_MODULE_ASSEMBLY: reasonCode=1025; break;
            case HARDWARE_MSC_BOARD: reasonCode=1026; break;
            case HARDWARE_OCP: reasonCode=1027; break;
            case HARDWARE_PCI_BACK_PLANE: reasonCode=1028; break;
            case HARDWARE_POWER_CORD: reasonCode=1029; break;
            case HARDWARE_POWER_SUPPLY: reasonCode=1030; break;
            case HARDWARE_RACK_POWER_UNIT: reasonCode=1031; break;
            case HARDWARE_RISER_CARD: reasonCode=1032; break;
            case HARDWARE_RM_BOARD: reasonCode=1033; break;
            case HARDWARE_SSD_LOGIC: reasonCode=1034; break;
            case HARDWARE_SSD_MEM: reasonCode=1035; break;
            case HARDWARE_SYSTEM_BOARD: reasonCode=1036; break;
            case HARDWARE_SYSTEM_CONTROLLER: reasonCode=1037; break;
            case HARDWARE_TEMP_PROBE: reasonCode=1038; break;
            case HARDWARE_TEMP_SERVER: reasonCode=1039; break;
            case HARDWARE_VHISP: reasonCode=1040; break;
            case HARDWARE_WACS_LOGIC: reasonCode=1041; break;
            case HARDWARE_WIRE_HARNESS: reasonCode=1042; break;
            case HARDWARE_XTOWN_BOARD: reasonCode=1043; break;
            case HARDWARE_NODE_BOARD: reasonCode=1044; break;
            
            // IO ERRORS
            case IO: reasonCode=2000; break;
            case IO_DISK_CABINET: reasonCode=2001; break;
            case IO_DISK_DRIVE: reasonCode=2002; break;
            case IO_DRIVE_CAGE: reasonCode=2003; break;
            case IO_FDDI: reasonCode=2004; break;
            case IO_FIBRE_CABLE: reasonCode=2005; break;
            case IO_FIBRE_CHANNEL: reasonCode=2006; break;
            case IO_FIBRE_DRIVE: reasonCode=2007; break;
            case IO_FIBRE_HBA: reasonCode=2008; break;
            case IO_FIBRE_RAID_CONTROLLER: reasonCode=2009; break;
            case IO_FIBRE_RAID_LCC: reasonCode=2010; break;
            case IO_FIBRE_RAID_MIDPLANE: reasonCode=2011; break;
            case IO_FIBRE_RAID_POWER_SUPPLY: reasonCode=2012; break;
            case IO_IDE_CABLE: reasonCode=2013; break;
            case IO_IOS_DISK_LOGIC: reasonCode=2014; break;
            case IO_PCI_FIBRE_CHANEL: reasonCode=2015; break;
            case IO_PCI_IO_MODULE: reasonCode=2016; break;
            case IO_PCI_SCSI_CONTROLLER: reasonCode=2017; break;
            case IO_PCI_SHOEBOX: reasonCode=2018; break;
            case IO_SAN_APPLIANCO: reasonCode=2019; break;
            case IO_SAN_CONTROLLER: reasonCode=2020; break;
            case IO_SAN_DISK_DRIVE: reasonCode=2021; break;
            case IO_SAN_FIBER_CABLE: reasonCode=2022; break;
            case IO_SAN_GBIC: reasonCode=2023; break;
            case IO_SAN_SHELF: reasonCode=2024; break;
            case IO_SAN_SWITCH: reasonCode=2025; break;
            case IO_SCSI_ADAPTER: reasonCode=2026; break;
            case IO_SCSI_CONTROLLER: reasonCode=2027; break;
            case IO_SCSI_DRIVE: reasonCode=2028; break;
            case IO_SCSI_BACK_PLANE: reasonCode=2029; break;
            case IO_HSV: reasonCode=2030; break;
            case IO_CONTROLLER: reasonCode=2031; break;
            
            // NETWORK ERRORS
            case NETWORK: reasonCode=3000; break;
            case NETWORK_1GB_ETHERNET_CARD: reasonCode=3001; break;
            case NETWORK_100MN_ETHERNET_CARD: reasonCode=3002; break;
            case NETWORK_ETHERNET_CABLE: reasonCode=3003; break;
            case NETWORK_ETHERNET_COPPER_CABLE: reasonCode=3004; break;
            case NETWORK_ETHERNET_FIBER_CABLE: reasonCode=3005; break;
            case NETWORK_ETHERNET_SWITCH: reasonCode=3006; break;
            case NETWORK_GE_BOARD: reasonCode=3007; break;
            case NETWORK_GE_CONNECTION: reasonCode=3008; break;
            case NETWORK_GE_SWITCH: reasonCode=3009; break;
            case NETWORK_INTERCONNECT: reasonCode=3010; break;
            case NETWORK_INTERCONNECT_CABLE: reasonCode=3011; break;
            case NETWORK_INTERCONNECT_INTERFACE: reasonCode=3012; break;
            case NETWORK_INTERCONNECT_MISC: reasonCode=3013; break;
            case NETWORK_INTERCONNECT_SOFT_ERROR: reasonCode=3014; break;
            case NETWORK_INTERCONNECT_SWITCH: reasonCode=3015; break;
            case NETWORK_PCI_ETHRTNET_BOARD: reasonCode=3016; break;
            case NETWORK_PCI_GBIT_ETHERNET_BOARD: reasonCode=3017; break;
            case NETWORK_ROUTER_BOARD: reasonCode=3018; break;
            case NETWORK_SITE_ETHERNET_SWITCH: reasonCode=3019; break;
            case NETWORK_SITE_NETWORK_INTERFACE: reasonCode=3020; break;
            case NETWORK_CONSOLE_NETWORK_DEVICE: reasonCode=3021; break;
            case NETWORK_ELAN: reasonCode=3022; break;
            
            // SOFTWARE ERRORS
            case SOFTWARE: reasonCode=4000; break;
            case SOFTWARE_DISK_IO_FIRMWARE_STORAGE: reasonCode=4001; break;
            case SOFTWARE_MPI_PVM_ARRAY_SERVICE: reasonCode=4002; break;
            case SOFTWARE_CLUSTER_FILE_SYSTEM: reasonCode=4003; break;
            case SOFTWARE_CLUSTER_SOFTWARE: reasonCode=4004; break;
            case SOFTWARE_COMPLIERS_AND_LIBRARIES: reasonCode=4005; break;
            case SOFTWARE_DST: reasonCode=4006; break;
            case SOFTWARE_DST_SCAN_ERRORS_SCRATCH: reasonCode=4007; break;
            case SOFTWARE_DST_UPGRD_INSTALL_OS: reasonCode=4008; break;
            case SOFTWARE_DST_UPGRD_INSTALL_3RD_PARTY: reasonCode=4009; break;
            case SOFTWARE_INTERCONNECT: reasonCode=4010; break;
            case SOFTWARE_IOS_SFTW: reasonCode=4011; break;
            case SOFTWARE_KERNEL_SFTW: reasonCode=4012; break;
            case SOFTWARE_MODIFY_KERNEL_PARAMETERS: reasonCode=4013; break;
            case SOFTWARE_MODIFY_SYSTEM_CONFIG: reasonCode=4014; break;
            case SOFTWARE_NETWORK: reasonCode=4015; break;
            case SOFTWARE_NFS: reasonCode=4016; break;
            case SOFTWARE_OS: reasonCode=4017; break;
            case SOFTWARE_PARALLEL_FILE_SYSTEM: reasonCode=4018; break;
            case SOFTWARE_PATCH_INSTALL: reasonCode=4019; break;
            case SOFTWARE_RESOURCE_MGMT_SYSTEM: reasonCode=4020; break;
            case SOFTWARE_SCHEDULER_SOFTWARE: reasonCode=4021; break;
            case SOFTWARE_SCRATCH_DRIVE: reasonCode=4022; break;
            case SOFTWARE_SCRATCH_FS: reasonCode=4023; break;
            case SOFTWARE_SECURITY_SOFTWARE: reasonCode=4024; break;
            case SOFTWARE_UPGRD_INSTALL_3RD_PARTY: reasonCode=4025; break;
            case SOFTWARE_UPGRD_INSTALL_OS: reasonCode=4026; break;
            case SOFTWARE_USER_CODE: reasonCode=4027; break;
            case SOFTWARE_VIZSCRATCH_FS: reasonCode=4028; break;
            case SOFTWARE_APPLICATIONS: reasonCode=4029; break;
            
            // HUMAN ERRORS
            case HUMAN_ERROR: reasonCode=5000; break;
            case USER: reasonCode=6000; break;
            case USER_MOUSE_ACTIVITY: reasonCode=6001; break;
            case USER_KEYBOARD_ACTIVITY: reasonCode=6002; break;
            case USER_SHUTDOWN: reasonCode=6003; break;
        }
        return reasonCode;
    }    
    private Integer reasonCode;
    
}