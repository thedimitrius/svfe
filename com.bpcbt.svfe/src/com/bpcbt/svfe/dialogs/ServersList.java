package com.bpcbt.svfe.dialogs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.wb.swt.ResourceManager;

import com.bpcbt.svfe.system.Configuration;
import com.bpcbt.svfe.system.Project;
import com.bpcbt.svfe.system.ProjectsList;

public class ServersList extends Dialog {

	protected 	Object 		result;
	protected 	Shell 		shell;
	
	private		Button 		btnConnect;
	private		Button		btnCancel;
	
	private		Tree		tree;
	private 	TreeColumn 	trclmnConfigurationName;
	private 	TreeColumn 	trclmnServerName;	

	public ServersList(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	public Object open(ProjectsList projects) {
		createContents();
		fillConfList(projects);
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shell = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shell.setSize(342, 363);
		shell.setText("Select remote server configuration");
		shell.setLayout(new GridLayout(2, false));
		
		tree = new Tree(shell, SWT.BORDER);
		tree.setLinesVisible(true);
		tree.setHeaderVisible(true);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		trclmnConfigurationName = new TreeColumn(tree, SWT.NONE);
		trclmnConfigurationName.setWidth(137);
		trclmnConfigurationName.setText("Configuration name");
		
		trclmnServerName = new TreeColumn(tree, SWT.NONE);
		trclmnServerName.setWidth(100);
		trclmnServerName.setText("Server name");
		
		btnConnect = new Button(shell, SWT.NONE);
		btnConnect.setText("Connect");
		btnConnect.setEnabled(false);
		btnConnect.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				shell.dispose();
			}
		});
		
		btnCancel = new Button(shell, SWT.NONE);
		btnCancel.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
		btnCancel.setText("Cancel");		
		btnCancel.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				result = null;
				shell.dispose();
			}
		});
	}
	
	private void fillConfList(ProjectsList projects){
		
		for (Project currentProject : projects.projects.values())
		{
			TreeItem treeItem = new TreeItem (tree, SWT.NONE);
			treeItem.setText(currentProject.projectName);
			for (Configuration currentConfig : currentProject.configurations.values())
			{
				TreeItem configItem = new TreeItem (treeItem, SWT.NONE);
				configItem.setText(new String[] {currentConfig.getConfigurationName(), currentConfig.getServerName()});
				configItem.setImage(ResourceManager.getPluginImage("org.eclipse.team.cvs.ui", "/icons/full/eview16/console_view.gif"));
				configItem.setData(currentConfig);				
			}
			
			treeItem.setExpanded(false);
			treeItem.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/obj16/prj_obj.gif"));			
		}
		
		tree.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				TreeItem treeItem;
				
				treeItem = (TreeItem) event.item;
				if (treeItem.getParentItem() ==  null)
				{
					btnConnect.setEnabled(false);
					result = null;
				}
				else
				{
					btnConnect.setEnabled(true);
					result = treeItem.getData();
				}
			}
		});		
	}
}
