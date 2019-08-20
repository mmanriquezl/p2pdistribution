package chord;

import java.io.File;
import java.lang.NullPointerException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.PriorityQueue;
import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

import peersim.edsim.EDSimulator;
import java.math.BigInteger;
import java.lang.NullPointerException;
import peersim.core.CommonState;
import java.util.Iterator;
import java.util.Comparator;
import java.util.Map;
import java.util.LinkedList;
import java.util.Queue;

//Protocolo que representa la agregacion de los datos al enviar etiquetas al servidor
//y el acuerdo que se debe generar en el servidor al momento de evaluar
//las etiquetas enviadas

public class ProtocoloAcuerdo2 implements EDProtocol {
	private static final String PAR_CSIZE = "cacheSize";
   private static final String PAR_DIR = "directorio";



   private final String dir;

   public Node node;



	//la cache de imagenes que guarda cada nodo encargado de esas imagenes.
	//Quiza deberia usar una TLRU: Time aware Least Recently Used
	//ya que se usa bastante en redes distribuidas

   //cache<llave, valor>: en este caso la llave es el nombre de la imagen pasada por la funcion de hash
   //y el valor puede ser un objeto imagen,
 	 public CacheLRU<String, File> cache;
   public Servidor servidor;
   public Usuario usuario;

	 //solo la va a tener el servidor instanciada;
	 Queue<Object> colaISP;
	 boolean transmitiendoISP;

	 //cola com servidor
	 Queue<Object> colaComSERVER;
	 boolean recibiendoComSERVER;

	public ProtocoloAcuerdo2(String prefix){
		int n = Configuration.getInt(prefix + "." + PAR_CSIZE);
      dir = Configuration.getString(prefix + "." + PAR_DIR);



		cache = new CacheLRU<String, File>(n);//el tamaño de la cache es fijo!!!!!
      servidor = null;
 	 }

   public void debug(String mensaje){
      System.out.println("("+CommonState.getTime()+")["+node.getID()+"]: "+mensaje);
	 }

   public void setServidor(){
      debug("El nodo "+node.getID()+" es el servidor");
      servidor = new Servidor(node, dir);
			colaISP = new LinkedList<>();
			transmitiendoISP = false;

			colaComSERVER = new LinkedList<>();
			recibiendoComSERVER = false;
   }

