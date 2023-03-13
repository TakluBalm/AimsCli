package aimscli.commands.Update;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Auth;
import aimscli.pgManager.AuthTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;

@Command(
	name = "passwd",
	description = "Update the password of the user",
	mixinStandardHelpOptions = true
)
public class updatePasswd extends SubCmd{

	@Override
	public Integer call() throws Exception {

		prompt("Current Password: ");
		String passwd = new String(Login.cons.readPassword());

		try{
			pgManager.authenticate(user.user_id, passwd);
		}catch(Exception e){
			Login.err.println(e.getMessage());
			return 1;
		}

		while(true){
			prompt("New Password: ");
			String passwd1 = new String(Login.cons.readPassword());
			prompt("Repeat Password: ");
			String passwd2 = new String(Login.cons.readPassword());

			if(passwd1.equals(passwd2)){
				passwd = passwd1;
				break;
			}
		}

		try{
			AuthTable at = new AuthTable();

			Auth orig = new Auth();
			orig.user_id = user.user_id;

			Auth updated = new Auth();
			updated.passwd = passwd;

			at.update(user, orig, updated);
			pgManager.commit();
		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
		}

		return 0;
	}
	
}
