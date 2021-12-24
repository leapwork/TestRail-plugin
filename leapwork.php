<?php

@set_time_limit(0);

$runId = null;
if (isset($_POST['runId']))
{
	if (preg_match('/^[0-9]+$/', $_POST['runId']))
	{
		$runId = (int) $_POST['runId'];
	}
}
if (!$runId)
{
	throw new TestRailException(
		'Run ID not available, exiting immediately'
	);
}

$testRailAddress = null;
if (isset($_POST['testRailAddress']))
{
	$testRailAddress = $_POST['testRailAddress'];
}
if (!$testRailAddress)
{
	throw new TestRailException(
		'TestRail address is not available, exiting immediately'
	);
}

$testRailUser = null;
if (isset($_POST['testRailUser']))
{
	$testRailUser = $_POST['testRailUser'];
}
if (!$testRailUser)
{
	throw new TestRailException(
		'TestRail User is not available, exiting immediately'
	);
}

$testRailPassword = null;
if (isset($_POST['testRailPassword']))
{
	$testRailPassword = $_POST['testRailPassword'];
}
if (!$testRailPassword)
{
	throw new TestRailException(
		'TestRail user password is not available, exiting immediately'
	);
}


$leapworkControllerURL = null;
if (isset($_POST['leapworkControllerURL']))
{
	$leapworkControllerURL = $_POST['leapworkControllerURL'];
}
if (!$leapworkControllerURL)
{
	throw new TestRailException(
		'Leaptest Controller URL is not available, exiting immediately'
	);
}

$leapworkHeader = null;
if (isset($_POST['leapworkHeader']))
{
	$leapworkHeader = $_POST['leapworkHeader'];
}
if (!$leapworkHeader)
{
	throw new TestRailException(
		'Leaptest Header URL is not available, exiting immediately'
	);
}

$scheduleId = null;
if (isset($_POST['scheduleId']))
{
	$scheduleId = $_POST['scheduleId'];
}
if (!$scheduleId)
{
	throw new TestRailException(
		'Schedule Id is not available, exiting immediately'
	);
}

$delay = null;
if (isset($_POST['delay']))
{
	if (preg_match('/^[0-9]+$/', $_POST['delay']))
	{
		$delay = (int) $_POST['delay'];
	}
}
if (!$delay)
{
	throw new TestRailException(
		'Delay is not available, exiting immediately'
	);
}

$doneStatusAs = null;
if (isset($_POST['doneStatusAs']))
{
	if (preg_match('/^[0-9]+$/', $_POST['doneStatusAs']))
	{
		$doneStatusAs = (int) $_POST['doneStatusAs'];
	}
}
if (!$doneStatusAs)
{
	throw new TestRailException(
		'Done status is not available, exiting immediately'
	);
}


//run jar
$cmd = "java -jar TestRailsCMD.jar $runId $testRailAddress $testRailUser $testRailPassword $leapworkControllerURL $scheduleId $delay $doneStatusAs $leapworkHeader";

if (substr(php_uname(), 0, 7) == "Windows"){ 
        pclose(popen("start /B ". $cmd, "r"));  
} 
else { 
        exec($cmd . " > /dev/null &");   
} 



class TestRailException extends Exception
{
}