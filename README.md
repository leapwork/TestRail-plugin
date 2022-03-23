# Leapwork Integration
Automate your testcase Execution with the help of the Leapwork.

# More Details
Leapwork’s completely visual, no-code automation platform makes it easy for business and IT users to automate repetitive processes, so enterprises can adopt and scale automation faster. 
For rapid results, at lower cost, and requiring fewer specialist resources than traditional automation approaches.

# Features:
 - Create your own Testrun with the reuired testcases in Testrail
 - Execute the testrun automatically from Testrail.
 - The automatic test execution will be done on the Leapwoprk
 - The test results of your test execution will be posted on Testrail.
 
# Installing
- Put files leapwork.php and TestRailsCMD.jar to TestRail root folder - the same folder where you can find index.php
- Administration => Customizations => UI scripts => Add Script => Clear script text area => Copy all the text from Leaptest customization UI script.txt to script text area => Activate script => Save UI Script
- If your TestRail uses Windows IIS, please provide IIS_USERS permissions for executing, creating, writing and reading files in TestRail root folder (where index.php). The same should be done for Unix/Linux systems if required. File "Leapwork.php" executes "TestRailsCMD.jar", which creates/modifies log files. Each test run creates/modifies its own log file during execution.

# Instruction
1. Create runlist in Leapwork with the number of teststeps you need to execute for you test.
2. Add the flows under runlist steps.
3. Create a schedule for the same runlist.
4. Create testcases under the project in Testrail. The number of testcases should be exact match as the number of RunlistSteps in Leapwork.
5. Also, the testcases template should be selected as 'Test Case(Steps)' in Testrail.
6. Add teststeps for the testcase, testcase Step Description should be same as the flow name inside runliststep.
7. Create a testrun in testrail, add the testcases as per the runlist in leapwork
7. Press button "Select Schedules" to get a list of all available schedules, select schedules you want to run.
8. Run your project and get results.

# Troubleshooting
- To view if your execution has been completed, view the log file created where the jar file has been kept.
- If you catch an error "No such run [runId]!" after schedule starting, increase time delay parameter.

# Screenshots
![ScreenShot](https://github.com/leapwork/TestRail-plugin/blob/main/images/AddUIScriptScreenshot.png)
![ScreenShot](https://github.com/leapwork/TestRail-plugin/blob/main/images/RunListScreenShot.png)
![ScreenShot](https://github.com/leapwork/TestRail-plugin/blob/main/images/SelectScheduleScreenshot.png)
![ScreenShot](https://github.com/leapwork/TestRail-plugin/blob/main/images/TestCasesScreenshot.png)
![ScreenShot](https://github.com/leapwork/TestRail-plugin/blob/main/images/TestRunScreenshot.png)
![ScreenShot](https://github.com/leapwork/TestRail-plugin/blob/main/images/TestStepsScreenshot.png)
