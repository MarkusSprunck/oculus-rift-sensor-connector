THREE.SimpleDatGuiOculusRiftDemo.html
-------------------------------------
For simple WebGL applications with THREE.js the Google's Chrome Experiment DAT.GUI is a good choice to render a minimal user interface, but there is no simple and reliable way to render DAT.GUI inside the WebGL scene. Oculus Rift Applications need to render the user interface inside the scene to display for both eyes. 

At the moment I know no support of a simple GUI in THREE.js. This was my motivation to develop a simple GUI based on THREE.js which should be as good as reasonable API compatible with DAT.GUI and has the same Look & Feel. The following article describes THREE.SimpleDatGui the result of these experiments. THREE.SimpleDatGui can serve as Heads-Up Display (HUD) in WebGL and/or in Oculus Rift applications.

Examples for THREE.SimpleDatGui
-------------------------------
1. http://webgl-examples.appspot.com/simple-webgl-gui-oculus-rift/THREE.SimpleDatGuiOculusRiftDemo.html
2. http://webgl-examples.appspot.com/simple-webgl-gui/THREE.SimpleDatGuiDemo.html?hud=false
3. http://webgl-examples.appspot.com/simple-webgl-gui/THREE.SimpleDatGuiDemo.html?hud=true

Use of Oculus Rift Sensor Connector
-----------------------------------
Oculus Rift Sensor provides sensor data at http://localhost:8444. 

1. Fork project from GitHub
2. Connect your Oculus Rift DK2 to your computer
3. Start _OculusRiftSensorConnector.jar_
4. Open _./client/THREE.SimpleDatGuiOculusRiftDemo.html_
5. Open Folder _Advanced Options_ and check _Oculus Control Active_ 

The example client demonstrates the use of *THREE.SimpleDatGui* for Oculus Rift.

Tested Configurations
---------------------
The application has been tested with the following browsers on Windows 7 with Oculus Rift DK2:
* Chrome 41 (60 FPS)
* Firefox 36 (60 FPS)
* Internet Explorer 11 (8 FPS, definitely to slow for Oculus Rift)

Chrome Options for Local Development 
------------------------------------
The Google Chrome browser will not load local file by default due to security reason.  When you fork the project and open from file system, start with the command line option _--allow-file-access-from-files_ to load the textures. 

See also http://www.chrome-allow-file-access-from-file.com/
	
Credits for Equirectangular Pictures
------------------------------------
* http://commons.wikimedia.org/wiki/File:Place_Dauphine,_Paris_November_2011.jpg
* http://commons.wikimedia.org/wiki/File:Place_de_la_Concorde,_Paris_March_2007.jpg
* http://commons.wikimedia.org/wiki/File:Parc_de_Belleville,_Paris_June_2007.jpg	

Credits for Libraries
---------------------
* THREE.js (see https://github.com/mrdoob)
	
Read More
---------
* http://www.sw-engineering-candies.com

