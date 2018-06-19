#!/bin/bash

#echo Uadmin

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cp="$DIR/../lib/*"
java -Dlog4j.configuration=file://${DIR}/log4j.properties -cp "$cp" com.dobrunov.zktreeutil.zkTreeUtilMain $@
