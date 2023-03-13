package aimscli.dataObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Faculty implements DataObject{
	public String auth_id, name, surname;
	
	public Faculty(){}

	private int getCnt(){
		int cnt = 0;

		if(auth_id != null)	cnt++;
		if(name != null)	cnt++;
		if(surname != null)	cnt++;

		return cnt;
	}

	@Override
	public String[] getVals() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] vals = new String[cnt];

		int k = 0;
		if(auth_id != null)	vals[k++] = "'" + auth_id + "'";
		if(name != null)	vals[k++] = "'" + name + "'";
		if(surname != null)	vals[k++] = "'" + surname + "'";
		
		return vals;
	}

	@Override
	public DataObject parse(ResultSet res) throws Exception {
		try{
			Faculty f = new Faculty();
			f.auth_id = res.getString("auth_id");
			f.name = res.getString("name");
			f.surname = res.getString("surname");
		}catch (SQLException e){
			throw new Exception("Invalid type");
		}

		return null;
	}

	@Override
	public boolean isValid() {
		if(auth_id == null || name == null)	return false;
		return true;
	}

	@Override
	public String[] primaryVals() {
		String[] pattr = new String[1];
		pattr[0] = String.format("auth_id='%s'", auth_id);
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
		if(surname != null)	attr[k++] = "surname";
		
		return attr;
	}
}
