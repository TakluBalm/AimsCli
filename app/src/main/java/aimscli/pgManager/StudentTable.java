package aimscli.pgManager;

import aimscli.dataObjects.Student;
import aimscli.pgManager.pgManager.Privilege;
import aimscli.pgManager.pgManager.User;

import java.sql.ResultSet;

public class StudentTable extends Table{

	public StudentTable(){
		super("student");
	}

	@Override
	public boolean insertPrivilege(User u) {
		if(u.privilege != Privilege.ADM)	return false;
		return true;
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

	public void insert(User u, Student stu) throws Exception{
		super.insert(u, stu);
	}

	public void update(User u, Student old, Student updated) throws Exception{
		if(u.privilege == Privilege.STU){
			if(!old.auth_id.equals(u.user_id) || !old.auth_id.equals(updated.auth_id)){
				throw new Exception("Permission Denied");
			}
		}
		super.update(u, old, updated);
	}

	public ResultSet query(User u, Student reference) throws Exception{
		return super.query(u, reference);
	}
}
