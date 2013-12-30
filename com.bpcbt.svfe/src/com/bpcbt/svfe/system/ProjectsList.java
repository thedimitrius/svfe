package com.bpcbt.svfe.system;


import java.util.HashMap;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

import com.bpcbt.svfe.SVFEActivator;
import com.bpcbt.svfe.eclipse.SVFENature;

public class ProjectsList {
	
	public  HashMap<String, Project> projects = new HashMap<String, Project>();
	private ISVFENeedUIUpdateListener uiUpdateListeners[];
			
	public ProjectsList ()
	{
		uiUpdateListeners = new ISVFENeedUIUpdateListener[0];
		IProject[] workspaceProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
						
		/* Initialize projects */
		for (int i=0; i<workspaceProjects.length; i++)
		{
			if (workspaceProjects[i].isOpen())
			{
				try {
					if (workspaceProjects[i].hasNature(SVFENature.NATURE_ID))
					{
						Project currentProject = new Project();
						if (currentProject.initFromPrefs(workspaceProjects[i]))
						{
							projects.put(workspaceProjects[i].getName(), currentProject);
						}
					}
				} catch (CoreException e) {	e.printStackTrace();}
			}
		}
		
		
		
		ResourcesPlugin.getWorkspace().addResourceChangeListener(new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) 
			{
				if (event.getType() == IResourceChangeEvent.POST_BUILD){
					for (IProject proj : ResourcesPlugin.getWorkspace().getRoot().getProjects())
					{
						if (proj.isOpen())
						{
							try {
								if (proj.hasNature(SVFENature.NATURE_ID))
									proj.touch(null);
							} catch (CoreException e) {
								e.printStackTrace();
							}
						}
					}
				}
				
			    if (event == null || event.getDelta() == null)
			        return;			    
			    try{
			    	event.getDelta().accept(new IResourceDeltaVisitor() {
			    		public boolean visit(IResourceDelta delta) throws CoreException {
			    			IProject project;
			    			
			    			if ((delta.getFlags() & IResourceDelta.MOVED_TO) !=0)
			    			{
			    				IResource resource = delta.getResource();
			    				if (resource instanceof IProject)
			    				{			    						
			    					if (((IProject) resource).hasNature(SVFENature.NATURE_ID))
			    					{
			    						Project currentProject = projects.get(resource.getName());
			    						UpdateUIEvent event = new UpdateUIEvent();
			    						event.eventType = UpdateUIEvent.ProjectClosed;
			    						event.projectName = resource.getName();
			    						event.object = currentProject;
			    						SVFEActivator.getDefault().getProjectsList().updateUI(event);

			    						projects.remove(resource.getName());			    					
			    					}
			    				}
			    			}			    			
			    			else if ((delta.getFlags() & IResourceDelta.OPEN)!=0)
			    			{
			    				IResource resource = delta.getResource();
			    				if (resource instanceof IProject)
			    				{
			    					if (((IProject) resource).hasNature(SVFENature.NATURE_ID))
			    					{
			    						project = (IProject) resource;
			    						if (project.isOpen())
			    						{
			    							Project newProj = new Project();
			    							newProj.initFromPrefs(project);
			    							projects.put(project.getName(), newProj);

			    							UpdateUIEvent event = new UpdateUIEvent();
			    							event.eventType = UpdateUIEvent.ProjectOpened;
			    							event.projectName = project.getName();
			    							event.object = newProj;
			    							SVFEActivator.getDefault().getProjectsList().updateUI(event);			    						
			    						}
			    						else
			    						{
			    							Project currentProject = projects.get(resource.getName());

			    							UpdateUIEvent event = new UpdateUIEvent();
			    							event.eventType = UpdateUIEvent.ProjectClosed;
			    							event.projectName = resource.getName();
			    							event.object = currentProject;
			    							SVFEActivator.getDefault().getProjectsList().updateUI(event);

			    							projects.remove(resource.getName());
			    						}
			    					}
			    				}
			    				return true;
			    			} /* if event was "open or close" */
			    			return true;
			    		}/* end of visit() implementation */
			    	}	/* end of class implementation */
			    	); /* end of accept() call */
			    }
			    catch (CoreException e) {}
			}	
		}, IResourceChangeEvent.POST_BUILD |
		   IResourceChangeEvent.POST_CHANGE |
		   IResourceChangeEvent.PRE_BUILD);
	}	
	
	
	public boolean convertProjectToSVFE (IProject project)
	{
		if (projects.get(project.getName())!=null)
		{
			return false;
		}		
		Project currentProject = new Project ();
		currentProject.initialize(project);
		projects.put(project.getName(), currentProject);
		return true;
	}
	
	public void addUIUpdateListener ( ISVFENeedUIUpdateListener newListener)
	{
		ISVFENeedUIUpdateListener newuiUpdateListeners[] = new ISVFENeedUIUpdateListener [ uiUpdateListeners.length+1];
		System.arraycopy(uiUpdateListeners, 0, newuiUpdateListeners, 0, uiUpdateListeners.length);
		newuiUpdateListeners[newuiUpdateListeners.length-1]=newListener;
		uiUpdateListeners = newuiUpdateListeners;
	}
	
	public void updateUI(UpdateUIEvent e)
	{
		for (int i=0; i<uiUpdateListeners.length; i++)
		{
			uiUpdateListeners[i].projectChanged(e);
		}
	}
}


