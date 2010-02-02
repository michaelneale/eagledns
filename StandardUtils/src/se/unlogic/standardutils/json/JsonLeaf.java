package se.unlogic.standardutils.json;

public class JsonLeaf implements JsonNode {

	private String value;
	
	public JsonLeaf(String value) {
		this.value = value;
	}

	public String toJson(StringBuilder stringBuilder) {
		if(value == null) {
			return stringBuilder.append("null").toString();
		}
		if(value.contains("'")) {
			value = value.replaceAll("'", "\"");
		}
		stringBuilder.append("'");
		stringBuilder.append(value);
		return stringBuilder.append("'").toString();
	}

}
