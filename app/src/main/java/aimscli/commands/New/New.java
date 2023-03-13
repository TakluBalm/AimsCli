package aimscli.commands.New;

import aimscli.commands.Base.Cmd;
import aimscli.pgManager.pgManager.User;
import picocli.CommandLine.Command;

@Command(
	name = "new",
	subcommands = {
		newStudent.class,
		newFaculty.class,
		newAdmin.class,
		newDBP.class,
		newProposal.class,
		newApproval.class,
		newOffering.class,
		newEnrollment.class,
		newRejection.class
	}
)
public class New extends Cmd{

	User u;

	public New(User user){
		super(user);
		u = user;
	}

	@Override
	public Cmd clone() {
		return new New(u);
	}

	@Override
	public Integer call() throws Exception {
		return null;
	}
	
}