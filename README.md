# Building the module
You can make changes and then build a new module to test by running `mvn clean package` and then looking in the `target` directory of the `netty-leak-build` Maven module for `netty-leak-Ignition-Module-unsigned.modl`.

# Running the module

Download the Ignition zip distribution for your platform from here: https://inductiveautomation.com/downloads/ignition

Unzip it somewhere, open a terminal, and cd into that directory.

`chmod +x ignition.sh ignition-gateway`


`rm user-lib/modules/*`


`vi data/ignition.conf`

find the lines that start with `wrapper.java.additional` and add one like this:


`wrapper.java.additional.5=-Dignition.allowunsignedmodules=true`


Build and copy the netty-leak module file to the `user-lib/modules` folder

`./ignition.sh start`


You should be able to access the gateway at localhost:8088 once it starts. The username and password is admin/password. Go to the Configure section on the top navigation, then Modules on the left navigation. You'll see just the netty-leak module since we removed the others before starting the software. Clicking the uninstall link will remove the module (and when there's no leak unload all the Classes and the ClassLoader).

