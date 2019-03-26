#!/bin/sh
echo "update configuration files with necessary environment variables in merchant remote server"
echo $HOSTNAME
mkdir -p /opt/arcot/logs/$HOSTNAME/
sed -i "s~INSTANCEID~$HOSTNAME~g;s~RS_DB_HOST~$RS_DB_HOST~g;s~RS_DB_PORT~$RS_DB_PORT~g;s~RS_DB_USER_NAME~$RS_DB_USER_NAME~g;s~RS_DB_USER_PWD_ENC~$RS_DB_USER_PWD_ENC~g;s~RS_DB_NAME~$RS_DB_NAME~g;s~RS_CARD_RANGE_DUMP_PATH~$RS_CARD_RANGE_DUMP_PATH~g;s~RS_DUMP_CARD_RANGE~$RS_DUMP_CARD_RANGE~g" /opt/arcot/envscript/env-remote-service.sh
sed -i "s~POD_SPECIFIC_DIRECTORY~$HOSTNAME~g" /opt/arcot/config/log4j2.xml
./envscript/start-remote-service.sh start
tail -f /dev/null
