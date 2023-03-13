package aimscli.pgManager;

import aimscli.dataObjects.DataObject;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;
import java.sql.Statement;

public abstract class Table{

	String name;
	String select;

	final Statement stmt = pgManager.getStmt();

	protected Table(String name){
		this.name = name;
		select = "*";
	}

	public abstract boolean insertPrivilege(User u);
	public abstract boolean updatePrivilege(User u);
	public abstract boolean queryPrivilege(User u);
	public abstract boolean deletePrivilege(User u);

	protected void insert(User u, DataObject data) throws Exception{
		if(!insertPrivilege(u))	throw new Exception("Permission Denied");

		if(!data.isValid())	throw new Exception("Invalid Data");

		String query = "INSERT INTO %s(%s) VALUES(%s);";

		String attr = concat(data.getAttr(), ", ");
		String vals = concat(data.getVals(), ", ");

		query = String.format(query, name, attr, vals);
		stmt.execute(query);
	}

	protected void update(User u, DataObject old, DataObject updates) throws Exception{
		if(!updatePrivilege(u))	throw new Exception("Permission Denied");

		String[] pattr = old.primaryVals();
		String where = concat(pattr, " and ");

		String set = cmpForm(updates.getAttr(), updates.getVals(), ", ");

		String query = String.format("UPDATE %s SET %s WHERE %s;", name, set, where);
		stmt.execute(query);
	}

	private ResultSet query(User u) throws Exception{
		return stmt.executeQuery(String.format("SELECT %s FROM %s;",select, name));
	}

	protected ResultSet query(User u, DataObject reference) throws Exception{
		if(!queryPrivilege(u))	throw new Exception("Permission Denied");

		if(reference == null)	return query(u);

		String where = cmpForm(reference.getAttr(), reference.getVals(), " and ");

		if(where == null)	return query(u);

		String query = String.format("SELECT %s FROM %s WHERE %s;", select, name, where);
		return stmt.executeQuery(query);
	}

	protected boolean delete(User u, DataObject d) throws Exception{
		String[] pattr = d.primaryVals();
		if(pattr == null)	throw new Exception("Cannot Delete non-unique Object");

		ResultSet r = query(u, d);
		if(r.next()){
			String query = "DELETE FROM %s WHERE %s;";
			String where = concat(pattr, " and ");

			query = String.format(query, name, where);
			stmt.execute(query);
			return true;
		}else{
			return false;
		}
	}

	protected ResultSet dynamicQuery(User u, String where) throws Exception{
		if(!queryPrivilege(u))	throw new Exception("Permission Denied");
		return stmt.executeQuery(String.format("SELECT %s FROM %s WHERE %s;", select, name, where));
	}

	private String cmpForm(String[] attr, String[] vals, String seperator){
		if(attr == null || vals == null)	return null;
		if(attr.length != vals.length)		return null;
		if(attr.length == 0)				return null;

		String ans = String.format("%s=%s", attr[0], vals[0]);
		for(int i = 1; i < attr.length; i++){
			ans += seperator + String.format("%s=%s", attr[i], vals[i]);
		}
		return ans;
	}

	private String concat(String[] words, String seperator){
		if(words == null || words.length == 0)	return null;
		String ans = words[0];
		for(int i = 1; i < words.length; i++){
			ans += seperator + words[i];
		}
		return ans;
	}
}
