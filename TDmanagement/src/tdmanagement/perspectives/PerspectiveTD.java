package tdmanagement.perspectives;

import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveTD implements IPerspectiveFactory{

	private IPageLayout factory;

	@Override
	public void createInitialLayout(IPageLayout layout) {
		defineActions(layout);
	    defineLayout(layout);
	}
	
	public void defineActions(IPageLayout layout) {
        // Add "new wizards".
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
        layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");

        // Add "show views".
        layout.addShowViewShortcut(IPageLayout.ID_PROJECT_EXPLORER);
        layout.addShowViewShortcut(IPageLayout.ID_BOOKMARKS);
        layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
        layout.addShowViewShortcut(IPageLayout.ID_PROP_SHEET);
        layout.addShowViewShortcut(IPageLayout.ID_TASK_LIST);
	}

	public void defineLayout(IPageLayout layout) {
        // Editors are placed for free.
        String editorArea = layout.getEditorArea();
       
        
        
        //Middle
        IFolderLayout leftMid =
                layout.createFolder("leftMid", IPageLayout.LEFT, 0.4f, editorArea);
        leftMid.addView("tdmanagement.views.SampleView");
        leftMid.addView("tdmanagement.views.RefactoringsView");
        leftMid.addView("tdmanagement.views.OpportunitiesView");
        
        //Top
        IFolderLayout leftTop =
        		layout.createFolder("leftTop", IPageLayout.TOP, 0.4f, "leftMid");
        leftTop.addView("tdmanagement.views.ChartView");
        leftTop.addView("tdmanagement.views.Forecasting");
        
        IFolderLayout leftLeftTop =
                layout.createFolder("leftLeftTop", IPageLayout.LEFT, 0.4f, "leftTop");
        leftLeftTop.addView(IPageLayout.ID_PROJECT_EXPLORER);
        
        
        //Bottom
        IFolderLayout rightBottom =
        		layout.createFolder("rightBottom", IPageLayout.BOTTOM, 0.7f, editorArea);
        rightBottom.addView("org.eclipse.ui.console.ConsoleView");
        rightBottom.addView(IPageLayout.ID_PROBLEM_VIEW);
        rightBottom.addView(IPageLayout.ID_BOOKMARKS);
        rightBottom.addView(IPageLayout.ID_OUTLINE);
        
        
        ////Is possible to add a view of an other plugin
        //Test
        //if(Platform.getBundle("HelloWorld2")!=null) {
        //	System.out.println("Found!!!");
        //    rightBottom.addView("helloworld2.views.SampleView");
        //}
        
        IFolderLayout leftBottom =
        		layout.createFolder("leftBottom", IPageLayout.BOTTOM, 0.5f, "leftMid");
        leftBottom.addView("org.eclipse.ui.views.AllMarkersView");
        
	}
	
}
