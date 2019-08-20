# -*- coding: utf-8 -*-
A = [1000, 2500, 5000] #cantidad de nodos
B = [0.5, 0.6, 0.7] # porcentaje de peers que se le envian tareas
C = [0.3] # porcentaje de peers que vuelven a pedir tareas
D = [0.6, 0.7, 0.8] #Threshold de consenso
E = [60000] # ventana de tiempo para calcular score
F = [0.3] # porcentaje de usuarios expertos
G = [[0.4,0.7]] # probabilidad de elegir opcion correcta [normal, experto]
H = [25] #tamaño de cache
I = [20] #segundos a esperar mientras se agregan respuestas locales en los nodos antes de enviar al servidor


countA = 1
for a in A:
    countB = 1
    for b in B:
        countC = 1
        for c in C:
            countD = 1
            for d in D:
                countE = 1
                for e in E:
                    countF = 1
                    for f in F:
                        countG = 1
                        for g in G:
                            countH = 1
                            for h in H:
                                countI = 1
                                for i in I:
                                    salida = open("A"+str(countA)+"B"+str(countB)+"C"+str(countC)+"D"+str(countD)+"E"+str(countE)+"F"+str(countF)+"G"+str(countG)+"H"+str(countH)+"I"+str(countI)+".cfg","w")
                                    salida.write("PARAMA "+str(a)+"\n")
                                    salida.write("PARAMB "+str(b)+"\n")
                                    salida.write("PARAMC "+str(c)+"\n")
                                    salida.write("PARAMD "+str(d)+"\n")
                                    salida.write("PARAME "+str(e)+"\n")
                                    salida.write("PARAMF "+str(f)+"\n")
                                    salida.write("PARAMG1 "+str(g[0])+"\n")
                                    salida.write("PARAMG2 "+str(g[1])+"\n")
                                    salida.write("PARAMH "+str(h)+"\n")
                                    salida.write("PARAMI "+str(i)+"\n")
                                    salida.write("""


CYCLE  100000
CYCLES 1000 #10^6
SIZE PARAMA #100
M 14
SUCC_SIZE 4

#buscar mas info sobre estos delay!!
MIN_DELAY 29 #milisegundos
MAX_DELAY 34 #milisegundos

random.seed 12345678
simulation.endtime CYCLE*CYCLES
#simulation.logtime CYCLE

simulation.experiments 1

network.size SIZE
#trabajar con TTL cuando un nodo se cae esperar cierto tiempo, y asumir que la respuesta no llego
#evaluar si las respuestas esperadas llegan a un concenso.
#cache LRU

#los tiempos de transferencia de una imagen y lista de id deben ser distintos,
#la imagen demora mas en enviar
#hacer eso jugando con cuanto tiempo esperar para enviar la imagen

#costo de salir al servidor es mayor a transferir alpha 0.2 por ejemplo
#penalizar con tiempos al momento de enviar un mensaje.

#buscar tiempos al momento de salir de la red p2p

#ranking de peers, que tan buenos son esos peer:
#tener un historial con sus tiempos de respuesta por ejemplo
#no siempre se puede sobrecargar a los peer que siempre responden bien.
#recalcular constantemente el puntaje de cada peer. cada cuanto tiempo?


protocol.tr UniformRandomTransport
{
	mindelay MIN_DELAY
	maxdelay MAX_DELAY
}

protocol.chord ChordProtocol
{
	transport tr
}

protocol.tesis ProtocoloAcuerdo2
{
   cacheSize PARAMH
   #directorio /home/mmanriquez/Imágenes/labelme/lfw
   directorio /home/mmanriquezl/lfw
}


control.traffic SolicitadorTareas
{
	protocol tesis
	users PARAMA*PARAMB
	step 100
	from 0
	until 9
}

init.constantes Constantes
{
	tasks 10
	threshold_r 10
	threshold_c PARAMD
	continuar PARAMC
	init_delay 1.5

	prob_experto PARAMF

	#por ahora 1.5, pero hay que probar con mas pesos
	peso 1.5

	#Min       : 0                    milisegundos
	#Max       : 82715000             milisegundos
	#Promedio  : 4158.0               milisegundos
	#Varianza  : 6.149232183200805E10 milisegundos
	#Desv. Est.: 247976.45418871538   milisegundos
	#Errores   : 2570
	#Total     : 114168

	min_task_delay 100
	max_task_delay 20000

	#http://www.verizonenterprise.com/about/network/latency/#vsat
	#latencias de un ping de 64bytes dentro de estados unidos
	#promedio todos los ping en un mes
	min_com_delay MIN_DELAY
	max_com_delay MAX_DELAY

	#se asume un servidor en EEUU, y un ping de 64 bytes demora eso
	#segun http://www.cloudping.info/
	min_server_delay 154
	max_server_delay 188

	#1 ping es un mensaje de 64 bytes
	com_size 64
	consenso_solvers 5

	prob_experto_correcta PARAMG2
	prob_normal_correcta PARAMG1

	alpha 1.0
	beta 1.0
	gamma 1.0
	delta 1.0

	#cuanto tiempo esperar antes de verificar si se reenvio la lista de tareas
	ttl_resend PARAMI*1000
	max_respuestas 20 #consenso_solvers*4
}

init.create ChordInitializer
{
	protocol chord
	miprotocolo tesis
	transport tr
	version A"""+str(countA)+"B"+str(countB)+"C"+str(countC)+"D"+str(countD)+"E"+str(countE)+"F"+str(countF)+"G"+str(countG)+"H"+str(countH)+"I"+str(countI)+"\n""""

}

control.maintain ChordMaintainer
{
	step 10500
}



control.observer ResultObserver
{
	protocol chord
    version A"""+str(countA)+"B"+str(countB)+"C"+str(countC)+"D"+str(countD)+"E"+str(countE)+"F"+str(countF)+"G"+str(countG)+"H"+str(countH)+"I"+str(countI)+"\n""""
	#step 90000
	at CYCLE*CYCLES
	FINAL
}

control.queueObserver QueueObserver
{
	protocol tesis
	step PARAME
	#at CYCLE

}


control.lnkob LinkObserver
{
	protocol chord
	FINAL
	at CYCLES
}""")

                                    salida.close()
                                    countI+=1
                                countH+=1
                            countG+=1
                        countF+=1
                    countE+=1
                countD+=1
            countC+=1
        countB+=1
    countA+=1
