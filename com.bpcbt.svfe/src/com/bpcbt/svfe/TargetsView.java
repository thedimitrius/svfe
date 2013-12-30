package com.bpcbt.svfe;

import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.SWT; 
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label; 
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.wb.swt.SWTResourceManager;

import com.bpcbt.svfe.system.Configuration;
import com.bpcbt.svfe.system.ISVFENeedUIUpdateListener;
import com.bpcbt.svfe.system.Project;
import com.bpcbt.svfe.system.Target;
import com.bpcbt.svfe.system.UpdateUIEvent;

public class TargetsView extends ViewPart {
	public TargetsView() {
	}
	private ExpandBar expandBar;
	

	private class ProjectUI {
		private Project		project;
		
		private String		projectName;

		private ExpandItem 	projectExpandItem;
		private Composite	projectComposite;
		
		private Button		cleanLibs;
		private Button		cleanModule;
		private Button		restart;
		private Button		gzip;
		
		private Button		consoleOpen;
		private Button		launch;
		private Button		editConfig;
		private Button 		editConfigList;
		private Combo		configurations;
		private Tree		targets;
		
		ProjectUI(Project currentProject, int expandIndex) 
		{
			Composite 	currentComposite;		/* Temporary pointer to a component, which pointer will not be later required */
			Label		currentLabel;
			TreeColumn	currentColumn;
			GridLayout	currentLayout;
			
			project = currentProject;
			projectName = currentProject.projectName;
			
			projectExpandItem = new ExpandItem(expandBar, SWT.NONE, expandIndex);
			projectExpandItem.setHeight(400);
			projectExpandItem.setText(currentProject.projectName);
			
			projectComposite = new Composite(expandBar, SWT.NONE);
			projectExpandItem.setControl(projectComposite);			
			currentLayout = new GridLayout(1,false);
			currentLayout.verticalSpacing=-2;
			projectComposite.setLayout(currentLayout);
			

			/* Configurations and launch buttons */
			currentComposite = new Composite(projectComposite, SWT.NONE);
			currentComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
			currentComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
			
			/* Open console to server button */
			consoleOpen = new Button(currentComposite, SWT.NONE);
			consoleOpen.setImage(ResourceManager.getPluginImage("org.eclipse.team.cvs.ui", "/icons/full/eview16/console_view.gif"));
			consoleOpen.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected (SelectionEvent e)
				{
					
					
					/*TODO*/
					 
					
					/*try {
						MessageConsole myConsole;
						String name = "OneMoreConsole" + seqno;
						seqno++;
						ConsolePlugin plugin = ConsolePlugin.getDefault();
						IConsoleManager conMan = plugin.getConsoleManager();
						IConsole[] existing = conMan.getConsoles();
						for (int i = 0; i < existing.length; i++)
							if (name.equals(existing[i].getName()))
								myConsole=(MessageConsole) existing[i];
						
						//no console found, so create a new one
						myConsole = new MessageConsole(name, null);
						conMan.addConsoles(new IConsole[]{myConsole});
						conMan.removeConsoles(new IConsole[]{lastConsole});
						
						lastConsole = myConsole;
						
						IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
						String id = IConsoleConstants.ID_CONSOLE_VIEW;
						IConsoleView view = (IConsoleView) page.showView(id);
						view.display(myConsole);	
						
						
						
					} catch (Exception er){}
					
					IProject currentProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
					try {
						currentProject.touch(null);
					} catch (CoreException e1) {
						e1.printStackTrace();
					}*/
					
				}
			});

