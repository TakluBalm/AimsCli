package aimscli;

import aimscli.commands.Curriculum.CurriculumCmd;
import aimscli.commands.Fetch.Fetch;
import aimscli.commands.New.New;
import aimscli.commands.Session;
import aimscli.commands.Update.Update;
import aimscli.commands.UploadGrades;
import aimscli.pgManager.pgManager;
import org.junit.jupiter.api.*;
import picocli.CommandLine;

import java.io.File;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    Connection c;
    Statement stmt;

    void connect() throws Exception {
        c = DriverManager.getConnection("jdbc:postgresql://localhost/test", "postgres", "postgres");
        stmt = c.createStatement();
    }

    void disconnect() throws Exception{
        stmt.close();
        c.close();
    }

    void clear() throws Exception {
        connect();
        stmt.execute("drop schema public cascade; create schema public; grant all on schema public to postgres; grant all on schema public to public;");
        pgManager.init(true);
        disconnect();
    }

    void insertAdmin(Statement stmt) throws Exception{
        stmt.execute("INSERT INTO auth(user_id, passwd, role) values ('admin', 'iitropar', 'A');");
    }
    void insertfaculty() throws  Exception{
        new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "dept", "-n", "CSE");
        new CommandLine(new New(adminAuth())).execute("faculty", "-n", "Apurva", "-d",
                "CSE", "--uid", "mudgal"
        );
    }
    void insertStudent() throws  Exception{
        new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "programme", "-n", "btech");
        new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "batch", "-n", "2020");
        new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "dept", "-n", "CSE");
        new CommandLine(new New(adminAuth())).execute("student", "-n"
                , "Jugal Chapatwala", "-p", "btech", "-b", "2020", "--uid", "2020csb1082", "-d", "CSE");
    }

    pgManager.User adminAuth() throws Exception {
        return pgManager.authenticate("admin", "iitropar");
    }
    pgManager.User facultyAuth() throws Exception{
        return pgManager.authenticate("mudgal", "apurva_iitropar");
    }
    pgManager.User studentAuth() throws  Exception{
        return pgManager.authenticate("2020csb1082", "jugal_iitropar");
    }

    @Test
    void adminAuthentication() throws Exception{
        clear();
        pgManager.User u = adminAuth();
        assertNotNull(u);
        assertTrue(u.privilege == pgManager.Privilege.ADM);
        disconnect();
    }

    @Test
    void addDepartment() throws Exception{
        clear();
        int exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
        , "dept", "-n", "CSE");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select * from dept;");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("CSE"));
        disconnect();
    }

    @Test
    void addBatch() throws Exception{
        clear();
        int exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "batch", "-n", "2020");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select * from batch;");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("2020"));
        disconnect();
    }
    @Test
    void addProgramme() throws Exception{
        clear();
        int exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "programme", "-n", "btech");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select * from programme;");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("btech"));
        disconnect();
    }

    @Test
    void addStudentWithSurname() throws Exception{
        clear();
        int exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "programme", "-n", "btech");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "batch", "-n", "2020");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "dept", "-n", "CSE");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("student", "-n"
                , "Jugal Chapatwala", "-p", "btech", "-b", "2020", "--uid", "2020csb1082", "-d", "CSE");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select * from student;");
        assertTrue(r.next());
        assertTrue(r.getString("programme").equals("btech") && r.getString("batch").equals("2020")
            && r.getString("name").equals("Jugal") && r.getString("surname").equals("Chapatwala")
        );
        disconnect();
    }

    @Test
    void addStudentWithoutSurname() throws Exception{
        clear();
        int exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "programme", "-n", "btech");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "batch", "-n", "2020");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "dept", "-n", "CSE");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("student", "-n"
                , "Jugal", "-p", "btech", "-b", "2020", "--uid", "2020csb1082", "-d", "CSE");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select * from student;");
        assertTrue(r.next());
        assertTrue(r.getString("programme").equals("btech") && r.getString("batch").equals("2020")
                && r.getString("name").equals("Jugal"));
        assertNull(r.getString("surname"));
        disconnect();
    }

    @Test
    void addFaculty() throws Exception{
        clear();
        int exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "dept", "-n", "CSE");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("faculty", "-n", "Apurva", "-d",
                "CSE", "--uid", "mudgal"
        );
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select * from faculty;");
        assertTrue(r.next());
        assertTrue(r.getString("name").equals("Apurva"));
        disconnect();
    }

    @Test
    void startSession() throws Exception{
        clear();

        int exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select year,sem from acadsession;");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("2020") && r.getString(2).equals("1"));
        disconnect();
    }

    @Test
    void endSession() throws Exception{
        clear();

        int exit = new CommandLine(new Session(adminAuth())).execute("end");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select count(*) from acadsession where current=true;");
        assertTrue(r.next());
        assertEquals(r.getInt(1), 0);
        disconnect();
    }

    @Test
    void addProposal() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select count(*) from proposal;");
        assertTrue(r.next());
        assertEquals(1, r.getInt(1));
        disconnect();
    }

    @Test
    void addProposalFailure() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math", "-p", "CS201"
        );
        assertEquals(1, exit);
        disconnect();
    }

    @Test
    void acceptProposal() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select course_id from courses;");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("CS101"));
        assertFalse(r.next());
        disconnect();
    }

    @Test
    void rejectProposal() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("rejection", "CS101");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select course_id from proposal;");
        assertFalse(r.next());
        disconnect();
    }

    @Test
    void updateProposal() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new Update(facultyAuth())).execute("proposal", "CS101", "--ltp", "2-3-4");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select ltp from proposal;");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("2-3-4"));
        disconnect();
    }

    @Test
    void offerCourse() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select course_id, instructor_id from offerings;");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("CS101") && r.getString(2).equals("mudgal"));
        disconnect();
    }
    @Test
    void enrollCourse() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal");
        assertEquals(0, exit);
        insertStudent();
        exit = new CommandLine(new New(studentAuth())).execute("enrollment", "CS101");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select course_id, student_id from enrollment;");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("CS101") && r.getString(2).equals("2020csb1082"));

        r = stmt.executeQuery("select pc from credits where student_id='2020csb1082'");
        assertTrue(r.next());
        assertEquals(4, r.getInt(1));
        disconnect();
    }

    @Test
    void enrollCourseAboveCredLimit() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "25", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal");
        assertEquals(0, exit);
        insertStudent();
        exit = new CommandLine(new New(studentAuth())).execute("enrollment", "CS101");
        assertEquals(1, exit);
        disconnect();
    }

    @Test
    void enrollCourseWithLessCG() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal", "--mincg", "4.2");
        assertEquals(0, exit);
        insertStudent();
        exit = new CommandLine(new New(studentAuth())).execute("enrollment", "CS101");
        assertEquals(1, exit);
        disconnect();
    }

    @Test
    void fetchEnrollment() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal");
        assertEquals(0, exit);
        insertStudent();
        exit = new CommandLine(new New(studentAuth())).execute("enrollment", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Fetch(studentAuth())).execute("enrollments", "-c", "CS101");
        assertEquals(0, exit);
        disconnect();
    }

    @Test
    void fetchCatalog() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Fetch(facultyAuth())).execute("catalog");
        assertEquals(0, exit);
        disconnect();
    }

    @Test
    void fetchProposals() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("rejection", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Fetch(facultyAuth())).execute("proposals");
        assertEquals(0, exit);
        disconnect();
    }

    @Test
    void fetchOfferings() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal");
        assertEquals(0, exit);
        exit = new CommandLine(new Fetch(facultyAuth())).execute("offerings");
        assertEquals(0, exit);
        disconnect();
    }

    @Test
    void fetchDescription() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal");
        assertEquals(0, exit);
        exit = new CommandLine(new Fetch(facultyAuth())).execute("description", "CS101");
        assertEquals(0, exit);
        disconnect();
    }

    @Test
    void fetchDescriptionCheckProposals() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal");
        assertEquals(0, exit);
        exit = new CommandLine(new Fetch(facultyAuth())).execute("description", "CS101", "--check-proposals");
        assertEquals(0, exit);
        disconnect();
    }

    @Test
    void addCurriculum() throws Exception{
        clear();

        int exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "programme", "-n", "btech");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("dbp", "-t"
                , "batch", "-n", "2020");
        assertEquals(0, exit);
        exit = new CommandLine(new CurriculumCmd(adminAuth())).execute("add", "-b", "2020", "-p",
                "btech", "--pc", "30", "--pe", "30", "--cp", "40"
        );
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select * from curriculum;");
        assertTrue(r.next());
        assertTrue(r.getString("batch").equals("2020") && r.getString("programme").equals("btech")
                && r.getInt("pc") == 30 && r.getInt("pe") == 30 && r.getInt("cp") == 40
        );
        disconnect();
    }

    @Test
    void checkCurriculum() throws Exception{
        clear();

        insertStudent();
        int exit = new CommandLine(new CurriculumCmd(adminAuth())).execute("add", "-b", "2020", "-p",
                "btech", "--pc", "30", "--pe", "30", "--cp", "40"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new CurriculumCmd(studentAuth())).execute("check");
        assertEquals(0, exit);
		disconnect();
    }

    @Test
    void updateCurriculum() throws Exception{
        clear();

        insertStudent();
        int exit = new CommandLine(new CurriculumCmd(adminAuth())).execute("add", "-b", "2020", "-p",
                "btech", "--pc", "30", "--pe", "30", "--cp", "40"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new CurriculumCmd(adminAuth())).execute("update", "-b", "2020", "-p",
                "btech", "--pc", "15", "--pe", "15", "--cp", "20"
        );
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select pc,pe,cp from curriculum");
        assertTrue(r.next());
        assertTrue(r.getInt(1) == 15 && r.getInt(2) == 15 && r.getInt(3) == 20);
		disconnect();
    }

    @Test
    void passwdUpdate() throws Exception{
        clear();

        insertStudent();
        int exit = new CommandLine(new Update(studentAuth())).execute("passwd");
        assertEquals(1,exit);
    }

    @Test
    void gradeUpload() throws Exception{
        clear();

        insertfaculty();
        int exit = new CommandLine(new New(facultyAuth())).execute("proposal", "-i", "CS101", "-t", "PC", "--ltp",
                "1-2-3", "-c", "4", "-n", "Discrete Math"
        );
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("approval", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new Session(adminAuth())).execute("start", "-y", "2020", "-s", "1");
        assertEquals(0, exit);
        exit = new CommandLine(new New(adminAuth())).execute("offering", "-c", "CS101", "-i", "mudgal");
        assertEquals(0, exit);
        insertStudent();
        exit = new CommandLine(new New(studentAuth())).execute("enrollment", "CS101");
        assertEquals(0, exit);
        exit = new CommandLine(new UploadGrades(facultyAuth())).execute("CS101", "-f", "grades.txt");
        assertEquals(0, exit);

        connect();
        ResultSet r = stmt.executeQuery("select grade from enrollment where student_id = '2020csb1082'");
        assertTrue(r.next());
        assertTrue(r.getString(1).equals("A-"));

        File f = new File("grades.txt");
        System.out.println(f.getAbsolutePath());
    }

}