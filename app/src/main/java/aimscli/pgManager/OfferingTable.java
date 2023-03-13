package aimscli.pgManager;

import aimscli.dataObjects.Offering;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class OfferingTable extends Table{

	public OfferingTable(){
		super("offerings");
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
		return true;
	}

	@Override
	public boolean deletePrivilege(User u) {
		return insertPrivilege(u);
	}

	public void insert(User u, Offering d) throws Exception{
		super.insert(u, d);
	}

	public void update(User u, Offering old, Offering updated) throws Exception{
		super.update(u, old, updated);
	}
	
	public ResultSet query(User u, Offering reference) throws Exception{
		if(u.privilege == Privilege.FAC){
			reference.instructor_id = u.user_id;
		}
		return super.query(u, reference);
	}
}