   public void setUsuario(){
      usuario = new Usuario(this.node.getID());
			if(!Utils.SCORES.containsKey(this.node.getID())){
				Utils.SCORES.put(this.node.getID(), new ScoreUser());
			}
   }
	@Override
	public void processEvent(Node nodo, int pid, Object evento) {
		Utils.holder.reset();
		Mensaje mm = (Mensaje)evento;
		switch(mm.getLabel()){
			 case "RE_INIT":
				 Utils.RE_INIT++;
				 inicializarVoluntario();
				 break;
			 //soy un voluntario que quiere participar
			 case "INIT":
			   Utils.INIT++;
				 inicializarVoluntario();
				 break;
			 //soy el servidor y me estan pidiendo las listas de tareas (deberia llegar solo al nodo 0: SERVIDOR)
			 case "RQST_TASKS":
			 	 Utils.RQST_TASKS++;
				 encolarProcesamientoSERVER(mm);
				 break;
			 //soy el servidor y me llegaron las etiquetas desde un usuario
			 case "SEND_LABELS":
			 	 Utils.SEND_LABELS++;
				 encolarProcesamientoSERVER(mm);
				 break;
			 //al servidor le estan pidiendo una imagen, deberia llegarle solo al nodo 0: SERVIDOR
			 case "RQST_SERVER_IMAGE":
			 	 Utils.RQST_SERVER_IMAGE++;
				 //crearLista(mm);
				 encolarProcesamientoSERVER(mm);
				 break;
			 case "PROCESS_DEQUEUE":
			 	 Utils.PROCESS_DEQUEUE++;
				 debug("DESENCOLANDO");
				 if(servidor.colaEspera.isEmpty()){
					 debug("ERROR: COLA ESTABA VACIA!!!!!!!!!!!!!!!!1");
				 }
				 else{
					 sacarTareaColaProcesamientoServer(pid);
				 }
				 break;

			 case "AGGREGATE":
			   Utils.AGGREGATE++;
				 debug("AGGREGATE");
				 encolarAgregacion(mm);
				 break;

			 case "AGGREGATE_DEQUEUE":
			 	 Utils.AGGREGATE_DEQUEUE++;
				 debug("AGGREGATE_DEQUEUE");
				 sacarAggregationTask(mm);
				 break;


				 /**********
					*  ISP_  *
					**********/
			 //soy el servidor y me estan pidiendo las listas de tareas (deberia llegar solo al nodo 0: SERVIDOR)
		 	 case "ISP_RQST_TASKS":
			 	 Utils.ISP_RQST_TASKS++;
				 encolarISP(mm);
				 break;
				 //soy el servidor y me llegaron las etiquetas desde un usuario
			 case "ISP_SEND_LABELS":
			 	 Utils.ISP_SEND_LABELS++;
				 encolarISP(mm);
				 break;
				 //al servidor le estan pidiendo una imagen, deberia llegarle solo al nodo 0: SERVIDOR
			 case "ISP_RQST_SERVER_IMAGE":
			 	  Utils.ISP_RQST_SERVER_IMAGE++;
					//crearLista(mm);
					encolarISP(mm);
					break;


				 case "ISP_DEQUEUE":
				 	 Utils.ISP_DEQUEUE++;
					 if(colaISP.isEmpty()){
						 debug("ERROR: COLA ISP ESTABA VACIA");
					 }
					 else{
						 sacarTareaColaISP(pid);
					 }
					 break;


				 /*******************
					*  COLA SERVIDOR  *
				 /*******************/

				 case "COM_RQST_TASKS":
				 	 Utils.COM_RQST_TASKS++;
					 encolarComunicacionServidor(mm);
					 break;
				 case "COM_SEND_LABELS":
				 	 Utils.COM_SEND_LABELS++;
					 encolarComunicacionServidor(mm);
					 break;
				 case "COM_RQST_SERVER_IMAGE":
				 	 Utils.COM_RQST_SERVER_IMAGE++;
					 encolarComunicacionServidor(mm);
					 break;

				 case "COM_SERVER_DEQUEUE":
				 	 Utils.COM_SERVER_DEQUEUE++;
					 if(colaComSERVER.isEmpty()){
						 debug("ERROR: COLA COM SERVER ESTABA VACIA");
					 }
					 else{
						 sacarColaComSERVER();
					 }
					 break;

				 //estan llegando las tareas
				 case "RSPNS_TASKS":
				 	 Utils.RSPNS_TASKS++;
					 llegoLista(mm);
					 break;


				 //me llegaron tareas del nodo anterior en el arbol
				 //case "RESEND_ANSWERS":
				 // Utils.RESEND_ANSWERS++;
				 //	 encolarRespuestas(mm);
				 //	 break;

				 case "TTL_RESEND":
				 	 Utils.TTL_RESEND++;
					 reenviarRespuestas(mm);
					 break;

				 case "COM_RQST_IMAGE":
				 	 Utils.COM_RQST_IMAGE++;
				 	 encolarPeticionImagen(mm);
					 break;
				 case "RQST_IMAGE_DEQUEUE":
				 	 Utils.RQST_IMAGE_DEQUEUE++;
				 	 sacarPeticionImagen(mm);
					 break;
				 //Se esta pidiendo una imagen a este peer
				 case "RQST_IMAGE":
				 	 Utils.RQST_IMAGE++;
					 buscarImagen(mm);
					 break;
				 //soy el nodo que pidio una imagen y me llego desde el peer encargado de esa imagen
				 case "RSPNS_IMAGE":
				 	 Utils.RSPNS_IMAGE++;
					 recibirImagen(mm);
					 break;
				 //soy el nodo encargado de una imagen, y me llego la imagen que pedi al servidor
				 case "RSPNS_SERVER_IMAGE":
				 	 Utils.RSPNS_SERVER_IMAGE++;
					 procesarImagen(mm);
					 break;
				 //soy un peer bien evaluado y necesito procesar mas imagenes
				 case "RESEND_TASKS":
				 	 Utils.RESEND_TASKS++;
					 llegoLista(mm);//uso la misma funcion?????
					 break;
				 case "MORE_TASKS":
				 	 Utils.MORE_TASKS++;
					 agregarMasTareas(mm);
					 break;
		 }

	}

   public void setNode(Node n){
      this.node = n;
   }

	 /************************
	 *  COSAS COLA SERVIDOR  *
	 ************************/
	 public void encolarComunicacionServidor(Mensaje msj){
		 if(Utils.SERVER_ID==this.node.getID()){
				colaComSERVER.add(msj);

				debug("Encolando peticion de tareas de "+msj.getSender().getID()+" en COM servidor");
				if(!recibiendoComSERVER){
					Mensaje msje = new Mensaje();
					msje.setLabel("COM_SERVER_DEQUEUE");
					//msje.setContenido(msj);
					recibiendoComSERVER = true;
					EDSimulator.add(0, msje, Utils.SERVER, Utils.MID);
				}
				//sendKnownPeer(nuevoMsj, sender);
		 }
		 else{
			 debug("ENCOLANDO MENSAJES AL SERVIDOR Y NO SOY EL SERVIDORR :((((((");
		 }
		 return;
	 }

	 public void sacarColaComSERVER(){
		 if(Utils.SERVER_ID==this.node.getID()){
			 Mensaje aux = (Mensaje) colaComSERVER.poll();
	 		 int comServerDelay = 0;



	 		 switch(aux.getLabel()){
	 			 case "COM_RQST_TASKS":
	 				 aux.setLabel("RQST_TASKS");

	 				 break;
	 			 case "COM_SEND_LABELS":
					 aux.setLabel("SEND_LABELS");

	 				 break;
	 			 case "COM_RQST_SERVER_IMAGE":
					 aux.setLabel("RQST_SERVER_IMAGE");

	 				 break;
	 		 }

			 //TODO: cuanto debe demorar?
			 EDSimulator.add(0, aux, Utils.SERVER, Utils.MID);



			 if(!colaComSERVER.isEmpty()){
	 			 Mensaje msje = new Mensaje();
	 			 msje.setLabel("COM_SERVER_DEQUEUE");
	 			 EDSimulator.add(comServerDelay, msje, Utils.SERVER, Utils.MID);
	 		 }
	 		 else{
	 			 recibiendoComSERVER = false;
	 		 }
		 }
		 else{
			 debug("DESENCOLANDO MENSAJES COM DEL SERVIDOR Y NO SOY EL SERVIDOR :((((((((((((((((");
		 }
	 }




