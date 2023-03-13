package aimscli.pgManager;

import aimscli.dataObjects.DBP;
import aimscli.dataObjects.DBP.Type;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class DBPTable extends Table{

	public DBPTable(Type t){
		super(t.toString());
	}

	@Override
	public boolean insertPrivilege(User u) {
		if(u.privilege != Privilege.ADM)	return false;
		return true;
	}

	@Override
	public boolean updatePrivilege(User u) {
		return insertPrivilege(u);
	}

	@Override
	public boolean queryPrivilege(User u) {
		return insertPrivilege(u);
	}

	@Override
	public boolean deletePrivilege(User u) {
		return insertPrivilege(u);
	}
	
	public void insert(User u, DBP d) throws Exception{
		super.insert(u, d);
	}

	public void update(User u, DBP old, DBP updated) throws Exception{
		super.update(u, old, updated);
	}

	public ResultSet query(User u, DBP reference) throws Exception {
		return super.query(u, reference);
	}
}
