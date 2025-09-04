# DMRP - Role Play Plugin for Minecraft

<p align="center">
    <img alt="DMRP logo" height="87" src="content/DMRP.png" title="DMRP logo" width="256"/>
</p>

## Introduction

DMRP is a Minecraft plugin designed to enhance role-playing experiences on your server. It introduces new commands and mechanics that can be used to create immersive role-playing scenarios. This plugin is compatible with Minecraft servers running Spigot or Bukkit.

[![modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/plugin/dmrp)
[![github](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/DublikuntMux/DMRP)
<img alt="spigot" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/spigot_vector.svg"/>
<img alt="paper" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/paper_vector.svg"/>
<img alt="purpur" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/supported/purpur_vector.svg"/>
<img alt="sponge" src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/unsupported/sponge_vector.svg"/>

## Features

- **Player Leash**: Allow players to leash each other using a leash.
- **Inventory Lock**: Allows locking and unlocking of player inventories, preventing them from moving items using a chain.
- **Dice Command**: Allows players to throw dice with a specified number of sides. Players can throw a single die or multiple dice at once.
- **Try Command**: Gives players a customizable chance to succeed in a specified action. This can be used for various role-playing scenarios.
- **Coin flip Command**: Simulates a coin flip with a 50/50 chance of landing on heads or tails.
- **Reload Command**: Reloads the plugin's configuration files, allowing changes to take effect without restarting the server.
- **Support for PlaceholderAPI**: If installed, DMRP can use placeholders to customize messages further.
- **Support for MiniMassages**: DMRP can use MiniMassages format to customize messages further.
- **Custom Placeholders**: Introduces new custom placeholders that allow server administrators to dynamically display plugin-specific data, such as player state.
- **Auto Update**: Automatically checks for plugin updates.

See command showcase on:  
[![modrinth-gallery](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/modrinth-gallery_vector.svg)](https://modrinth.com/plugin/dmrp/gallery)

## Installation

1. Download the latest version of DMRP from the [Modrinth](https://modrinth.com/plugin/dmrp) or [GitHub repository](https://github.com/DublikuntMux/DMRP).
2. Place the downloaded `.jar` file into your server's `plugins` directory.
3. Restart your server to load the plugin.

## Configuration

DMRP uses two configuration files: `config.yaml` and `language.yaml`. These files are located in the plugin's directory under `plugins/DMRP/`.

- `config.yaml`: Contains settings such as the maximum dice sides, and leash distance.
- `language.yaml`: Contains messages used by the plugin, which can be customized to fit your server's theme.

## Commands

- **/rp-reload**: Reloads the plugin's configuration files.
- **/try {what to try}**: Attempts a specified action with a configured chance of success.
- **/dice {amount} {sides}**: Throws a specified number of dice with a specified number of sides.
- **/coinflip**: Simulates a coin flip with a 50/50 chance.
- **/lockinv {player}**: Locks or unlocks the inventory of a specified player.
- **/leash {player}**: Leashes/unleashes a player.

## Custom Placeholders

DMRP introduces custom placeholders that allow server administrators to dynamically display plugin-specific data. These placeholders can be used with PlaceholderAPI to enhance the role-playing experience. Below is a description of each placeholder:

- **%dmrp_leashed%**: Displays whether a player is currently leashed.  
  - Returns a customizable message based on the player's leash status:
    - If the player is leashed: Displays the message defined in `placeholder.leash.leashed` in the `language.yaml` file.
    - If the player is not leashed: Displays the message defined in `placeholder.leash.not_leashed`.

- **%dmrp_lockinv%**: Displays whether a player's inventory is locked.  
  - Returns a customizable message based on the player's inventory lock status:
    - If the player's inventory is locked: Displays the message defined in `placeholder.lockinv.locked` in the `language.yaml` file.
    - If the player's inventory is unlocked: Displays the message defined in `placeholder.lockinv.unlocked`.

These placeholders can be customized in the `language.yaml` file to fit your server's theme and enhance immersion.

## Permissions

DMRP uses a permission system to control access to its commands. By default, all commands (except /rp-reload) are restricted to all users. You can customize these permissions in your server's `permissions.yml` file.

### Permission Nodes

- `dmrp.command.*`: Gives access to all DMRP commands.
- `dmrp.command.reload`: Access to reload the plugin configuration.
- `dmrp.command.try`: Access to the `/try` command.
- `dmrp.command.dice`: Access to the `/dice` command.
- `dmrp.command.coinflip`: Access to the `/coinflip` command.
- `dmrp.command.lockinv`: Access to the `/lockinv` command.
- `dmrp.command.leash`: Access to the `/leash` command.

- `dmrp.leash.use`: Allows players to leash others.
- `dmrp.leash.can`: Allows players to be leashed.
- `dmrp.leash.admin`: Gives access to unleash players that you don't own.

- `dmrp.lock.use`: Gives access to lock players inventories.
- `dmrp.lock.can`: Allows players to be inventory locked.
- `dmrp.lock.admin`: Gives access to unlock inventories that you don't own.

## Support

If you encounter any issues or have suggestions for improvements, please open an issue on the [GitHub repository](https://github.com/DublikuntMux/DMRP/issues).

## Contributing

Contributions to DMRP are welcome! If you'd like to contribute, please fork the repository, make your changes, and submit a pull request.

## License

DMRP is licensed under the BSD-3 License. See the `LICENSE.md` file for more details.
