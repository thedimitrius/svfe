package com.bpcbt.svfe.dialogs;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import com.bpcbt.svfe.system.Configuration;
import com.bpcbt.svfe.system.Project;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;


public class ConfigurationSettings extends Dialog {

	protected Object result;
	protected Shell shlSvfeCompileConfiguration;
	private Text configNameEdit;
	private Text serverNameEdit;
	private Text serverUsernameEdit;
	private Text serverSrcDirEdit;
	private Text passwordEdit;
	private Text keyLocationEdit;
	private Text gzipLocationEdit;
	
	private Button btnKeyAuth;
	private Button btnPasswordAuth;
	private Button keyPathSelectbtn;
	
	private Project currentProject;
	private Configuration currentConf;
	

	public ConfigurationSettings(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	public Object open() {
		createContents();
		shlSvfeCompileConfiguration.open();
		shlSvfeCompileConfiguration.layout();
		Display display = getParent().getDisplay();
		while (!shlSvfeCompileConfiguration.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shlSvfeCompileConfiguration = new Shell(getParent(), getStyle() | SWT.APPLICATION_MODAL);
		shlSvfeCompileConfiguration.setSize(468, 253);
		shlSvfeCompileConfiguration.setText("SVFE Compile configuration settings");
		shlSvfeCompileConfiguration.setLayout(new GridLayout(4, false));
		
		Label lblConfigurationName = new Label(shlSvfeCompileConfiguration, SWT.NONE);
		lblConfigurationName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblConfigurationName.setText("Configuration name:");
		
		configNameEdit = new Text(shlSvfeCompileConfiguration, SWT.BORDER);
		configNameEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblDestinationServer = new Label(shlSvfeCompileConfiguration, SWT.NONE);
		lblDestinationServer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblDestinationServer.setText("Destination server:");
		
		serverNameEdit = new Text(shlSvfeCompileConfiguration, SWT.BORDER);
		serverNameEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblServerUsername = new Label(shlSvfeCompileConfiguration, SWT.NONE);
		lblServerUsername.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerUsername.setText("Server username");
		
		serverUsernameEdit = new Text(shlSvfeCompileConfiguration, SWT.BORDER);
		serverUsernameEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		btnKeyAuth = new Button(shlSvfeCompileConfiguration, SWT.RADIO);
		btnKeyAuth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				enableKeyAuth();
			}
		});
		btnKeyAuth.setSelection(true);
		btnKeyAuth.setText("Key auth");
		
		Label lblKeyLocation = new Label(shlSvfeCompileConfiguration, SWT.NONE);
		lblKeyLocation.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblKeyLocation.setAlignment(SWT.RIGHT);
		lblKeyLocation.setText("Key location");
		
