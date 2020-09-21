package tdmanagement.handlers;

import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import exa2pro.Exa2Pro;
import exa2pro.Issue;
import exa2pro.PieChart;
import exa2pro.Project;
import exa2pro.ProjectCredentials;
import parsers.CodeFile;
import tdmanagement.views.ChartView;
import tdmanagement.views.MetricsView;
import tdmanagement.views.RefactoringsView;

public class LoadLastHandler extends AbstractHandler{

	public static Project p;

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
	    	MessageDialog.openInformation(
	    			window1.getShell(),
	    			"Load Analysis of ",
	    			name+" located in "+location);
	    	
	    	//Get preferences
	    	Exa2Pro.getProjetsFromFile();
	    	ProjectCredentials projectC=null;
	    	for(ProjectCredentials pc: Exa2Pro.projecCredentialstList) {
	    		if(pc.getProjectKey().equals(name)) {
	    			projectC=pc;
	    		}
	    	}
	    	
	    	if(projectC==null) {
	    		MessageDialog.openInformation(window1.getShell(),
		    			"No Analysis", "No Analysis found of "+name);
	    	}
	    	else {
	    		//Open Metrics-Refactoring View
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("tdmanagement.views.RefactoringsView");
				} catch (PartInitException e) {
					e.printStackTrace();
				}
		    	try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("tdmanagement.views.SampleView");
				} catch (PartInitException e) {
					e.printStackTrace();
				}

		    	//Get project
	            p= projectC.getProjects().get(projectC.getProjects().size()-1);
	    		
	            // Metrics
		    	//Remove Previous content and add new results 
		    	MetricsView.removePrev();
		    	MetricsView.array=p.getprojectFiles();
		    	MetricsView.addElementsToTableFile();
		    	
		    	
		    	// Refactorings
		    	//Add the in need of refactoring
		    	RefactoringsView.removePrev();
		    	HashMap<String, Double> thresholds= PieChart.calculateThresholds();
		    	//create the lists for all the methods and files
		        HashMap<String, Double> allFilesCohesion = new HashMap<>();
		        HashMap<String, Double> allFilesLCOP = new HashMap<>();
		        HashMap<String, Double> allFilesFanOut = new HashMap<>();
		        HashMap<String, Double> allMethodsCC = new HashMap<>();
		        HashMap<String, Double> allMethodsLOC = new HashMap<>();
		        for(CodeFile cf: p.getprojectFiles()){
		            if(cf.fanOut > thresholds.get("FanOut"))
		                allFilesFanOut.put(cf.file.getName(), (double) cf.fanOut);
		            if(Math.round(cf.cohesion * 10.0)/10.0 > thresholds.get("LCOL"))
		                allFilesCohesion.put(cf.file.getName(), Math.round(cf.cohesion * 10.0)/10.0);
		            if(cf.lcop > thresholds.get("LCOP"))
		                allFilesLCOP.put(cf.file.getName(), (double) cf.lcop);
		            allMethodsCC.putAll(prefixHashMap(cf.methodsCC, cf.file.getName(), thresholds, "CC"));
		            allMethodsLOC.putAll(prefixHashMap(cf.methodsLOC, cf.file.getName(), thresholds, "LOC"));
		        }
		        //sort the lists
		        HashMap<String, Double> sortedCC= allMethodsCC.entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
		        .collect(
		            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new));
		        HashMap<String, Double> sortedLOC= allMethodsLOC.entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
		        .collect(
		            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new));
		        HashMap<String, Double> sortedCohecion= allFilesCohesion.entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
		        .collect(
		            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new));
		        HashMap<String, Double> sortedLCOP= allFilesLCOP.entrySet()
		    	        .stream()
		    	        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
		    	        .collect(
		    	            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
		    	                LinkedHashMap::new));
		        HashMap<String, Double> sortedFanOut= allFilesFanOut.entrySet()
		        .stream()
		        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
		        .collect(
		            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
		                LinkedHashMap::new));
		    	//add the lists
		        RefactoringsView.arrayCC= sortedCC;
		        RefactoringsView.arrayLOC= sortedLOC;
		        RefactoringsView.arrayCoh= sortedCohecion;
		        RefactoringsView.arrayLCOP= sortedLCOP;
		        RefactoringsView.arrayFO= sortedFanOut;
		    	RefactoringsView.addElementsToTableFileFanOut();
		    	
		    	
		    	
		    	//Sonar
	    		//Open Markers view as well
		    	try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.AllMarkersView");
				} catch (PartInitException e) {
					e.printStackTrace();
				}
		    	//Add markers
	            try {
			    	Collections.sort(p.getprojectReport().getIssuesList());
		            
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					
		        	int depth = IResource.DEPTH_INFINITE;
		        	try {
		        	   workspace.getRoot().deleteMarkers("com.ibm.mymarkers.mymarker", true, depth);
		        	} catch (CoreException e) {
		        		System.out.println(e);
		        	}
		        	
		            for(Issue i: p.getprojectReport().getIssuesList()){
						for(CodeFile cf: p.getprojectFiles()) {
							if(cf.file.getAbsolutePath().endsWith(i.getIssueDirectory().split(":")[1].replaceAll("/", "\\\\"))) {
				            	///** add markers **///
								IPath locationIssue = Path.fromOSString(cf.file.getAbsolutePath());
								IFile ifile = workspace.getRoot().getFileForLocation(locationIssue);
								IMarker marker = null;
								try {
									marker = ifile.createMarker("com.ibm.mymarkers.mymarker");
									marker.setAttribute(IMarker.MESSAGE, i.getIssueDebt() +"   "+ i.getIssueName() );
									marker.setAttribute(IMarker.LINE_NUMBER, Integer.parseInt(i.getIssueStartLine()) );
									marker.setAttribute(IMarker.TEXT, i.getIssueName());
									if( i.getIssueSeverity().equals("INFO")  )
										marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
									else if( i.getIssueSeverity().equals("MINOR") || i.getIssueSeverity().equals("MAJOR") )
										marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
									else 
										marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
								} catch (CoreException e) {
									e.printStackTrace();
								}
								///** add markers **///
							}
						}
		            }
	            }
	            catch(NullPointerException e) {
	            	e.printStackTrace();
	            }
		    	
	            //Charts
	            try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("tdmanagement.views.ChartView");
				} catch (PartInitException e) {
					e.printStackTrace();
				}
	            
	            ChartView.project=p;
	            ChartView.build();
	            
	    	}
	    	
	    	
	    }
		
		
		return null;
	}
	
	
	private HashMap<String, Double> prefixHashMap(HashMap source, String prefix, HashMap<String, Double> thresholds, String metric) {
        HashMap<String, Double> result = new HashMap<>();
        Iterator iter = source.entrySet().iterator();
        while(iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if(((Integer)value)*1.0 > thresholds.get(metric))
                result.put(prefix + '.' + key.toString(), (int)value*1.0);
        }
        return result;
    }
}
