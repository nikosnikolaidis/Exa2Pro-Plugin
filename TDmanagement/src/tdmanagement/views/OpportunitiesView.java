package tdmanagement.views;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;
import org.eclipse.ui.texteditor.ITextEditor;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.SWT;

import java.io.File;
import java.util.ArrayList;

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

public class OpportunitiesView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tdmanagement.views.OpportunitiesView";

	@Inject
	IWorkbench workbench;

	private static TableViewer viewer;
	private Action action1;
	private Action doubleClickAction;

	// all columns
	static TableViewerColumn colMethodName;
	static TableViewerColumn colLines;
	static TableViewerColumn colBenefit;
	static TableViewerColumn colCluster;

	public static ArrayList<String> array = new ArrayList<>();
	public static boolean split;

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
		hookDoubleClickAction();
		contributeToActionBars();
	}

	/**
	 * Remove previous columns in Table
	 */
	public static void removePrev() {
		if (colMethodName != null) {
			colMethodName.getColumn().dispose();
			colLines.getColumn().dispose();
			colBenefit.getColumn().dispose();

			viewer.getTable().removeAll();
		}
		else if (colCluster!=null){
			colCluster.getColumn().dispose();
			viewer.getTable().removeAll();
		}
	}

	/**
	 * Adds columns for opportunities
	 */
	public static void addElementsToTable() {
		removePrev();

		if(split) {
		colMethodName = new TableViewerColumn(viewer, SWT.NONE);
		colMethodName.getColumn().setWidth(200);
		colMethodName.getColumn().setText("Procedure");
		colMethodName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String temp = (String) element;
				return temp.split(" ")[2];
			}
		});
		colLines = new TableViewerColumn(viewer, SWT.NONE);
		colLines.getColumn().setWidth(100);
		colLines.getColumn().setText("Lines");
		colLines.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String temp = (String) element;
				return temp.split(" ")[0] + "-" + temp.split(" ")[1];
			}
		});
		colBenefit = new TableViewerColumn(viewer, SWT.NONE);
		colBenefit.getColumn().setWidth(100);
		colBenefit.getColumn().setText("Benefit");
		colBenefit.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				String temp = (String) element;
				return temp.split(" ")[3];
			}
		});
		}
		else {
			colCluster = new TableViewerColumn(viewer, SWT.NONE);
			colCluster.getColumn().setWidth(400);
			colCluster.getColumn().setText("Cluster");
			colCluster.setLabelProvider(new ColumnLabelProvider() {
				@Override
				public String getText(Object element) {
					return (String) element;
				}
			});
		}

		viewer.setInput(array);
	}

	/**
	 * Set action1
	 */
	private void makeActions() {
		action1 = new Action() {
			public void run() {
				removePrev();
			}
		};
		action1.setText("Clear Opportunities");
		action1.setToolTipText("Clear Opportunities");
		action1.setImageDescriptor(
				PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));

		doubleClickAction = new Action() {
			public void run() {
				// get selection
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object obj = selection.getFirstElement();
				//showMessage("Opportunity detection for " + obj.toString());
				File fileToOpen = new File(obj.toString().split(" ")[4]);

				// Open Editor
				IEditorPart openEditor;
				IWorkbenchWindow window1 = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
				IWorkbenchPage page = window1.getActivePage();
				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				IPath location = Path.fromOSString(fileToOpen.getAbsolutePath());
				IFile ifile = workspace.getRoot().getFileForLocation(location);
				
				try {
					openEditor = IDE.openEditor(page, ifile, true);
					//Select lines
					int LineStart = Integer.parseInt(obj.toString().split(" ")[0]);
					int LineStop = Integer.parseInt(obj.toString().split(" ")[1]);
					if (openEditor instanceof ITextEditor) {
						ITextEditor textEditor = (ITextEditor) openEditor;
						IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
						int sum=0;
						for(int i=LineStart; i<=LineStop; i++) {
							sum += document.getLineLength(i - 1);
						}
						textEditor.selectAndReveal(document.getLineOffset(LineStart - 1), sum);
					}
				} catch (PartInitException | BadLocationException e) {
					e.printStackTrace();
				}

			}
		};
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
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
