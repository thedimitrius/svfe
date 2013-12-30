package com.bpcbt.svfe.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;

import com.bpcbt.svfe.system.Configuration;
import com.bpcbt.svfe.system.Project;

import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class ConfigurationsList extends Dialog {

	protected Object result;
	protected Shell shlSvfeConfigurations;
	private Table table;
	private Project currentProject;

	public ConfigurationsList(Shell parent, int style) {
		super(parent, style );
		setText("SWT Dialog");
	}

	public Object open() {
		createContents();
		shlSvfeConfigurations.open();
		shlSvfeConfigurations.layout();
		Display display = getParent().getDisplay();
		while (!shlSvfeConfigurations.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shlSvfeConfigurations = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		shlSvfeConfigurations.setSize(389, 300);
		shlSvfeConfigurations.setText("SVFE Configurations");
		shlSvfeConfigurations.setLayout(new GridLayout(2, false));
		
		Label lblSvfeCompilationConfigurations = new Label(shlSvfeConfigurations, SWT.NONE);
		lblSvfeCompilationConfigurations.setText("SVFE compilation configurations");
		new Label(shlSvfeConfigurations, SWT.NONE);
		
		table = new Table(shlSvfeConfigurations, SWT.BORDER | SWT.FULL_SELECTION);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 5));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableColumn tblclmnConfigurationName = new TableColumn(table, SWT.NONE);
		tblclmnConfigurationName.setWidth(149);
		tblclmnConfigurationName.setText("Configuration name");
		
		TableColumn tblclmnDescription = new TableColumn(table, SWT.NONE);
		tblclmnDescription.setWidth(134);
		tblclmnDescription.setText("Server");
		
		Button btnAdd = new Button(shlSvfeConfigurations, SWT.NONE);
		btnAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnAdd.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				addConfig();
			}
		});
		btnAdd.setText("Add...");
		
		Button btnEdit = new Button(shlSvfeConfigurations, SWT.NONE);
		btnEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnEdit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editConfig();
			}
		});
		btnEdit.setText("Edit...");
		
		Button btnDelete = new Button(shlSvfeConfigurations, SWT.NONE);
		btnDelete.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnDelete.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				removeConfig();
			}
		});
		btnDelete.setText("Delete");
		
		Button btnSetActive = new Button(shlSvfeConfigurations, SWT.NONE);
		btnSetActive.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setActiveConfig();
			}
		});
		btnSetActive.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnSetActive.setText("Set active");
		new Label(shlSvfeConfigurations, SWT.NONE);
		new Label(shlSvfeConfigurations, SWT.NONE);
		
		Button btnOk = new Button(shlSvfeConfigurations, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shlSvfeConfigurations.dispose();
			}
		});
		btnOk.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		btnOk.setText("OK");
	}
	
	public void initialize(Project proj)
	{
		currentProject = proj;
		
		createContents();
		
		shlSvfeConfigurations.open();
		shlSvfeConfigurations.layout();
		Display display = getParent().getDisplay();
		
		reinitTable();
		
		
		while (!shlSvfeConfigurations.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}
	
	
	
	private void addConfig()
	{		
		ConfigurationSettings confSettings = new ConfigurationSettings(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		confSettings.createNewConfig(currentProject);
		reinitTable();
	}
	
	private void editConfig()
	{
		ConfigurationSettings confSettings = new ConfigurationSettings(getParent(), SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		confSettings.editConfig(currentProject, currentProject.configurations.get(table.getSelection()[0].getText(0)));
		reinitTable();
	}
	
	private void removeConfig()
	{
		if (table.getItemCount() == 1)
		{
			MessageBox dialog = new MessageBox(getParent(), SWT.ICON_ERROR | SWT.OK);
			dialog.setText("Error");
			dialog.setMessage("Cannot remove the last configuration. At least one configuration should exist for each project.");
			dialog.open();			
		}
		else
		{
			currentProject.removeConfiguration(table.getSelection()[0].getText(0));
			reinitTable();
		}
	}
	
	private void reinitTable()
	{
		TableItem currentRow;
		
		table.setRedraw(false);
		table.removeAll();
		for (Configuration currentConfig : currentProject.configurations.values())
		{
		    currentRow = new TableItem(table, SWT.NONE);
		    currentRow.setText(0, currentConfig.getConfigurationName());
		    currentRow.setText(1, currentConfig.getServerName());
		    if (currentConfig.getConfigurationName().compareTo(currentProject.getDefaultConfigurationName())==0)
		    {
		    	FontData[] fd = currentRow.getFont().getFontData();
		    	fd[0].setStyle(SWT.BOLD);		    
		    	currentRow.setFont(new Font(shlSvfeConfigurations.getDisplay(), fd[0]));
		    }
		}
		table.setRedraw(true);
		
		table.setSelection(0);		
	}
	
	private void setActiveConfig()
	{
		currentProject.setDefaultConfigurationName(table.getSelection()[0].getText(0));
		reinitTable();
	}	
}
