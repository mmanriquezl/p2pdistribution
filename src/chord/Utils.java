package chord;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Collections;
import java.util.Map.Entry;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.config.Configuration;


public class Utils {
	public static int PID;
	public static int MID;
	public static int TID;
	public static int M;
	public static int SUCC_SIZE;
	public static long SERVER_ID;
	public static Node SERVER;
	//probabilidad de que el usuario responda la siguiente tarea

	public static HashMap<BigInteger, ChordProtocol> NODES = new HashMap<BigInteger, ChordProtocol>();


	//hash map con las posiciones de cada nodo (ID) en el arreglo VELOCES
	public static HashMap<Long, Integer> POSICIONES = new HashMap<Long, Integer>();

  // arreglo ordenado segun la velocidad de respuesta de los usuarios
	public static ArrayList<Entry<Long, ScoreUser>> VELOCES = null;
	//node.ID versus el puntaje del nodo
	public static HashMap<Long, ScoreUser> SCORES = new HashMap<Long, ScoreUser>();

	public static ArrayList<PriorityQueue<Par<Node, Double>>> TOPUSERS = new ArrayList<PriorityQueue<Par<Node, Double>>>();

	public static int colaActual = 0;


	public static ArrayList<ChordMessage> receivedMessages;
  private static boolean initialized = false;

	public static int FAILS = 0, SUCCESS = 0;
	public static long CACHE_HITS = 0;
	public static long CACHE_PETICIONES = 0;

	public static float LATENCIA_SERVIDOR = 0.0f;
	public static int MENSAJES_SERVIDOR = 0;

	public static float LATENCIA_RED_P2P = 0.0f;
	public static int MENSAJES_RED_P2P = 0;

	public static int C_RESPUESTAS = 0;
	public static long SIM_TIME = 0;

	public static int TTL_RESEND_OK = 0;
	public static int TTL_RESEND_FAIL = 0;
	public static int TTL_RESEND = 0;

	public static ArrayList<Long> UTILIZACION_PEERS = new ArrayList<Long>();

	public static Holder holder;

	public static PrintWriter printerLatRed;
	public static PrintWriter printerLatServer;
	public static PrintWriter printerUtilizacion;
	public static PrintWriter printerColas;
	public static PrintWriter printerTiempos;
	public static PrintWriter printerThroughput;
	public static PrintWriter printerCache;

	public static int TASKS = 0;

	public static int INIT = 0;
	public static int RE_INIT = 0;
	public static int ISP_RQST_TASKS = 0;
	public static int COM_RQST_TASKS = 0;
	public static int RQST_TASKS = 0;
	public static int RSPNS_TASKS = 0;

	public static int PROCESS_DEQUEUE = 0;
	public static int ISP_DEQUEUE = 0;
	public static int COM_SERVER_DEQUEUE = 0;
	public static int RQST_IMAGE_DEQUEUE = 0;
	public static int RESEND_TASKS = 0;


	public static int COM_RQST_IMAGE = 0;
	public static int RQST_IMAGE = 0;
	public static int RSPNS_IMAGE = 0;

	public static int ISP_RQST_SERVER_IMAGE = 0;
	public static int COM_RQST_SERVER_IMAGE = 0;
	public static int RQST_SERVER_IMAGE = 0;
	public static int RSPNS_SERVER_IMAGE = 0;

	public static int RESEND_ANSWERS = 0;
	public static int ISP_SEND_LABELS = 0;
	public static int COM_SEND_LABELS = 0;
	public static int SEND_LABELS = 0;
	public static int MORE_TASKS = 0;

	public static int CHORD_LOOK_UP = 0;
	public static int CHORD_SUCCESSOR = 0;
	public static int CHORD_SUCCESSOR_FOUND = 0;
	public static int CHORD_FINAL = 0;
	public static int CHORD_NOTIFY = 0;

	public static int AGGREGATE = 0;
	public static int AGGREGATE_DEQUEUE = 0;

	public static void initialize(int pid, int mid, int tid, String v){
		if(!initialized){
			PID = pid;
			TID = tid;
			MID = mid;
			SUCC_SIZE = Configuration.getInt("SUCC_SIZE", 4);
			M = Configuration.getInt("M", 10);
			receivedMessages = new ArrayList<ChordMessage>();

			try{
				printerLatRed = new PrintWriter(new FileWriter("latencia_red"+v+".txt"));
				printerLatServer = new PrintWriter(new FileWriter("latencia_servidor"+v+".txt"));
				printerUtilizacion = new PrintWriter(new FileWriter("utilizacion"+v+".txt"));
				printerColas = new PrintWriter(new FileWriter("colas"+v+".txt"));
				printerTiempos = new PrintWriter(new FileWriter("tiempo_respuesta"+v+".txt"));
				printerThroughput = new PrintWriter(new FileWriter("throughput"+v+".txt"));
				printerCache = new PrintWriter(new FileWriter("cache_hits"+v+".txt"));
			}
			catch(IOException ioe){
				System.exit(1);
			}

			holder = new Holder();

			//primero le asigno un valor invalido para que no interfiera con el algoritmo
			SERVER_ID = -1;
			Comparator<Par<Node, Double>> scoreComparator = new Comparator<Par<Node, Double>>(){
				@Override
				public int compare(Par<Node, Double> p1, Par<Node, Double> p2) {
					if (p1.getValue() < p2.getValue()){
						return 1;
					}
					if (p1.getValue() > p2.getValue()){
						return -1;
					}
					return 0;
				}
			};

			if(TOPUSERS.size()==0){
				for (int i = 0; i < 2 ; i++ ) {
					TOPUSERS.add(new PriorityQueue<Par<Node, Double>>(10, scoreComparator));
				}
			}

		}
	}



