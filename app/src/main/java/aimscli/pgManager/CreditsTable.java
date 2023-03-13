package aimscli.pgManager;

import aimscli.dataObjects.Credits;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class CreditsTable extends Table{

	public CreditsTable(){
		super("credits");
	}

	@Override
	public boolean insertPrivilege(User u) {
		if(u.privilege == Privilege.ADM)	return true;
		return false;
	}

	@Override
	public boolean updatePrivilege(User u) {
		if(u.privilege == Privilege.FAC)	return false;
		return true;
	}

	@Override
	public boolean queryPrivilege(User u) {
		return true;
	}

	@Override
	public boolean deletePrivilege(User u) {
		return insertPrivilege(u);
	}

	public void insert(User u, Credits d) throws Exception{
		super.insert(u, d);
	}

	public void update(User u, Credits row, Credits updates) throws Exception{
		if(u.privilege == Privilege.STU){
			updates.student_id = null;
			row.student_id = u.user_id;
		}
		super.update(u, row, updates);
	}

	public ResultSet query(User u, Credits reference) throws Exception{
		if(u.privilege == Privilege.STU){
			reference.student_id = u.user_id;
		}
		return super.query(u, reference);
	}
}
