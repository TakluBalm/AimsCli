package aimscli.commands.Curriculum;

import aimscli.commands.Base.Cmd;
import aimscli.pgManager.pgManager.User;
import picocli.CommandLine.Command;

@Command(
	name = "curriculum",
	description = "Manage Curriculums",
	mixinStandardHelpOptions = true,
	subcommands = {
		checkCurriculum.class,
		addCurriculum.class,
		updateCurriculum.class
	}
)
public class CurriculumCmd extends Cmd{

	User u;

	public CurriculumCmd(User user){
		super(user);
		u = user;
	}

	@Override
	public Cmd clone() {
		return new CurriculumCmd(u);
	}

	@Override
	public Integer call() throws Exception {
		return 0;
	}
	
}
