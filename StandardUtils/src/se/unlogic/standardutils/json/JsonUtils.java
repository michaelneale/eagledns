package se.unlogic.standardutils.json;

import java.util.Collection;

import se.unlogic.standardutils.validation.ValidationError;

public class JsonUtils {
	
	public static JsonNode encode(Collection<ValidationError> validationErrors) {
		JsonArray jsonArray = new JsonArray();
		JsonObject jsonObject;
		for(ValidationError error : validationErrors) {
			jsonObject = new JsonObject();
			jsonObject.putField("field", new JsonLeaf(error.getFieldName()));
			jsonObject.putField("errorType", new JsonLeaf(error.getValidationErrorType().toString()));
			jsonObject.putField("messageKey", new JsonLeaf(error.getMessageKey()));
			jsonArray.addNode(jsonObject);
		}
		return jsonArray;
	}
}
