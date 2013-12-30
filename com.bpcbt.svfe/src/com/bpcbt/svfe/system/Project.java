package com.bpcbt.svfe.system;

import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.bpcbt.svfe.SVFEActivator;
import com.bpcbt.svfe.eclipse.SVFENature;

public class Project {
	public static final String nodeIdentifier = SVFEActivator.PLUGIN_ID;	
	
	private IScopeContext projectScope;
	private Preferences prefs;
	
	public  String projectName;
	
	public HashMap<String, Target> targets = new HashMap<String, Target> ();
	public HashMap<String, Configuration> configurations = new HashMap<String, Configuration> ();
	
	private String 	defaultConfigurationName;
	private	int		numConfigurations;
	private	int		numTargets;
	
	
	private boolean cleanLibs;
	private boolean cleanModule;
	private boolean restart;
	private boolean gzip;
	
	Project( )	{	}	
		
	public boolean initFromPrefs(IProject currentProject)
	{
		/* Per-configuration preferences */
		String currentConfigurationName;
		String currentConfigurationNodeName;
		Preferences currentConfigurationNode;
		Configuration currentConfiguration;
		
		/* Per-target preferences */
		String currentTargetNodeName;
		Preferences currentTargetNode;
		Target currentTarget;
		
		/* Getting per-project preferences root */		
		projectScope = new ProjectScope(currentProject);
		prefs = projectScope.getNode(nodeIdentifier);
		
		if (prefs == null)
		{
			/* Project does not seem to be SVFE */
			return false;
		}
				
		/********************************************************************************************/
		/* Configure project and init project preferences */
		
		numConfigurations = prefs.getInt("numConfigs", 0);
		numTargets = prefs.getInt("numTargets", 0);
		defaultConfigurationName = prefs.get("defaultConfigurationName", "default");
		
		cleanLibs = prefs.getBoolean("cleanLibs", false);
		cleanModule = prefs.getBoolean("cleanModule", false);
		restart = prefs.getBoolean("restart", false);
		gzip = prefs.getBoolean("gzip", false);
		
		projectName = currentProject.getName();
				
		/* First initialize configurations */
		for (int i=0; i<numConfigurations;i++)
		{
			currentConfigurationNodeName = new String("Configuration");
			currentConfigurationNodeName+=String.valueOf(i);
			try {
				if (prefs.nodeExists(currentConfigurationNodeName))
				{
					currentConfigurationNode = prefs.node(currentConfigurationNodeName);
					currentConfigurationName = currentConfigurationNode.get("configurationName", "none");
					currentConfiguration = new Configuration ();
					if (currentConfiguration.initFromPrefs(currentConfigurationNode))
					{
						configurations.put(currentConfigurationName, currentConfiguration);
					}
				}
			} catch (BackingStoreException e) {	e.printStackTrace();}
		} /* end of configurations initialization */
		
		/* Now initialize targets */
		for (int i=0; i<numTargets;i++)
		{
			currentTargetNodeName = new String("Target");
			currentTargetNodeName+=String.valueOf(i);
			try
			{
				if (prefs.nodeExists(currentTargetNodeName))
				{
					currentTargetNode = prefs.node(currentTargetNodeName);
					currentTarget = new Target ();
					if (currentTarget.initFromPrefs(currentTargetNode))
					{
						targets.put(currentTarget.getTargetID(), currentTarget);
					}		
				}
			} catch (BackingStoreException e) { e.printStackTrace();}
		} /* End of targets initialization */
		
		return true;
	} /* end of initFromPrefs */
	
	
	public void initialize(IProject project)
	{
		projectName = project.getName();
		IScopeContext projectScope = new ProjectScope(project);
		prefs = projectScope.getNode(nodeIdentifier);
		
		/* First, add nature to the project */
		try {
			IProjectDescription description = project.getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 0, natures.length);
			newNatures[natures.length] = SVFENature.NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, null);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}			
		
		numConfigurations = 1;
		numTargets = 73;
		defaultConfigurationName = new String("default");
		cleanLibs=false;
		cleanModule = false;
		restart = false;
		gzip = false;
		
		
		/* Second - setup default configuration */
		Preferences configPrefs = prefs.node("Configuration0");
		Configuration currentConfiguration = new Configuration();
		currentConfiguration.initialize(configPrefs, "default", "localhost", "smartfe", "smartfe1", "c:\\id_dsa", "/home/smartfe/src", "C:\\", false );
		configurations.put("default", currentConfiguration);
				
		/* Now set up default targets */
		Preferences targetPrefs;
		Target currentTarget;
		
        targetPrefs= prefs.node("Target0");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"authorization/acq_fraudmon","af"           ,"", false, "", "0");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target1");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"authorization/host_notif"  ,"host_notif"   ,"", false, "", "1");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target2");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"authorization/hstint"      ,"h h1"         ,"", false, "", "2");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target3");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"authorization/stdauth"     ,"stdauth"      ,"", false, "", "3");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target4");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"comms/crout"               ,"crout"        ,"", false, "", "4");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target5");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"comms/mcp"                 ,"mcp"          ,"", false, "", "5");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target6");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"comms/tcpcomms"            ,"tcpcomms"     ,"", false, "", "6");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target7");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/8583pos"            ,"8583pos"      ,"", false, "", "7");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target8");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/acqhost"            ,"ah"           ,"", false, "", "8");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target9");  currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/acqint"             ,"acqint"       ,"", false, "", "9");  targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target10"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/atmint"             ,"a ad aw"      ,"", false, "", "10"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target11"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/devmon"             ,"d"            ,"", false, "", "11"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target12"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/epayint"            ,"epayint"      ,"", false, "", "12"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target13"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/hypercom"           ,"hypercom"     ,"", false, "", "13"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target14"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/nwint"              ,"n"            ,"", false, "", "14"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target15"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/posint"             ,"posint"       ,"", false, "", "15"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target16"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"device/voice"              ,"voice"        ,"", false, "", "16"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target17"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"kernel/hsm"                ,"w"            ,"", false, "", "17"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target18"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"kernel/timer"              ,"t"            ,"", false, "", "18"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target19"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"transaction/splitint"      ,"split"        ,"", false, "", "19"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target20"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"transaction/txrout"        ,"tx"           ,"", false, "", "20"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target21"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"ui/atndnt/host"            ,""             ,"", false, "", "21"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target22"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"ui/atndnt/atmdir"          ,""             ,"", false, "", "22"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target23"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"ui/atndnt/ntwk"            ,""             ,"", false, "", "23"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target24"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/acc_card_loader",""             ,"", false, "", "24"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target25"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/8583tcp"        ,""             ,"", false, "", "25"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target26"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/acc_card_loader",""             ,"", false, "", "26"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target27"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/acct_load"      ,""             ,"", false, "", "27"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target28"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/acq_loader"     ,""             ,"", false, "", "28"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target29"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/atm_balances"   ,""             ,"", false, "", "29"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target30"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/atm_ctr_offline",""             ,"", false, "", "30"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target31"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/bin_load"       ,""             ,"", false, "", "31"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target32"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/bin_range_load" ,""             ,"", false, "", "32"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target33"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/ch_debug_lvl"   ,""             ,"", false, "", "33"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target34"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/cnvt_rate_loader",""            ,"", false, "", "34"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target35"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/cr_txn"         ,""             ,"", false, "", "35"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target36"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/crcref"         ,""             ,"", false, "", "36"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target37"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/crypto_password",""             ,"", false, "", "37"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target38"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/crypto_tools"   ,""             ,"", false, "", "38"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target39"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/cutoff_utl"     ,""             ,"", false, "", "39"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target40"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/cutover_tools"  ,""             ,"", false, "", "40"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target41"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/emv_script_gen" ,""             ,"", false, "", "41"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target42"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/encrypt_tools"  ,""             ,"", false, "", "42"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target43"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/extract_utrnno" ,""             ,"", false, "", "43"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target44"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/fee_loader"     ,""             ,"", false, "", "44"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target45"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/fraud_monitoring",""            ,"", false, "", "45"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target46"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/helper_scripts" ,""             ,"", false, "", "46"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target47"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/include"        ,""             ,"", false, "", "47"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target48"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/install_checksum",""            ,"", false, "", "48"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target49"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/lib"            ,""             ,"", false, "", "49"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target50"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/mcc_loader"     ,""             ,"", false, "", "50"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target51"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/miscellaneous"  ,""             ,"", false, "", "51"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target52"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/offln_stmt_loader",""           ,"", false, "", "52"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target53"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/perllib"        ,""             ,"", false, "", "53"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target54"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/pid_notifier"   ,""             ,"", false, "", "54"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target55"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/post_pos_batch" ,""             ,"", false, "", "55"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target56"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/proc_inst_acct" ,""             ,"", false, "", "56"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target57"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/proc_trn_file"  ,""             ,"", false, "", "57"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target58"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/q"              ,""             ,"", false, "", "58"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target59"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/rsa"            ,""             ,"", false, "", "59"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target60"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/sp"             ,""             ,"", false, "", "60"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target61"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/startstop"      ,""             ,"", false, "", "61"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target62"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/stat_watcher"   ,""             ,"", false, "", "62"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target63"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/storforw"       ,""             ,"", false, "", "63"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target64"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/svfe_log_monitor",""            ,"", false, "", "64"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target65"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/svfemon"        ,""             ,"", false, "", "65"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target66"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/sys_watcher"    ,""             ,"", false, "", "66"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target67"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/tar_gen"        ,""             ,"", false, "", "67"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target68"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/tcp_loader"     ,""             ,"", false, "", "68"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target69"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/trl_upload"     ,""             ,"", false, "", "69"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target70"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/utrnno_cutter"  ,""             ,"", false, "", "70"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target71"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/vouch_loader"   ,""             ,"", false, "", "71"); targets.put(currentTarget.getTargetID(), currentTarget); 
		targetPrefs= prefs.node("Target72"); currentTarget = new Target(); currentTarget.initialize(targetPrefs,"prod_tools/x25_utils"      ,""             ,"", false, "", "72"); targets.put(currentTarget.getTargetID(), currentTarget); 
        
		flushPrefs();
		
		UpdateUIEvent event = new UpdateUIEvent();
		event.eventType = UpdateUIEvent.ProjectOpened;
		event.projectName = projectName;
		event.object = this;
		SVFEActivator.getDefault().getProjectsList().updateUI(event);
	} /* end of initialize */
	
	public void targetChecked(String targetID, boolean isChecked)
	{
		targets.get(targetID).setChecked(isChecked);		
	}	
	
	
	public boolean isCleanLibs() {
		return cleanLibs;
	}

	public void setCleanLibs(boolean cleanLibs) {
		this.cleanLibs = cleanLibs;
		flushPrefs();
	}

	public boolean isCleanModule() {
		return cleanModule;
	}

	public void setCleanModule(boolean cleanModule) {
		this.cleanModule = cleanModule;
		flushPrefs();
	}

	public boolean isRestart() {
		return restart;
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
		flushPrefs();
	}

	public boolean isGzip() {
		return gzip;
	}

	public void setGzip(boolean gzip) {
		this.gzip = gzip;
		flushPrefs();
	}
	
	public String getDefaultConfigurationName() {
		return defaultConfigurationName;
	}
	
	public void setDefaultConfigurationName(String defaultConfigurationName){
		this.defaultConfigurationName = defaultConfigurationName;
		flushPrefs();
		UpdateUIEvent event = new UpdateUIEvent();
		event.eventType = UpdateUIEvent.ConfigurationsModified;
		event.projectName = projectName;
		SVFEActivator.getDefault().getProjectsList().updateUI(event);
	}
	

	private void flushPrefs() 
	{
		prefs.put("defaultConfigurationName", defaultConfigurationName);
		prefs.putInt("numConfigs", numConfigurations);
		prefs.putInt("numTargets", numTargets);
		prefs.putBoolean("cleanLibs", cleanLibs);
		prefs.putBoolean("cleanModule", cleanModule);
		prefs.putBoolean("restart", restart);
		prefs.putBoolean("gzip", gzip);
		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}		
	}
	
	public boolean addConfiguration(String confName, String serverName, String serverUsername, String serverPassword, String keyLocation, String serverSrcLocation, String gzipFilesLocation, boolean authByKey)
	{
		String currentConfigurationNodeName;
		String currentConfigurationName;
		Preferences currentConfigurationNode;
		Configuration currentConfiguration;
		
		if (configurations.get(confName)!= null)
		{
			MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Error");
			dialog.setMessage("Configuration with this name already exists");
			dialog.open();
			return false;
		}
		
		numConfigurations++;
		currentConfigurationNodeName = new String("Configuration");
		currentConfigurationNodeName+=String.valueOf(numConfigurations-1);
		currentConfigurationNode = prefs.node(currentConfigurationNodeName);
		currentConfigurationName = confName;
		currentConfiguration = new Configuration ();
		currentConfiguration.initialize(currentConfigurationNode, confName, serverName, serverUsername, serverPassword, keyLocation, serverSrcLocation, gzipFilesLocation, authByKey);
		configurations.put(currentConfigurationName, currentConfiguration);
		flushPrefs();
		
		UpdateUIEvent event = new UpdateUIEvent();
		event.eventType = UpdateUIEvent.ConfigurationsModified;
		event.projectName = projectName;
		SVFEActivator.getDefault().getProjectsList().updateUI(event);		
		return true;
	}
	
	public boolean removeConfiguration(String confName)
	{
		if (confName.equals(defaultConfigurationName))
		{
			MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Error");
			dialog.setMessage("Cannot remove default configuration");
			dialog.open();
			return false;
		}
		configurations.get(confName).removePrefs();
		configurations.remove(confName);
		
		UpdateUIEvent event = new UpdateUIEvent();
		event.eventType = UpdateUIEvent.ConfigurationsModified;
		event.projectName = projectName;
		SVFEActivator.getDefault().getProjectsList().updateUI(event);
		return true;		
	}
	
	public boolean editConfiguration(String oldConfName, String confName, String serverName, String serverUsername, String serverPassword, String keyLocation, String serverSrcLocation, String gzipFilesLocation, boolean authByKey)
	{
		Configuration currentConfiguration;
				
		if (!oldConfName.equals(confName) && configurations.get(confName)!=null)
		{
			MessageBox dialog = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Error");
			dialog.setMessage("Configuration with this name already exists");
			dialog.open();
			return false;
		}
		
		if (oldConfName.compareTo(defaultConfigurationName) == 0)
		{
			defaultConfigurationName = new String(confName);
		}	
		
		currentConfiguration = configurations.get(oldConfName);
		
		currentConfiguration.setConfigurationName(confName);
		currentConfiguration.setServerName(serverName);
		currentConfiguration.setServerUsername(serverUsername);
		currentConfiguration.setServerPassword(serverPassword);
		currentConfiguration.setKeyLocation(keyLocation);
		currentConfiguration.setServerSrcLocation(serverSrcLocation);
		currentConfiguration.setGzipFilesLocation(gzipFilesLocation);
		currentConfiguration.setAuthByKey(authByKey);
		
		if (oldConfName.compareTo(confName)!=0)
		{
			configurations.put(confName, currentConfiguration);
			configurations.remove(oldConfName);
		}
		flushPrefs();
		
		UpdateUIEvent event = new UpdateUIEvent();
		event.eventType = UpdateUIEvent.ConfigurationsModified;
		event.projectName = projectName;
		SVFEActivator.getDefault().getProjectsList().updateUI(event);
		
		return true;
	}
	
	
	
	
	public boolean addTarget(String targetLocation, String targetRestartAliases, String targetGzipFiles, boolean isChecked, String targetName)
	{
		String currentTargetNodeName;
		Preferences currentTargetNode;
		Target currentTarget;
		
		numTargets++;
		currentTargetNodeName = new String("Target");
		currentTargetNodeName+=String.valueOf(numTargets-1);
		currentTargetNode = prefs.node(currentTargetNodeName);
		currentTarget = new Target();
		currentTarget.initialize(currentTargetNode, targetLocation, targetRestartAliases, targetGzipFiles, isChecked, targetName, String.valueOf(numTargets-1));
		targets.put(currentTarget.getTargetID(), currentTarget);
		flushPrefs();
		
		UpdateUIEvent event = new UpdateUIEvent();
		event.eventType = UpdateUIEvent.TargetAdded;
		event.projectName = projectName;
		event.object = currentTarget;
		SVFEActivator.getDefault().getProjectsList().updateUI(event);
				
		return true;
	}	
	
	public boolean editTarget(String targetID, String targetLocation, String targetRestartAliases, String targetGzipFiles, String targetName) 
	{
		Target currentTarget;
		
		currentTarget = targets.get(targetID);
		
		currentTarget.setTargetLocation(targetLocation);
		currentTarget.setTargetRestartAliases(targetRestartAliases);
		currentTarget.setTargetGzipFiles(targetGzipFiles);
		currentTarget.setTargetName(targetName);
		
		flushPrefs();
		
		UpdateUIEvent event = new UpdateUIEvent();
		event.eventType = UpdateUIEvent.TargetModified;
		event.projectName = projectName;
		event.object = currentTarget;
		SVFEActivator.getDefault().getProjectsList().updateUI(event);
		return true;		
	}
	
	public void removeTarget(String targetID){
		Target currentTarget = targets.get(targetID);
		
		UpdateUIEvent event = new UpdateUIEvent();
		event.eventType = UpdateUIEvent.TargetRemoved;
		event.projectName = projectName;
		event.object = currentTarget;
		SVFEActivator.getDefault().getProjectsList().updateUI(event);
		

		currentTarget.removePrefs();
		targets.remove(targetID);		
		flushPrefs();
	}

	
} /* end of class Project */
