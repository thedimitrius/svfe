package com.bpcbt.svfe;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.bpcbt.svfe.dialogs.ConfigurationSettings;
import com.bpcbt.svfe.dialogs.ConfigurationsList;
import com.bpcbt.svfe.dialogs.ServersList;
import com.bpcbt.svfe.dialogs.TargetSettings;
import com.bpcbt.svfe.dialogs.TargetsList;
import com.bpcbt.svfe.system.Configuration;
import com.bpcbt.svfe.system.ProjectsList;

/**
 * The activator class controls the plug-in life cycle
 */
public class SVFEActivator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.bpcbt.svfe"; //$NON-NLS-1$

	// The shared instance
	private static SVFEActivator plugin;
	
	// Fields
	private ProjectsList projects;
	
	private ConfigurationsList confListDlg;
	private TargetsList targetsListDlg;
	
	private ConfigurationSettings confSettingsDlg;
	private TargetSettings targetSettingsDlg;
	
	private ServersList serversListDlg;
	
	private ConsoleView consView;
	
	public void openConfigList(String projectName, Shell shell)
	{
		confListDlg = new ConfigurationsList(shell, SWT.DIALOG_TRIM);
		confListDlg.initialize(projects.projects.get(projectName));
	}
	
	public void editConfig(String projectName, Configuration conf, Shell shell)
	{		
		confSettingsDlg = new ConfigurationSettings(shell, SWT.DIALOG_TRIM);
		confSettingsDlg.editConfig(projects.projects.get(projectName), conf);
	}
		
	public void openTargetsList (String projectName, Shell shell)
	{
		targetsListDlg = new TargetsList(shell, SWT.DIALOG_TRIM);
		targetsListDlg.initialize(projects.projects.get(projectName));
	}
	
	public void createNewTarget(String projectName, Shell shell, String initialPath)
	{
		targetSettingsDlg = new TargetSettings(shell, SWT.DIALOG_TRIM);
		targetSettingsDlg.createNewTarget(projects.projects.get(projectName), initialPath);
	}
	
	public void editTarget(String projectName, Shell shell, String targetID)
	{
		targetSettingsDlg = new TargetSettings (shell, SWT.DIALOG_TRIM);
		targetSettingsDlg.editTarget(projects.projects.get(projectName), projects.projects.get(projectName).targets.get(targetID));
	}
	
	public Configuration openServersList(Shell shell)
	{
		serversListDlg = new ServersList(shell, SWT.DIALOG_TRIM);
		return (Configuration)serversListDlg.open(projects);
	}
	
	public void setActiveConsoleView (ConsoleView view) { consView = view;}
	
	public ConsoleView getActiveConsoleView () { return consView;}
	
	
	
	
	
	
	
	
	
	public SVFEActivator() {}
	
	public ProjectsList getProjectsList() {	return projects;}
		
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		projects = new ProjectsList();
		
	}	
	
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static SVFEActivator getDefault() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
