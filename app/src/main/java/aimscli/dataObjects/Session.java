package aimscli.dataObjects;

import java.sql.ResultSet;

public class Session implements DataObject{

	public Integer id;
	public String year;
	public Character sem;
	public Boolean current;

	@Override
	public String[] getAttr() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getAttr'");
	}

	@Override
	public String[] getVals() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'getVals'");
	}

	@Override
	public String[] primaryVals() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'primaryVals'");
	}

	@Override
	public DataObject parse(ResultSet res) throws Exception {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'parse'");
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'isValid'");
	}
	
}
