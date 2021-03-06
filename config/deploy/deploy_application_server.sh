#!/usr/bin/env bash
echo "[[ Performing prechecks ]]"
set -e

BASE="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$BASE"

if [[ "$(whoami)" != "root" ]]; then
  echo "This script requires root permissions (sudo)"
  exit 1
fi

if [[ ! $# -eq 3 ]]; then
  echo "Usage: deploy_application_server.sh 'remote_user_name' 'remote_config_repo_path' 'remote_config_repo_branch'"
  exit 1
fi

# SSH keys
echo "[[ Enabling passwordless remote access ]]"
eval "$(ssh-agent)" > /dev/null
ssh-add "$HOME/.ssh/id_rsa" > /dev/null

# Fix bash files
find ../.. -name "*.sh" ! -name "deploy_*.sh" -exec dos2unix -q {} \;
find ../.. -name "*.sh" ! -name "deploy_*.sh" -exec chmod +x {} \;

# Export useful constants
export ABRAID_SUPPORT_PATH='/var/lib/abraid'
declare -r ABRAID_SUPPORT_PATH
export WEBAPP_PATH='/var/lib/tomcat7/webapps'
declare -r WEBAPP_PATH
export DATA_MANAGER_CRON_SCRIPT_PATH='/etc/cron.daily/abraid'
declare -r DATA_MANAGER_CRON_SCRIPT_PATH
export REMOTE_USER="$1"
declare -r REMOTE_USER
export CONFIG_PATH="$2"
declare -r CONFIG_PATH
export CONFIG_BRANCH="$3"
declare -r CONFIG_BRANCH

# Apply under-construction page
echo "[[ Applying under-construction page ]]"
rm -f "/etc/nginx/sites-enabled/proxy"
rm -f "/etc/nginx/sites-enabled/maintenance"
ln -s "/etc/nginx/sites-available/maintenance" "/etc/nginx/sites-enabled/maintenance"
service nginx restart > /dev/null

# Stop servlet containers
echo "[[ Stopping services ]]"
service tomcat7 stop > /dev/null
service gunicorn stop > /dev/null
echo -e "#\x21/bin/sh\n\n:" > "$DATA_MANAGER_CRON_SCRIPT_PATH"

# Source useful functions
source "functions.sh"

# Checking for dir
if [[ ! -d "$ABRAID_SUPPORT_PATH" ]]; then
  echo "[[ Creating support directory ]]"
  mkdir -p "$ABRAID_SUPPORT_PATH"
  permissionFix "tomcat7" "tomcat7" "$ABRAID_SUPPORT_PATH"
fi

# Getting config
echo "[[ Updating ABRAID configuration ]]"
. up_config.sh "shared" "application"

# Upgrading database
echo "[[ Upgrading database ]]"
. up_db.sh

# Upgrading machinelearning
echo "[[ Upgrading machinelearning ]]"
. up_ml.sh

# Upgrading geoserver
echo "[[ Upgrading geoserver ]]"
. up_gs.sh

# Upgrading modeloutput
echo "[[ Upgrading modeloutput ]]"
. up_mo.sh

# Upgrading publicsite
echo "[[ Upgrading publicsite ]]"
. up_ps.sh

# Upgrading datamanager
echo "[[ Upgrading datamanager ]]"
. up_dm.sh

# Waiting
echo "[[ Main updates complete ]]"
read -p "You should now update all modelling servers before continuing. Press [enter] to continue ..."

# Bring services back up
echo "[[ Restarting services ]]"
service gunicorn start > /dev/null
service tomcat7 start > /dev/null
echo -e "#\x21/bin/sh\n\nsudo -H -u abraid /var/lib/abraid/datamanager/datamanager.sh" > "$DATA_MANAGER_CRON_SCRIPT_PATH"
chmod o+x "$DATA_MANAGER_CRON_SCRIPT_PATH"

# Remove under-construction page
echo "[[ Removing under-construction page ]]"
rm -f "/etc/nginx/sites-enabled/proxy"
rm -f "/etc/nginx/sites-enabled/maintenance"
ln -s "/etc/nginx/sites-available/proxy" "/etc/nginx/sites-enabled/proxy"
service nginx restart > /dev/null

echo "[[ Done ]]"
echo "You may now delete the artifacts directory"
