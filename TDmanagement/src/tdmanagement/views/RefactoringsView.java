package tdmanagement.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

import tdmanagement.Activator;
import tdmanagement.handlers.LoadLastHandler;
import tdmanagement.handlers.SampleHandler;
import parsers.CodeFile;
import tdmanagement.preferences.PreferenceConstants;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;

import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class RefactoringsView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tdmanagement.views.RefactoringsView";

	@Inject
	IWorkbench workbench;

	private static TableViewer viewer;
	private Action action1FO;
	private Action action2Coh;
	private Action action2LCOP;
	private Action action3CC;
	private Action action4LOC;
	private Action doubleClickAction;

	// all columns
	static TableViewerColumn colFileName;
	static TableViewerColumn colFanOut;
	static TableViewerColumn colCohesion;
	static TableViewerColumn colLCOP;
	static TableViewerColumn colMethodName;
	static TableViewerColumn colCC;
	static TableViewerColumn colLOC;

	public static HashMap<String, Double> arrayCC = new HashMap<String, Double>();
	public static HashMap<String, Double> arrayLOC = new HashMap<String, Double>();
	public static HashMap<String, Double> arrayCoh = new HashMap<String, Double>();
	public static HashMap<String, Double> arrayLCOP = new HashMap<String, Double>();
	public static HashMap<String, Double> arrayFO = new HashMap<String, Double>();

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
		hookDoubleClickAction();
		contributeToActionBars();
	}

	/**
	 * Remove previous columns in Table
	 */
	public static void removePrev() {
		if (colCC != null) {
			colMethodName.getColumn().dispose();
			colCC.getColumn().dispose();
		}
		if (colLOC != null) {
			colMethodName.getColumn().dispose();
			colLOC.getColumn().dispose();
		}
		if (colFanOut != null) {
			colFileName.getColumn().dispose();
			colFanOut.getColumn().dispose();
		}
		if (colCohesion != null) {
			colFileName.getColumn().dispose();
			colCohesion.getColumn().dispose();
		}
		if(colLCOP != null) {
			colFileName.getColumn().dispose();
			colLCOP.getColumn().dispose();
		}
		viewer.getTable().removeAll();
	}

	/**
	 * Adds columns for file metrics FanOut and sets all the files to the table
	 */
	public static void addElementsToTableFileFanOut() {
		colFileName = new TableViewerColumn(viewer, SWT.NONE);
		colFileName.getColumn().setWidth(200);
		colFileName.getColumn().setText("File");
		colFileName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getName();
			}
		});
		colFanOut = new TableViewerColumn(viewer, SWT.NONE);
		colFanOut.getColumn().setWidth(100);
		colFanOut.getColumn().setText("FanOut");
		colFanOut.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getMetric().intValue() + "";
			}
		});

		for (String key : arrayFO.keySet()) {
			TempMethodOneMetric tM = new TempMethodOneMetric(key, arrayFO.get(key));
			viewer.add(tM);
		}
	}

	/**
	 * Adds columns for file metrics Cohesion and sets all the files to the table
	 */
	public static void addElementsToTableFileCohesion() {
		colFileName = new TableViewerColumn(viewer, SWT.NONE);
		colFileName.getColumn().setWidth(200);
		colFileName.getColumn().setText("File");
		colFileName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getName();
			}
		});

		colCohesion = new TableViewerColumn(viewer, SWT.NONE);
		colCohesion.getColumn().setWidth(100);
		colCohesion.getColumn().setText("Cohesion");
		colCohesion.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getMetric() + "";
			}
		});

		for (String key : arrayCoh.keySet()) {
			TempMethodOneMetric tM = new TempMethodOneMetric(key, arrayCoh.get(key));
			viewer.add(tM);
		}
	}
	
	/**
	 * Adds columns for file metrics LCOP and sets all the files to the table
	 */
	public static void addElementsToTableFileLCOP() {
		colFileName = new TableViewerColumn(viewer, SWT.NONE);
		colFileName.getColumn().setWidth(200);
		colFileName.getColumn().setText("File");
		colFileName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getName();
			}
		});

		colLCOP = new TableViewerColumn(viewer, SWT.NONE);
		colLCOP.getColumn().setWidth(100);
		colLCOP.getColumn().setText("LCOP");
		colLCOP.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getMetric() + "";
			}
		});

		for (String key : arrayLCOP.keySet()) {
			TempMethodOneMetric tM = new TempMethodOneMetric(key, arrayLCOP.get(key));
			viewer.add(tM);
		}
	}

	/**
	 * Adds columns for method metrics CC and adds all the file's method to the
	 * table by using ThempMethod class
	 */
	public void addElementsToTableMethodsCC() {
		colMethodName = new TableViewerColumn(viewer, SWT.NONE);
		colMethodName.getColumn().setWidth(300);
		colMethodName.getColumn().setText("Method");
		colMethodName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getName();
			}
		});
		colCC = new TableViewerColumn(viewer, SWT.NONE);
		colCC.getColumn().setWidth(100);
		colCC.getColumn().setText("CC");
		colCC.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getMetric().intValue() + "";
			}
		});

		for (String key : arrayCC.keySet()) {
			TempMethodOneMetric tM = new TempMethodOneMetric(key, arrayCC.get(key));
			viewer.add(tM);
		}
	}

	/**
	 * Adds columns for method metrics LOC and adds all the file's method to the
	 * table by using ThempMethod class
	 */
	public void addElementsToTableMethodsLOC() {
		colMethodName = new TableViewerColumn(viewer, SWT.NONE);
		colMethodName.getColumn().setWidth(300);
		colMethodName.getColumn().setText("Method");
		colMethodName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getName();
			}
		});
		colLOC = new TableViewerColumn(viewer, SWT.NONE);
		colLOC.getColumn().setWidth(100);
		colLOC.getColumn().setText("LOC");
		colLOC.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				TempMethodOneMetric temp = (TempMethodOneMetric) element;
				return temp.getMetric().intValue() + "";
			}
		});

		for (String key : arrayLOC.keySet()) {
			TempMethodOneMetric tM = new TempMethodOneMetric(key, arrayLOC.get(key));
			viewer.add(tM);
		}
	}

	/**
	 * Set action1, action2, action3 and action4
	 */
	private void makeActions() {
		action1FO = new Action() {
			public void run() {
				removePrev();
				// add new columns and data
				addElementsToTableFileFanOut();
			}
		};
		action1FO.setText("FanOut Files");
		action1FO.setToolTipText("See FanOut Files need Refactoring");
		action1FO.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

		action2Coh = new Action() {
			public void run() {
				removePrev();
				// add new columns and data
				addElementsToTableFileCohesion();
			}
		};
		action2Coh.setText("Cohesion(LCOL) Files");
		action2Coh.setToolTipText("See Cohesion(LCOL) Files need Refactoring");
		action2Coh.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));
		
		action2LCOP = new Action() {
			public void run() {
				removePrev();
				// add new columns and data
				addElementsToTableFileLCOP();
			}
		};
		action2LCOP.setText("Cohesion(LCOP) Files");
		action2LCOP.setToolTipText("See Cohesion(LCOP) Files need Refactoring");
		action2LCOP.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE));

		action3CC = new Action() {
			public void run() {
				removePrev();
				// add new columns and data
				addElementsToTableMethodsCC();
			}
		};
		action3CC.setText("CC Method/Function");
		action3CC.setToolTipText("See CC Method/Function need Refactoring");
		action3CC.setImageDescriptor(workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));

		action4LOC = new Action() {
			public void run() {
				removePrev();
				// add new columns and data
				addElementsToTableMethodsLOC();
			}
		};
		action4LOC.setText("LOC Method/Function Metrics");
		action4LOC.setToolTipText("See LOC Method/Function need Refactoring");
		action4LOC.setImageDescriptor(workbench.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));

		doubleClickAction = new Action() {
			public void run() {
				if (colCohesion != null) {
					// show message
					IStructuredSelection selection = viewer.getStructuredSelection();
					Object obj = selection.getFirstElement();
					showMessage("Opportunity detection for " + obj.toString());

					//Get Project Files
					ArrayList<CodeFile> projectFiles;
					if(SampleHandler.p!=null) {
						projectFiles= SampleHandler.p.getprojectFiles();
					}
					else {
						projectFiles= LoadLastHandler.p.getprojectFiles();
					}
					
					// calculate Opportunities
					CodeFile temp = null;
					for (CodeFile cf : projectFiles) {
						if (obj.toString().equals(cf.file.getName())) {
							temp = cf;
						}
					}

					if (temp != null) {
						// Open Opportunities View
						try {
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView("tdmanagement.views.OpportunitiesView");
						} catch (PartInitException e) {
							e.printStackTrace();
						}

						// Add new results
						temp.parse();
						temp.calculateOpportunities(Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.P_BOOLEANopport));
						
						
						
						OpportunitiesView.array = temp.opportunities;
						OpportunitiesView.addElementsToTable();
					}
				}
			}
		};
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				RefactoringsView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1FO);
		manager.add(action2Coh);
		manager.add(action2LCOP);
		manager.add(new Separator());
		manager.add(action3CC);
		manager.add(action4LOC);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1FO);
		manager.add(action2Coh);
		manager.add(action2LCOP);
		manager.add(new Separator());
		manager.add(action3CC);
		manager.add(action4LOC);
		manager.add(new Separator());
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(viewer.getControl().getShell(), "Sample View", message);
	}

	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}
