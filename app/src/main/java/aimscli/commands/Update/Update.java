package aimscli.commands.Update;

import aimscli.commands.Base.Cmd;
import aimscli.pgManager.pgManager.User;
import picocli.CommandLine.Command;

@Command(
	name = "update",
	description = "Update values in database",
	subcommands = {
		updatePasswd.class,
		updateProposal.class
	},
	mixinStandardHelpOptions = true
)
public class Update extends Cmd{

	User u;

	public Update(User user){
		super(user);
		u = user;
	}

	@Override
	public Cmd clone() {
		return new Update(u);
	}

	@Override
	public Integer call() throws Exception {
		return 0;
	}
		
}
