package tdmanagement.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import exa2pro.Exa2Pro;
import exa2pro.Project;
import exa2pro.ProjectCredentials;

public class DeleteHandler extends AbstractHandler{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String name="";
		IPath location=null;
		
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	    if (window != null)
	    {
	        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
	        Object firstElement = selection.getFirstElement();
	        if (firstElement instanceof IAdaptable)
	        {
	            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
	            name = project.getFullPath().toString().replace("/", "");
	            location= project.getLocation();
	            System.out.println(name +" -- "+ location);
	        }
	    }
	    
	    if(location!=null) {
	    	IWorkbenchWindow window1 = HandlerUtil.getActiveWorkbenchWindowChecked(event);
	    	
	    	//Get preferences
	    	Exa2Pro.getProjetsFromFile();
	    	ProjectCredentials projectC=null;
	    	for(ProjectCredentials pc: Exa2Pro.projecCredentialstList) {
	    		if(pc.getProjectKey().equals(name)) {
	    			projectC=pc;
	    		}
	    	}
	    	
	    	//Delete or show messages
	    	if(projectC==null) {
	    		MessageDialog.openInformation(window1.getShell(),
		    			"No Analysis", "No Analysis found of "+name);
	    	}
	    	else {
	    		boolean delete= MessageDialog.openConfirm(window1.getShell(), "Delete",
	    				"Are you sure you want to delete all the analysis info of this project?");
	    		if(delete) {
	    			Exa2Pro.projecCredentialstList.remove(projectC);
	    			Project.saveToFile();
	    		}
	    	}
	    }
		return null;
	
	}

}
