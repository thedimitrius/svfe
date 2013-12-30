package com.bpcbt.svfe.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.wb.swt.ResourceManager;

import com.bpcbt.svfe.system.Project;
import com.bpcbt.svfe.system.Target;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class TargetsList extends Dialog {

	protected Object result;
	protected Shell shlSvfeBuildTargets;
	
	private Project currentProject;
	private String  newTargetLocation;
	
	private Tree tree;

	public TargetsList(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	public Object open() {
		createContents();
		shlSvfeBuildTargets.open();
		shlSvfeBuildTargets.layout();
		Display display = getParent().getDisplay();
		while (!shlSvfeBuildTargets.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
		return result;
	}

	private void createContents() {
		shlSvfeBuildTargets = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shlSvfeBuildTargets.setSize(412, 455);
		shlSvfeBuildTargets.setText("SVFE Build targets");
		shlSvfeBuildTargets.setLayout(new GridLayout(2, false));
		
		tree = new Tree(shlSvfeBuildTargets, SWT.BORDER);
		GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);
		gd_tree.widthHint = 373;
		tree.setLayoutData(gd_tree);
		
		Button btnAdd = new Button(shlSvfeBuildTargets, SWT.NONE);
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addTarget();
			}
		});
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnAdd.setText("Add...");
		
		Button btnEdit = new Button(shlSvfeBuildTargets, SWT.NONE);
		btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editTarget();
			}
		});
		btnEdit.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		btnEdit.setText("Modify...");

		Button btnDelete = new Button(shlSvfeBuildTargets, SWT.NONE);
		btnDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeTarget();
			}
		});
		btnDelete.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		btnDelete.setText("Delete");
		new Label(shlSvfeBuildTargets, SWT.NONE);
		new Label(shlSvfeBuildTargets, SWT.NONE);

		Button btnOk = new Button(shlSvfeBuildTargets, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shlSvfeBuildTargets.dispose();
			}
		});
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnOk.setText("OK");
		
		newTargetLocation = "";

	}

	public void initialize(Project project)
	{
		currentProject = project;
		createContents();		
		
		shlSvfeBuildTargets.open();
		shlSvfeBuildTargets.layout();
		Display display = getParent().getDisplay();
		
		TreeColumn currentColumn;

		TreeItem treeItem;
		String[] currentTargetPath;
		
		
		currentColumn = new TreeColumn(tree, SWT.NONE);
		currentColumn.setWidth(200);
		currentColumn.setText("Target location");
		
		currentColumn = new TreeColumn(tree, SWT.NONE);
		currentColumn.setWidth(100);
		currentColumn.setText("Target name");
		
		tree.setHeaderVisible(true);
		

		treeItem = new TreeItem (tree, SWT.NONE);
		treeItem.setText(currentProject.projectName);
		for (Target currentTarget : currentProject.targets.values())
		{
			currentTargetPath = currentTarget.getTargetLocation().split("/");
			locateOrCreatePath(treeItem, currentTargetPath, 0, currentTarget.isChecked(), currentTarget.getTargetName(), currentTarget.getTargetID());
		}
		
		treeItem.setExpanded(true);
		treeItem.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/obj16/prj_obj.gif"));
		

		tree.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent event) 
			{
				TreeItem treeItem;
				
				treeItem = (TreeItem) event.item;
				if (treeItem.getParentItem() ==  null)
				{
					newTargetLocation = "";
				}
				else
				{
					newTargetLocation = currentProject.targets.get((String)treeItem.getData()).getTargetLocation();
				}
			}
		});
		while (!shlSvfeBuildTargets.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}
	
	private void locateOrCreatePath(TreeItem item, String[] path, int level, boolean isChecked, String targetName, String targetID)
	{
		int j = 0;
		TreeItem treeItem = null;

			for (TreeItem currentItem : item.getItems())
			{
				if (currentItem.getText().compareTo(path[level])==0 && currentItem.getItemCount()!=0)
				{
					treeItem = currentItem;
					break;
				}
				else if (currentItem.getText().compareTo(path[level])>0)
				{
					break;
				}
				else
				{
					j++;
				}
			}

		if (treeItem == null || level == path.length-1)
		{	/* Not located element */
			treeItem = new TreeItem(item, SWT.NONE, j);
			treeItem.setText(path[level]);
			treeItem.setImage(ResourceManager.getPluginImage("org.eclipse.ui", "/icons/full/obj16/fldr_obj.gif"));
			treeItem.setExpanded(true);
		}

		if (level == path.length-1)
		{
			treeItem.setChecked(isChecked);
			treeItem.setImage(ResourceManager.getPluginImage("org.eclipse.ant.ui", "/icons/full/obj16/targetinternal_obj.gif"));
			treeItem.setText(new String[] {path[level], targetName});
			treeItem.setData(targetID);
		}
		else
		{			
			locateOrCreatePath(treeItem, path, level+1, isChecked, targetName, targetID);
			treeItem.setExpanded(true);
		}
	}
	
	private void reinit_list ()
	{
		TreeItem treeItem;
		String[] currentTargetPath;
		tree.removeAll();
		treeItem = new TreeItem (tree, SWT.NONE);
		treeItem.setText(currentProject.projectName);
		for (Target currentTarget : currentProject.targets.values())
		{
			currentTargetPath = currentTarget.getTargetLocation().split("/");
			locateOrCreatePath(treeItem, currentTargetPath, 0, currentTarget.isChecked(), currentTarget.getTargetName(), currentTarget.getTargetID());
		}
		
		treeItem.setExpanded(true);
		treeItem.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/obj16/prj_obj.gif"));
	}
	
	
	
	private void addTarget()
	{		
		TargetSettings targetSettings = new TargetSettings(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		targetSettings.createNewTarget(currentProject, newTargetLocation);
		reinit_list();
	}
	
	private void editTarget()
	{
		TargetSettings targetSettings = new TargetSettings(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		targetSettings.editTarget(currentProject, currentProject.targets.get((String)tree.getSelection()[0].getData()));
		reinit_list();
	}
	
	private void removeTarget()
	{
		currentProject.removeTarget((String)tree.getSelection()[0].getData());
		reinit_list();	
	}
	

}


