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
- **Player Leash**: Allow player leash each other using leash.
- **Dice Command**: Allows players to throw dice with a specified number of sides. Players can throw a single die or multiple dice at once.
- **Try Command**: Gives players a customizable chance to succeed in a specified action. This can be used for various role-playing scenarios.
- **Reload Command**: Reloads the plugin's configuration files, allowing changes to take effect without restarting the server.
- **Support for PlaceholderAPI**: If installed, DMRP can use placeholders to customize messages further.
- **Support for MiniMassages**: DMRP can use MiniMassages format to customize messages further.
- **Auto update**: Automatically check for plugin update.

See command showcase on:  
[![modrinth-gallery](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/documentation/modrinth-gallery_vector.svg)](https://modrinth.com/plugin/dmrp/gallery)


## Installation

1. Download the latest version of DMRP from the [Modrinth](https://modrinth.com/plugin/dmrp) or [GitHub repository](https://github.com/DublikuntMux/DMRP).
2. Place the downloaded `.jar` file into your server's `plugins` directory.
3. Restart your server to load the plugin.

## Configuration

DMRP uses two configuration files: `config.yaml` and `language.yaml`. These files are located in the plugin's directory under `plugins/DMRP/`.

- `config.yaml`: Contains settings such as the distance at which player command outputs are heard.
- `language.yaml`: Contains messages used by the plugin.

## Commands

- **/rp-reload**: Reloads the plugin's configuration files.
- **/try {what to try}**: Attempts a specified action with a configured chance of success.
- **/dice {amount} {sides}**: Throws a specified number of dice with a specified number of sides.

## Permissions

DMRP uses a permission system to control access to its commands. By default, all commands (except /rp-reload) are restricted to all users. You can customize these permissions in your server's `permissions.yml` file.

## Support

If you encounter any issues or have suggestions for improvements, please open an issue on the [GitHub repository](https://github.com/DublikuntMux/DMRP/issues).

## Contributing

Contributions to DMRP are welcome! If you'd like to contribute, please fork the repository, make your changes, and submit a pull request.

## License

DMRP is licensed under the BSD-3 License. See the `LICENSE.md` file for more details.
