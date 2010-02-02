package se.unlogic.standardutils.xml;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.unlogic.standardutils.annotations.NoAnnotatedFieldsFoundException;
import se.unlogic.standardutils.date.DateUtils;
import se.unlogic.standardutils.reflection.ReflectionUtils;
import se.unlogic.standardutils.string.StringUtils;

public class XMLGenerator {

	private static ConcurrentHashMap<Class<?>, ClassXMLInfo> fieldMap = new ConcurrentHashMap<Class<?>, ClassXMLInfo>();

	@SuppressWarnings("unchecked")
	public static Element toXML(Object bean, Document doc){

		ClassXMLInfo classInfo = fieldMap.get(bean.getClass());

		if(classInfo == null){

			Class<?> clazz = bean.getClass();

			XMLElement xmlElement = clazz.getAnnotation(XMLElement.class);

			if(xmlElement == null){

				throw new MissingXMLAnnotationException(clazz);
			}

			String elementName = xmlElement.name();

			if(StringUtils.isEmpty(elementName)){

				elementName = clazz.getSimpleName();
			}

			List<FieldXMLInfo> annotatedFields = new ArrayList<FieldXMLInfo>();

			Class<?> currentClazz = clazz;

			while (currentClazz != Object.class) {

				Field[] fields = clazz.getDeclaredFields();

				for(Field field : fields){

					XMLElement elementAnnotation = field.getAnnotation(XMLElement.class);

					if(elementAnnotation != null){

						String name = elementAnnotation.name();

						if(StringUtils.isEmpty(name)){

							name = field.getName();
						}

						if(Collection.class.isAssignableFrom(field.getType())){

							boolean elementable = false;

							if(ReflectionUtils.isGenericlyTyped(field) && Elementable.class.isAssignableFrom((Class<?>) ReflectionUtils.getGenericType(field))){

								elementable = true;
							}

							String childName = elementAnnotation.childName();

							if(StringUtils.isEmpty(childName)){

								childName = "value";
							}

							annotatedFields.add(new FieldXMLInfo(name, field, FieldType.Element,elementAnnotation.cdata(),elementable,true,childName));

						}else{

							String childName = null;

							if(!StringUtils.isEmpty(elementAnnotation.childName())){

								childName = elementAnnotation.childName();
							}

							annotatedFields.add(new FieldXMLInfo(name, field, FieldType.Element,elementAnnotation.cdata(),Elementable.class.isAssignableFrom(field.getType()),false,childName));
						}

						ReflectionUtils.fixFieldAccess(field);
					}

					XMLAttribute attributeAnnotation = field.getAnnotation(XMLAttribute.class);

					if(attributeAnnotation != null){

						String name = attributeAnnotation.name();

						if(StringUtils.isEmpty(name)){

							name = field.getName();
						}

						annotatedFields.add(new FieldXMLInfo(name, field, FieldType.Attribute,false,false,false,null));

						ReflectionUtils.fixFieldAccess(field);
					}
				}

				currentClazz = currentClazz.getSuperclass();
			}

			if(annotatedFields.isEmpty()){

				throw new NoAnnotatedFieldsFoundException(clazz,XMLElement.class,XMLAttribute.class);
			}

			classInfo = new ClassXMLInfo(elementName, annotatedFields);

			fieldMap.put(clazz, classInfo);
		}

		Element classElement = doc.createElement(classInfo.getElementName());

		for(FieldXMLInfo fieldInfo : classInfo.getFields()){

			Object fieldValue;
			try {
				fieldValue = fieldInfo.getField().get(bean);
			} catch (IllegalArgumentException e) {

				throw new RuntimeException(e);

			} catch (IllegalAccessException e) {

				throw new RuntimeException(e);
			}

			if(fieldValue == null){

				continue;

			}else if(fieldValue instanceof Date){

				fieldValue = DateUtils.dateTimeFormatter.format(fieldValue);
			}

			if(fieldInfo.getFieldType() == FieldType.Attribute){

				Attr attribute = doc.createAttribute(fieldInfo.getName());

				attribute.setValue(fieldValue.toString());

				classElement.appendChild(attribute);

			}else if(fieldInfo.isList()){

				List fieldValues = (List)fieldValue;

				if(fieldValues.isEmpty()){

					continue;
				}

				Element subElement = doc.createElement(fieldInfo.getName());


				for(Object value : fieldValues){

					if(value != null){
						if(fieldInfo.isElementable()){

							Element subSubElement = ((Elementable)value).toXML(doc);

							if(subSubElement != null){

								subElement.appendChild(subSubElement);
							}
						}else{

							if(fieldInfo.isCDATA()){

								subElement.appendChild(XMLUtils.createCDATAElement(fieldInfo.getChildName(), value, doc));

							}else{

								subElement.appendChild(XMLUtils.createElement(fieldInfo.getChildName(), value, doc));
							}
						}
					}
				}

				if(subElement.hasChildNodes()){
					classElement.appendChild(subElement);
				}

			}else if(fieldInfo.isElementable()){

				Element subElement = ((Elementable)fieldValue).toXML(doc);

				if(subElement != null){

					if(fieldInfo.getChildName() != null){

						Element middleElement = doc.createElement(fieldInfo.getChildName());
						classElement.appendChild(middleElement);
						middleElement.appendChild(subElement);

					}else{
						classElement.appendChild(subElement);
					}
				}
			}else if(fieldInfo.isCDATA()){

				classElement.appendChild(XMLUtils.createCDATAElement(fieldInfo.getName(), fieldValue.toString(), doc));

			}else{
				classElement.appendChild(XMLUtils.createElement(fieldInfo.getName(), fieldValue.toString(), doc));
			}
		}

		return classElement;
	}
}
