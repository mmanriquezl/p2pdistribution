package chord;

import peersim.core.Node;
import java.util.ArrayList;
import java.io.File;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

public class Servidor{

   Node nodo;
   /*
   https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html
   Implementation note: this implementation provides O(log(n)) time for the
   enqueing and dequeing methods (offer, poll, remove() and  add); linear time
   for the remove(Object) and contains(Object) methods; and constant time
   for the retrieval methods (peek,  element, and size).
   */
   PriorityQueue<Tarea> encoladas;
   PriorityQueue<Tarea> pendientes;
   ArrayList<Tarea> espera;
   ArrayList<Tarea> respondidas;
   ArrayList<Tarea> sinresponder;

   Queue<Object> colaEspera = new LinkedList<>();

   boolean ejecutando;


   static int idTracker = 0;
   static Comparator<Tarea> comparador;


   public Servidor(Node nodo, String dir){
      espera = new ArrayList<Tarea>();
      respondidas = new ArrayList<Tarea>();
      sinresponder = new ArrayList<Tarea>();


      comparador = new ComparadorTareas();

      encoladas = new PriorityQueue<Tarea>(10, comparador);
      pendientes = new PriorityQueue<Tarea>(10, comparador);
      //llenar de tareas la lista de pendientes


      final File folder = new File(dir);
      listFilesForFolder(folder);
      Utils.TASKS = encoladas.size();
      System.out.println("Se crearon "+encoladas.size()+" tareas.");
      this.ejecutando = false;
      this.nodo = nodo;
   }

   public void encolar(Object o){
     colaEspera.add(o);
   }

   public Object desencolar(){
     return colaEspera.poll();
   }

