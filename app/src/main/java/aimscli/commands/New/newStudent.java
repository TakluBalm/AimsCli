package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Auth;
import aimscli.dataObjects.Student;
import aimscli.pgManager.AuthTable;
import aimscli.pgManager.StudentTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name="student",
	description = "Add a new student into the database",
	mixinStandardHelpOptions = true
)
public class newStudent extends SubCmd{
	
	@Option(names={"--uid", "--user"}, arity="1", description = "User ID")	String user_id;
	@Option(names={"-d", "--dept"}, arity="1", description = "Department")		String dept;
	@Option(names={"-b", "--batch"}, arity="1", defaultValue = "Batch")		String batch;
	@Option(names={"-p","--prog"}, arity="1", description = "Programme")		String programme;
	@Option(names={"-n", "--name"}, arity="1", description = "Name")		String arg_name;
	
	@Override
	public Integer call() throws Exception{

		Student student = new Student();
		Auth auth = new Auth();

		String[] name = new String[2];

		if(user_id == null || !validUid(user_id))
			user_id = fetchLine("UID: ", "Invalid Input", this::validUid);
		if(dept == null)
			dept = fetchLine("Department: ");
		if(batch == null || !validBatch(batch))
			batch = fetchLine("Batch: ", "Invalid Input", this::validBatch);
		if(programme == null)
			programme = fetchLine("Programme: ");
		if(arg_name == null || !validName(arg_name))
			name = fetchName("Name: ", "Invalid Input");
		else{
			String[] temp = arg_name.split(" ",2);
			name[0] = temp[0];
			if(temp.length == 2){
				name[1] = temp[1];
			}
		}

		auth.user_id = user_id;
		auth.dept = dept;
		auth.passwd = String.format("%s_iitropar", name[0].toLowerCase());
		auth.role = 'S';
		
		student.auth_id = user_id;
		student.batch = batch;
		student.programme = programme;
		student.name = name[0];
		student.surname = name[1];
		
		try{
			AuthTable at = new AuthTable();
			StudentTable st = new StudentTable();
			at.insert(user, auth);
			st.insert(user, student);
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
