package tdmanagement.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

import java.text.DecimalFormat;

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
import tdmanagement.preferences.PreferenceConstants;
import tdmanagement.views.ChartView;
import tdmanagement.views.Forecasting;
import tdmanagement.views.MetricsView;
import tdmanagement.views.RefactoringsView;
import tdmanagement.Activator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;

public class SampleHandler extends AbstractHandler {
	
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
	    			"Analysis",
	    			name+" located in "+location);
	    	
	    	//Get preferences
	    	Exa2Pro.sonarURL= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_STRING_URL);
	    	Exa2Pro.iCodePath= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH_iCode);
	    	Exa2Pro.sonarScannerPath= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH_SonarScanner);
	    	Exa2Pro.TDForecasterPath= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH_Forecaster);
	    	Exa2Pro.ClusteringPath= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH_Clustering);
	    	Exa2Pro.pythonRun= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_STRING_PYTHON);
	    	if(Exa2Pro.isWindows())
	    		Exa2Pro.Dos2UnixPath= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH_Dos2Unix);
	    	
	    	//Create Project
	    	boolean exists= false;
	    	ProjectCredentials pC=null;
	    	Exa2Pro.getProjetsFromFile();
	    	for(ProjectCredentials pcLtemp: Exa2Pro.projecCredentialstList) {
	    		if(pcLtemp.getProjectName().equals(name)) {
	    			exists=true;
	    			pC=pcLtemp;
	    			break;
	    		}
	    	}
	    	if(!exists) {
	    		pC=new ProjectCredentials(name, name, location.toString());
	    		Exa2Pro.projecCredentialstList.add(pC);
	    	}
	    	p= new Project(pC, pC.getProjects().size()+1+"");
	    	
	    	//Run analysis
	    	if(!Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_BOOLEAN)) {
		    	p.projectVersionAnalysis();
	    	}
	    	else {
	    		p.projectVersionAnalysisFull();

	    		//Open Markers view as well
		    	try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.AllMarkersView");
				} catch (PartInitException e) {
					e.printStackTrace();
				}

		        if(!Exa2Pro.projecCredentialstList.isEmpty()){
		            Collections.sort(p.getprojectReport().getIssuesList());
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					
					int depth = IResource.DEPTH_INFINITE;
		        	try {
		        	   workspace.getRoot().deleteMarkers("com.ibm.mymarkers.mymarker", true, depth);
		        	} catch (CoreException e) {
		        		System.out.println(e);
		        	}
		        	
		        	for(Issue i: p.getprojectReport().getIssuesList()){
		            	String[] nameFile= i.getIssueDirectory().split(":")[1].split("\\.");
		                if(nameFile[nameFile.length-1].equalsIgnoreCase("f90") || nameFile[nameFile.length-1].equalsIgnoreCase("f") 
		                				|| nameFile[nameFile.length-1].equalsIgnoreCase("f77") || nameFile[nameFile.length-1].equalsIgnoreCase("for") 
		                                || nameFile[nameFile.length-1].equalsIgnoreCase("fpp") || nameFile[nameFile.length-1].equalsIgnoreCase("ftn")){
		                    CodeFile cf= p.getFortranFilesIndexed().get(Integer.parseInt(nameFile[0]));
		                    ///** add markers **///
		                    addMarker(workspace, i, cf);
		                }
		                else {
		                	for(CodeFile cf: p.getprojectFiles()) {
								if(cf.file.getAbsolutePath().endsWith(i.getIssueDirectory().split(":")[1].replaceAll("/", "\\\\"))) {
					            	///** add markers **///
									addMarker(workspace, i, cf);
								}
							}
		                }
		            }
		            
		        }
	    	}

            //Charts
	    	try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("tdmanagement.views.Forecasting");
			} catch (PartInitException e) {
				e.printStackTrace();
			}
            Forecasting.project=p;
            Forecasting.build();
            
            try {
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("tdmanagement.views.ChartView");
			} catch (PartInitException e) {
				e.printStackTrace();
			}
            
            ChartView.project=p;
            ChartView.build();
	    	
	    	
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
	    	
	    	// Metrics
	    	//Remove Previous content and add new results 
            DecimalFormat round = new DecimalFormat("#,###.#");
	    	MetricsView.removePrev();
	    	//set array for file files
	    	MetricsView.array=p.getprojectFiles();
	    	//set arrayOverview for overview metrics
	    	MetricsView.arrayOverview.put("Technical Debt", p.getprojectReport().getTotalDebt());
	    	MetricsView.arrayOverview.put("Source Code Debt", round.format(p.getprojectReport().getTDPrincipalSourceCodeDebt()) + " €");
	    	MetricsView.arrayOverview.put("Design Debt", round.format(p.getprojectReport().getTDPrincipalDesignDebt()) + " €");
	        if(p.getprojectReport().getTotalTDInterest()!=0)
	        	MetricsView.arrayOverview.put("TD Interest", round.format(p.getprojectReport().getTotalTDInterest()) + " €");
	        else
	        	MetricsView.arrayOverview.put("TD Interest", "-");
	    	MetricsView.arrayOverview.put("Code Smells", p.getprojectReport().getTotalCodeSmells()+"");
	    	//system metrics
	    	double sumLOC=0;
	        int sumLCOP=0;
	        int countNonUndif=0;
	        int sumCC=0;
	        int sumLCOL=0;
	        int sumFO=0;
	        int c=0;
	        for(CodeFile cf: p.getprojectFiles()){
	            sumLOC+= cf.totalLines;
	            sumFO+= cf.fanOut;
	            if(cf.lcop!=-1){
	                sumLCOP+= cf.lcop;
	                countNonUndif++;
	            }
	            for (Map.Entry<String, Integer> entry : cf.methodsCC.entrySet()) {
	                sumCC+= entry.getValue();
	                c++;
	            }
	            for (Map.Entry<String, Double> entry : cf.methodsLCOL.entrySet()) {
	                sumLCOL+= entry.getValue();
	            }
	        }
	        DecimalFormat df = new DecimalFormat("#.#");
	        MetricsView.arrayOverview.put("CC", df.format(sumCC*1.0/c) +"");
	        MetricsView.arrayOverview.put("LCOL", df.format(sumLCOL*1.0/c) +"");
	        MetricsView.arrayOverview.put("CBF", df.format(sumFO*1.0/p.getprojectFiles().size()) +"");
	        MetricsView.arrayOverview.put("LOC", df.format(sumLOC/p.getprojectFiles().size()) +"");
	        if(countNonUndif==0)
	        	MetricsView.arrayOverview.put("LCOP", "-");
	        else
	        	MetricsView.arrayOverview.put("LCOP", df.format(sumLCOP/countNonUndif) +"");
	    	//set default table
	    	MetricsView.addElementsToTableOverview();
	    	
	    	
	    	// Refactorings
	    	//Add the in need of refactoring
	    	RefactoringsView.removePrev();
	    	HashMap<String, Double> thresholds= PieChart.calculateThresholds();
	    	//create the lists for all the methods and files
	        HashMap<String, Double> allFilesLCOP = new HashMap<>();
	        HashMap<String, Double> allFilesFanOut = new HashMap<>();
	        HashMap<String, Double> allMethodsCC = new HashMap<>();
	        HashMap<String, Double> allMethodsLCOL = new HashMap<>();
	        HashMap<String, Double> allFilesLOC = new HashMap<>();
	        for(CodeFile cf: p.getprojectFiles()){
	            if(cf.fanOut >= thresholds.get("FanOut"))
	                allFilesFanOut.put(cf.file.getName(), (double) cf.fanOut);
	            if(cf.lcop!=-1 && Math.round(cf.lcop * 10.0)/10.0 >= thresholds.get("LCOP"))
	                allFilesLCOP.put(cf.file.getName(), (double) cf.lcop);
	            if(cf.totalLines>=thresholds.get("LOC"))
	                allFilesLOC.put(cf.file.getName(), (double) cf.totalLines);
	            allMethodsCC.putAll(prefixHashMap(cf.methodsCC, cf.file.getName(), thresholds, "CC"));
	            allMethodsLCOL.putAll(prefixHashMap(cf.methodsLCOL, cf.file.getName(), thresholds, "LCOL"));
	        }
	        
	        //sort the lists
	        HashMap<String, Double> sortedCC= allMethodsCC.entrySet()
	        .stream()
	        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
	        .collect(
	            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
	                LinkedHashMap::new));
	        HashMap<String, Double> sortedLCOL= allMethodsLCOL.entrySet()
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
	        HashMap<String, Double> sortedLCOP= allFilesLCOP.entrySet()
	        .stream()
	        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
	        .collect(
	            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
	                LinkedHashMap::new));
	        HashMap<String, Double> sortedFilesLOC= allFilesLOC.entrySet()
	        .stream()
	        .sorted(Collections.reverseOrder(HashMap.Entry.comparingByValue()))
	        .collect(
	            toMap(HashMap.Entry::getKey, HashMap.Entry::getValue, (e1, e2) -> e2,
	                LinkedHashMap::new));
	    	//add the lists
	        RefactoringsView.arrayCC= sortedCC;
	        RefactoringsView.arrayLCOL= sortedLCOL;
	        RefactoringsView.arrayLOC= sortedFilesLOC;
	        RefactoringsView.arrayLCOP= sortedLCOP;
	        RefactoringsView.arrayCBF= sortedFanOut;
	        
	    	RefactoringsView.addElementsToTableFileCBF();
	    }
		
		
		return null;
	}


	private void addMarker(IWorkspace workspace, Issue i, CodeFile cf) {
		IPath locationIssue = Path.fromOSString(cf.file.getAbsolutePath());
		IFile ifile = workspace.getRoot().getFileForLocation(locationIssue);
		IMarker marker = null;
		try {
			marker = ifile.createMarker("com.ibm.mymarkers.mymarker");
			marker.setAttribute(IMarker.MESSAGE, i.getIssueDebt() +"  "+ i.getIssueName() );
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
	}
	

	private HashMap<String, Double> prefixHashMap(HashMap source, String prefix, HashMap<String, Double> thresholds, String metric) {
        HashMap<String, Double> result = new HashMap<>();
        Iterator iter = source.entrySet().iterator();
        while(iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if(value instanceof Integer) {
            	if((Integer)value >= thresholds.get(metric))
            		result.put(prefix + '.' + key.toString(), ((Integer)value)*1.0);
            }
            else {
            	if((Double)value >= thresholds.get(metric))
            		result.put(prefix + '.' + key.toString(), (Double)value);
            }
        }
        return result;
    }
}
