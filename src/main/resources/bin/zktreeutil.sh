#!/bin/bash

echo Uadmin

set cp=".;../lib/*"
java -cp $cp com.dobrunov.zktreeutil.zkTreeUtilMain $@
