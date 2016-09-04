#!/bin/bash

#echo Uadmin

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cp="$DIR/../lib/*"
java -cp "$cp" com.dobrunov.zktreeutil.zkTreeUtilMain $@
