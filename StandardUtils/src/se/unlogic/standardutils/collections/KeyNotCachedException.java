package se.unlogic.standardutils.collections;

public class KeyNotCachedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1833583474702621444L;
	private Object key;	
	
	public KeyNotCachedException(Object key) {
		this.key = key;
	}

	public Object getKey() {
		return key;
	}

	@Override
	public String toString() {
		if(key != null){
			return "KeyNotCachedException, key: " + key.toString();
		}else{
			return "KeyNotCachedException, key: null";
		}
	}	
}
