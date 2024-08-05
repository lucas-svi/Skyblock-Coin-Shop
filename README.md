# Skyblock Coin Shop

Skyblock Coin Shop is a Discord-based application that creates a marketplace for Hypixel Skyblock coins, connecting sellers and buyers through automated cryptocurrency transactions.

## Features

- Automated cryptocurrency transactions
- Secure and reliable marketplace
- User-friendly Discord interface
- Connects Hypixel Skyblock coin sellers and buyers
- Real-time updates and notifications

## Setup Instructions

### Prerequisites

1. **Discord Account**: Ensure you have a Discord account and a server where the bot will be deployed.
2. **Discord Bot Token**: Create a Discord bot and get the bot token from the [Discord Developer Portal](https://discord.com/developers/applications).
3. **Cryptocurrency Wallet**: Set up a cryptocurrency wallet for handling transactions.
4. **Python 3.x**: Install Python from [Python Downloads](https://www.python.org/downloads/).
5. **Git**: Install Git from [Git SCM](https://git-scm.com/downloads).

### Step-by-Step Setup

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/lucas-svi/skyblock-coin-shop.git
   cd SkyblockCoinShop
   ```

2. **Install Dependencies**:
   - Install the required Python packages:
   ```bash
   pip install -r requirements.txt
   ```

3. **Set Your Environment Variables**:
   - Set the Discord bot token and cryptocurrency wallet details in your environment variables:
   ```bash
   export DISCORD_BOT_TOKEN='your_discord_bot_token'
   export CRYPTO_WALLET_ADDRESS='your_crypto_wallet_address'
   export CRYPTO_WALLET_PRIVATE_KEY='your_crypto_wallet_private_key'
   ```

4. **Configure the Bot**:
   - Update the configuration file with your settings if necessary.

5. **Run the Bot**:
   - Start the Discord bot:
   ```bash python bot.py```

### Usage

- **Join the Discord Server**: Invite the bot to your Discord server using the OAuth2 URL provided in the Discord Developer Portal.
- **Buy and Sell Coins**: Use commands in the Discord server to list coins for sale, buy coins, and manage your transactions.
- **Real-time Notifications**: Receive real-time updates and notifications about your transactions.

### Example Commands

- **List Coins for Sale**:
  ```plaintext
  !list 100000 0.001BTC
  ```

- **Buy Coins**:
  ```plaintext
  !buy 100000
  ```

- **Check Balance**:
  ```plaintext
  !balance
