# Smart Transportation - Patras Uni Bus
Patras Uni Bus is a proposition of a cyber-physical system that monitors bus traffic and passengers' attendance and stats in public transport. This application is focused on the buses traveling through the campus of University of Patras. It could be used for reducing the spread of Covid-19 or other infectious diseases in public transportation such as buses, which was the main concern when the system was developed. The Google Maps API is used for the map visualizations. Microsoft SQL Server is used for the database, Java for the HTTP server and the world simulator and HTML/CSS/Javascript for the front-end.

## Description
Patras Uni Bus is a web application that helps passengers, bus drivers and people waiting at the bus stops have an overview of the bus route (quantitatively and qualitatively) and its attendance based on live data. 

![vlcsnap-2023-09-13-17h32m16s358](https://github.com/ThomasPappas00/Patras-Uni-Bus/assets/75483971/6dc71a19-1f75-4581-a23a-99f48f5232f6)

The system uses 7 cyber bus stops and 2 travel lines inside the campus of the University of Patras. The system knows the precise number of people waiting at the bus stops with 2D image recognition (camera-laser) or 3D image recognition (2-3 laser devices) devices deployed in each one. Also, buses should be equipped with a GPS system that sends their location, an Enter/Exit Laser Tripwire (2 wires for direction) that keeps tracks of the numbers of the passengers and take temperature with a temperature gun near the bus doors (reading speed 1 sec, Distance to Spot Ratio is 10:1) to monitor passengers wellbeing and providing or denying the ride to them. Students can be informed about crowded buses in real time, the bus fleet can be increased or decreased in size based on demand and potentially risky students (based on their temperature) can be denied access to the buses in extreme circumstances.

![vlcsnap-2023-09-13-17h32m49s665](https://github.com/ThomasPappas00/Patras-Uni-Bus/assets/75483971/30fe4669-b4ee-49dc-95f9-cb59d512a2d1)
![vlcsnap-2023-09-13-17h33m20s079](https://github.com/ThomasPappas00/Patras-Uni-Bus/assets/75483971/6299cbe1-0f05-4dc6-af97-62ab7c77e932)

## Instructions
Microsoft SQL Server and Java Runtime Environment are prerequisites for the machine that hosts the back-end. The database should first be enabled as a windows service (MSSQLSERVER → Start). Then, open the project in eclipse and put the 4 jars in the classpath of the project files located in the C:...\Patras-UniBus\Server Side\WebContent\WEB INF\lib folder. JDBC is the driver enabling the communication with the database, while the Jackson libraries are used for converting objects to JSON and vice versa. Next, we create a Tomcat v9.0 Server at localhost from eclipse and add the PatrasUniBus project to the server. We start the server (Start) and the see the message _INFO: Server startup in [xxxx] milliseconds in the eclipse console_. To create a fresh instance of the map (buses, bus stops, passengers, routes) run the _InitTransoprtation_ module. Then run _SimulateLine1_ and _SimulateLine2_ to create buses that move in the campus and take passengers on and off, provide fleet based on attendance and block students with fever (high temperature). The client can access data at the _http://{{ip}}:8080/CattleMonitoring/CampusBuses/busstops/_ and _http://{{ip}}:8080/CattleMonitoring/CampusBuses/buses/_ endpoints with a GET request with the bus _line_ as a parameter.

## Demo
https://github.com/ThomasPappas00/Patras-Uni-Bus/assets/75483971/d135a43a-016f-4071-a7ae-d4d3cb22fa86

## License

[MIT](https://choosealicense.com/licenses/mit/)

