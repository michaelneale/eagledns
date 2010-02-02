package se.unlogic.standardutils.collections;

import java.util.Collection;
import java.util.TreeSet;

public class NotNullTreeSet<Type> extends TreeSet<Type> {

	private static final long serialVersionUID = -7808520266670296566L;

	@Override
	public boolean add(Type o) {

		if(o == null){
			throw new NullPointerException(this.getClass() + " does not allow null values!");
		}
			
		return super.add(o);
	}

	@Override
	public boolean addAll(Collection<? extends Type> c) {

		if(c == null || c.contains(null)){
			throw new NullPointerException(this.getClass() + " does not allow null values!");
		}
		
		return super.addAll(c);
	}
}
