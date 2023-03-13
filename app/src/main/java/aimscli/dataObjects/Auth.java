package aimscli.dataObjects;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Auth implements DataObject{

	public String user_id, passwd, dept;
	public Character role;

	public Auth(){}

	@Override
	public String[] getVals() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] vals = new String[cnt];

		int k = 0;
		if(user_id != null)	vals[k++] = String.format("'%s'", user_id);
		if(passwd != null)	vals[k++] = String.format("'%s'", passwd);
		if(role != null)	vals[k++] = String.format("'%c'", role);
		if(dept != null)	vals[k] = String.format("'%s'", dept);

		return vals;
	}

	@Override
	public DataObject parse(ResultSet res) throws Exception {
		try{
			Auth a = new Auth();

			a.user_id = res.getString("user_id");
			a.passwd = res.getString("passwd");
			a.role = res.getString("role").charAt(0);
			a.dept = res.getString("dept");

			return a;
		}catch (SQLException e){
			throw new Exception("Invalid type");
		}
	}

	@Override
	public boolean isValid() {
		if(user_id == null || passwd == null || role == null)	return false;
		return true;
	}

	@Override
	public String[] primaryVals() {
		if(user_id == null)	return null;

		String[] pattr = new String[1];
		pattr[0] = String.format("user_id='%s'", user_id);
		return pattr;
	}

	@Override
	public String[] getAttr() {
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] attr = new String[cnt];

		int k = 0;
		if(user_id != null)	attr[k++] = "user_id";
		if(passwd != null)	attr[k++] = "passwd";
		if(role != null)	attr[k++] = "role";
		if(dept != null)	attr[k] = "dept";

		return attr;
	}

	private int getCnt(){
		int cnt = 0;
		if(user_id != null)	cnt++;
		if(passwd != null)	cnt++;
		if(role != null)	cnt++;
		if(dept != null)	cnt++;
		return cnt;
	}
	
}
