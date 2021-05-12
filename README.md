# Networks 2 - Project

Here we will explain how to run our program in Intellij and Eclipse.

## Intellij
Firstly open the project.<br/>
![Capture 1](./images/Capture1.PNG)<br/>
Open -> Select project -> ok<br/>

After you should se that a compound has been already set.<br/>
![Capture 2](./images/Capture2.PNG)<br/>

If you want to check what that compound contains you can click on:<br/>
Project (run part) -> Edit Configurations...<br/>
![Capture 4](./images/Capture4.PNG)<br/>
![Capture 3](./images/Capture3.PNG)<br/>

Why did we use a compound?<br/>
Since we have just 1 code for the servers, we need to execute it multiple times. So that our implementation works right you need to execute 10 times the Server.class and 1 time the Client.class.<br/>

## Eclipse
Firstly open the project by selecting "Open Projects from File System".<br/>
Select the directory of the project and click "Finish".<br/>
![openProject](./images/openProject.png)<br/>

After that right click on the project and click on "Run as" and then click on "Run configurations".<br/>
![runAs](./images/run_as.png)<br/>

Then click on "Java Application" and click on the icon "Add new launch configuration".<br/>
After that click on search and click on Client and change the Name of the configuration to "Client".<br/>
After that click on the icon "Add new launch configuration" again, after that click on search but this time select the Server and change the Name of the configuration to "Server".<br/>
![java_application](./images/java_application.png)<br/>

Now click on "Launch Group" and click on the icon "Add new launch configuration", change the name of the configuration to "Key-Value Storage Project".<br/>
After that click on the "Add..." button and add one client.<br/>
After that click on the "Add..." again and add a server --> repeat this until you got 10 servers.<br/>
After that click apply and click "Run" now our Program should start and you should be able to test it.<br/>
![launch_group](./images/launch_group.png)<br/>

To change between servers and the client click on this icon shown on the image and select the desired server/client.<br/>
![console](./images/console.png)<br/>
