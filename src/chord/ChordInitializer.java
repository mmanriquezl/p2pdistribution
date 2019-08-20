package chord;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import peersim.config.Configuration;
import peersim.core.Control;
import peersim.core.Network;
import peersim.core.Node;
import peersim.core.GeneralNode;
import peersim.dynamics.NodeInitializer;
import java.lang.NullPointerException;
import java.util.Map.Entry;

public class ChordInitializer implements NodeInitializer, Control {

	private static final String PAR_PROT = "protocol";
	private static final String PAR_MIPROT = "miprotocolo";
	private static final String PAR_TRANS = "transport";

	private static final String PAR_VERSION = "version";



	public String VERSION;
	int pid = 0;
	int mid = 0;
	int tid = 0;


	public ChordInitializer(String prefix) {
		pid = Configuration.getPid(prefix + "." + PAR_PROT);
		mid = Configuration.getPid(prefix + "." + PAR_MIPROT);
		tid = Configuration.getPid(prefix + "." + PAR_TRANS);
		VERSION = Configuration.getString(prefix + "." + PAR_VERSION);

		Utils.initialize(pid, mid, tid, VERSION);
	}


	public boolean execute() {
      //uno de los nodos sera el servidor, por lo que no hace falta un id para el
		ArrayList<BigInteger> ids = Utils.generateIDs(Network.size());

		for (int i = 0; i < Network.size(); i++) {
			Node node = (Node) Network.get(i);
			ChordProtocol cp = Utils.getChordFromNode(node);
			cp.node = node;
			cp.chordId = ids.get(i);
			Utils.NODES.put(cp.chordId, cp);
			cp.fingerTable = new BigInteger[Utils.M];
			cp.successorList = new BigInteger[Utils.SUCC_SIZE];

			ProtocoloAcuerdo2 pa = Utils.getProtocol2FromNode(node);
			pa.setNode(node);
			//este nodo sera de un usuario
			pa.setUsuario();
		}
		NodeComparator nc = new NodeComparator(pid);
		Network.sort(nc);
		myCreateFingerTable();
    //printNeighs();

		//ya tengo lleno el HashMap SCORES ahora puedo traspasar al ArrayList VELOCES
		//cada vez que vaya a buscar al nodo mas lento que el nodo actual,
		//actualizo vuelvo a ordenar el ArrayList VELOCES en base a su tiempo de respuesta
		//promedio.

		if(Utils.VELOCES==null){
			Utils.VELOCES = new ArrayList<Entry<Long, ScoreUser>>(Utils.SCORES.entrySet());
		}

    //agregar un nuevo nodo a la red, el nodo servidor
    addServer();
		//se inicializa en 0 la utilizacion de cada peer, incluido el servidor
		inicializaUtilizacion();
		return false;
	}

   public void addServer(){
      Node server = (Node)Network.get(0).clone();
      ProtocoloAcuerdo2 pa = Utils.getProtocol2FromNode(server);
      pa.setNode(server);
      pa.setServidor();
      Utils.SERVER_ID = server.getID();
      Utils.SERVER = server;
      Network.add(server);
   }



	public void initialize(Node n) {
		ChordProtocol cp = (ChordProtocol) n.getProtocol(pid);
		cp.join(n);
	}

	public ChordProtocol findNodeforId(BigInteger id) {
		for (int i = 0; i < Network.size(); i++) {
         Node aux = Network.get(i);
         if(aux.getID()!=Utils.SERVER_ID){
            ChordProtocol cp = Utils.getChordFromNode(aux);
            if(cp.chordId.compareTo(id) >= 0)
            return cp;
         }
		}
		return Utils.getChordFromNode(Network.get(0));
	}

	public void inicializaUtilizacion(){
		for (int i = 0; i < Network.size(); i++) {
			Utils.UTILIZACION_PEERS.add(0l);
		}
	}



	public void myCreateFingerTable() {

		for (int i = 0; i < Network.size(); i++) {
			ChordProtocol cp = Utils.getChordFromNode(Network.get(i));
			for (int a = 0; a < Utils.SUCC_SIZE; a++)
				cp.successorList[a] = Utils.getChordFromNode(Network.get((a + i + 1)%Network.size())).chordId;
			if (i > 0)
				cp.predecessor =  Utils.getChordFromNode(Network.get(i - 1)).chordId;
			else
				cp.predecessor =  Utils.getChordFromNode(Network.get(Network.size() - 1)).chordId;

			for (int j = 0; j < cp.fingerTable.length; j++) {

				long a = (long) (cp.chordId.longValue() + Math.pow(2, j)) %(long)Math.pow(2, Utils.M);
				BigInteger id = new BigInteger(a+"");
				cp.fingerTable[j] = findNodeforId(id).chordId;

			}
		}

	}



	public void printNeighs(){
		for (int i = 0; i < Network.size(); i++) {
			Node node = (Node) Network.get(i);
         if(node.getID()!=Utils.SERVER_ID){
            ChordProtocol cp = (ChordProtocol) node.getProtocol(pid);

            System.out.print(cp + "@" +node.getID() + ": ");
            //			System.out.print((ChordProtocol) cp.predecessor.getProtocol(pid));
            //			for(int j =0; j < cp.successorList.length; j++){
            //				System.out.print((ChordProtocol) cp.successorList[j].getProtocol(pid) + "@"+cp.successorList[j].getIndex() + " ");
            //			}
            for(int j =0; j < cp.fingerTable.length; j++){
               if(cp.fingerTable[j] != null)
               System.out.print(cp.fingerTable[j] + " ");
            }
            System.out.println();
         }
		}
	}




	class NodeComparator implements Comparator<Node> {

		public int pid = 0;

		public NodeComparator(int pid) {
			this.pid = pid;
		}

		@Override
		public int compare(Node arg0, Node arg1) {
			BigInteger one = null;
         BigInteger two = null;
         try{
            one = ((ChordProtocol) (arg0).getProtocol(pid)).chordId;
         }
         catch(NullPointerException e){
            System.out.println("one era el servidor");
            one = new BigInteger(0+"");
         }
         try{
            two = ((ChordProtocol) (arg1).getProtocol(pid)).chordId;
         }
         catch(NullPointerException e){
            System.out.println("two era el servidor");
            two = new BigInteger(0+"");
         }
         try{
            return one.compareTo(two);
         }
         catch(NullPointerException e){
            System.out.println("fallo la comparacion");
            return 0;
         }
		}
	}
}
