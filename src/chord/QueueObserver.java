package chord;

import java.util.ArrayList;
import peersim.core.Control;
import peersim.core.Network;
import java.util.PriorityQueue;
import peersim.core.Node;
import java.io.PrintWriter;


public class QueueObserver implements Control {

  public static ArrayList<ArrayList<Integer>> largos = null;



	public QueueObserver(String prefix) {
    if(largos==null){
      largos = new ArrayList<ArrayList<Integer>>();
      for(int i = 0; i < Network.size(); i++){
        if(Network.get(i).getID()!=Utils.SERVER_ID){
          largos.add(new ArrayList<Integer>());
          largos.get(largos.size()-1).add(0);
        }
      }
    }
  }


	public boolean execute() {
    //System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
    //observando largo cola en cada usuario
    //System.out.println("Hay "+Network.size()+" nodos");
  /*  for(int i = 0; i < Network.size(); i++){
      int actual = (int) Network.get(i).getID();
       if(actual!=Utils.SERVER_ID){
          ProtocoloAcuerdo pa = Utils.getProtocolFromNode(Network.get(i));
          ListaTareas lista =pa.usuario.getTareas();

          if(largos==null){
            System.out.println("largos era null");
          }
          if(lista!=null){
            largos.get(actual).add(lista.getPorResponder());
          }
          else{
            largos.get(actual).add(0);
          }
       }
		}


    //System.out.println("Hay "+Utils.TOPUSERS.get(Utils.colaActual).size()+" nodos en la cola actual");
    //actualizando el score de cada usuario
    //System.out.println("Actualizando el score de cada usuario");
    int cola = (Utils.colaActual + 1)%2;
    PriorityQueue<Par<Node, Double>> temp = Utils.TOPUSERS.get(cola);

    temp.clear();

    for(int i = 0; i < Network.size(); i++){
      Node nodo = Network.get(i);
      if(nodo.getID()!=Utils.SERVER_ID){
        ScoreUser score = Utils.SCORES.get(nodo.getID());
        //System.out.println("USER "+nodo.getID()+" = "+score.score()+":"+score.toString());
        temp.add(new Par<Node, Double>(nodo, score.score()));
      }
    }
    //tengo los usuarios ordenados por score
    Utils.colaActual = cola;
    //System.out.println("Hay "+Utils.TOPUSERS.get(Utils.colaActual).size()+" nodos en la nueva cola");
    //System.out.println("OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO");
		*/
    return false;
  }

  public static void printResult(PrintWriter printer){
    for (int i = 0;i < largos.size() ;i++ ) {
      //ventanas
      printer.printf("  [%d]:",i);
      for (int j = 0; j < largos.get(i).size() ;j++ ) {
        printer.printf("(%d):",largos.get(i).get(j));
      }
      printer.printf("\n");
    }
  }

  public static void printResult(){
    System.out.println("LARGO COLAS:");
    //NODOS
    for (int i = 0;i < largos.size() ;i++ ) {
      //ventanas
      System.out.print("  ["+i+"]:");
      for (int j = 0; j < largos.get(i).size() ;j++ ) {
        System.out.print("("+largos.get(i).get(j)+"):");
      }
      System.out.println();
    }
  }
}
