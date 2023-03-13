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
		name = "add",
		mixinStandardHelpOptions = true,
		description = "Add a new curriculum"
)
public class addCurriculum extends SubCmd{
	
	@Option(names={"-b", "--batch"}, description = "Batch which will follow the curriculum")
		String batch;
	@Option(names={"-p", "--programme"}, description = "Programme which will follow the curriculum")
		String programme;
	@Option(names={"--pc"}, description = "Programme Core credit requirement")
		Integer pc;
	@Option(names={"--pe"}, description = "Programme Elective credit requirement")
		Integer pe;
	@Option(names={"--cp"}, description = "Capstone Project credit requirement")
		Integer cp;

	@Override
	public Integer call() throws Exception {
		Curriculum c = new Curriculum();

		if(batch == null || !validBatch(batch)){
			batch = fetchLine("Batch: ", "Invalid Batch Year", this::validBatch);
		}
		c.batch = batch;

		if(programme == null || !validDept(programme)){
			programme = fetchLine("Programme: ", "Invalid Programme name", this::validDept);
		}
		c.programme = programme;

		if(pc == null){
			pc = Integer.parseInt(fetchLine("Programme Core (min credits): ", "Invalid number", this::validNum));
		}
		c.pc = pc;

		if(pe == null){
			pe = Integer.parseInt(fetchLine("Programme Elective (min credits): ", "Invalid number", this::validNum));
		}
		c.pe = pe;

		if(cp == null){
			cp = Integer.parseInt(fetchLine("Capstone (min credits): ", "Invalid number", this::validNum));
		}
		c.cp = cp;

		try{
			CurriculumTable ct = new CurriculumTable();
			ct.insert(user, c);
			pgManager.commit();

			return 0;
		}catch(Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}
	}
	
}
