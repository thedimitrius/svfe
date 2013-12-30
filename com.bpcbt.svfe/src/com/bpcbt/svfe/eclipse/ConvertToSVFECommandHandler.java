package com.bpcbt.svfe.eclipse;

import java.util.Iterator;

import org.eclipse.core.commands.*;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.bpcbt.svfe.SVFEActivator;
import com.bpcbt.svfe.system.ProjectsList;

public class ConvertToSVFECommandHandler extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection 		selection = HandlerUtil.getCurrentSelection(event);
		ProjectsList	projects;
		Object 			element; 
		IProject 		project; 
		
		if (selection instanceof IStructuredSelection) 
		{
			for (Iterator<?> it = ((IStructuredSelection) selection).iterator(); it	.hasNext();) 
			{
				element = it.next();
				project = null;
				if (element instanceof IProject) 
					project = (IProject) element;
				else if (element instanceof IAdaptable) 
					project = (IProject) ((IAdaptable) element).getAdapter(IProject.class);
				
				if (project != null) 
				{
					projects = SVFEActivator.getDefault().getProjectsList();
					projects.convertProjectToSVFE(project);
				}
			}
		}
		return null;
	}
}
