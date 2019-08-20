package chord;

import peersim.core.CommonState;

public class Respuesta{
   long idUsuario;//el id del usuario sera el id del nodo donde esta corriendo
   double peso;//el peso de esta respuesta dependendiendo si el usuario es experto o no
   int idTarea;
   long t_ini; //instante en que el usuario inicia la tarea
   long t_fin; //instante en que el usuario responde la tarea
   int respuesta;//posicion del arreglo de opciones que el usuario eligio


   public Respuesta(int taskId){
      idTarea = taskId;
      idUsuario = -1;
      peso = 0.0;
      t_ini = 0;
      t_fin = 0;
      respuesta = -1;
   }
   public void setIni(){
      t_ini = CommonState.getTime();
   }
   public void setFin(){
      t_fin = CommonState.getTime();
   }

   public long getUserID(){
     return idUsuario;
   }

   public double getPeso(){
     return peso;
   }
   public void setRespuesta(int r, long userId, double p){
      respuesta = r;
      idUsuario = userId;
      peso = p;
   }


   public long getDeltaRespuesta(){
      if(t_fin != 0 && t_ini!=0){
         return t_fin - t_ini;
      }
      else{
         return -1;
      }
   }
   public int getRespuesta(){
      return respuesta;
   }
   public Respuesta clone(){
     Respuesta r = new Respuesta(this.idTarea);
     r.idUsuario = this.idUsuario;//el id del usuario sera el id del nodo donde esta corriendo
     r.peso = this.peso;//el peso de esta respuesta dependendiendo si el usuario es experto o no
     r.t_ini = this.t_ini; //instante en que el usuario inicia la tarea
     r.t_fin = this.t_fin; //instante en que el usuario responde la tarea
     r.respuesta = this.respuesta;
     return r;
   }
}
