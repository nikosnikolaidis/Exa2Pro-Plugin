package tdmanagement.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import tdmanagement.Activator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class PreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public PreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Preference page of Technical Debt Managment tool");
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.P_BOOLEAN,
				"Complete scan with SonarQube", getFieldEditorParent()));

		addField(new BooleanFieldEditor(PreferenceConstants.P_BOOLEANopport,
				"Faster Opportunity extractor (with a drawback of accuracy)", getFieldEditorParent()));
		
		addField(new StringFieldEditor(PreferenceConstants.P_STRING_URL,
				"Sonar Qube URL preference:", getFieldEditorParent()));
		
		addField(new FileFieldEditor(PreferenceConstants.P_PATH_SonarScanner, 
				"Sonar Scanner path preference:", getFieldEditorParent()));

		addField(new FileFieldEditor(PreferenceConstants.P_PATH_iCode, 
				"iCode path preference:", getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
}