package se.unlogic.standardutils.collections;

public class KeyAlreadyCachedException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6139847169384918434L;
	private Object key;
	
	public KeyAlreadyCachedException(Object key) {
		this.key = key;
	}

	public Object getKey() {
		return key;
	}

	@Override
	public String toString() {
		if(key != null){
			return "KeyAlreadyCachedException, key: " + key.toString();
		}else{
			return "KeyAlreadyCachedException, key: null";
		}
	}	
}
