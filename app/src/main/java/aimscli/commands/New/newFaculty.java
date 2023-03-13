package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Auth;
import aimscli.dataObjects.Faculty;
import aimscli.pgManager.AuthTable;
import aimscli.pgManager.FacultyTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name = "faculty",
	description = "Add a new Faculty member into the database",
	mixinStandardHelpOptions = true
)
public class newFaculty extends SubCmd{

	@Option(names={"--uid", "--user"}, description = "User ID")	String user_id;
	@Option(names={"-d", "--dept"}, description = "Department")		String dept;
	@Option(names={"-n", "--name"}, description = "Name")		String arg_name;

	@Override
	public Integer call() throws Exception {

		Faculty faculty = new Faculty();
		Auth auth = new Auth();

		String[] name = new String[2];

		if(user_id == null || !validUid(user_id))
			user_id = fetchLine("UID: ", "Invalid Input", this::validUid);
		if(dept == null)
			dept = fetchLine("Department: ");
		if(arg_name == null || !validName(arg_name)){
			name = fetchName("Name: ", "Invalid Name");
		}else{
			String[] temp = arg_name.split(" ",2);
			name[0] = temp[0];
			if(temp.length == 2){
				name[1] = temp[1];
			}
		}
			
		auth.user_id = user_id;
		auth.dept = dept;
		auth.passwd = String.format("%s_iitropar", name[0].toLowerCase());
		auth.role = 'F';
		
		faculty.auth_id = user_id;
		faculty.name = name[0];
		faculty.surname = name[1];
		
		try{
			AuthTable at = new AuthTable();
			FacultyTable ft = new FacultyTable();
			at.insert(user, auth);
			ft.insert(user, faculty);
			pgManager.commit();
		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}

		return 0;
	}

	String[] fetchName(String p, String errmsg){
		String[] in = new String[2];

		String tmp = fetchLine(p, errmsg, this::validName);
		int i = 0;
		while(i < tmp.length() && tmp.charAt(i) != ' ')
			i++;
		
		if(i < tmp.length()){
			in[0] = tmp.substring(0, i).strip();
			in[1] = tmp.substring(i+1).strip();
			if(in[1].equals(""))
				in[1] = null;
		}else{
			in[0] = tmp;
		}

		return in;
	}
}
