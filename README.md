# Thing you may not know about:
This is a new version of [**Silky Spawner**](https://www.spigotmc.org/resources/Ⓢilky-Ⓢpawner-a-whole-new-level-of-spawner-modification-✔.84748/) (one of my previous works) so you may see some similarities between these two. I recommend you to try it once.

# This plugin allows you to:
- Pickup spawners
- Modify spawned type (using command)
- Show statistics (can be adjusted in config.yml)
- Change messages and command descriptions
- Create floating name and lore
- Set name to spawner
- Access full modification to lore (add/set/remove/reset)
- Modify spawner’s minimum delay, maximum delay, required player range, spawn count, and spawn range
- Use “tab” key to complete the commands
- Click on text to get command

# Commands (starts with /justanotherspawnerpickup or /jasp):
- "*help*": Show a list of commands and its descriptions
- "reload*": Reload plugin
- "*set <mob name> <amount>*": Indicate specific mob for spawner to  be spawned
- "*moblist*": Show all mobs available for "set" command (number of mob depends on version of server)
- “*setname*”: Name your hold spawner
- “*lore add <lore>*”: Add one more line to spawner’s lore
- “*lore set [line] <lore>*”: Set a line of lore into something else
- “*lore remove [line]*”: Remove a line of lore
- “*lore reset [amount/all]*”: Reset lore (specify a number or all if you want to do more than one) 
- “*clear <radius>*”: Clear all the plugin’s holographic name and lines of lore (Only things that this plugin created will be removed, others may stand still)
- “*modify mindelay [time]*”: Set minimum time required for each mob to spawn (Must be lower than maxdelay)
- “*modify maxdelay [time]*”: Set maximum time required for each mob to spawn (Must be higher than mindelay)
- “*modify playerrange [radius]*”: Required distance that player need go stand away from in order to start spawning.
- “*modify count [number]*”: Number of mobs can be spawned at a time
- “*modify range [radius]*”: Spawning range (Warning: larger range can cause lag and delay in your server!)


# Permissions:
***jasp.**** – Use all commands with one single permission\
***jasp.admin.reload***\
***jasp.command.help***\
***jasp.command.set***\
***jasp.command.moblist***\
***jasp.command.setname***\
***jasp.command.lore.[add/set/remove/reset]***\
***jasp.command.clear***\
***jasp.command.modify.[mindelay/maxdelay/playerrange/count/range]***\
***jasp.menu.statistics***

# Working on:
-	RGB (1.16.x only)
-	Something to change spawned type (can be obtained by certain actions and used on spawner to change its spawned type)
-	Command to get item above

--- 

### This plugin still in developing stage, that means more features and bug fixes will be applied in the future. If you have any ideas, bugs or glitches when using it. Feel free to make a report on my [***Issues***](https://github.com/knighthat/JASP/issues) website.

### And if you happen to like this plugin and want to see it grows, PLEASE consider donating though my [***Paypal***](https://www.paypal.me/TnKnightN), your tips will make a big changes. Thank you!
