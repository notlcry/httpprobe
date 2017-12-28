#!/bin/bash

echo `date +%F\ %T`' stop probe ...'
PID=`ps aux|grep java|grep httpProbe.jar|awk '{print $2}'`
if [ $PID ] ; then
    kill -9 $PID
fi
