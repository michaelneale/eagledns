package se.unlogic.eagledns;

public interface ZoneProviderUpdatable {
	public void setChangeListener(ZoneChangeCallback ev);
	
	static interface ZoneChangeCallback {
		public void zoneDataChanged();
	}
}

