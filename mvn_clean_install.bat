call ./setenv.bat
call mvn -e clean source:jar install clean compile
pause