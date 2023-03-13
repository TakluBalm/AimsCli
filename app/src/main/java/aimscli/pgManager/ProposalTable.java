package aimscli.pgManager;

import aimscli.dataObjects.Course;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class ProposalTable extends Table{

	public ProposalTable(){
		super("proposal");
	}

	@Override
	public boolean insertPrivilege(User u) {
		if(u.privilege == Privilege.STU)	return false;
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
	
	public void insert(User u, Course p) throws Exception{
		super.insert(u, p);
	}

	public void update(User u, Course old, Course updates) throws Exception{
		if(u.privilege == Privilege.FAC){
			old.dept = u.dept;
			updates.dept = u.dept;
		}
		super.update(u, old, updates);
	}

	public ResultSet query(User u, Course reference) throws Exception{
		if(u.privilege == Privilege.FAC){
			reference.dept = u.dept;
		}
		return super.query(u, reference);
	}

	public void delete(User u, Course p) throws Exception{
		if(u.privilege == Privilege.FAC){
			if(!u.dept.equals(p.dept))	throw new Exception("Permission Denied");
		}
		super.delete(u, p);
	}
}
