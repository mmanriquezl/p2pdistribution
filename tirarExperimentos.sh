#!/bin/bash
LIB_JARS=`find -L lib/ -name "*.jar" | tr [:space:] :`
#for a in {1..3}
#do
#   for b in {1..3}
#   do
#     for c in 1
#     do
#       for d in {1..3}
#       do
#         for e in 1
#         do
#           for f in 1
#           do
#             for g in 1
#             do
#               for h in 1
#               do
#                 java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
#                 wait
#                 #echo "java -Xmx1g -cp $(LIB_JARS):bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}.cfg"
#               done
#             done
#           done
#         done
#       done
#     done
#   done
#done

###############
# a=1
# b=1
# c=1
# d=1
# e=1
# f=1
# g=1
# h=1
# i=1
# java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
# wait
# a=2
# java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
# wait
# a=3
# java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
# wait
# a=1
# b=2
# java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
# wait
# b=3
# java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
# wait
# b=1
# d=2
# java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
# wait
# d=3
# java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
# wait

#########################3

a=2
b=1
c=1
d=1
e=1
f=1
g=1
h=1
i=1
java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
wait

b=2
java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
wait
b=3
java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
wait

#######################
#d=1
#i=2
#java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
#wait
#i=3
#java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
#wait
#i=4
#java -Xmx8g -cp ${LIB_JARS}:bin peersim.Simulator ./config/A${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.cfg > salidaA${a}B${b}C${c}D${d}E${e}F${f}G${g}H${h}I${i}.txt
#wait