		keyLocationEdit = new Text(shlSvfeCompileConfiguration, SWT.BORDER);
		keyLocationEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		keyPathSelectbtn = new Button(shlSvfeCompileConfiguration, SWT.NONE);
		keyPathSelectbtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectKeyPath();
			}
		});
		keyPathSelectbtn.setText("...");
		
		btnPasswordAuth = new Button(shlSvfeCompileConfiguration, SWT.RADIO);
		btnPasswordAuth.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				enablePassAuth();
			}
		});
		btnPasswordAuth.setText("Password auth");
		
		Label lblPassword = new Label(shlSvfeCompileConfiguration, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setAlignment(SWT.RIGHT);
		lblPassword.setText("Password");
		
		passwordEdit = new Text(shlSvfeCompileConfiguration, SWT.BORDER);
		passwordEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblServerSrc = new Label(shlSvfeCompileConfiguration, SWT.NONE);
		lblServerSrc.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblServerSrc.setText("Server src dir");
		
		serverSrcDirEdit = new Text(shlSvfeCompileConfiguration, SWT.BORDER);
		serverSrcDirEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Label lblLocationOfArchived = new Label(shlSvfeCompileConfiguration, SWT.NONE);
		lblLocationOfArchived.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 2, 1));
		lblLocationOfArchived.setText("Location of archived binaries");
		
		gzipLocationEdit = new Text(shlSvfeCompileConfiguration, SWT.BORDER);
		gzipLocationEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button button_1 = new Button(shlSvfeCompileConfiguration, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectGzipPath();
			}
		});
		button_1.setText("...");
		
		Button btnAccept = new Button(shlSvfeCompileConfiguration, SWT.NONE);
		btnAccept.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				acceptChanges();
			}
		});
		GridData gd_btnAccept = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnAccept.widthHint = 67;
		btnAccept.setLayoutData(gd_btnAccept);
		btnAccept.setText("OK");
		
		Button btnCancel = new Button(shlSvfeCompileConfiguration, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shlSvfeCompileConfiguration.dispose();
			}
		});
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 67;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
	}
	
	public void createNewConfig(Project project)
	{
		currentProject = project;
		currentConf = null;
		createContents();
		btnKeyAuth.setSelection(true);
		btnPasswordAuth.setSelection(false);
		
		keyLocationEdit.setEditable(true);
		passwordEdit.setEditable(false);
		keyPathSelectbtn.setEnabled(true);
		
		shlSvfeCompileConfiguration.open();
		shlSvfeCompileConfiguration.layout();
		Display display = getParent().getDisplay();
		while (!shlSvfeCompileConfiguration.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		
	}
	
	public void editConfig(Project project, Configuration conf)
	{
		currentProject = project;
		currentConf = conf;
		createContents();
		configNameEdit.setText(conf.getConfigurationName());
		serverNameEdit.setText(conf.getServerName());
		serverUsernameEdit.setText(conf.getServerUsername());
		
		btnKeyAuth.setSelection(conf.isAuthByKey());
		btnPasswordAuth.setSelection(!conf.isAuthByKey());
		keyLocationEdit.setEditable(conf.isAuthByKey());
		passwordEdit.setEditable(!conf.isAuthByKey());
		keyPathSelectbtn.setEnabled(conf.isAuthByKey());
		
		keyLocationEdit.setText(conf.getKeyLocation());
		passwordEdit.setText(conf.getServerPassword());
		serverSrcDirEdit.setText(conf.getServerSrcLocation());
		gzipLocationEdit.setText(conf.getGzipFilesLocation());
		
		
		shlSvfeCompileConfiguration.open();
		shlSvfeCompileConfiguration.layout();
		Display display = getParent().getDisplay();
		while (!shlSvfeCompileConfiguration.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
		
	public void acceptChanges()
	{
		boolean rc;
		if (currentConf == null)
		{
			rc = currentProject.addConfiguration(configNameEdit.getText(),
											serverNameEdit.getText(),
											serverUsernameEdit.getText(), 
											passwordEdit.getText(), 
											keyLocationEdit.getText(), 
											serverSrcDirEdit.getText(), 
											gzipLocationEdit.getText(), 
											btnKeyAuth.getSelection());
		}
		else
		{
			rc = currentProject.editConfiguration(currentConf.getConfigurationName(), 
											 configNameEdit.getText(),
											 serverNameEdit.getText(),
											 serverUsernameEdit.getText(), 
											 passwordEdit.getText(), 
											 keyLocationEdit.getText(), 
											 serverSrcDirEdit.getText(), 
											 gzipLocationEdit.getText(), 
											 btnKeyAuth.getSelection());
		}
		
		if (rc)
		{
			shlSvfeCompileConfiguration.dispose();
		}
	}
	
	private void enableKeyAuth()
	{
		keyLocationEdit.setEditable(true);
		passwordEdit.setEditable(false);
		keyPathSelectbtn.setEnabled(true);
	}
	
	private void enablePassAuth()
	{
		keyLocationEdit.setEditable(false);
		passwordEdit.setEditable(true);
		keyPathSelectbtn.setEnabled(false);
	}
	
	private void selectGzipPath()
	{
		DirectoryDialog dialog = new DirectoryDialog(new Shell(), SWT.OPEN);
		dialog.setFilterPath("c:\\");
		gzipLocationEdit.setText(dialog.open());
	}
	
	private void selectKeyPath()
	{
		FileDialog dialog = new FileDialog(new Shell(), SWT.OPEN);
		dialog.setFilterPath("c:\\");
		keyLocationEdit.setText(dialog.open());
	}
	
}