	 /**************
	 *  COSAS ISP  *
	 ***************/
	 public void encolarISP(Mensaje msj){
			if(Utils.SERVER_ID==this.node.getID()){
				 colaISP.add(msj);

				 debug("Encolando peticion de tareas de "+msj.getSender().getID()+" en ISP");
				 if(!transmitiendoISP){
					 Mensaje msje = new Mensaje();
	         msje.setLabel("ISP_DEQUEUE");
					 //msje.setContenido(msj);
					 transmitiendoISP = true;
	    		 EDSimulator.add(0, msje, Utils.SERVER, Utils.MID);
				 }
         //sendKnownPeer(nuevoMsj, sender);
      }
			else{
				debug("ENCOLANDO MENSAJES AL SERVIDOR Y NO SOY EL ISP!!!!!!!!");
			}
      return;
		}
		public void sacarTareaColaISP(int pid){
		 if(Utils.SERVER_ID==this.node.getID()){
			 Mensaje aux = (Mensaje) colaISP.poll();
	 		 int comServerDelay = 0;


			 int tipo = -1;
	 		 switch(aux.getLabel()){
	 			 case "ISP_RQST_TASKS":
	 				 aux.setLabel("COM_RQST_TASKS");
					 tipo = 1;
	 				 break;
	 			 case "ISP_SEND_LABELS":
					 aux.setLabel("COM_SEND_LABELS");
					 tipo = 5;
	 				 break;
	 			 case "ISP_RQST_SERVER_IMAGE":
					 aux.setLabel("COM_RQST_SERVER_IMAGE");
					 tipo = 3;
	 				 break;
	 		 }
			 comServerDelay = Utils.serverDelay();
			 Utils.addLatenciaServidor(comServerDelay, tipo, aux.getSize());
			 //TODO: esta bien que se demore comServerDelay ??
			 EDSimulator.add(comServerDelay, aux, Utils.SERVER, pid);



	 		 if(!colaISP.isEmpty()){
	 			 Mensaje msje = new Mensaje();
	 			 msje.setLabel("ISP_DEQUEUE");
				 //TODO: esta bien que se demore comServerDelay ??
	 			 EDSimulator.add(comServerDelay, msje, Utils.SERVER, pid);
	 		 }
	 		 else{
	 			 transmitiendoISP = false;
	 		 }
		 }
		 else{
			 debug("DESENCOLANDO MENSAJES AL SERVIDOR Y NO SOY EL ISP!!!!!!!!");
		 }


 	 }




