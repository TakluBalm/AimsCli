package aimscli.dataObjects;

import java.sql.ResultSet;

public interface DataObject {
	public String[] getAttr();
	public String[] getVals();
	public String[] primaryVals();
	public DataObject parse(ResultSet res) throws Exception;
	public boolean isValid();
}
