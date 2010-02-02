package se.unlogic.standardutils.io;

import java.io.File;
import java.io.FileFilter;


public class EndsWithFileFilter implements FileFilter {

	private String suffix;

	public EndsWithFileFilter(String suffix) {
		super();
		this.suffix = suffix;
	}

	public boolean accept(File file) {

		if(file.getName().toLowerCase().endsWith(suffix)){

			return true;
		}

		return false;
	}
}
