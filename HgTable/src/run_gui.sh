#!/bin/sh
java -cp ".:lib/*" HgTable --GUIMode true  --script_mode false  --javahome $JAVA_HOME
# while ;
# do
#     java -cp ".:lib/*" HgTable --GUIMode true  --script_mode false  --javahome $JAVA_HOME
# 	  if [$? -eq 0]
# 	  then
# 	      break
#     fi
# done