package com.bpcbt.svfe.eclipse;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class TargetPathSelectionDialog extends ElementTreeSelectionDialog {

    private static ITreeContentProvider contentProvider = new ITreeContentProvider() {
        public Object[] getChildren(Object element) {
        	if (element instanceof IContainer) {
                try {            
                	List<IResource> dirs = new ArrayList<IResource>(((IContainer) element).members().length);                	
                	for (IResource res : ((IContainer) element).members() )
                		if (res instanceof IFolder)
                			dirs.add(res);
                	return dirs.toArray();
                }
                catch (CoreException e) {
                }
            }
            return null;
        }

        public Object getParent(Object element) {return ((IResource) element).getParent();}
        public boolean hasChildren(Object element) {return element instanceof IContainer;}
        public Object[] getElements(Object input) {return (Object[]) input;}

        public void dispose() {}
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
    };

    private static final IStatus OK = new Status(IStatus.OK, "com.bpcbt.svfe", 0, "", null);
    private static final IStatus ERROR = new Status(IStatus.ERROR, "com.bpcbt.svfe", 0, "", null);


    private ISelectionStatusValidator validator = new ISelectionStatusValidator() {
        public IStatus validate(Object[] selection) {
            return selection.length == 1 && selection[0] instanceof IFolder ? OK : ERROR;
        }
    };

    public TargetPathSelectionDialog(String projectName) {
        this(Display.getDefault().getActiveShell(), WorkbenchLabelProvider.getDecoratingWorkbenchLabelProvider(),
                contentProvider);

        setTitle("Select path to target");
        setMessage("Select path to target");

        setInput(computeInput(projectName));
        setValidator(validator);
    }

    public TargetPathSelectionDialog(Shell parent, ILabelProvider labelProvider, ITreeContentProvider contentProvider) {
        super(parent, labelProvider, contentProvider);
    }

    private Object[] computeInput(String projectName) {
        /* Before populating a tree, refresh a project and root */
    	IProject currentProject = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
    	IResource[] members;

    	try {
    		currentProject.refreshLocal(IResource.DEPTH_INFINITE, null);    	
    		ResourcesPlugin.getWorkspace().getRoot().refreshLocal(IResource.DEPTH_ONE, null);
    		members = currentProject.members();


    		List<IResource> dirs = new ArrayList<IResource>(members.length);
    		for (IResource res : members)
    		{
    			if (res instanceof IFolder)
    				dirs.add(res);
    		}
    		return dirs.toArray();
    	} catch (CoreException e) { e.printStackTrace();}
    	return null;
    }
}