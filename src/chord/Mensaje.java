package chord;

import java.util.ArrayList;
import peersim.core.Node;

public class Mensaje {
	String label;
   Object contenido;
   Node sender;
	 //nodo que es mas lento que yo, y voy a enviarle mis resultados

	 //donde asigno este valor????????
	 Node siguiente;
	 int size;
	public Mensaje() {
		this.label = "default";
		this.contenido = null;
		//this.siguiente = null;
		this.size = 0;
	}
	public Mensaje(String label, Object content) {
		this.label = label;
		this.contenido = content;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.size += label.length();
		this.label = label;
	}
	public Object getContenido() {
		return contenido;
	}

	public void setContenido(Object content, long s) {
		this.size += s;
		this.contenido = content;
	}

	public int getSize(){
		return this.size;
	}

   public void setSender(Node n){
		 this.size += 4;//4 bytes por un entero que es el id del nodo
		 sender = n;
   }
   public Node getSender(){
      return sender;
   }


}
