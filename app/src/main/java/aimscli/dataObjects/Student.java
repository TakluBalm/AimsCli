package aimscli.dataObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Student implements DataObject{
	public String auth_id, name, surname, batch, programme;

	public Student(){}

	private int getCnt(){
		int cnt = 0;

		if(auth_id != null)	cnt++;
		if(name != null)	cnt++;
		if(surname != null)	cnt++;
		if(batch != null)	cnt++;
		if(programme != null)	cnt++;

		return cnt;
	}

	@Override
	public String[] getVals() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] vals = new String[cnt];
		
		int k = 0;
		if(auth_id != null)	vals[k++] = String.format("'%s'", auth_id);
		if(name != null)	vals[k++] = String.format("'%s'", name);
		if(batch != null)	vals[k++] = String.format("'%s'", batch);
		if(programme != null)	vals[k++] = String.format("'%s'", programme);
		if(surname != null)	vals[k++] = "'" + surname + "'";

		return vals;
	}

	@Override
	public Student parse(ResultSet res) throws Exception {
		try{
			Student s = new Student();
			
			s.auth_id = res.getString("auth_id");
			s.name = res.getString("name");
			s.surname = res.getString("surname");
			s.batch = res.getString("batch");
			s.programme = res.getString("programme");

			return s;
		}catch(SQLException e){
			throw new Exception("Invalid type");
		}
	}

	@Override
	public boolean isValid() {
		if(auth_id == null || name == null || batch == null || programme == null)	return false;
		return true;
	}

	@Override
	public String[] primaryVals() {
		String[] pattr = new String[1];
		pattr[0] = "auth_id";
		return pattr;
	}

	@Override
	public String[] getAttr() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] attr = new String[cnt];
		
		int k = 0;
		if(auth_id != null)	attr[k++] = "auth_id";
		if(name != null)	attr[k++] = "name";
		if(batch != null)	attr[k++] = "batch";
		if(programme != null)	attr[k++] = "programme";
		if(surname != null)	attr[k++] = "surname";

		return attr;
	}
}
