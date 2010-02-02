package se.unlogic.standardutils.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class JsonObject implements JsonNode {

	private final Map<String, JsonNode> fields = new HashMap<String, JsonNode>();
	
	public String toJson(StringBuilder stringBuilder) {
		stringBuilder.append("{");
		Iterator<Entry<String, JsonNode>> iterator = fields.entrySet().iterator();
		Entry<String, JsonNode> field;
		while(iterator.hasNext()) {
			field = iterator.next();
			stringBuilder.append(field.getKey());
			stringBuilder.append(":");
			field.getValue().toJson(stringBuilder);
			if(iterator.hasNext()) {
				stringBuilder.append(",");
			}
		}
		stringBuilder.append("}");
		return stringBuilder.toString();
	}
	
	public void putField(String key, JsonNode value) {
		this.fields.put(key, value);
	}
	
	public void removeField(String key) {
		this.fields.remove(key);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fields == null) ? 0 : fields.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JsonObject other = (JsonObject) obj;
		if (fields == null) {
			if (other.fields != null)
				return false;
		} else if (!fields.equals(other.fields))
			return false;
		return true;
	}
}
