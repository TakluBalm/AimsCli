f = open("commands.sql")
lines = f.readlines()
f.close()

out = open("output.txt", "w")

for i in range(len(lines)):
	line = lines[i].strip(" \n\t")
	if len(line) == 0 or line[0] == '-':
		continue
	out.write(f'"{line} "')
	if i != len(lines)-1:
		out.write('+\n')

out.close()