			/* Launch button */
			launch = new Button(currentComposite, SWT.NONE);
			launch.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/etool16/run_exc.gif"));
			launch.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected (SelectionEvent event)
				{
										//project.
					//BuildAction action = new BuildAction(getSite().getWorkbenchWindow(), BuildAction.ID_BUILD);
					
					//action.run();
					//TODO
					//buildProjectPressed ((String) ((Button) e.getSource()).getData());
					
				}
			});

			/* Configurations combo */
			configurations = new Combo(currentComposite, SWT.NONE);
			int defaultIndex=0;
			for (Configuration currentConfiguration : currentProject.configurations.values())
			{
				configurations.add(currentConfiguration.getConfigurationName());
				if (currentConfiguration.getConfigurationName().compareTo(currentProject.getDefaultConfigurationName()) == 0)
				{
					configurations.select(defaultIndex);
				}
				defaultIndex++;				
			}
			configurations.setLayoutData(new RowData(150, SWT.DEFAULT));
			configurations.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected (SelectionEvent e)
				{
					project.setDefaultConfigurationName((String) ((Combo)e.getSource()).getText());;
				}
			});


			/* Edit current configuration button */
			editConfig = new Button(currentComposite, SWT.NONE);
			editConfig.setImage(ResourceManager.getPluginImage("com.bpcbt.svfe", "/icons/conf/edit_config.png"));
			editConfig.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected (SelectionEvent e)
				{
					SVFEActivator.getDefault().editConfig(projectName, project.configurations.get(project.getDefaultConfigurationName()), getSite().getWorkbenchWindow().getShell());
				}
			});
			
			/* Edit configurations list button */
			editConfigList = new Button (currentComposite, SWT.NONE);
			editConfigList.setImage(ResourceManager.getPluginImage("com.bpcbt.svfe", "/icons/conf/conf_list.png"));
			editConfigList.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected (SelectionEvent e)
				{
					SVFEActivator.getDefault().openConfigList(projectName, getSite().getWorkbenchWindow().getShell());
				}
			});
			

			/* Clean buttons */
			currentComposite = new Composite(projectComposite, SWT.NONE);
			currentComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
			currentComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));

			currentLabel = new Label(currentComposite, SWT.NONE);
			currentLabel.setText("Make clean:");

			cleanLibs = new Button(currentComposite, SWT.CHECK);
			cleanLibs.setText("libs");
			cleanLibs.setSelection(currentProject.isCleanLibs());
			cleanLibs.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected (SelectionEvent e)
				{
					project.setCleanLibs( ((Button)e.getSource()).getSelection() );
				}
			});

			cleanModule = new Button(currentComposite, SWT.CHECK);
			cleanModule.setText("module");
			cleanModule.setSelection(currentProject.isCleanModule());
			cleanModule.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected (SelectionEvent e)
				{
					project.setCleanModule( ((Button)e.getSource()).getSelection() );										
				}
			});

			/* After build buttons */
			currentComposite = new Composite(projectComposite, SWT.NONE);
			currentComposite.setLayout(new RowLayout(SWT.HORIZONTAL));
			currentComposite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));

			currentLabel = new Label(currentComposite, SWT.NONE);
			currentLabel.setText("After build:");

			restart = new Button(currentComposite, SWT.CHECK);
			restart.setText("restart");
			restart.setSelection(currentProject.isRestart());
			restart.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected (SelectionEvent e)
				{
					project.setRestart( ((Button)e.getSource()).getSelection() );										
				}
			});

			gzip = new Button(currentComposite, SWT.CHECK);
			gzip.setText("gzip and copy");
			gzip.setSelection(currentProject.isGzip());
			gzip.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected (SelectionEvent e)
				{
					project.setGzip( ((Button)e.getSource()).getSelection() );										
				}
			});

			/* Targets tree */
			targets = new Tree(projectComposite, SWT.CHECK);
			targets.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			targets.setData(currentProject.projectName);

			currentColumn = new TreeColumn(targets, SWT.NONE);
			currentColumn.setWidth(200);
			currentColumn.setText("Target location");

			currentColumn = new TreeColumn(targets, SWT.NONE);
			currentColumn.setWidth(100);
			currentColumn.setText("Target name");
			
			targets.setHeaderVisible(true);

			populateTargets(currentProject, targets);
			projectComposite.layout();
			SVFEActivator.getDefault().getProjectsList().addUIUpdateListener (new ProjectUIUpdateListener());
		}
		
		private class ItemAndPath { public String path; public TreeItem item; }
		
		private class ProjectUIUpdateListener implements ISVFENeedUIUpdateListener
		{
			Target currentTarget;
			public void projectChanged (UpdateUIEvent e)
			{
				if (e.projectName.compareTo(projectName)==0)
				{
					switch (e.eventType)
					{
					case UpdateUIEvent.ProjectClosed:
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								projectExpandItem.getControl().dispose();
								projectExpandItem.dispose();
								expandBar.layout();
							}
						});			
						break;
					case UpdateUIEvent.ConfigurationsModified:
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								configurations.removeAll();
								int defaultIndex=0;
								for (Configuration currentConfiguration : project.configurations.values())
								{
									configurations.add(currentConfiguration.getConfigurationName());
									if (currentConfiguration.getConfigurationName().compareTo(project.getDefaultConfigurationName()) == 0)
									{
										configurations.select(defaultIndex);
									}
									defaultIndex++;		
								}
							}
						});					
						break;
					case UpdateUIEvent.TargetAdded:
						currentTarget = (Target)e.object;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								String[] currentTargetPath = currentTarget.getTargetLocation().split("/");
								locateOrCreatePath(targets.getItem(0), currentTargetPath, 0, currentTarget.isChecked(), currentTarget.getTargetName(), currentTarget.getTargetID());
							}
						});
						break;
					case UpdateUIEvent.TargetModified:
						currentTarget = (Target)e.object;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								ItemAndPath itemAndPath;
								String targetID = currentTarget.getTargetID();
								itemAndPath = locateTargetByTargetID(targets.getItem(0), targetID);
								if (currentTarget.getTargetLocation().compareTo(itemAndPath.path)==0)
								{
									/* Target path did not change */
									itemAndPath.item.setText(1,currentTarget.getTargetName());
								}
								else
								{
									/* Target path changed. Need to remove old one and create new one */
									removeEmptyTargetDirs(itemAndPath.item);
									locateOrCreatePath(targets.getItem(0), currentTarget.getTargetLocation().split("/"), 0, currentTarget.isChecked(), currentTarget.getTargetName(), targetID);
								}
							}
						});
						break;
					case UpdateUIEvent.TargetRemoved:
						currentTarget = (Target)e.object;
						Display.getDefault().asyncExec(new Runnable() {
							public void run() {
								String[] currentTargetPath = currentTarget.getTargetLocation().split("/");
								removeEmptyTargetDirs(getTargetItem(currentTargetPath, 0, targets.getItem(0)));						
							}
						});
						break;
					} /* switch (e.eventType) */		
				} /* if (e.projectName.compareTo(projectName)==0) */
			} /*private void projectChanged (UpdateUIEvent e) */
		} /* private class projectUIUpdateListener implements ISVFENeedUIUpdateListener */

		private void populateTargets(Project currentProject, Tree currentTree)
		{
			TreeItem treeItem;
			String[] currentTargetPath;	
			Menu currentMenu;

			treeItem = new TreeItem (currentTree, SWT.NONE);
			treeItem.setText(currentProject.projectName);
			for (Target currentTarget : currentProject.targets.values())
			{
				currentTargetPath = currentTarget.getTargetLocation().split("/");
				locateOrCreatePath(treeItem, currentTargetPath, 0, currentTarget.isChecked(), currentTarget.getTargetName(), currentTarget.getTargetID());
			}

			treeItem.setExpanded(true);
			treeItem.setImage(ResourceManager.getPluginImage("org.eclipse.debug.ui", "/icons/full/obj16/prj_obj.gif"));

			/* Listener for checking targets */
			currentTree.addListener(SWT.Selection, new Listener() 
			{
				public void handleEvent(Event event) 
				{	
					if (event.detail == SWT.CHECK) 
					{
						TreeItem currentItem = (TreeItem)event.item;
						String targetID = (String) currentItem.getData();
						boolean isChecked = currentItem.getChecked();
						
						if (currentItem.getItemCount()==0)
							project.targetChecked(targetID, isChecked);							
						else
						{
							currentItem.setGrayed(false);							
							checkChildTreeItems(currentItem, isChecked);
						}
						checkParentTreeItems(currentItem);
					}
				}
			});

			currentMenu = new Menu(currentTree);
			currentTree.setMenu(currentMenu);
			
			currentMenu.addMenuListener(new MenuAdapter()
			{
				public void menuShown(MenuEvent e)
				{
					Menu menu = (Menu)e.getSource();
					MenuItem[] items = menu.getItems();
					MenuItem newItem;
					
					TreeItem currentTreeItem;
					String currentPath = new String();
					String currentTargetID;
					
					/* First remove all old items */
					for (int i = 0; i < items.length; i++)
						items[i].dispose();
										
					/* Path to selected target */
					currentTreeItem = targets.getSelection()[0];
					while (currentTreeItem.getParentItem()!=null)
					{
						currentPath = currentTreeItem.getText() + "/" + currentPath;
						currentTreeItem = currentTreeItem.getParentItem();
					}
					
					/* Create new items */
					newItem = new MenuItem(menu, SWT.NONE);
					newItem.setText("New target");
					newItem.setData(currentPath);
					newItem.setImage(ResourceManager.getPluginImage("com.bpcbt.svfe", "/icons/targ/target_add.gif"));            	
					newItem.addSelectionListener(new SelectionAdapter()	{
						public void widgetSelected(SelectionEvent e){
							MenuItem item = (MenuItem)e.widget;
							String path = (String) item.getData();
							SVFEActivator.getDefault().createNewTarget(projectName, getSite().getWorkbenchWindow().getShell(), path);
						}
					});
					
					if (targets.getSelection()[0].getParentItem() != null && targets.getSelection()[0].getData() != null)
					{
						/* Get currently selected target ID */
						currentTargetID = (String)targets.getSelection()[0].getData();
						
						newItem = new MenuItem(menu, SWT.NONE);
						newItem.setText("Edit target");
						newItem.setData(currentTargetID);
						newItem.setImage(ResourceManager.getPluginImage("com.bpcbt.svfe", "/icons/targ/target_edit.gif"));
						newItem.addSelectionListener(new SelectionAdapter() 
						{
							public void widgetSelected(SelectionEvent e)
							{
								MenuItem menuItem = (MenuItem)e.widget;
								String targetID = (String)menuItem.getData();
								SVFEActivator.getDefault().editTarget(projectName, getSite().getWorkbenchWindow().getShell(), targetID);
							}
						});

						newItem = new MenuItem(menu, SWT.NONE);
						newItem.setText("Delete target");
						newItem.setData(currentTargetID);
						newItem.setImage(ResourceManager.getPluginImage("com.bpcbt.svfe", "/icons/targ/target_delete.gif"));
						newItem.addSelectionListener(new SelectionAdapter() 
						{
							public void widgetSelected(SelectionEvent e)
							{
								MenuItem menuItem = (MenuItem)e.widget;
								String targetID = (String)menuItem.getData();
								MessageBox messageBox = new MessageBox(getSite().getWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.APPLICATION_MODAL);
							    messageBox.setMessage("Do you really want to remove target " + project.targets.get(targetID).getTargetLocation() + " ?");
					            messageBox.setText("Removing target");
					            int response = messageBox.open();
							        if (response == SWT.YES)
							        	project.removeTarget(targetID);	
							}
						});
					}

					newItem = new MenuItem(menu, SWT.NONE);
					newItem.setText("Edit targets list");
					newItem.setImage(ResourceManager.getPluginImage("com.bpcbt.svfe", "/icons/targ/target_list.gif"));
					newItem.addSelectionListener(new SelectionAdapter() 
					{
						public void widgetSelected(SelectionEvent e)
						{
							SVFEActivator.getDefault().openTargetsList(projectName, getSite().getWorkbenchWindow().getShell());	            			
						}
					});

					new MenuItem(menu, SWT.SEPARATOR);

					newItem = new MenuItem(menu, SWT.NONE);
					newItem.setText("Edit current configuration");
					newItem.setImage(ResourceManager.getPluginImage("com.bpcbt.svfe", "/icons/conf/edit_config.png"));
					newItem.addSelectionListener(new SelectionAdapter() 
					{
						public void widgetSelected(SelectionEvent e)
						{
							SVFEActivator.getDefault().editConfig(projectName, project.configurations.get(project.getDefaultConfigurationName()), getSite().getWorkbenchWindow().getShell());
						}
					});

					newItem = new MenuItem(menu, SWT.NONE);
					newItem.setText("Edit configurations list");
					newItem.setImage(ResourceManager.getPluginImage("com.bpcbt.svfe", "/icons/conf/conf_list.png"));
					newItem.addSelectionListener(new SelectionAdapter() 
					{
						public void widgetSelected(SelectionEvent e)
						{
							SVFEActivator.getDefault().openConfigList(projectName, getSite().getWorkbenchWindow().getShell());	            			
						}
					});

				} /* void menuShow() */
			});		/* addMenuListener */
		} /* populateTargets */
		
		private ItemAndPath locateTargetByTargetID (TreeItem treeItem, String targetID)
		{
			ItemAndPath result;
			if (treeItem.getItemCount() == 0)
			{
				if (((String)treeItem.getData()).compareTo(targetID) == 0)
				{	
					result = new ItemAndPath();
					result.item = treeItem;
					result.path = targetID;
					return result;
				}
				else
				{
					return null;
				}
			}
			else
			{
				for (TreeItem currentItem : treeItem.getItems())
				{
					result = locateTargetByTargetID(currentItem, targetID);
					if (result != null)
					{
						result.path = treeItem.getText() + result.path;
						return result;
					}
				}				
			}			
			return null;
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
				treeItem.setExpanded(false);
			}

			if (level == path.length-1)
			{
				treeItem.setChecked(isChecked);
				treeItem.setImage(ResourceManager.getPluginImage("org.eclipse.ant.ui", "/icons/full/obj16/targetinternal_obj.gif"));
				treeItem.setText(new String[] {path[level], targetName});
				treeItem.setData(targetID);
				checkParentTreeItems(treeItem);
			}
			else
			{			
				locateOrCreatePath(treeItem, path, level+1, isChecked, targetName, targetID);				
			}
		}

		private void checkChildTreeItems(TreeItem item, boolean setChecked)
		{		
			for (TreeItem childItem : item.getItems())
			{
				childItem.setGrayed(false);
				childItem.setChecked(setChecked);
				if (childItem.getItemCount() != 0 )
				{
					checkChildTreeItems(childItem, setChecked);
				}
				else
				{
					project.targetChecked((String)childItem.getData(), setChecked);					
				}
			}
		}

		private void checkParentTreeItems(TreeItem item)
		{
			TreeItem parentItem = item.getParentItem();
			if (parentItem != null)
			{
				boolean checkedExist = false;
				boolean uncheckedExist = false;
				for (TreeItem childItem : parentItem.getItems())
				{
					if (childItem.getGrayed())
					{
						checkedExist = true;
						uncheckedExist = true;
						break;
					}
					else if (childItem.getChecked())
						checkedExist = true;
					else
						uncheckedExist = true;	
				}

				parentItem.setChecked(checkedExist);
				parentItem.setGrayed(checkedExist && uncheckedExist);

				checkParentTreeItems(parentItem);
			}
		}
						
		private void removeEmptyTargetDirs(TreeItem currentItem)
		{
			if (currentItem == null)return;
			TreeItem parentItem = currentItem.getParentItem();
			if (parentItem.getItemCount() == 1)
				removeEmptyTargetDirs(parentItem);
			else
				currentItem.dispose();
		}
		
		private TreeItem getTargetItem (String[] targetPath, int level, TreeItem treeItem)
		{	
			for (TreeItem currentTreeItem : treeItem.getItems() )
			{
				if (currentTreeItem.getText().compareTo(targetPath[level]) == 0)
				{
					if (level == targetPath.length-1)
						return currentTreeItem;
					else
						return getTargetItem (targetPath, level+1, currentTreeItem);
				}
			}
			return null;
		}		
	} /* private class ProjectUI */

	public void createPartControl(Composite parent) 
	{
		parent.setLocation(0,0);
		parent.setLayout(new FillLayout(SWT.VERTICAL));	

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		expandBar = new ExpandBar(composite, SWT.V_SCROLL);
		expandBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		expandBar.setBackground(SWTResourceManager.getColor(SWT.COLOR_WHITE));		
		expandBar.setSpacing(1);

		/* Populating projects expand bar */
		for (Project currentProject : SVFEActivator.getDefault().getProjectsList().projects.values())
		{
			int expandIndex = 0;
			for (ExpandItem currentItem : expandBar.getItems())
			{
				if (currentItem.getText().compareTo(currentProject.projectName)>0)
					break;
				expandIndex++;
			}
			
			new ProjectUI (currentProject, expandIndex);
			
		}
		
		SVFEActivator.getDefault().getProjectsList().addUIUpdateListener(new ISVFENeedUIUpdateListener() {
			Project currentProject;
			public void projectChanged(UpdateUIEvent e)
			{
				if (e.eventType == UpdateUIEvent.ProjectOpened)
				{
					currentProject = (Project)e.object;
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							int expandIndex = 0;
							for (ExpandItem currentItem : expandBar.getItems())
							{
								if (currentItem.getText().compareTo(currentProject.projectName)>0)
									break;
								expandIndex++;
							}							
							new ProjectUI (currentProject, expandIndex);			
						}
					});					
				}
			}
		});
	}




	public void setFocus() {}
}
