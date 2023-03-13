package aimscli.commands.Curriculum;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.Curriculum;
import aimscli.pgManager.CurriculumTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine;
import picocli.CommandLine.Option;

@CommandLine.Command(
		name = "update",
		description = "Update a curriculum",
		mixinStandardHelpOptions = true
)
public class updateCurriculum extends SubCmd{

	@Option(names={"-b", "--batch"}, description = "Batch which will follow the curriclum")
		String batch;
	@Option(names={"-p", "--programme"}, description = "Programme which will follow the curriclum")
		String programme;
	@Option(names={"--pc"}, description = "Programme Core credit requirement")
		Integer pc;
	@Option(names={"--pe"}, description = "Programme Elective credit requirement")
		Integer pe;
	@Option(names={"--cp"}, description = "Capstone Project credit requirement")
		Integer cp;

	@Override
	public Integer call() throws Exception {
		Curriculum orig = new Curriculum();

		if(batch == null || !validBatch(batch)){
			batch = fetchLine("Batch: ", "Invalid Batch Year", this::validBatch);
		}
		orig.batch = batch;

		if(programme == null || !validDept(programme)){
			programme = fetchLine("Programme: ", "Invalid Programme name", this::validDept);
		}
		orig.programme = programme;

		Curriculum updates = new Curriculum();
		if(pc == null){
			pc = Integer.parseInt(fetchLine("Programme Credits(PC): ", "Invalid Number", this::validNum));
		}
		updates.pc = pc;
		if(pe == null){
			pe = Integer.parseInt(fetchLine("Programme Electives(PE): ", "Invalid Number", this::validNum));
		}
		updates.pe = pe;
		if(cp == null){
			cp = Integer.parseInt(fetchLine("Capstone Projects(CP): ", "Invalid Number", this::validNum));
		}
		updates.cp = cp;

		try{
			CurriculumTable ct = new CurriculumTable();
			ct.update(user, orig, updates);
			pgManager.commit();
			return 0;

		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
