#!/bin/sh
java -cp ".:lib/*" HgTable --script_mode true  --javahome $JAVA_HOME
# while ;
# do
#    java -cp ".:lib/*" HgTable --script_mode true  --javahome $JAVA_HOME
# 	 if [$? -eq 0]
# 	 then
# 	     break
# 	 fi
# done