package tdmanagement.views;

import java.awt.Frame;

import javax.inject.Inject;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import exa2pro.Exa2Pro;
import exa2pro.LineChart;
import exa2pro.Project;
import panels_frames.JPanelForecasting;
import tdmanagement.Activator;
import tdmanagement.preferences.PreferenceConstants;

public class Forecasting extends ViewPart{

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "tdmanagement.views.Forecasting";

	@Inject IWorkbench workbench;
	
	private static Composite composite;
	private static Frame frame;
	
	public static Project project;
	
	private static javax.swing.JPanel jPanelChart;

	
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
		composite = new Composite(parent, SWT.EMBEDDED | SWT.NO_BACKGROUND);
		frame = SWT_AWT.new_Frame(composite);
	}
	
	public static void build() {
    	Exa2Pro.TDForecasterPath= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_PATH_Forecaster);
    	Exa2Pro.pythonRun= Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.P_STRING_PYTHON);
    	
		if(!Exa2Pro.TDForecasterPath.equals("")) {
			JPanelForecasting jpF=new JPanelForecasting(project);
			frame.add(jpF);
			if(!jpF.hasResults) {
				showMessage("Cannot provide reliable results for this project. Please reduce forecasting horizon");
			}
			frame.repaint();
		}
	}
	
	
	private static void showMessage(String message) {
		MessageDialog.openInformation(composite.getShell(),
			"Forecasting View",
			message);
	}

	@Override
	public void setFocus() {
		composite.setFocus();
	}

}
