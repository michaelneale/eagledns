package se.unlogic.standardutils.settings;

import java.util.List;

public interface SettingNode {

	public SettingNode getSetting(String expression);
	
	public List<? extends SettingNode> getSettings(String expression);
	
	public Integer getInteger(String expression);
	
	public List<Integer> getIntegers(String expression);
	
	public int getInt(String expression);
	
	public Double getDouble(String expression);
	
	public List<Double> getDoubles(String expression);
	
	public Long getLong(String expression);
	
	public List<Long> getLongs(String expression);
	
	public String getString(String expression);
	
	public List<String> getStrings(String expression);
	
	public Boolean getBoolean(String expression);
	
	public boolean getPrimitiveBoolean(String expression);

}
