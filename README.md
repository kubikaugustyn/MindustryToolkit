# Mindustry Toolkit

A Java Mindustry mod that consists of multiple parts called 'features' eg. AutoFill, Identity and later others (suggest
new features ;-D)

## Settings

You can find Mindustry Toolkit settings obviously in the bottom category called 'Mindustry Toolkit'
![Settings image](settingsIcon.png)

## AutoFill

Inspired by [this mods](https://github.com/Ferlern/extended-UI-v7) AutoFill.
But better, with lots of settings, so you can customize ammo filled to turrets and much more.
I'm planning also feature that will work like processor + container + flare, its contents will be moved to core and / or
it will be filled from core.
![AutoFill settings example](autofillSettingsImg.png)

## Identity

Inspired by Ukrainian player called Igor, who can change his UUID on server, so server thinks he's different user. By
that way Igor managed to spam [Sectorized](https://github.com/Pointifix/SectorizedPlugin) server's leaderboard with
Igors.

You can create user 'accounts', quickly switch between them in-game without restarting your client, it will only kick
you from server and reconnect back, so server can be told different UUID => different player

Account has 2 parameters

- Name - Player's name
- UUID - Player's UUID (the identity server checks)

If you manage to get somebody's UUID, you can create user with his name and UUID he gave you and pretend to be him, it's
like stealing somebody's Minecraft account, BUT the UUID is only working on one server, normal user has unique UUID on
every single server, you can read your UUID for server with this code in console:

```javascript
// To get your Sectorized uuid you do this:
Core.settings.getString("usid-89.58.37.204:6567", "No USID found :-(")
// For OmniDustry:
Core.settings.getString("usid-109.94.209.233:6567", "No USID found :-(")
// Others:
Core.settings.getString("usid-<serverIP>:<serverPORT(default:6567)>", "No USID found :-(")
```