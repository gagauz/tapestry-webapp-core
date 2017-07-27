call ./setenv.bat
call mvn -e clean install source:jar install
pause