	public static void updateTopUsers(){
		for(int i = 0; i < Network.size(); i++){
			int actual = (int) Network.get(i).getID();
			 if(actual!=Utils.SERVER_ID){
					ProtocoloAcuerdo2 pa = Utils.getProtocol2FromNode(Network.get(i));
					ListaTareas lista =pa.usuario.getTareas();

					if(QueueObserver.largos==null){
						System.out.println("largos era null");
					}
					if(lista!=null){
						QueueObserver.largos.get(actual).add(lista.getPorResponder());
					}
					else{
						QueueObserver.largos.get(actual).add(0);
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
	}

	public static Node getNodeByID(long id){
		for (int i = 0; i < Network.size() ;i++ ) {
			Node aux = (Node)Network.get(i);
			if(aux.getID()==id){
				return aux;
			}
		}
		return null;
	}

	public static void cerrarPrinters(){
		printerLatRed.close();
		printerLatServer.close();
		printerUtilizacion.close();
		printerColas.close();
		printerTiempos.close();
		printerThroughput.close();
		printerCache.close();
	}

	public static int getCostoAgregar(){
		//revisar!!!!!!!!!!!
		return randomMinMax(7, 13);
	}

	//costo de leer a disco 8 millisegundos por byte -> benchmark de acceder a disco
	public static int getUsoDiscoServidor(long bytes){
		//SSD => 478,4862963 MB/s ->  501729.2466 Bytes/milisegundos
		return  (int)Math.ceil(bytes/501729.2466);
	}
	public static long getUsoDiscoPeer(long bytes){
		//Mecanico => 133,4038 MB/s -> 139884,023 Bytes/milisegundos
		return (long)Math.ceil(bytes/139884.023);
	}

	public static void cacheHit(long nodo, String path){
		printerCache.printf("%d %d %s %s\n",CommonState.getTime(), nodo,Utils.stringToBI(path), path);
	}

	public static void addUso(long peerID, long delay){
		printerUtilizacion.printf("%d %d %d\n",CommonState.getTime(), peerID, delay);
		UTILIZACION_PEERS.set((int)peerID, UTILIZACION_PEERS.get((int)peerID) + delay);
	}

	public static void addLatenciaServidor(long milisegundos, int tipo, long bytes){
		printerLatServer.printf("%d %d %d %d\n",CommonState.getTime(), milisegundos, tipo, bytes);
		LATENCIA_SERVIDOR+=milisegundos;
		MENSAJES_SERVIDOR++;
	}
	public static void addLatenciaRed(long milisegundos, long nodo, int tipo, long bytes){
		printerLatRed.printf("%d %d %d %d %d\n",CommonState.getTime(), nodo, milisegundos, tipo, bytes);
		LATENCIA_RED_P2P+=milisegundos;
		MENSAJES_RED_P2P++;
	}

	public static float getLatenciaServidor(){
		return LATENCIA_SERVIDOR/MENSAJES_SERVIDOR;
	}
	public static float getLatenciaRed(){
		return LATENCIA_RED_P2P/MENSAJES_RED_P2P;
	}

	public static ChordProtocol getChordFromNode(Node n){
		return (ChordProtocol) n.getProtocol(PID);
	}

   public static ProtocoloAcuerdo1 getProtocolFromNode(Node n){
      return (ProtocoloAcuerdo1) n.getProtocol(MID);
   }

	 public static ProtocoloAcuerdo2 getProtocol2FromNode(Node n){
      return (ProtocoloAcuerdo2) n.getProtocol(MID);
   }


	public static int distance(BigInteger a, BigInteger b){
		int ia = a.intValue();
		int ib = b.intValue();
		if(ib >= ia) return ib-ia;
		return ib+(int)Math.pow(2, M)-ia;

	}

	public static ArrayList<BigInteger> generateIDs(int nr){
		HashSet<BigInteger> ids = new HashSet<BigInteger>();

		while(ids.size() != nr)
			ids.add(new BigInteger(Utils.M, CommonState.r));

		return new ArrayList<BigInteger>(ids);
	}

	public static BigInteger getRingId(ListaTareas lista){
		ArrayList<Integer> ids = lista.getIdsOrdenados();
		String llave = "";
		for(Integer i: ids){
			llave += i.toString();
		}
		return stringToBI(llave);
	}

	public static BigInteger stringToBI(String cadena) {
		try {
			MessageDigest sha1=MessageDigest.getInstance("SHA-1");
			sha1.reset();
			sha1.update(cadena.getBytes());
			byte[] digest=sha1.digest();
			BigInteger key = new BigInteger(1,digest);
			BigInteger base = new BigInteger("2");
			//hago modulo de la llave generada para que no supere el maximo numero del anillo
			//que es 2^M
			return key.mod(base.pow(Utils.M));
		}
		catch(NoSuchAlgorithmException nsae) {
			return new BigInteger("1");
		}
	}

	public static BigInteger generateNewID(){
		BigInteger newId;
		do
			newId= new BigInteger(Utils.M, CommonState.r);
		while(Utils.NODES.containsKey(newId));

		return newId;
	}

	public static boolean isUp(BigInteger nid){
		if(!NODES.containsKey(nid) || NODES.get(nid) == null)
			return false;


		return NODES.get(nid).isUp();
	}

   public static int randomMinMax(int min, int max){
      return CommonState.r.nextInt((max - min) + 1) + min;
   }

	 public static void logRespuesta(int c){
		 C_RESPUESTAS+=c;//eso es cantidad de tareas respondidas
		 printerThroughput.printf("%d %d %d\n",CommonState.getTime(), c, C_RESPUESTAS);
		 SIM_TIME = CommonState.getTime();
	 }

	 public static float getThroughput(){
		 return (float)C_RESPUESTAS/SIM_TIME;
	 }


	 public static long init_delay(){
		 return (long)Constantes.INIT_DELAY*fakeServerImageDelay(1024*1024);
	 }

   public static int localDelay(){
      return randomMinMax(Constantes.MIN_COM_DELAY, Constantes.MAX_COM_DELAY);
   }

   public static int serverDelay(){
      return randomMinMax(Constantes.MIN_SERVER_DELAY, Constantes.MAX_SERVER_DELAY);
   }

   //asumo una velocidad de 1MB/seg. por lo que el
   //rango va de 900 milisegundos a 1100 milisegundos
   public static long fakeLocalImageDelay(long bytes){
      return (bytes*randomMinMax(900,1100))/(1024*1024);
   }
   //asumo una velocidad 3 veces mas lenta con el servidor
   //por lo que el rango va de 2700 milisegundos a 3300 milisegundos
   public static long fakeServerImageDelay(long bytes){
      return (bytes*randomMinMax(2700,3300))/(1024*1024);
   }
   public static int localImageDelay(long bytes){
      return (int)(bytes*localDelay())/Constantes.COM_SIZE;
   }

   //el tiempo que se demora un usuario en realizar una tarea
   public static int taskDelay(){
      return randomMinMax(Constantes.MIN_TASK_DELAY,Constantes.MAX_TASK_DELAY);
   }


	 public static Node buscarSiguiente(Node nodo){
		 //ArrayList<Entry<Long, ScoreUser>> VELOCES = null;
		 Node destino;
		 //obtener la posicion en el arreglo VELOCES de este "nodo"
		 //y calcular el log en base 2 de la posicion, para tener
		 //la profundidad del arbol

		 //ordeno el arreglo VELOCES para buscar en que posicion del arbol esta este nodo
		 //y luego elegir uno del siguiente nivel como quien recibirá los datos
		 ordenarVELOCES();

		 double pos = -1;
		 //esta ordenado por ScoreUser y quiero obtener la posicion del nodo con ID nodo.getID()
		 //no me queda otra que recorrer toda la lista :/
		 for(int i = 0; i < VELOCES.size(); i++) {
			 if(VELOCES.get(i).getKey()==nodo.getID()) {
				 pos = (double) i+1;//ya que el log2(1) = 0, que es la raiz del arbol
				 break;
			 }
	   }
		 assert(pos > 0);//si es 0 o negativo arroja un error del tipo AssertionError
		 double altura = log2(pos + 1);//ESTA BIEN ESE + 1... NO LO QUITES!!!!!!!!!!

		 if (altura == 1.0){
			 //retornar el SERVER_ID
			 return SERVER;
		 }
		 else{
			 int padre = (int) ((pos - 1.0)/2.0);
			 destino = getNodeByID(VELOCES.get(padre).getKey());
			 return destino;
		 }
	 }

	 public static void ordenarVELOCES(){
		 //ArrayList<Entry<Long, ScoreUser>> VELOCES = null;

		 Collections.sort(VELOCES,
		 		new Comparator<Entry<Long, ScoreUser>>() {
        	@Override
	        public int compare(Entry<Long, ScoreUser> entry, Entry<Long, ScoreUser> other)
	        {
	            return Double.compare(entry.getValue().score(), other.getValue().score());
	        }
			  }
		 );

	 }


		public static double log2(double num){
			return (Math.log(num)/Math.log(2));
		}


}
