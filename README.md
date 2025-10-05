# 🎯 HeadHunter

A progression system centered around defeating mobs from spawners. Kill mobs to earn XP, collect MobHeads, and level up to unlock better rewards and access to higher tier mobs.

## 📋 Features

### 🏆 Core Progression System
- **Tier-Based Mobs**: Four tiers (Common, Uncommon, Rare, Legendary) with increasing difficulty and rewards
- **Level System**: Gain XP from killing mobs to level up and unlock new tiers
- **Mob-Specific Tracking**: Individual progress tracking for each mob type
- **XP Rewards**: Earn XP based on mob tier and configured multipliers

### 💰 Reward Systems
- **Gold Nuggets**: Stackable currency items dropped by spawner mobs
- **Right-Click Redemption**: Convert nuggets to economy money or custom rewards
- **Configurable Rates**: Set custom redemption rates per nugget
- **Sound Effects**: Customizable sounds for redemption and level-ups
- **Title Notifications**: Display level-up achievements with titles

### 🎨 Interactive GUIs
- **Two-Tier GUI System**:
  - Main menu showing mob tier categories (Common/Uncommon/Rare/Legendary)
  - Individual tier menus displaying mob-specific stats
- **Progress Visualization**: Progress bars showing kills/XP for each mob
- **Tier Locking**: Higher tiers require reaching specific levels
- **Fully Customizable**: All GUI elements configurable in messages.yml

### 🛡️ Protection Features
- **Anti-Trading**: Prevent players from trading mob heads and nuggets
- **Spawner Detection**: Only mobs from spawners drop rewards
- **Drop Control**: Choose between inventory or ground drops

### 🔌 Integrations
- **Vault Economy**: Optional economy integration for redemptions
- **PlaceholderAPI**: Display player stats in other plugins
- **MySQL Support**: Database storage for player data (configurable)

## 📦 Installation

1. Download `HeadHunter.jar`
2. Place in your server's `plugins` folder
3. Restart the server
4. Configure `config.yml`, `messages.yml`, and `mobs.yml` to your liking
5. (Optional) Install Vault and PlaceholderAPI for additional features

## 🎮 Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/hh` | `headhunter.gui` | Open the rarity selection GUI |
| `/hh stats` | `headhunter.stats` | View your mob statistics (legacy) |
| `/hh reload` | `headhunter.reload` | Reload plugin configuration |

## 🔑 Permissions

| Permission | Description | Default |
|-----------|-------------|---------|
| `headhunter.gui` | Access to GUI commands | All players |
| `headhunter.stats` | View statistics | All players |
| `headhunter.reload` | Reload configuration | Operators |

## 📊 PlaceholderAPI Placeholders

| Placeholder | Description |
|------------|-------------|
| `%headhunter_level%` | Player's current level |
| `%headhunter_xp%` | Player's current XP |
| `%headhunter_xp_required%` | XP required for next level |
| `%headhunter_xp_progress%` | XP progress percentage |
| `%headhunter_kills_<mob>%` | Total kills for specific mob |
| `%headhunter_level_<mob>%` | Level for specific mob |

*Replace `<mob>` with mob type (e.g., `zombie`, `skeleton`, `creeper`)*

## ⚙️ Configuration

### config.yml
Main plugin settings including:
- Spawner-only kills requirement
- XP calculation formulas
- Economy integration
- Title notifications
- Sound effects
- GUI settings

### messages.yml
All player-facing text including:
- Chat messages and prefixes
- GUI titles and item descriptions
- Level-up notifications
- Progress bar formatting
- Error messages

### mobs.yml
Mob tier configuration:
- Define which mobs belong to each tier
- Set XP rewards per mob
- Configure required levels to unlock tiers
- Customize mob head textures

## 🎯 Mob Tiers

### Common (Level 1+)
Starting tier with basic mobs like zombies, skeletons, and spiders.

### Uncommon (Level 5+)
Intermediate mobs including creepers, witches, and endermen.

### Rare (Level 10+)
Challenging mobs such as blazes, ghasts, and piglins.

### Legendary (Level 15+)
Elite mobs including withers, ender dragons, and wardens.

## 🔧 How It Works

1. **Kill Mobs**: Players kill mobs spawned from spawners
2. **Earn Rewards**: Receive gold nuggets and XP based on mob tier
3. **Level Up**: Accumulate XP to increase your level and unlock new tiers
4. **Redeem Nuggets**: Right-click nuggets to convert them to economy money
5. **Track Progress**: Use `/hh` to view your progression through different mob tiers
6. **Unlock Tiers**: Reach required levels to access higher tier mobs and better rewards

## 📈 XP Calculation

```
Base XP = Mob Tier XP × XP Multiplier
Required XP for Level = Base XP × (Current Level + 1)
```

Customizable in `config.yml`:
```yaml
xp-per-level-base: 100
xp-multiplier: 1.5
```

## 🎨 GUI Customization

All GUI elements are fully customizable in `messages.yml`:
- Menu titles and sizes
- Item materials and names
- Lore descriptions
- Slot positions
- Colors and formatting
- Locked/unlocked states

## 🛠️ Development

### Building from Source

```bash
git clone <repository-url>
cd HeadHunter
mvn clean package
```

The compiled jar will be in `target/HeadHunter-1.0.jar`

### Requirements
- Java 17+
- Spigot/Paper 1.20.4+
- Maven 3.6+

### Dependencies
- Vault (optional)
- PlaceholderAPI (optional)

## 📝 License

This plugin was created by **wilcodwg**.

## 🐛 Support

For bugs, issues, or feature requests, please contact the plugin author.

## 🔄 Version History

### v1.0
- Initial release
- Tier-based mob progression system
- Gold nugget redemption
- Two-tier GUI system
- PlaceholderAPI integration
- Vault economy support
- Fully configurable messages and settings

---
