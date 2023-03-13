Extract the zip file.
cd to the directory and run
	./gradlew install
	./app/build/install/app/bin/app --first-run
NOTE: This is work for Linux. For windows running app.bat instead should work (not tested)

The app will first ask you to login.
Once you log in you will be greeted by a prompt. This is like a shell with limited commands to offer.
The commands are broadly:
->	new
->	fetch
->	update
->	session
->	exit/quit
->	curriculum
->	upload-grades

These commands together with their subcommands allow for all the needed functionalities.

new:
	student     Add a new student into the database
	faculty     Add a new Faculty member into the database
	admin       Add a new admin into the database
	dbp         Add a new department/Batch/Programme to database
	proposal    Propose a new Course to be added to catalog
	approval    Approve an existing Course proposal
	offering    Put up a course as being offered for a particular Semester
	enrollment  Enroll to a course
	rejection   Reject a proposed Course

fetch:
	proposals    Fetch Course proposals from database
	catalog      Fetch the courses from the catalog
	description  Fetch description of a Course
	offerings    Fetch the Offered courses for the semester
	enrollments  Fetch enrollments in a particular course
	grade        Fetch grade of students
	cgpa         Fetch the CGPA of a student

update:
	passwd    Update the password of the user
	proposal  Update some fields of a proposed course

session:
	start
	end

exit/quit:
	close the Application

curriculum:
	check   Check if a student has completed his/her curriculum
	add     Add a new curriculum
	update  Update a curriculum

upload-grades:
	Upload grades for students of a course

Using --help after any command will provide with help messages
