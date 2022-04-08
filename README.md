# MCCaptcha
_Captcha plugin for minecraft servers to help defend against bots!_

**Captcha Process**<br />
When a player joins your server, most actions are disabled. They cannot execute commands, interact with the world, move, etc. They are sent a message telling them to go to a link to verify they aren't a bot. When they go to the url, they must pass a google recaptcha in order to proceed. Once they have done that, they can close the window and return to the game. They must then click [VERIFY] to have the plugin check the server to see if they passed the captcha. If true, they are returned their ability to interact with the world.<br />

**Configuration Properties**<br />
`bypass-permission`: permission to bypass captcha entirely.<br />
`callback-command`: command to execute when a player passes the captcha. It provides the username in the first argument.<br />
`reload-permission`: permission to use `/mccaptcha reload`.<br />
`verify-others-permission`: permission to use `/mccaptcha verify [username]`.<br />
_Set to **none** to disable a feature, or remove it entirely!_<br />
_More configuration options coming soon._<br />

**Commands**<br />
`/mccaptcha verify` - Verifies with mccaptcha servers to check if you completed the captcha.<br />
`/mccaptcha verify [username]` - Force verifies a user.<br />
`/mccaptcha reload` - Reloads configuration<br />

**Notes**<br />
1. A known issue is players can still interact with their inventory before being verified. I am working on figuring that out.
2. Please do note that this is a **pre-release**. Tell me about any bugs you find [here](https://github.com/CollidaDev/MCCaptcha/issues).
3. I am working on this project solo at the moment. Please be patient with me.

Have a good day ;)
