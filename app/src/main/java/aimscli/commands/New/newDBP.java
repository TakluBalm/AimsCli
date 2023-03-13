package aimscli.commands.New;

import aimscli.commands.Base.SubCmd;
import aimscli.commands.Login;
import aimscli.dataObjects.DBP;
import aimscli.dataObjects.DBP.Type;
import aimscli.pgManager.DBPTable;
import aimscli.pgManager.pgManager;
import aimscli.viewManager.ansi;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


@Command(
	name="dbp",
	description = "Add a new department/Batch/Programme to database",
	mixinStandardHelpOptions = true
)
public class newDBP extends SubCmd{

	@Option(names = {"-n", "--name"}, description = "Name for the new object of specified type")
		String name;
	@Option(names = {"-t", "--table"}, description = "Specify the table", required = true)
		String table;
	@Option(names = {"-r", "--remove"}, description = "Remove the mentioned department", arity="0")
		boolean rm;

	@Override
	public Integer call() throws Exception{

		if(!table.equals("dept") && !table.equals("batch") && !table.equals("programme")){
			table = fetchLine("What do you want to add (dept/batch/programme)? ", "Invalid Option", this::validDBP);
		}

		Type t = Type.programme;
		if(table.equals("dept"))	t = Type.dept;
		if(table.equals("batch"))	t = Type.batch;

		switch(t){
			case dept:{
				if(name == null || !validDept(name)){
					name = fetchLine("Department Name: ", "Invalid Department Name", this::validDept);
				}
				break;
			}
			case batch:{
				if(name == null || !validBatch(name)){
					name = fetchLine("Batch Name: ", "Invalid Batch", this::validBatch);
				}
				break;
			}
			case programme:{
				if(name == null || !validDept(name)){
					name = fetchLine("Programme Name: ", "Invalid Programme name", this::validDept);
				}
				break;
			}
		}

		DBPTable dt = new DBPTable(t);
		DBP d = new DBP();

		d.name = name;

		try{
			dt.insert(user, d);
			pgManager.commit();
		}catch (Exception e){
			pgManager.rollback();
			Login.err.println(ansi.Err(e.getMessage()));
			return 1;
		}

		return 0;
	}

	boolean validDBP(String in){
		if(in.equalsIgnoreCase("dept") || in.equalsIgnoreCase("batch") || in.equalsIgnoreCase("programme"))	return true;
		return false;
	}
}
