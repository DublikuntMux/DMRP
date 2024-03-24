# DMRP - Role Play Plugin for Minecraft

## Introduction

DMRP is a Minecraft plugin designed to enhance role-playing experiences on your server. It introduces new commands and mechanics that can be used to create immersive role-playing scenarios. This plugin is compatible with Minecraft servers running Spigot or Bukkit.

## Features

- **Dice Command**: Allows players to throw dice with a specified number of sides. Players can throw a single die or multiple dice at once.
- **Try Command**: Gives players a 50% chance to succeed in a specified action. This can be used for various role-playing scenarios.
- **Reload Command**: Reloads the plugin's configuration files, allowing changes to take effect without restarting the server.
- **Support for PlaceholderAPI**: If installed, DMRP can use placeholders to customize messages further.
- **Support for MiniMasages**: If installed, DMRP can use MiniMasages format to customize messages further.

## Installation

1. Download the latest version of DMRP from the [GitHub repository](https://github.com/DublikuntMux/DMRP).
2. Place the downloaded `.jar` file into your server's `plugins` directory.
3. Restart your server to load the plugin.

## Configuration

DMRP uses two configuration files: `config.yaml` and `language.yaml`. These files are located in the plugin's directory under `plugins/DMRP/`.

- `config.yaml`: Contains settings such as the distance at which player commands are heard.
- `language.yaml`: Contains messages and prefixes used by the plugin.

## Commands

- **/rp-reload**: Reloads the plugin's configuration files.
- **/try [what to try]**: Attempts a specified action with a 50% chance of success.
- **/dice [amount] [sides]**: Throws a specified number of dice with a specified number of sides.

## Permissions

DMRP uses a permission system to control access to its commands. By default, all commands are restricted to operators. You can customize these permissions in your server's `permissions.yml` file.

## Support

If you encounter any issues or have suggestions for improvements, please open an issue on the [GitHub repository](https://github.com/DublikuntMux/DMRP/issues).

## Contributing

Contributions to DMRP are welcome! If you'd like to contribute, please fork the repository, make your changes, and submit a pull request.

## License

DMRP is licensed under the MIT License. See the `LICENSE` file for more details.