   public void listFilesForFolder(final File folder) {
		for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isDirectory()) {
	            listFilesForFolder(fileEntry);
	        } else {
	            //System.out.println(fileEntry.getName());
               String[] opciones = {"A","B","C","D"};
               Tarea aux = new Tarea(idTracker, fileEntry,opciones, Utils.randomMinMax(0, 3));
               idTracker++;
	             //pendientes.add(aux);
               encoladas.add(aux);
	        }
	    }
   }

   public ArrayList<Tarea> getTareasOrdenadas(int cantidad){
      ArrayList<Tarea> tareas = new ArrayList<Tarea>();
      //saco las N tareas pedidas en el orden de prioridad definido
      while(cantidad>0){
         Tarea t = encoladas.poll();
         if(t!=null){
            tareas.add(t);
            cantidad--;
         }
         else{
            break;
         }
      }
      if(cantidad>0){
        //sacar tareas de pendientes -->
        //pendientes tambien tiene que ser una cola de prioridad
        while(cantidad>0){
           Tarea t = pendientes.poll();
           if(t!=null){
              tareas.add(t);
              cantidad--;
           }
           else{
              break;
           }
        }
      }



      //actualizo los valores de cada tarea y los vuelvo a poner en la cola
      //de prioridad, para actualizar el orden :)
      //System.out.print("Tareas:");
      ArrayList<Tarea> enviar = new ArrayList<Tarea>();
      for (int i = 0; i < tareas.size() ; i++ ) {
         Tarea aux = tareas.get(i);
         //System.out.print("["+aux.id+","+aux.score()+"] ");
         //agrego una pequeña diferencia, para alterar el orden
         aux.setEnvio(i==0?1:0);

         if(aux.getEnvios()>=Constantes.THRESHOLD_RESPUESTAS){
           pendientes.add(aux);
         }
         else{
           encoladas.add(aux);
         }

         enviar.add(aux.clone());
      }

      //System.out.println();


      return enviar;

   }

   public ArrayList<Tarea> agregarRespuestas(ListaTareas lista){
     System.out.println("En AGREGAR RESPUESTAS");
     ArrayList<Tarea> sinConsenso = new ArrayList<Tarea>();

     boolean enCola, enPendientes;
     //saco las tareas de la cola de prioridad y las guardo en temporal
     ArrayList<Tarea> temporal = new ArrayList<Tarea>();


     //agregar respuesta a lista de respuestas de cada tarea

     //si la tarea tiene la cantidad de respuestas pedida Y hay mayoria
     //la dejo en la lista de respondidas

     int cont = 0;

     /*ArrayList<Tarea> enCola = new ArrayList<Tarea>();
     ArrayList<Tarea> enPendientes = new ArrayList<Tarea>();

     for (int i = 0; i < lista.tareas.size() ; i++) {
       Tarea aux = lista.tareas.get(i);
       //esta en enconladas?
       //contains(Object)method that is used to check if a particular element is present in the queue, have leaner time complexity i.e. O(n).
       if(encoladas.contains(aux)){
         enCola.add(aux);
       }
       //o esta en pendientes?
       //es de orden lineal!!!!!!!!!!
       else if(pendientes.contains(aux)){
         enPendientes.add(aux);
       }
     }*/

     //System.out.println(enCola.size()+" tareas en cola de prioridad y "+enPendientes.size()+" en cola de pendientes");

     //Orden log(N) de 3 a 6 milisegundos sacar 10000 elementos y volverlos a poner






     //saco las respuestas de las tareas ya consideradas como sin responder
     for(Tarea t: sinresponder){
       int pos = lista.estaEn(t.id);
       if(pos>=0){
         System.out.println("La Tarea "+t.id+" no se procesa por que ya no se respondio");
         lista.tareas.remove(pos);
       }
     }




     //TODO: cuando las tareas son reenviadas para resolver consenso, no queda en encoladas!!!!!


     while(encoladas.size()>0){
       //System.out.println("revisando encoladas");
       Tarea aux = encoladas.poll();

       if(aux==null){
         //System.out.println("No esta en la cola de prioridad...");
         break;
       }
       else{
         //System.out.println("Buscando "+aux.id+ " en encoladas");
         boolean estaba = false;
         for (int i = 0; i < lista.tareas.size() ; i++) {
           if(aux.id == lista.tareas.get(i).id){
             estaba = true;
             cont++;
             //aqui tengo que fijarme de sacar las respuestas del arreglo de respuestas si es que existe
             //si no sacarla del objeto como antes
             Tarea externa = lista.tareas.get(i);
             if(externa.respuestas.size()>0){

               for(Respuesta r:externa.respuestas){
                  aux.addRespuesta(r);
               }
               System.out.println("Ahora la tarea "+aux.id+" tiene "+aux.getCantidadRespuestas()+" respuestas"+aux.respuestasToString());

               if( aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS &&
                   aux.hayConsenso()){
                 //poner en lista de tareas terminadas
                 respondidas.add(aux);
                 //la respuesta es parte del consenso?
                 for(Respuesta r:externa.respuestas){
                   if(aux.esDelConsenso(r.getRespuesta())){
                     Utils.SCORES.computeIfPresent(r.getUserID(), (id, obj)-> obj.addCorrectas());
                   }
                 }
                 //System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas:"+aux.respuestasToString());
                 System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas());
               }
             }
             else{
               Respuesta r = externa.getRespuestaObj();
               aux.addRespuesta(r);
               System.out.println("Aahora la tarea "+aux.id+" tiene "+aux.getCantidadRespuestas()+" respuestas"+aux.respuestasToString());
               if( aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS &&
                   aux.hayConsenso()){
                 //poner en lista de tareas terminadas
                 respondidas.add(aux);
                 //la respuesta es parte del consenso?
                 if(aux.esDelConsenso(r.getRespuesta())){
                   Utils.SCORES.computeIfPresent(r.getUserID(), (id, obj)-> obj.addCorrectas());
                 }
                 //System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas:"+aux.respuestasToString());
                 System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas());
               }
             }
             /*if( aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS &&
                 aux.hayConsenso()){
               //poner en lista de tareas terminadas
               respondidas.add(aux);
               //la respuesta es parte del consenso?
               if(aux.esDelConsenso(r.getRespuesta())){
                 Utils.SCORES.computeIfPresent(r.getUserID(), (id, obj)-> obj.addCorrectas());
               }
               //System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas:"+aux.respuestasToString());
               System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas());
             }*/
             if(aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS && !aux.hayConsenso()){
               //System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas::"+aux.respuestasToString()+" PERO NO HAY CONSENSO");
               System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" PERO NO HAY CONSENSO");
               if(aux.getCantidadRespuestas() <Constantes.MAX_RESPUESTAS){
                 sinConsenso.add(aux);
                 //agregar a pendientes
                 pendientes.add(aux);
               }
               else{
                 System.out.println("Se supero el max de respuestas. Se considera sin responder");
                 sinresponder.add(aux);
               }


             }
             if(aux.getCantidadRespuestas()<Constantes.THRESHOLD_RESPUESTAS){
               //poner en lista temporal
               temporal.add(aux);
             }
           }
         }

         if(!estaba){
           temporal.add(aux);
         }

         if(cont == lista.tareas.size()){
           break;
         }

       }
     }
     int valor = cont;

     //System.out.println("Habian "+valor+" tareas en cola de prioridad");

     //vuelvo a poner en la cola de prioridad las tareas sacadas
     for (int i = 0; i < temporal.size() ; i++) {
       encoladas.add(temporal.get(i));
     }

     temporal.clear();

     //Orden log(N) de 3 a 6 milisegundos sacar 10000 elementos y volverlos a poner

     while(pendientes.size()>0){
       //System.out.println("revisando pendientes");
       Tarea aux = pendientes.poll();
       if(aux==null){
         //System.out.println("No esta en la cola de pendientes.");
         break;
       }
       else{
         //System.out.println("Buscando "+aux.id+" en pendientes");
         boolean estaba = false;
         for (int i = 0; i < lista.tareas.size() ; i++) {
           if(aux.id == lista.tareas.get(i).id){
             estaba = true;
             cont++;


             if(lista.tareas.get(i).respuestas.size()>0){

               for(Respuesta r: lista.tareas.get(i).respuestas){
                 aux.addRespuesta(r);
               }
               System.out.println("Ahhora la tarea "+aux.id+" tiene "+aux.getCantidadRespuestas()+" respuestas"+aux.respuestasToString());
               if( aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS &&
               aux.hayConsenso()){
                 //poner en lista de tareas terminadas
                 respondidas.add(aux);
                 for(Respuesta r: lista.tareas.get(i).respuestas){
                   //la respuesta es parte del consenso?
                   if(aux.esDelConsenso(r.getRespuesta())){
                     Utils.SCORES.computeIfPresent(r.getUserID(), (id, obj)-> obj.addCorrectas());
                   }
                 }
                 System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas());
               }

             }
             else{
               Respuesta r = lista.tareas.get(i).getRespuestaObj();
               aux.addRespuesta(r);
               System.out.println("Ahora la tarea "+aux.id+" tiene "+aux.getCantidadRespuestas()+" respuestasss"+aux.respuestasToString());
               if( aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS &&
                   aux.hayConsenso()){
                 //poner en lista de tareas terminadas
                 respondidas.add(aux);
                 //la respuesta es parte del consenso?
                 if(aux.esDelConsenso(r.getRespuesta())){
                   Utils.SCORES.computeIfPresent(r.getUserID(), (id, obj)-> obj.addCorrectas());
                 }
                 //System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas:"+aux.respuestasToString());
                 System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas());
               }
             }

             //esto necesito hacerlo desde el arreglo de respuestas
             /*Respuesta r = lista.tareas.get(i).getRespuestaObj();
             aux.addRespuesta(r);
             if( aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS &&
                 aux.hayConsenso()){
               //poner en lista de tareas terminadas
               respondidas.add(aux);
               //la respuesta es parte del consenso?
               if(aux.esDelConsenso(r.getRespuesta())){
                 Utils.SCORES.computeIfPresent(r.getUserID(), (id, obj)-> obj.addCorrectas());
               }
               //System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas:"+aux.respuestasToString());
               System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas());
             }*/


             if(aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS && !aux.hayConsenso()){
               //System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas::"+aux.respuestasToString()+" PERO NO HAY CONSENSO");
               System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" PERO NO HAY CONSENSO");

               if(aux.getCantidadRespuestas() <Constantes.MAX_RESPUESTAS){
                 temporal.add(aux);
                 //Para que al modificar la tarea en el lado del usuario no afecte a otros usuarios
                 sinConsenso.add(aux.clone());
               }
               else{
                 sinresponder.add(aux);
               }


             }
             if(aux.getCantidadRespuestas()<Constantes.THRESHOLD_RESPUESTAS){
               //poner en lista temporal
               System.out.println("Aún no se reciben todas las respuestas necesarias para la tarea "+aux.id);
               temporal.add(aux);
             }
           }
         }
         if(!estaba){
           temporal.add(aux);
         }
         if(cont == lista.tareas.size()){
           break;
         }
       }
     }

     //System.out.println("Habian "+(cont - valor)+" tareas en cola de prioridad de pendientes");

     //vuelvo a poner en la cola de prioridad las tareas sacadas
     for (int i = 0; i < temporal.size() ; i++) {
       pendientes.add(temporal.get(i));
     }

     /*
     //Orden N de 4 a 7 milisegundos 100 indexOf en un arreglo de 10000 elementos
     //agrego a pendientes las respuestas
     for (int i = 0; i < enPendientes.size() ;i++ ) {
       int pos = pendientes.indexOf(enPendientes.get(i));
       //deberia ser siempre positivo ya que ya me fije que este!
       if(pos >= 0){
         Tarea aux = pendientes.get(pos);

         Respuesta r = enPendientes.get(i).getRespuestaObj();
         aux.addRespuesta(r);
         if( aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS &&
             aux.hayConsenso()) {
           //poner en lista de tareas terminadas
           respondidas.add(aux);
           //sacar de pendientes
           pendientes.remove(pos);

           //la respuesta es parte del consenso?
           if(aux.esDelConsenso(r.getRespuesta())){
             Utils.SCORES.computeIfPresent(r.getUserID(), (id, obj)-> obj.addCorrectas());
           }

           System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas. ");
         }
         if(aux.getCantidadRespuestas()>=Constantes.THRESHOLD_RESPUESTAS && !aux.hayConsenso()){
           System.out.println("Se termino la tarea "+aux.id+" con "+aux.getCantidadRespuestas()+" respuestas. PERO NO HAY CONSENSO");
         }
       }
     }
     */

     if(sinConsenso.size()>0){
       System.out.println("Hay "+sinConsenso.size()+" tareas sin consenso");
     }
     return sinConsenso;

   }

   /*public ArrayList<Tarea> getNPendientes(int cantidad){
      ArrayList<Tarea> aux;
      //intento sacar todas desde espera
      if(espera.size()>=cantidad){
         aux = new ArrayList(espera.subList(0, cantidad));
      }
      else{
         //sino saco las que puedo de espera
         aux = new ArrayList(espera.subList(0, espera.size()));
         //saco el resto de pendientes
         int resto = cantidad - aux.size();
         if(resto<=pendientes.size()){
            aux.addAll(pendientes.subList(0,resto));
         }
         else{
            aux.addAll(pendientes.subList(0,pendientes.size()));
         }
      }

      return aux;

   }*/

   public class ComparadorTareas implements Comparator<Tarea>{
       @Override
       public int compare(Tarea x, Tarea y){
           if (x.score() < y.score()){
               return 1;
           }
           if (x.score() > y.score()){
               return -1;
           }
           return 0;
       }
   }
}
