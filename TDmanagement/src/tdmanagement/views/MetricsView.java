package tdmanagement.views;


import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import parsers.CodeFile;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;

import java.util.ArrayList;

import javax.inject.Inject;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class MetricsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tdmanagement.views.SampleView";

	@Inject IWorkbench workbench;
	
	private static TableViewer viewer;
	private Action action1;
	private Action action2;
	
	//all columns
	static TableViewerColumn colFileName;
	static TableViewerColumn colCBF;
	static TableViewerColumn colLOC;
	static TableViewerColumn colLCOP;
	static TableViewerColumn colMethodName;
	static TableViewerColumn colCC;
	static TableViewerColumn colLCOL;
	
	public static ArrayList<CodeFile> array=new ArrayList<CodeFile>();
	

	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		@Override
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		@Override
		public Image getImage(Object obj) {
			return workbench.getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		
		
		// Create the help context id for the viewer's control
		workbench.getHelpSystem().setHelp(viewer.getControl(), "TDmanagement.viewer");
		getSite().setSelectionProvider(viewer);
		makeActions();
		hookContextMenu();
		contributeToActionBars();
	}
	
	/**
	 * Remove previous columns in Table
	 */
	public static void removePrev() {
		if(colMethodName!=null) {
			colMethodName.getColumn().dispose();
			colCC.getColumn().dispose();
			colLCOL.getColumn().dispose();
		}
		if(colFileName!=null) {
			colFileName.getColumn().dispose();
			colCBF.getColumn().dispose();
			colLOC.getColumn().dispose();
			colLCOP.getColumn().dispose();
		}
		viewer.getTable().removeAll();
	}
	
	
	/**
	 * Adds columns for file metrics 
	 * and sets all the files to the table
	 */
	public static void addElementsToTableFile() {
		colFileName = new TableViewerColumn(viewer, SWT.NONE);
		colFileName.getColumn().setWidth(200);
		colFileName.getColumn().setText("File");
		colFileName.setLabelProvider(new ColumnLabelProvider() {
		    @Override
		    public String getText(Object element) {
		        CodeFile cf = (CodeFile) element;
		        return cf.file.getName();
		    }
		});
		colCBF = new TableViewerColumn(viewer, SWT.NONE);
		colCBF.getColumn().setWidth(100);
		colCBF.getColumn().setText("CBF");
		colCBF.setLabelProvider(new ColumnLabelProvider() {
		    @Override
		    public String getText(Object element) {
		        CodeFile cf = (CodeFile) element;
		        return cf.fanOut+"";
		    }
		});
		colLOC = new TableViewerColumn(viewer, SWT.NONE);
		colLOC.getColumn().setWidth(100);
		colLOC.getColumn().setText("LOC");
		colLOC.setLabelProvider(new ColumnLabelProvider() {
		    @Override
		    public String getText(Object element) {
		        CodeFile cf = (CodeFile) element;
		        return cf.totalLines+"";
		        //return  (Math.round(cf.cohesion * 10.0)/10.0)+"";
		    }
		});
		colLCOP = new TableViewerColumn(viewer, SWT.NONE);
		colLCOP.getColumn().setWidth(100);
		colLCOP.getColumn().setText("LCOP");
		colLCOP.setLabelProvider(new ColumnLabelProvider() {
		    @Override
		    public String getText(Object element) {
		        CodeFile cf = (CodeFile) element;
		        if(cf.lcop != -1)
		        	return cf.lcop +"";
		        else
		        	return "NonDefined";
		    }
		});
		
		viewer.setInput(array);
	}
	
	/**
	 * Adds columns for method metrics 
	 * and adds all the file's method to the table by using ThempMethod class
	 */
	public void addElementsToTableMethods() {
		colMethodName = new TableViewerColumn(viewer, SWT.NONE);
		colMethodName.getColumn().setWidth(300);
		colMethodName.getColumn().setText("Method");
		colMethodName.setLabelProvider(new ColumnLabelProvider() {
		    @Override
		    public String getText(Object element) {
		    	TempMethod temp= (TempMethod)element;
		        return temp.getName();
		    }
		});
		colCC = new TableViewerColumn(viewer, SWT.NONE);
		colCC.getColumn().setWidth(100);
		colCC.getColumn().setText("CC");
		colCC.setLabelProvider(new ColumnLabelProvider() {
		    @Override
		    public String getText(Object element) {
		    	TempMethod temp= (TempMethod)element;
		        return temp.getCC()+"";
		    }
		});
		colLCOL = new TableViewerColumn(viewer, SWT.NONE);
		colLCOL.getColumn().setWidth(100);
		colLCOL.getColumn().setText("LCOL");
		colLCOL.setLabelProvider(new ColumnLabelProvider() {
		    @Override
		    public String getText(Object element) {
		    	TempMethod temp= (TempMethod)element;
		    	if(temp.getLCOL()!=-1)
		    		return temp.getLCOL()+"";
		    	return "-";
		    }
		});
		
		for(CodeFile cf: array){
            for(String key :cf.methodsLOC.keySet()){
            	TempMethod tM;
            	if(cf.methodsLCOL.containsKey(key))
            		tM=new TempMethod(cf.file.getName()+": "+key, cf.methodsCC.get(key), cf.methodsLCOL.get(key));
            	else
            		tM=new TempMethod(cf.file.getName()+": "+key, cf.methodsCC.get(key), -1);
            	viewer.add(tM);
            }
        }
	}
	
	/**
	 * Set action1 and action2
	 */
	private void makeActions() {
		action1 = new Action() {
			public void run() {
				removePrev();
				//add new columns and data
				addElementsToTableFile();
			}
		};
		action1.setText("File Metrics");
		action1.setToolTipText("See File metrics");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		
		
		action2 = new Action() {
			public void run() {
				removePrev();
				//add new columns and data
				addElementsToTableMethods();
			}
		};
		action2.setText("Method/Function Metrics");
		action2.setToolTipText("See Method/Function metrics");
		action2.setImageDescriptor(workbench.getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));
	}
	

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				MetricsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
		manager.add(action2);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
		manager.add(action2);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
	
}
