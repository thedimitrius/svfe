package com.bpcbt.svfe.dialogs;

import org.eclipse.core.resources.IFolder;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.ExpandAdapter;
import org.eclipse.swt.events.ExpandEvent;

import com.bpcbt.svfe.eclipse.TargetPathSelectionDialog;
import com.bpcbt.svfe.system.Project;
import com.bpcbt.svfe.system.Target;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;


public class TargetSettings extends Dialog {

	protected Object result;
	protected Shell shlSvfeCompilationTarget;
	private Text targetPathEdit;
	private Text restartEdit;
	private Text gzipEdit;
	private Text targetNameEdit;
	private Text text;
	private Text text_1;
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	
	ExpandBar expandBar;
	private Project currentProject;
	private Target currentTarget;

	public TargetSettings(Shell parent, int style) {
		super(parent, style);
		setText("SWT Dialog");
	}

	public Object open() {
		createContents();
		shlSvfeCompilationTarget.open();
		shlSvfeCompilationTarget.layout();
		Display display = getParent().getDisplay();
		while (!shlSvfeCompilationTarget.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	private void createContents() {
		shlSvfeCompilationTarget = new Shell(getParent(), getStyle());
		shlSvfeCompilationTarget.setSize(481, 203);
		shlSvfeCompilationTarget.setText("SVFE Compilation target settings");
		shlSvfeCompilationTarget.setLayout(new GridLayout(3, false));
		
		Label lblTargetName = new Label(shlSvfeCompilationTarget, SWT.NONE);
		lblTargetName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblTargetName.setText("Target name");
		
		targetNameEdit = new Text(shlSvfeCompilationTarget, SWT.BORDER);
		targetNameEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblPathToTarget = new Label(shlSvfeCompilationTarget, SWT.NONE);
		lblPathToTarget.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPathToTarget.setText("Workspace path to target");
		
		targetPathEdit = new Text(shlSvfeCompilationTarget, SWT.BORDER);
		targetPathEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnPathToTarget = new Button(shlSvfeCompilationTarget, SWT.NONE);
		btnPathToTarget.addSelectionListener(new SelectionAdapter() {			
			public void widgetSelected(SelectionEvent e) {
				TargetPathSelectionDialog dialog = new TargetPathSelectionDialog (currentProject.projectName);
				dialog.setBlockOnOpen(true);
				if (dialog.open() == ElementTreeSelectionDialog.OK)
				{
					targetPathEdit.setText( ((IFolder)dialog.getResult()[0]).getProjectRelativePath().toString());
				}
				
			}
		});
		btnPathToTarget.setText("...");
		
		Label lblRestartAliases = new Label(shlSvfeCompilationTarget, SWT.NONE);
		lblRestartAliases.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRestartAliases.setText("Restart aliases");
		
		restartEdit = new Text(shlSvfeCompilationTarget, SWT.BORDER);
		restartEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblFilesForArchiving = new Label(shlSvfeCompilationTarget, SWT.NONE);
		lblFilesForArchiving.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFilesForArchiving.setText("Files for archiving");
		
		gzipEdit = new Text(shlSvfeCompilationTarget, SWT.BORDER);
		gzipEdit.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		expandBar = new ExpandBar(shlSvfeCompilationTarget, SWT.NONE);
		expandBar.addExpandListener(new ExpandAdapter() {
			public void itemExpanded(ExpandEvent e) {
				shlSvfeCompilationTarget.setSize(481, 380);
			}
			public void itemCollapsed(ExpandEvent e) {
				shlSvfeCompilationTarget.setSize(481, 203);
			}
		});
		expandBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WIDGET_BACKGROUND));
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		
		ExpandItem xpndtmAdvancedSettings = new ExpandItem(expandBar, SWT.NONE);
		xpndtmAdvancedSettings.setText("Advanced settings");
		
		Composite composite = new Composite(expandBar, SWT.NONE);
		xpndtmAdvancedSettings.setControl(composite);
		xpndtmAdvancedSettings.setHeight(170);
		composite.setLayout(new GridLayout(2, false));
		
		Label lblLocalScriptBefore = new Label(composite, SWT.NONE);
		lblLocalScriptBefore.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocalScriptBefore.setText("Local script before build");
		
		text = new Text(composite, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblRemoteScriptBefore = new Label(composite, SWT.NONE);
		lblRemoteScriptBefore.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRemoteScriptBefore.setText("Remote script before build");
		
		text_1 = new Text(composite, SWT.BORDER);
		text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblLocalScriptAfter = new Label(composite, SWT.NONE);
		lblLocalScriptAfter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblLocalScriptAfter.setText("Local script after build");
		
		text_2 = new Text(composite, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 1, 1));
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Remote script after build");
		
		text_3 = new Text(composite, SWT.BORDER);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Local script after restart");
		
		text_4 = new Text(composite, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblNewLabel_2 = new Label(composite, SWT.NONE);
		lblNewLabel_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_2.setText("Remote script after restart");
		
		text_5 = new Text(composite, SWT.BORDER);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnOk = new Button(shlSvfeCompilationTarget, SWT.NONE);
		btnOk.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				acceptChanges();
			}
		});
		GridData gd_btnOk = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnOk.widthHint = 65;
		btnOk.setLayoutData(gd_btnOk);
		btnOk.setText("OK");
		
		Button btnCancel = new Button(shlSvfeCompilationTarget, SWT.NONE);
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shlSvfeCompilationTarget.dispose();
			}
		});
		GridData gd_btnCancel = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		gd_btnCancel.widthHint = 67;
		btnCancel.setLayoutData(gd_btnCancel);
		btnCancel.setText("Cancel");
		new Label(shlSvfeCompilationTarget, SWT.NONE);
	}
	
	public void createNewTarget(Project project, String initialPath)
	{
		currentProject = project;
		currentTarget = null;
		createContents();
		targetPathEdit.setText(initialPath);
		shlSvfeCompilationTarget.open();
		shlSvfeCompilationTarget.layout();
		Display display = getParent().getDisplay();
		while (!shlSvfeCompilationTarget.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	public void editTarget(Project project, Target target)
	{
		currentProject = project;
		currentTarget = target;
		createContents();
		
		targetNameEdit.setText(target.getTargetName());
		targetPathEdit.setText(target.getTargetLocation());
		restartEdit.setText(target.getTargetRestartAliases());
		gzipEdit.setText(target.getTargetGzipFiles());		
		
		shlSvfeCompilationTarget.open();
		shlSvfeCompilationTarget.layout();
		Display display = getParent().getDisplay();
		while (!shlSvfeCompilationTarget.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	private void acceptChanges ()
	{
		boolean rc;
		String targetPath;
		if (targetPathEdit.getText().charAt(0)=='/')
			targetPath = targetPathEdit.getText().substring(1);
		else
			targetPath = targetPathEdit.getText();
	
		if (currentTarget == null)
		{
			rc=currentProject.addTarget(targetPath, restartEdit.getText(), gzipEdit.getText(), false, targetNameEdit.getText());			
		}
		else
		{
			rc=currentProject.editTarget(currentTarget.getTargetID(), targetPath, restartEdit.getText(), gzipEdit.getText(), targetNameEdit.getText());
		}
		if (rc)
		{
			shlSvfeCompilationTarget.dispose();
		}
	}

}
