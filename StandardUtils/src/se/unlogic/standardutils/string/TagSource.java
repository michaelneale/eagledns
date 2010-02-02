package se.unlogic.standardutils.string;

import java.util.Set;


public interface TagSource {

	public Set<String> getTags();
	
	public String getTagValue(String tag);
}
