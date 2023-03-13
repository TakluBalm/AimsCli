package aimscli.commands.Fetch;

import aimscli.commands.Base.Cmd;
import aimscli.pgManager.pgManager.User;
import picocli.CommandLine.Command;

@Command(
	name = "fetch",
	description = "Fetch information from database",
	mixinStandardHelpOptions = true,
	subcommands = {
		fetchProposals.class,
		fetchCatalog.class,
		fetchDescription.class,
		fetchOfferings.class,
		fetchEnrollments.class,
		fetchGrade.class,
		fetchCGPA.class,
		fetchTranscript.class
	}
)
public class Fetch extends Cmd{

	User u;

	public Fetch(User user){
		super(user);
		this.u = user;
	}

	@Override
	public Cmd clone() {
		return new Fetch(u);
	}

	@Override
	public Integer call() throws Exception {
		return 0;
	}
		
}
