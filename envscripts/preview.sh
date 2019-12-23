
#!/bin/bash
sed -i "s~NEW_DB_HOST~10.74.239.191~g;s~NEW_DB_USER~mraqa~g;s~NEW_DB_PWD~dost1234~g;s~NEW_DB_SERVICE~mradb~g;s~TDS_HOSTNAME~10.16.0.14:32090" config/3DSProperties.properties

