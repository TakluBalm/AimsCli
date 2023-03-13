package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Auth;
import aimscli.pgManager.AuthTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
	name = "admin",
	description = "Add a new admin into the database",
	mixinStandardHelpOptions = true
)
public class newAdmin extends SubCmd{
	@Option(names={"--uid", "--user"}, arity="1")	String user_id;

	@Override
	public Integer call() throws Exception {
		Auth a = new Auth();

		if(user_id == null){
			user_id = fetchLine("User ID: ", "Invalid Input", this::validUid);
		}
		a.user_id = user_id;
		a.passwd = String.format("%s_iitropar", user_id);
		a.role = 'A';

		try{
			AuthTable at = new AuthTable();
			at.insert(user, a);
			pgManager.commit();
		}catch(Exception e){
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}

		return 0;
	}
	
}
