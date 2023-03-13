package aimscli.pgManager;

import aimscli.dataObjects.Auth;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class AuthTable extends Table{

	public AuthTable(){
		super("auth");
	}

	@Override
	public boolean insertPrivilege(User u) {
		if(u.privilege != Privilege.ADM)	return false;
		return true;
	}

	@Override
	public boolean updatePrivilege(User u) {
		return true;
	}

	@Override
	public boolean queryPrivilege(User u) {
		if(u.privilege != Privilege.ADM)	return false;
		return true;
	}

	@Override
	public boolean deletePrivilege(User u) {
		return insertPrivilege(u);
	}

	public void insert(User u, Auth a) throws Exception{
		super.insert(u, a);
	}

	public void update(User u, Auth old, Auth updates) throws Exception{
		if(u.privilege != Privilege.ADM){
			old.user_id = u.user_id;
			updates.user_id = updates.dept = null;
			updates.role = null;
		}
		super.update(u, old, updates);
	}

	public ResultSet query(User u, Auth reference) throws Exception{
		return super.query(u, reference);
	}
	
}
