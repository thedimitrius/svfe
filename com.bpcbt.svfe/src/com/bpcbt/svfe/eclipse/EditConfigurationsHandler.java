package com.bpcbt.svfe.eclipse;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.bpcbt.svfe.SVFEActivator;

public class EditConfigurationsHandler extends AbstractHandler {
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		ISelection 		selection = HandlerUtil.getCurrentSelection(event);
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
					SVFEActivator.getDefault().openConfigList(project.getName(), HandlerUtil.getActiveShell(event) );
				}
			}
		}
		
		return null;
	}
}
