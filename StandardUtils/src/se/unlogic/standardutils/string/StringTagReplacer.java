package se.unlogic.standardutils.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class StringTagReplacer {

	private List<TagSource> tagSources = new ArrayList<TagSource>();

	public StringTagReplacer() {
		super();
	}

	public StringTagReplacer(List<TagSource> tagSources) {
		super();
		this.tagSources = tagSources;
	}

	public StringTagReplacer(TagSource... tagSources) {
		super();
		this.tagSources = Arrays.asList(tagSources);
	}

	public boolean addTagSource(TagSource o) {

		return tagSources.add(o);
	}

	public boolean removeTagSource(TagSource o) {

		return tagSources.remove(o);
	}

	public String replace(String source){

		for(TagSource tagSource : tagSources){

			for(String tag : tagSource.getTags()){

				if(source.contains(tag)){

					source = source.replace(tag, tagSource.getTagValue(tag));
				}
			}
		}

		return source;
	}
}
