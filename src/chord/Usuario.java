
package chord;

import peersim.core.Node;
import peersim.core.CommonState;
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.Queue;

//clase que representa el comportamiento del usuario
public class Usuario{
   long id;
   ListaTareas lista;
   ListaTareas colaAgregar;
   int actual;
   boolean experto;
   double peso;
   boolean etiquetando;


   Queue<Object> colaEspera;
   Queue<Object> colaAgregacion;

   boolean ejecutando;
   boolean agregando;

   Node sig;
   public Usuario(long id){
      this.id = id;
      lista = null;
      colaAgregar = new ListaTareas();
      this.peso = 1.0;
      this.etiquetando = false;
      float num = CommonState.r.nextFloat();

      this.experto = num <=Constantes.PROB_EXPERTO;
      if(experto){
        this.peso = Constantes.PESO_EXPERTO;
        //System.out.println("{"+this.id+"}:EXPERTO ("+num+"<="+Constantes.PROB_EXPERTO+") con peso :"+this.peso);
      }
      //else{
      //  System.out.println("{"+this.id+"}:NORMAL("+num+">"+Constantes.PROB_EXPERTO+") con peso :"+this.peso);
      //}

      this.colaEspera = new LinkedList<>();
      this.colaAgregacion = new LinkedList<>();
      this.ejecutando = false;
      this.agregando = false;
   }

   public void encolar(Object o){
     this.colaEspera.add(o);
   }

   public Object desencolar(){
     return this.colaEspera.poll();
   }

   public void encolarAgg(Object o){
     this.colaAgregacion.add(o);
   }

   public Object desencolarAgg(){
     return this.colaAgregacion.poll();
   }

   public void setTareas(ListaTareas t){
      lista = t;
      actual = 0;
   }

   public void setSiguiente(Node s){
     this.sig = s;
   }

   public Node getSiguiente(){
     return this.sig;
   }

   public void setEtiquetando(boolean e){
     etiquetando = e;
   }

   public boolean getEtiquetando(){
     return etiquetando;
   }

   public ListaTareas getTareas(){
      return lista;
   }

   public void addTareas(ArrayList<Tarea> t){
      lista.addAll(t);
   }

   public boolean hayTareas(){
      return actual<lista.tareas.size();
   }

   public Tarea getActual(){
      return lista.tareas.get(actual);
   }

   public void responderActual(){
      if((lista.tareas.size()-1)>=actual){
         Tarea aux = lista.tareas.get(actual);
         aux.mostrarTarea();
         int opcion = -1;
         if(this.experto){
           if(CommonState.r.nextFloat()<Constantes.EXPERTO_CORRECTA){
             opcion = aux.posCorrecta;
           }
           else{
             opcion = CommonState.r.nextInt(aux.opciones.length);
           }
         }
         else{
           if(CommonState.r.nextFloat()<Constantes.NORMAL_CORRECTA){
             opcion = aux.posCorrecta;
           }
           else{
             opcion = CommonState.r.nextInt(aux.opciones.length);
           }
         }
         if(!aux.setRespuesta(opcion, this.id, this.peso)){
           System.out.println("HORROR!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
         }
         //Utils.holder.hold(Utils.taskDelay());
      }
      actual++;
   }
}
