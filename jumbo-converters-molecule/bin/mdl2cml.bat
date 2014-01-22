@REM ----------------------------------------------------------------------------
@REM  Copyright 2001-2006 The Apache Software Foundation.
@REM
@REM  Licensed under the Apache License, Version 2.0 (the "License");
@REM  you may not use this file except in compliance with the License.
@REM  You may obtain a copy of the License at
@REM
@REM       http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM  Unless required by applicable law or agreed to in writing, software
@REM  distributed under the License is distributed on an "AS IS" BASIS,
@REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@REM  See the License for the specific language governing permissions and
@REM  limitations under the License.
@REM ----------------------------------------------------------------------------
@REM
@REM   Copyright (c) 2001-2006 The Apache Software Foundation.  All rights
@REM   reserved.

@echo off

set ERROR_CODE=0

:init
@REM Decide how to startup depending on the version of windows

@REM -- Win98ME
if NOT "%OS%"=="Windows_NT" goto Win9xArg

@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" @setlocal

@REM -- 4NT shell
if "%eval[2+2]" == "4" goto 4NTArgs

@REM -- Regular WinNT shell
set CMD_LINE_ARGS=%*
goto WinNTGetScriptDir

@REM The 4NT Shell from jp software
:4NTArgs
set CMD_LINE_ARGS=%$
goto WinNTGetScriptDir

:Win9xArg
@REM Slurp the command line arguments.  This loop allows for an unlimited number
@REM of arguments (up to the command line limit, anyway).
set CMD_LINE_ARGS=
:Win9xApp
if %1a==a goto Win9xGetScriptDir
set CMD_LINE_ARGS=%CMD_LINE_ARGS% %1
shift
goto Win9xApp

:Win9xGetScriptDir
set SAVEDIR=%CD%
%0\
cd %0\..\.. 
set BASEDIR=%CD%
cd %SAVEDIR%
set SAVE_DIR=
goto repoSetup

:WinNTGetScriptDir
set BASEDIR=%~dp0\..

:repoSetup


if "%JAVACMD%"=="" set JAVACMD=java

if "%REPO%"=="" set REPO=%BASEDIR%\repo

set CLASSPATH="%BASEDIR%"\etc;"%REPO%"\cml\jumbo-converters-core\0.3-SNAPSHOT\jumbo-converters-core-0.3-SNAPSHOT.jar;"%REPO%"\org\lensfield\lensfield2-api\0.2\lensfield2-api-0.2.jar;"%REPO%"\log4j\log4j\1.2.13\log4j-1.2.13.jar;"%REPO%"\org\xml-cml\jumbo\6.1-SNAPSHOT\jumbo-6.1-20140120.182313-218.jar;"%REPO%"\org\xml-cml\euclid\1.1-SNAPSHOT\euclid-1.1-20140120.154748-124.jar;"%REPO%"\commons-io\commons-io\2.0\commons-io-2.0.jar;"%REPO%"\org\apache\commons\commons-math\2.2\commons-math-2.2.jar;"%REPO%"\joda-time\joda-time\1.6.2\joda-time-1.6.2.jar;"%REPO%"\org\xml-cml\cmlxom\3.2-SNAPSHOT\cmlxom-3.2-20140120.181752-202.jar;"%REPO%"\org\ccil\cowan\tagsoup\tagsoup\1.2\tagsoup-1.2.jar;"%REPO%"\xom\xom\1.2.5\xom-1.2.5.jar;"%REPO%"\xml-apis\xml-apis\1.3.03\xml-apis-1.3.03.jar;"%REPO%"\xerces\xercesImpl\2.8.0\xercesImpl-2.8.0.jar;"%REPO%"\xalan\xalan\2.7.0\xalan-2.7.0.jar;"%REPO%"\jtidy\jtidy\4aug2000r7-dev\jtidy-4aug2000r7-dev.jar;"%REPO%"\org\freemarker\freemarker\2.3.16\freemarker-2.3.16.jar;"%REPO%"\jgrapht\jgrapht\0.6.0\jgrapht-0.6.0.jar;"%REPO%"\javax\vecmath\1.2\vecmath-1.2.jar;"%REPO%"\cdk\cdk\20071025\cdk-20071025.jar;"%REPO%"\net\sf\jni-inchi\jni-inchi\0.7\jni-inchi-0.7.jar;"%REPO%"\net\sf\jnati\jnati-deploy\0.3\jnati-deploy-0.3.jar;"%REPO%"\net\sf\jnati\jnati-core\0.3\jnati-core-0.3.jar;"%REPO%"\cml\jumbo-converters-molecule\0.3-SNAPSHOT\jumbo-converters-molecule-0.3-SNAPSHOT.jar
goto endInit

@REM Reaching here means variables are defined and arguments have been captured
:endInit

%JAVACMD% %JAVA_OPTS%  -classpath %CLASSPATH_PREFIX%;%CLASSPATH% -Dapp.name="mdl2cml" -Dapp.repo="%REPO%" -Dapp.home="%BASEDIR%" -Dbasedir="%BASEDIR%" org.xmlcml.cml.converters.molecule.mdl.MDL2CMLConverter %CMD_LINE_ARGS%
if %ERRORLEVEL% NEQ 0 goto error
goto end

:error
if "%OS%"=="Windows_NT" @endlocal
set ERROR_CODE=%ERRORLEVEL%

:end
@REM set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" goto endNT

@REM For old DOS remove the set variables from ENV - we assume they were not set
@REM before we started - at least we don't leave any baggage around
set CMD_LINE_ARGS=
goto postExec

:endNT
@REM If error code is set to 1 then the endlocal was done already in :error.
if %ERROR_CODE% EQU 0 @endlocal


:postExec

if "%FORCE_EXIT_ON_ERROR%" == "on" (
  if %ERROR_CODE% NEQ 0 exit %ERROR_CODE%
)

exit /B %ERROR_CODE%
