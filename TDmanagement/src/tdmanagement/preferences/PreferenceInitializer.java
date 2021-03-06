package tdmanagement.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import tdmanagement.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_BOOLEAN, true);
		store.setDefault(PreferenceConstants.P_BOOLEANopport, true);
		store.setDefault(PreferenceConstants.P_STRING_URL, "http://localhost:9000");
		store.setDefault(PreferenceConstants.P_STRING_PYTHON, "python");
		store.setDefault(PreferenceConstants.P_Number_ClusteringThreshold, "0.8");
	}

}
