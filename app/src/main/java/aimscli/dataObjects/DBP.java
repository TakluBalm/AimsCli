package aimscli.dataObjects;

import java.sql.ResultSet;

public class DBP implements DataObject{

	public String name;

	public enum Type{
		dept,
		batch,
		programme;

		@Override
		public String toString() {
			switch(this){
				case dept:
					return "dept";
				case batch:
					return "batch";
				case programme:
					return "programme";
				default:
					return null;
			}
		}
	}

	public DBP(){}

	private int getCnt(){
		if(name == null)	return 0;
		return 1;
	}

	@Override
	public String[] getAttr(){
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] attr = new String[cnt];

		int k = 0;
		if(name != null)	attr[k] = "name";

		return attr;
	}

	@Override
	public String[] getVals(){
		int cnt = getCnt();
		if(cnt == 0)	return null;
		String[] vals = new String[cnt];

		int k = 0;
		if(name != null)	vals[k] = String.format("'%s'", name);

		return vals;
	}

	@Override
	public String[] primaryVals(){
		if(name == null)	return null;

		String[] pattr = new String[1];
		pattr[0] = String.format("name='%s'", name);

		return pattr;
	}

	@Override
	public DBP parse(ResultSet res) throws Exception{
		DBP d = new DBP();
		
		d.name = res.getString("name");

		return d;
	}

	@Override
	public boolean isValid(){
		if(name != null)	return true;
		return false;
	}
	
}
