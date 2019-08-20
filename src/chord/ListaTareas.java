package chord;

import peersim.core.CommonState;
import java.util.Collections;
import java.util.ArrayList;

public class ListaTareas{
   //instante en que se envio la lista de tareas
   long t_envio;
   long t_recepcion;
   long t_envio2;
   long t_recepcion2;
   ArrayList<Tarea> tareas;
   boolean mas;

   public ListaTareas(){
      t_envio = 0;
      t_envio2 = 0;
      t_recepcion = 0;
      t_recepcion2 = 0;
      tareas = new ArrayList<Tarea>();
   }

   public ArrayList<Integer> getIdsOrdenados(){
     ArrayList<Integer> ids = new ArrayList<Integer>();
     for(Tarea t: tareas ){
       ids.add(t.id);
     }
     Collections.sort(ids);
     return ids;
   }

   public int getSize(){
     return 8*4 + tareas.size()*tareas.get(0).getOpcionesSize();//4 long
   }

   public int getSizeRespondida(){
     int tamanio = 0;
     for (Tarea t: tareas){
       tamanio+= t.getOpcionesSize() + t.respuestas.size();
     }
     return tamanio;
   }

   public void setTareas(ArrayList<Tarea> lista){
     tareas = lista;
   }

   public void addAll(ArrayList<Tarea> lista){
     tareas.addAll(lista);
   }

   public void vaciar(){
     tareas.clear();
     t_envio = 0;
     t_envio2 = 0;
     t_recepcion = 0;
     t_recepcion2 = 0;
   }

   public void agregarTareas(ListaTareas otra){
     for(int i = 0; i < otra.tareas.size(); i++){
	    	Tarea remota = otra.tareas.get(i);
	        int pos = this.estaEn(remota.id);

	        if(pos >= 0){
	        	//revisar si hay respuestas en el arreglo de respuestas!!!!!!!!!
              if(remota.respuestas.isEmpty()){
                if(remota.getRespuestaObj().getRespuesta()>=0){
                  tareas.get(pos).respuestas.add(remota.getRespuestaObj().clone());
                }
              }
              else{
                for(Respuesta r : remota.respuestas){
                  if(r.getRespuesta()>=0){
                    tareas.get(pos).respuestas.add(r.clone());
                  }
                }
              }
	        }
	        else{

            Tarea aux = remota.clone();
            aux.respuesta = remota.respuesta;
            aux.ultimoEnvio = remota.ultimoEnvio;
            aux.envios = remota.envios;
            aux.correcta = remota.correcta;
            //a.consenso = (ArrayList<Double>)this.consenso.clone();
            if(remota.respuestas.isEmpty()){
              if(remota.getRespuestaObj().getRespuesta()>=0){
                aux.respuestas.add(remota.getRespuestaObj().clone());
              }
            }
            else{
              for(Respuesta r : remota.respuestas){
                if(r.getRespuesta()>=0){
                  aux.respuestas.add(r.clone());
                }
              }
            }

            tareas.add(aux);
	        }
	    }
   }

   public int estaEn(int id) {
		for (int i = 0; i< this.tareas.size(); i++) {
			if(this.tareas.get(i).id == id) {
				return i;
			}
		}
		return -1;
	}

   public int getPorResponder(){
     int cantidad = tareas.size();
     for(int i = 0; i < tareas.size(); i++){
       if(tareas.get(i).haSidoRespondida()){
         cantidad--;
       }
     }
     return cantidad;
   }

   public void addTarea(Tarea t){
      tareas.add(t);
   }
   public void setEnvio(){
      t_envio = CommonState.getTime();
   }
   public void setEnvio2(){
      t_envio2 = CommonState.getTime();
   }
   public void pedirMas(boolean m){
     mas = m;
   }
   public boolean pideMas(){
     return mas;
   }
   public void setRecepcion(){
      t_recepcion = CommonState.getTime();
   }
   public void setRecepcion2(){
      t_recepcion2 = CommonState.getTime();
   }
   //tiempo de comunicacion desde el servidor hacia el usuario
   public long getDeltaS2U(){
      if(t_recepcion != 0 && t_envio!=0){
         return t_recepcion - t_envio;
      }
      else{
         return -1;
      }
   }
   //tiempo de comunicacion desde el usuario hacia el servidor
   public long getDeltaU2S(){
      if(t_recepcion2 != 0 && t_envio2!=0){
         return t_recepcion2 - t_envio2;
      }
      else{
         return -1;
      }
   }

   public String toString(){
     String salida = "[";
     for(int i = 0; i < tareas.size(); i++){
       Respuesta r = tareas.get(i).getRespuestaObj();
       salida+=tareas.get(i).id+":"+tareas.get(i).getRespuestaPos()+":"+r.getPeso();
       if(i < tareas.size()-1){
         salida+=", ";
       }
     }
     salida+="]";
     return salida;
   }

   public ListaTareas clone(){
     ListaTareas clon = new ListaTareas();
     clon.t_envio = this.t_envio;
     clon.t_envio2 = this.t_envio2;
     clon.t_recepcion = this.t_recepcion;
     clon.t_recepcion2 = this.t_recepcion2;
     for(Tarea t: this.tareas){
       clon.tareas.add(t.clone());
     }
     return clon;
   }
}
