package chord;

import java.math.BigInteger;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDSimulator;

//simula que los usuarios piden tareas
//cada usuario es en el fondo un peer
//definir como parametro:
// * la cantidad de usuarios a pedir tareas
// * la cantidad de tareas a pedir
//cada tarea es una imagen y una pregunta asociada a dicha imagen
//es posible que para una pregunta se use la misma imagen
public class SolicitadorTareas implements Control {

	private static final String PAR_PROT = "protocol";
	private static final String PAR_USERS = "users";

	private final int mid;

	private final int users;


	public SolicitadorTareas(String prefix){
		mid = Configuration.getPid(prefix + "." + PAR_PROT);
		users = Configuration.getInt(prefix + "." + PAR_USERS);
	}

	@Override
	public boolean execute() {
		int size = Network.size();
		Node servidor = Utils.SERVER;
		//ChordProtocol servidorCP = Utils.getChordFromNode(servidor);


      int cant = 0;
		//elegir nodos al azar
		for(int i = 0; i < size; i++){
			Node solicitador = Network.get(i);

			if(solicitador != null && solicitador.isUp() == true && solicitador.getID()!=Utils.SERVER_ID){
            //mandar mensaje al nodo 0 (servidor) solicitud de tareas
            //System.out.println("Soy el nodo "+solicitador.getID()+" y estoy pidiendo tareas");

            Mensaje mm = new Mensaje();
            mm.setLabel("INIT");

            EDSimulator.add(Utils.init_delay(), mm, solicitador, mid);
            cant++;
            if(cant==users){
               break;
            }
         }
		}

		return false;
	}

}
