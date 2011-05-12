#!/bin/sh
# Demonstration of format string pipes. Sets the xterm title to the 2nd argument,
# and returns the first  unchanged.
#
# this sets the title
printf "\033]0;$2\007" > /dev/tty
echo "$1"