   /******************
    * COSAS SERVIDOR *
    ******************/
		public void encolarProcesamientoSERVER(Mensaje msj){
       if(Utils.SERVER_ID==this.node.getID()){
 				 servidor.encolar(msj);
 				 debug("Encolando peticion de tareas de "+msj.getSender().getID()+" en procesamiento de servidor");
 				 if(!servidor.ejecutando){
 					 Mensaje msje = new Mensaje();
 	         msje.setLabel("PROCESS_DEQUEUE");
 					 //msje.setContenido(msj);
 					 servidor.ejecutando = true;
 	    		 EDSimulator.add(0, msje, Utils.SERVER, Utils.MID);
 				 }
          //sendKnownPeer(nuevoMsj, sender);
       }
 			else{
 				debug("ME ESTAN PIDIENDO TAREAS Y NO SOY EL SERVIDOR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
 			}
       return;
    }
		public void sacarTareaColaProcesamientoServer(int pid){

	 		 /*
	 			 String texto = colaEspera.poll();
	 			 debug(texto);

	 			 if(!colaEspera.isEmpty()){
	 				 Mensaje msje = new Mensaje();
	 				 msje.setLabel("SACA");
	 				 EDSimulator.add(100, msje, node, pid);
	 			 }
	 			 else{
	 				 ejecutando = false;
	 			 }
	 		 */
	 		 Mensaje aux = (Mensaje) servidor.desencolar();
	 		 int processDelay = 0;
	 		 switch(aux.getLabel()){
	 			 case "RQST_TASKS":
	 				 processDelay = crearLista(aux);
	 				 break;
	 			 case "SEND_LABELS":
	 				 processDelay = procesarEtiquetas(aux);
	 				 break;
	 			 case "RQST_SERVER_IMAGE":
	 				 processDelay = servirImagen(aux);
	 				 break;
	 		 }




	 		 if(!servidor.colaEspera.isEmpty()){
	 			 Mensaje msje = new Mensaje();
	 			 msje.setLabel("PROCESS_DEQUEUE");
	 			 EDSimulator.add(processDelay, msje, Utils.SERVER, pid);
	 		 }
	 		 else{
	 			 servidor.ejecutando = false;
	 		 }
	 	 }
    //soy el servidor y me estan pidiendo las listas de tareas (deberia llegar solo al nodo Util.SERVER)
		public int crearLista(Mensaje msj){
			int comDelay = 0;

			int procDelay = 1;//sacar 10 elementos de una cola de prioridad de 3000 elementos, y volverlos a poner
			Utils.addUso(Utils.SERVER_ID, (long)procDelay);

			ListaTareas enviar = new ListaTareas();
			Node sender = msj.getSender();
			enviar.setEnvio();

			//costo de crear la lista de TAREAS
			enviar.setTareas(servidor.getTareasOrdenadas(Constantes.TASKS));

			debug("El nodo "+sender.getID()+" esta pidiendo tareas.");
			if(enviar.tareas.size()>0){
				debug("Enviando: "+enviar.toString());
				Utils.SCORES.computeIfPresent(sender.getID(), (id, obj)-> obj.addEnviadas(enviar.tareas.size()));

				//enviar esas 10 tareas al nodo que las solicitó
				Mensaje nuevoMsj = new Mensaje();
				nuevoMsj.setLabel("RSPNS_TASKS");
				nuevoMsj.setContenido(enviar, enviar.getSize());


				//buscar un usuario más lento que este usuario
				//nuevoMsj.siguiente = Utils.buscarSiguiente(sender);

				comDelay += Utils.serverDelay();
				Utils.addLatenciaServidor(comDelay, 2, nuevoMsj.getSize());
				EDSimulator.add(comDelay + procDelay, nuevoMsj, sender, Utils.MID);
			}
			else{
				debug("PERO NO HAY MAS TAREAS EN EL SERVIDOR.");
			}
			return procDelay;
	 }
   //soy el servidor y me llegaron las etiquetas desde un usuario
	 public int procesarEtiquetas(Mensaje msj){
		 	Node sender = msj.getSender();

			ListaTareas respuestas = (ListaTareas) msj.getContenido();
			respuestas.setRecepcion2();

			Utils.SCORES.computeIfPresent(sender.getID(), (id, obj)-> obj.addU2SDelay(respuestas.getDeltaU2S()));

			//al llegar las respuestas se cambia el valor del peso asignado!!!!!!!!!!!!!!!!!!!!

			String salida = "Me llegaron las respuestas de "+sender.getID() + "("+respuestas.tareas.size()+")";

			for(int i = 0; i < respuestas.tareas.size(); i++){
				salida +=" - ("+respuestas.tareas.get(i).id+")"+respuestas.tareas.get(i).respuestasToString();
			}
			debug(salida);


			//TODO: el score no es del usuario que envio las tareas!!!
			// se debe sacar el id del usuario del objeto respuesta en el arreglo de respuestas dentro de la tarea

			for(int i = 0; i < respuestas.tareas.size(); i++){
				for(Respuesta r: respuestas.tareas.get(i).respuestas){
					Utils.SCORES.computeIfPresent(r.idUsuario, (id, obj)-> obj.addRespondidas(1));
				}
			}
			//Utils.SCORES.computeIfPresent(sender.getID(), (id, obj)-> obj.addRespondidas(respuestas.tareas.size()));


			//costo de procesar las respuestas
			int procDelay = Utils.getCostoAgregar();
			Utils.addUso(Utils.SERVER_ID, procDelay);
			ArrayList<Tarea> sinConsenso = servidor.agregarRespuestas(respuestas);
			Utils.logRespuesta(respuestas.tareas.size());

			if(sinConsenso.size()>0){
				//elegir el usuario con puntaje mas alto de la ultima ventana
				/*
				double mayor = 0.0;
				long idMejor = -1;
				Iterator it = Utils.SCORES.entrySet().iterator();
				Comparator<Par<Long, Double>> scoreComparator = new Comparator<Par<Long, Double>>(){
					@Override
					public int compare(Par<Long, Double> p1, Par<Long, Double> p2) {
						if (p1.getValue() < p2.getValue()){
							return 1;
						}
						if (p1.getValue() > p2.getValue()){
							return -1;
						}
						return 0;
					}
				};
				//PriorityQueue<Par<Long, Double>> topUsers = new PriorityQueue<Par<Long, Double>>(5, scoreComparator);

		    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
					double puntaje = ((ScoreUser)pair.getValue()).score();
					topUsers.add(new Par(pair.getKey(), puntaje));
		    }
				*/

				//preparando la lista de tareas a enviar
				ListaTareas enviar = new ListaTareas();
				enviar.setEnvio();
				//debo clonar estas tareas sin consenso, una copia para cada usuario al que le envio
				//para resolver el conflicto
				enviar.setTareas(sinConsenso);

				int comDelay = 0;
				int auxProcDelay = procDelay;
				Utils.updateTopUsers();
				//enviando mensajes a los usuarios seleccionados
				for(int i = 0; i < Constantes.CONSENSO_SOLVERS; i++){
					Par<Node, Double> usuario = Utils.TOPUSERS.get(Utils.colaActual).poll();
					if(usuario == null){
						debug("Usuario es nulllllllllllll en la cola "+Utils.colaActual+" :O");
					}


					Node user = (Node)usuario.getKey();
					debug("Se saco el nodo "+user.getID());
					Utils.SCORES.computeIfPresent(user.getID(), (id, obj)-> obj.addEnviadas(sinConsenso.size()));

					Mensaje nuevoMsj = new Mensaje();

					nuevoMsj.setLabel("MORE_TASKS");
					nuevoMsj.setContenido(enviar.clone(), enviar.getSize());
					nuevoMsj.siguiente = Utils.buscarSiguiente(user);
					debug("Enviando al usuario ("+user.getID()+", score:"+usuario.getValue()+") "+enviar.tareas.size()+" tareas");
					int auxComDelay = Utils.serverDelay();
					comDelay += auxComDelay;
					Utils.addLatenciaServidor(auxComDelay, 6, nuevoMsj.getSize());
					EDSimulator.add(comDelay + auxProcDelay, nuevoMsj, user, Utils.MID);

					//el uso generado antes del for solo se aplica como delay la primera vez
					//luego solo uso como delay la secuencializacion de enviar un mensaje
					//por la red
					auxProcDelay = 0;
				}
			}

			//hago que el servidor reenvie las tareas sin consenso
			// ó
			//hago que los usuarios pidan más tareas naturalmente
			//NO AMBAS!!!!


			/*if(respuestas.pideMas()){
				ListaTareas enviar = new ListaTareas();
				enviar.setEnvio();
				debug("El nodo "+sender.getID()+" esta pidiendo MAS tareas.");

				enviar.setTareas(servidor.getTareasOrdenadas(Constantes.TASKS));
				if(enviar.tareas.size()>0){
					debug("Enviando: "+enviar.toString());

					//enviar esas 10 tareas al nodo que las solicitó
					Mensaje nuevoMsj = new Mensaje();
					nuevoMsj.setLabel("RESEND_TASKS");
					nuevoMsj.setContenido(enviar);
					//sendKnownPeer(nuevoMsj, sender);
					int delay = Utils.serverDelay();
					Utils.addLatenciaServidor(delay);
					EDSimulator.add(delay + uso, nuevoMsj, sender, Utils.MID);
				}
				else{
					debug("PERO NO HAY MAS TAREAS.");
				}
			}*/

			return procDelay;
   }
   //al servidor le estan pidiendo una imagen, deberia llegarle solo al nodo: SERVIDOR
   public int servirImagen(Mensaje msj){
      //busco la imagen en disco
      Par aux = (Par)msj.getContenido();
      File abuscar = (File) aux.getValue();
      Node sender = msj.getSender();
      debug("El nodo "+sender.getID()+" esta pidiendo la imagen "+abuscar.getName()+" ("+abuscar.length()+") bytes");
      Mensaje nuevoMsj = new Mensaje();
      nuevoMsj.setLabel("RSPNS_SERVER_IMAGE");
      //nuevoMsj.setContenido(abuscar);
      nuevoMsj.setContenido(aux, abuscar.length());
			int procDelay = Utils.getUsoDiscoServidor(abuscar.length());
			Utils.addUso(Utils.SERVER_ID, procDelay);
			long comDelay = Utils.fakeServerImageDelay(abuscar.length());
			Utils.addLatenciaServidor(comDelay, 4, nuevoMsj.getSize());
      EDSimulator.add(comDelay + procDelay , nuevoMsj, sender, Utils.MID);
			return procDelay;
   }


   /******************
    * COSAS USUARIOS *
    ******************/
    //soy un voluntario que quiere participar
    public void inicializarVoluntario(){
			Mensaje msj = new Mensaje();
      msj.setLabel("ISP_RQST_TASKS");
      msj.setSender(node);
			//int delay = Utils.serverDelay();
			debug("pidiendo tareas");
			int comLocalDelay = Utils.localDelay();//es local por que primero pasa al ISP
			//Utils.addLatenciaServidor(comLocalDelay, 1);
			Utils.addLatenciaRed(comLocalDelay, node.getID(), 1,msj.getSize());
      EDSimulator.add(comLocalDelay , msj, Utils.SERVER, Utils.MID);
   }

	 public void encolarAgregacion(Mensaje msj){
		 usuario.encolarAgg(msj);
		 debug("Encolando agregacion de tarea");
		 if(!usuario.agregando){
			 Mensaje mm = new Mensaje();
			 mm.setLabel("AGGREGATE_DEQUEUE");
			 usuario.agregando = true;
			 EDSimulator.add(0,mm,this.node, Utils.MID);

			 Mensaje nuevo = new Mensaje();
		 	 nuevo.setLabel("TTL_RESEND");
		 	 nuevo.setSender(node);
		 	 EDSimulator.add(Constantes.TTL_RESEND, nuevo, this.node, Utils.MID);
		 }

	 }

	 public void sacarAggregationTask(Mensaje msj){
		 Mensaje aux = (Mensaje) usuario.desencolarAgg();
		 debug("Sacando tarea para agregar");
		 //agregar!!
		 ListaTareas lista = new ListaTareas();
		 lista.addTarea((Tarea) aux.getContenido());
		 usuario.colaAgregar.agregarTareas(lista);

		 if(!usuario.colaAgregacion.isEmpty()){
			 Mensaje msje = new Mensaje();
			 msje.setLabel("AGGREGATE_DEQUEUE");
			 EDSimulator.add(0, msje, this.node, Utils.MID);
		 }
		 else{
			 usuario.agregando = false;
		 }
	 }

	 public void sendAggregationToServer(Mensaje msj){

	 }

	 public void encolarPeticionImagen(Mensaje msj){
		 	usuario.encolar(msj);
			debug("Encolando peticion de imagen de "+msj.getSender().getID());
			if(!usuario.ejecutando){
				Mensaje mm = new Mensaje();
				mm.setLabel("RQST_IMAGE_DEQUEUE");
				usuario.ejecutando = true;
				EDSimulator.add(0,mm,this.node, Utils.MID);
			}
	 }

	 public void sacarPeticionImagen(Mensaje msj){
		 Mensaje aux = (Mensaje) usuario.desencolar();
		 debug("Sacando peticion de imagen desde "+aux.getSender().getID());
		 aux.setLabel("RQST_IMAGE");
		 //TODO: cuanto debe demorar?
		 EDSimulator.add(0, aux, this.node, Utils.MID);

		 if(!usuario.colaEspera.isEmpty()){
			 Mensaje msje = new Mensaje();
			 msje.setLabel("RQST_IMAGE_DEQUEUE");
			 EDSimulator.add(0, msje, this.node, Utils.MID);
		 }
		 else{
			 usuario.ejecutando = false;
		 }
	 }

	public void llegoLista(Mensaje msj){
      //ArrayList<Tarea> tareas = (ArrayList<Tarea>)msj.getContenido();
			ListaTareas tareas = (ListaTareas) msj.getContenido();
			tareas.setRecepcion();

			//asigno el nodo siguiente
			if(msj.siguiente!=null){
				usuario.setSiguiente(msj.siguiente);
				debug("Seteando como siguiente"+msj.siguiente.getID());
			}
			else{
				debug("SIGUIENTE ERA NULLLLLLLLL!!!!!!!!!!!!!!!!");
			}

			//puedo agregar el delay a este usuario
			Utils.SCORES.computeIfPresent(this.node.getID(),
			(id, objScore) -> objScore.addS2UDelay(tareas.getDeltaS2U()) );

      debug("Me llegaron "+tareas.tareas.size()+" tareas.");

			if(tareas!=null && tareas.tareas.size()>0){
				//deberian siempre llegarme tareas cuando no tengo ninguna tarea en mi lista
				if(!usuario.getEtiquetando()){
					usuario.setTareas(tareas);
					usuario.setEtiquetando(true);
					Tarea actual = usuario.getActual();
					BigInteger dest = Utils.stringToBI(actual.imagen.getPath());
					Mensaje nuevo = new Mensaje();
					nuevo.setLabel("COM_RQST_IMAGE");
					nuevo.setSender(node);
					nuevo.setContenido(actual.imagen, actual.imagen.length());
					sendUnknownPeer(0,nuevo, dest);
				}
				else{
					usuario.addTareas(tareas.tareas);
					debug("ME LLEGARON TAREAS CUANDO YA ESTABA ETIQUETANDO!!!!!!!!!!!!!!!!!!!!!!!! :O?");
				}
		 }
	}



	/*public void encolarRespuestas(Mensaje msj){
		ListaTareas lista = (ListaTareas) msj.getContenido();
    debug("#######################Me llegaron "+lista.tareas.size()+" para encolar desde "+msj.sender.getID());
		debug("TAREAS");
		for(int i = 0; i < lista.tareas.size(); i++){
			debug("("+lista.tareas.get(i).id+")"+lista.tareas.get(i).respuestasToString());
		}
		//si no tengo tareas encoladas, agrego esta lista a la cola y listo
		if(usuario.colaAgregar.tareas.isEmpty()){
			debug("#############Y mi cola estaba vacia");
		}
		else{
			debug("#################Y mi cola ya tenia tareas");
		}

		usuario.colaAgregar.agregarTareas(lista);
		debug("TAREAS AGREGADAS");
		for(int i = 0; i < usuario.colaAgregar.tareas.size(); i++){
			debug("("+usuario.colaAgregar.tareas.get(i).id+")"+usuario.colaAgregar.tareas.get(i).respuestasToString());
		}

		//TODO: si luego de un TTL no se ha vaciado la colaAgregar, enviarla al servidor!!!
		//enviar un mensaje a si mismo como medio para contar el TTL???
		Mensaje nuevo = new Mensaje();
		nuevo.setLabel("TTL_RESEND");
		nuevo.setSender(node);
		EDSimulator.add(Constantes.TTL_RESEND, nuevo, this.node, Utils.MID);
	}*/

	public void reenviarRespuestas(Mensaje msj){
		if(!usuario.colaAgregar.tareas.isEmpty()){
			debug("El TTL acabo y no se habían enviado las tareas");
			Utils.TTL_RESEND_OK++;
			Mensaje nuevo = new Mensaje();
			nuevo.setLabel("ISP_SEND_LABELS");
			nuevo.setSender(node);
			ListaTareas aux = usuario.colaAgregar.clone();
			for(Tarea t:usuario.colaAgregar.tareas){
				int pos = aux.estaEn(t.id);
				if(pos >=0){
					for(Respuesta r: t.respuestas){
						aux.tareas.get(pos).respuestas.add(r);
					}
				}
			}
			nuevo.setContenido(aux, aux.getSizeRespondida());
			usuario.colaAgregar.vaciar();
			//int delay = Utils.serverDelay();
			//Utils.addLatenciaServidor(delay, 5);
			int localComDelay = Utils.localDelay();
			Utils.addLatenciaRed(localComDelay, node.getID(),2, nuevo.getSize());

			//en vez de enviarla directamente al servidor, podria enviarla al nodo siguiente noma??
			EDSimulator.add(localComDelay, nuevo, Utils.SERVER, Utils.MID);
		}
		else{
			debug("El TTL acabo y no tenia tareas que enviar :DDDD");
			Utils.TTL_RESEND_FAIL++;
		}
	}

	public void agregarMasTareas(Mensaje msj){
		ListaTareas lista = (ListaTareas) msj.getContenido();
		lista.setRecepcion();

		Utils.SCORES.computeIfPresent(this.node.getID(),
		(id, objScore) -> objScore.addS2UDelay(lista.getDeltaS2U()) );

		usuario.setSiguiente(msj.siguiente);
		debug("Se seteo como siguiente "+usuario.sig.getID());

		if(lista!=null && lista.tareas.size()>0){
			//si ya tenia tareas simplemente las agrego a la cola que tenia
			if(usuario.getEtiquetando()){
				usuario.addTareas(lista.tareas);
			}
			//si no estaba procesando tareas, hago que comience a procesar
			else{
				usuario.setTareas(lista);
				usuario.setEtiquetando(true);
				Tarea actual = usuario.getActual();
				BigInteger dest = Utils.stringToBI(actual.imagen.getPath());
				Mensaje nuevo = new Mensaje();
				nuevo.setLabel("COM_RQST_IMAGE");
				nuevo.setSender(node);
				nuevo.setContenido(actual.imagen, actual.imagen.length());
				sendUnknownPeer(0,nuevo, dest);
			}

		}

	}

   //Soy el peer encargado de esta imagen que se esta pidiendo
   public void buscarImagen(Mensaje msj){
      //buscar la imagen en mi cache local
      File abuscar = (File)msj.getContenido();
      //BigInteger imagenBI = Utils.stringToBI(abuscar.getPath());
			String keyImagen = abuscar.getPath();

      debug("Me estan pidiendo la imagen "+keyImagen);
      //si la tengo la envio al nodo que la pidio
      //File imagen = cache.get(imagenBI);
			File imagen = cache.get(keyImagen);
			Utils.CACHE_PETICIONES++;
      if(imagen!=null){
				 Utils.CACHE_HITS++;
				 Utils.cacheHit(this.node.getID(),keyImagen);
         debug("La imagen estaba en cache.");
         Node dest = msj.getSender();
         Mensaje nuevo = new Mensaje();
         nuevo.setLabel("RSPNS_IMAGE");
         nuevo.setSender(this.node);
         nuevo.setContenido(imagen, imagen.length());
				 long delay = Utils.fakeLocalImageDelay(imagen.length());
				 Utils.addLatenciaRed(delay, node.getID(),3, nuevo.getSize());
         EDSimulator.add(delay, nuevo, dest, Utils.MID);
      }
      else{
         debug("La imagen NO estaba en cache.");
         //si no la tengo la pido al servidor
         Mensaje nuevo = new Mensaje();
         nuevo.setLabel("ISP_RQST_SERVER_IMAGE");
         nuevo.setSender(this.node);
         Par<Node, File> aux = new Par<Node, File>(msj.getSender(),abuscar);
         nuevo.setContenido(aux, 4+4);//id de nodo + id de imagen
				 int delay = Utils.localDelay();
				 Utils.addLatenciaRed(delay, this.node.getID(),4, nuevo.getSize());
         EDSimulator.add(delay, nuevo, Utils.SERVER, Utils.MID);
      }
   }
   public void recibirImagen(Mensaje msj){
      File imagen = (File)msj.getContenido();
      if(imagen.getName().equals(usuario.getActual().imagen.getName())){
         debug("Me llego la imagen que pedi ("+Utils.stringToBI(imagen.getPath())+","+imagen.getName()+")");
         //respondo la tarea
         usuario.responderActual();
				 //cuanto me demore en responder la tarea
				 int taskDelay = Utils.taskDelay();
         //si me quedan tareas pido la imagen de la siguiente tarea
         if(usuario.hayTareas()){
            Tarea actual = usuario.getActual();
            BigInteger dest = Utils.stringToBI(actual.imagen.getPath());
            Mensaje nuevo = new Mensaje();
            nuevo.setLabel("COM_RQST_IMAGE");
            nuevo.setSender(node);
            nuevo.setContenido(actual.imagen, actual.imagen.length());
						//int delay = Utils.holder.getHold();
						Utils.addUso((int)this.node.getID(), (long)taskDelay);
            sendUnknownPeer(taskDelay,nuevo, dest);
         }
         //si no me quedan tareas
         else{
            debug("NO ME QUEDAN TAREAS");
            //envio el batch de respuestas

						//TODO: antes de enviar, verificar si tengo datos que me llegaron para agregación
						//si tengo--> AGREGAR!!!
						/*if(usuario.colaAgregar.tareas.size() > 0){
							usuario.lista.agregarTareas(usuario.colaAgregar);

							//TODO: no se esta vaciando bien!!!!! :(
							usuario.colaAgregar.vaciar();


						}*/
						//si no tengo no hacer nada

						for(Tarea t : usuario.getTareas().tareas){
							BigInteger taskChordId = Utils.stringToBI(Integer.toString(t.id));

							Mensaje nuevo = new Mensaje();
							nuevo.setLabel("AGGREGATE");//enviar al nodo encargado de la tarea
							int tipo = 7;

							nuevo.setSender(node);
							usuario.lista.setEnvio2();

							usuario.lista.pedirMas(CommonState.r.nextFloat()<=Constantes.P_CONTINUAR);

							Tarea aux = t.clone();
							aux.respuesta = t.respuesta;
							aux.ultimoEnvio = t.ultimoEnvio;
							aux.envios = t.envios;

							//la cantidad de opciones mas la opcion correcta mas la posicion en el anillo de esta tarea
							nuevo.setContenido(aux, aux.getOpcionesSize() + 4 + Utils.M);
							//int localComDelay = Utils.localDelay();
							//Utils.addLatenciaRed(localComDelay, this.node.getID(),tipo, nuevo.getSize());
							debug("AGGREGATE: Enviando tarea "+t.id+" a posicion del anillo "+taskChordId);
							sendUnknownPeer(taskDelay, nuevo, taskChordId);

							//EDSimulator.add(localComDelay, nuevo, usuario.getSiguiente(), Utils.MID);
						}

						usuario.setEtiquetando(false);
						debug("fin recibirImagen");

						if(usuario.lista.pideMas()){
							Mensaje msg = new Mensaje();
							msg.setLabel("RE_INIT");
							EDSimulator.add(Utils.init_delay(), msg, this.node, Utils.MID);
						}
            //sendServidor(nuevo);
         }
      }
      else{
				 debug("Estaba pidiendo "+usuario.getActual().imagen.getName()+"("+Utils.stringToBI(usuario.getActual().imagen.getPath())+") y me llego "+imagen.getName()+"("+Utils.stringToBI(imagen.getPath())+")");
         debug("ME LLEGO UNA IMAGEN QUE NO PEDI!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
      }
   }
   public void procesarImagen(Mensaje msj){
      Par aux = (Par)msj.getContenido();
      File aguardar = (File)aux.getValue();
      //BigInteger imagenBI = Utils.stringToBI(aguardar.getPath());
			String keyImagen = aguardar.getPath();
      debug("Guardando en cache la imagen ("+keyImagen+","+aguardar.getName()+")");
      //cache.put(imagenBI,aguardar);
			cache.put(keyImagen, aguardar);
      Mensaje nuevoMsj = new Mensaje();
      nuevoMsj.setLabel("RSPNS_IMAGE");
      nuevoMsj.setSender(this.node);
      nuevoMsj.setContenido(aguardar, aguardar.length());
      Node nodo = (Node) aux.getKey();
    	debug("Enviando imagen "+keyImagen+" a nodo "+nodo.getID());
			long delay = Utils.fakeLocalImageDelay(aguardar.length());
			Utils.addLatenciaRed(delay, nodo.getID(), 3, nuevoMsj.getSize());
      EDSimulator.add(delay,nuevoMsj,nodo,Utils.MID);
   }

   private void sendServidor(Mensaje msj){
      //Transport transport;
      try{
         if(node==null){
            debug("node era null");
         }
         //transport = (Transport) node.getProtocol(Utils.TID);
         //transport.send(this.node, Utils.SERVER, msj, Utils.MID);
         EDSimulator.add(Utils.serverDelay(), msj, Utils.SERVER, Utils.MID);

      }
      catch(NullPointerException e){
         System.out.println(e);
      }
	}

   private void sendKnownPeer(Mensaje msj, Node dest){
      //Transport transport = (Transport) node.getProtocol(Utils.TID);
		//transport.send(this.node, dest, msj, Utils.MID);
      EDSimulator.add(Utils.localDelay(), msj, dest, Utils.MID);
   }

   private void sendUnknownPeer(int delay, Mensaje msj, BigInteger  pointOnRing){
      ChordMessage message = new ChordMessage(ChordMessage.LOOK_UP, pointOnRing);
      ChordProtocol senderCp = Utils.getChordFromNode(this.node);
      message.setSender(senderCp.chordId);
      message.setExtra(msj, msj.getSize());
      //message.setLabel("successor");



      //coloco el evento en el nodo que envia el mensaje LOOK_UP
      //para que haga correr el protocolo Chord
      debug("Colocando evento en ChordProtocol");
      EDSimulator.add(delay, message, this.node, Utils.PID);
   }


	public Object clone(){
		ProtocoloAcuerdo2 pa = null;
		try {
            pa = (ProtocoloAcuerdo2) super.clone();

        } catch (CloneNotSupportedException e) {
        } // never happens
     return pa;
	}

}
