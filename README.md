Economy — Paper 1.8.8 starter plugin
==========================================

Included:
- Every first-time player receives $20,000 (UUID-based, one time).
- /money (aliases /bal and /balance)
- /pay <player> <amount>
- /dbshop opens a clickable inventory shop.
- Shop kits: Starter $500, Miner $1,000, Archer $2,000, Warrior $3,500, Diamond $6,000, Food $150, Building $300.
- /dbproperty list and /dbproperty <cottage|village|large|mansion|plot>
- /dbsmpsetmoney <player> <amount> (operator permission only)
- Balances and virtual property records saved to plugins/DBSMPEconomy/data.yml.

IMPORTANT LIMITATIONS:
- This project is SOURCE CODE, not a ready-to-upload .jar.
- The property command records a virtual purchase only; it does not automatically build a house, claim land, or prevent griefing.
- This starter economy does not yet pay minigame rewards automatically.
- This plugin is standalone and does not hook into Vault.
- Paper/Spigot 1.8.8 is old. Test on a backup first.

Build:
Requires Java 8-compatible build setup and Maven with internet access to download the Spigot API.
From this folder run:
  mvn package
The output JAR will be:
  target/DBSMPEconomy-1.0.0.jar

Install:
1. Stop the server.
2. Back up the world and plugins folder.
3. Upload target/DBSMPEconomy-1.0.0.jar into the server's plugins folder.
4. Start the server and check console for "DBSMP Economy enabled".
5. Test /money and /dbshop.
6. Keep OP permissions restricted to trusted admins.

Commands:
  /money
  /pay <player> <amount>
  /dbshop
  /dbproperty list
  /dbproperty <cottage|village|large|mansion|plot>
  /dbsmpsetmoney <player> <amount>   (admin permission)

No real-money transactions are included; all money is in-game currency.


ONLINE BUILD OPTION (Chromebook):
1. Extract this ZIP on your Chromebook.
2. On GitHub, create a new repository (for example, dbsmp-economy). Do not add a README or license during creation.
3. Open the new repository, choose Add file > Upload files, and upload the extracted project files and folders, including the hidden .github folder. If the Chromebook file picker hides .github, use the browser's upload flow or create the workflow file manually at .github/workflows/build.yml using the included build.yml content.
4. Commit the uploaded files to the main branch.
5. Open the repository's Actions tab and select "Build DBSMP Economy plugin". If needed, click Run workflow.
6. Open the completed green workflow run. Under Artifacts, download DBSMPEconomy-1.0.0 and extract it to get DBSMPEconomy-1.0.0.jar.
7. Stop your server, back up the plugins folder, upload the JAR directly into plugins (not the ZIP), then start the server and check the console.

This workflow has not been run here; a successful green run is required before installing the JAR. GitHub account sign-in and internet access are needed.
