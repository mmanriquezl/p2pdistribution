package chord;

import java.io.File;
import java.util.ArrayList;
import peersim.core.CommonState;

public class Tarea{
   int id;//contador simple
   File imagen;
   String opciones[];
   int posCorrecta;
   /***********************************************************
    *para cuando cada Tarea guarda una respuesta de un usuario*
    ***********************************************************/
   Respuesta respuesta;
   long ultimoEnvio;
   int envios;
   //int respuesta;//posicion en el arreglo de opciones es la respuesta del voluntario
   ArrayList<Respuesta> respuestas;
   ArrayList<Double> consenso;

   int correcta;




   public Tarea(int id, File imagen, String opciones[], int pos){
      this.id = id;
      this.imagen = imagen;
      this.opciones = opciones;
      this.posCorrecta = pos;
      //usada por el usuario
      this.respuesta = new Respuesta(id);
      this.envios = 0;
      this.ultimoEnvio = 1;//le pongo 1 para que no me alegue por una division por 0
      //usada por el servidor
      this.respuestas = new ArrayList<Respuesta>();
      this.consenso = new ArrayList<Double>();
      for (int i = 0; i < opciones.length ; i++ ) {
        consenso.add(0.0);
      }
      //cuando se determine el consenso, se guardará la posicion de la opcion correcta
      correcta = -1;
   }

   public int getOpcionesSize(){
     return this.opciones.length*opciones[0].length();
   }

   public boolean haSidoRespondida(){
     //si es distinto de -1 quiere decir que ya se respondio
     return respuesta.respuesta !=-1;
   }

   //tamaño de la imagen en bytes
   public long getSize(){
      //en este punto ya me fije si el File ya es valido
      return imagen.length();
   }
   public void mostrarTarea(){
      respuesta.setIni();
   }
   public double score(){
      return (double)1/(double)this.ultimoEnvio + this.envios;
      //return this.ultimoEnvio + this.envios;
   }
   public boolean setRespuesta(int r, long userId, double peso){
      if(r >= 0 && r <= opciones.length){
         respuesta.setFin();
         respuesta.setRespuesta(r, userId, peso);
         return true;
      }
      return false;
   }
   public void addRespuesta(Respuesta r){
     respuestas.add(r);
     Double cantidad = consenso.get(r.getRespuesta());
     consenso.set(r.getRespuesta(), cantidad + r.getPeso());
   }
   public int getRespuestaPos(){
      return respuesta.getRespuesta();
   }
   public Respuesta getRespuestaObj(){
      return respuesta;
   }
   public void setEnvio(int diff){
      ultimoEnvio = CommonState.getTime() + diff;
      envios++;
   }
   public long getUltimoEnvio(){
      return ultimoEnvio;
   }
   public int getEnvios(){
      return envios;
   }

   public int getCantidadRespuestas(){
     return respuestas.size();
   }

   public String respuestasToString(){
     //ArrayList<Respuesta> respuestas;
     String salida = "[";
     if(respuestas.size()>0){
       for(int i = 0; i < respuestas.size(); i++){
         salida += respuestas.get(i).getRespuesta()+"/"+respuestas.get(i).getPeso();
         if(i!=respuestas.size()-1){
           salida +=", ";
         }
       }
     }
     else{
       salida += respuesta.getRespuesta()+"//"+respuesta.getPeso();
     }
     salida +="]";
     return salida;
   }

   public boolean esDelConsenso(int opcion){
     return opcion==correcta;
   }

   public boolean hayConsenso(){
     double mayor = 0.0;
     double total = 0.0;
     int posMayor = -1;
     for (int i = 0; i < consenso.size() ; i++) {
       total += consenso.get(i);
       if(consenso.get(i) > mayor){
         mayor = consenso.get(i);
         posMayor = i;
       }
     }
     boolean consenso = (mayor/total)>=Constantes.THRESHOLD_CONSENSO;
     if(consenso){
       correcta = posMayor;
     }
     return consenso;
   }

   public boolean equals(Tarea o){
     if(o == null){
       return false;
     }
     return this.id==o.id;
   }

   @Override
   public int hashCode(){
     return id;
   }

   public Tarea clone(){
     Tarea a = new Tarea(this.id, this.imagen, this.opciones, this.posCorrecta);
     //a.respuesta = this.respuesta;
     //a.ultimoEnvio = this.ultimoEnvio;
     //a.envios = this.envios;
     //a.correcta = this.correcta;
     //a.consenso = (ArrayList<Double>)this.consenso.clone();
     return a;
   }


   /*public boolean addRespuesta(int r){
      if(r >= 0 && r <= opciones.length){
         respuestas.add(r);
         return true;
      }
      return false;
   }*/
   /*public int getRespuestasSize(){
      return respuestas.size();
   }*/
   //public void buscarConsenso(){}

}
