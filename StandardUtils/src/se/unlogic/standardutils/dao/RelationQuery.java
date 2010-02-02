package se.unlogic.standardutils.dao;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RelationQuery {

	private List<Field> relations;
	
	public RelationQuery() {}
	
	public RelationQuery(List<Field> relations) {

		this.relations = relations;
	}

	public RelationQuery(Field... relations) {

		this.addRelations(relations);
	}
	
	public RelationQuery(RelationQuery relationQuery) {

		this.addRelations(relationQuery);
	}	
	
	public List<Field> getRelations() {

		return relations;
	}

//	public Field[] getRelationsArray() {
//
//		if(this.relations == null || this.relations.isEmpty()){
//			
//			return null;
//		}
//		
//		return this.relations.toArray(new Field[relations.size()]);
//	}	
	
	public void setRelations(List<Field> relations) {

		this.relations = relations;
	}
	
	public synchronized void addRelation(Field relation){
		
		if(this.relations == null){
			
			this.relations = new ArrayList<Field>();
		}
		
		this.relations.add(relation);
	}
	
	public synchronized void addRelations(Field... relations){
		
		if(this.relations == null){
			
			this.relations = new ArrayList<Field>();
		}
		
		this.relations.addAll(Arrays.asList(relations));
	}
	
	public static boolean hasRelations(RelationQuery query){
		
		if(query == null || query.getRelations() == null || query.getRelations().isEmpty()){
			return false;
		}
		
		return true;
	}
	
	public void addRelations(RelationQuery relationQuery) {

		if(hasRelations(relationQuery)){
			
			this.addRelations(relationQuery.getRelations());
		}
	}
	
	public void addRelations(List<Field> relations){
		
		if(this.relations == null){
			
			this.relations = relations;
			
		}else{
			
			this.relations.addAll(relations);
		}		
	}
}
