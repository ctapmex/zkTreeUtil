#!/bin/bash

echo Uadmin

cp=".:../lib/*"
java -cp $cp com.dobrunov.zktreeutil.zkTreeUtilMain $@
