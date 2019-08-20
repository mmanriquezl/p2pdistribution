/**
 *
 */
package chord;

import peersim.config.Configuration;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.core.Node;
import peersim.edsim.EDProtocol;
import peersim.edsim.EDSimulator;
import peersim.transport.Transport;

import java.io.File;

import java.math.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.lang.Runtime;
import java.io.IOException;

public class ChordProtocol implements EDProtocol, Comparable<ChordProtocol> {

	public Node node;

	public BigInteger predecessor;
	public BigInteger[] fingerTable, successorList;
	public BigInteger chordId;



	public int fingerToFix=0;
   public ChordProtocol(String prefix) {

	}
	public void debug(String mensaje){
		 //System.out.println("("+CommonState.getTime()+")["+node.getID()+"]: "+mensaje);
		 return;
	}

   public void processEvent(Node node, int pid, Object event) {

		ChordMessage msg = (ChordMessage) event;
		receive(msg);
	}

	private void receive(ChordMessage msg){
		//Mensaje mm = (Mensaje)msg.getExtra();
		//if(mm!=null) {
         //System.out.println("Soy el nodo "+node.getID()+" ("+this.chordId+") y estoy buscando la imagen "+((File)mm.getContenido()).getPath());
			//System.out.print("MILABEL: "+mm.getLabel());
			//System.out.print(" TIPO : "+tipos[msg.getType()]);
			//if(msg.getLabel()!=null)
			//	System.out.println(" CHORDLABEL: "+msg.getLabel());
			//else
			//	System.out.println();
		//}

		switch(msg.getType()){
   		case ChordMessage.LOOK_UP:
				Utils.CHORD_LOOK_UP++;
				debug("CHORDEVENT: llego evento LOOK_UP");
   			onRoute(msg);
   			break;
   		case ChordMessage.SUCCESSOR:
				Utils.CHORD_SUCCESSOR++;
				debug("CHORDEVENT: llego evento SUCCESSOR");
   			onRoute(msg);
   			break;
   		case ChordMessage.SUCCESSOR_FOUND:
			  Utils.CHORD_SUCCESSOR_FOUND++;
				debug("CHORDEVENT: llego evento SUCCESSOR_FOUND");
   			onSuccessorFound(msg);
   			break;
   		case ChordMessage.FINAL:
			  Utils.CHORD_FINAL++;
				debug("CHORDEVENT: llego evento FINAL");
   			onFinal(msg);
   			break;
   		case ChordMessage.NOTIFY:
			  Utils.CHORD_NOTIFY++;
				debug("CHORDEVENT: llego evento NOTIFY");
   			onNotify(msg);
   			break;
		}

	}

	private void send(ChordMessage msg, BigInteger destID){
		Transport transport = (Transport) node.getProtocol(Utils.TID);
		msg.addToPath(destID);
		ChordProtocol cpDest = Utils.NODES.get(destID);
		if(cpDest != null && cpDest.isUp()){
			//transport.send(node, cpDest.node, msg, Utils.PID);
			//long delay = Utils.localDelay();
			long delay = transport.getLatency(node, cpDest.node);
			if(msg.getExtra()!=null){
				Utils.addLatenciaRed(delay, node.getID(),10+msg.getType(), Utils.M + (msg.getLabel()!=null?msg.getLabel().length():0));
			}
			EDSimulator.add(delay, msg, cpDest.node, Utils.PID);
		}
		else{
			Utils.FAILS++;
		}
	}


	public void onRoute(ChordMessage msg){
		BigInteger target = (BigInteger)msg.getContent();
		Object content = msg.isType(ChordMessage.LOOK_UP) ? msg.getPath(): Utils.NODES.get(successorList[0]).clone();
		int type = msg.isType(ChordMessage.LOOK_UP) ? ChordMessage.FINAL : ChordMessage.SUCCESSOR_FOUND;
    Mensaje mm = (Mensaje)msg.getExtra();
		if (target.equals(chordId) || inAB(target, chordId, successorList[0]) ||
			(msg.isType(ChordMessage.SUCCESSOR) && inAB(target, chordId, successorList[0]))) {

			if(msg.isType(ChordMessage.SUCCESSOR) && target.equals(chordId)){
			  //System.out.println(target + "es igual a "+chordId+ " y el mensaje es de tipo SUCCESSOR");
				content = this.clone();
			}
			ChordMessage finalmsg = new ChordMessage(type, content);
			finalmsg.setLabel(msg.getLabel());
			finalmsg.setSender(chordId);
         if(mm!=null){
            if(target.equals(chordId) || inAB(target, chordId, successorList[0])){
               //System.out.println("Llegue al nodo ("+chordId+") y soy el encargado de la imagen "+target);
               if(mm!=null){
                  //System.out.println("Llego al final del chord.");
                  if(mm.getLabel()=="COM_RQST_IMAGE") {
                     //le pongo delay 0 ya que solo estoy colocando el evento
                     //en el protocolo de Acuerdo (buscar un mejor nombre)
                     int delay = 0;
                     EDSimulator.add(delay, mm, node, Utils.MID);
                  }
									if(mm.getLabel()=="AGGREGATE") {
                     //le pongo delay 0 ya que solo estoy colocando el evento
                     //en el protocolo de Acuerdo (buscar un mejor nombre)
                     int delay = 0;
                     EDSimulator.add(delay, mm, node, Utils.MID);
                  }
               }
            }
            /*if(msg.isType(ChordMessage.SUCCESSOR) && inAB(target, chordId, successorList[0])){
               System.out.println(target+" esta en el rango ("+chordId +" - "+ successorList[0]+")");
            }
   			if(msg.isType(ChordMessage.SUCCESSOR) && target.equals(chordId)){
               System.out.println(target + "es igual a "+chordId+ " y el mensaje es de tipo SUCCESSOR");
            }*/
            finalmsg.setExtra(mm, mm.getSize());
         }
         //no me interesa enviar mensajes de tipo FINAL
			//send(finalmsg, msg.getSender());
		}
		else{
         /*if(mm!=null){
            System.out.println("Yo "+this.node.getID()+" ("+this.chordId+") no soy el encargado.");
         }*/
			BigInteger dest = closestPrecedingNode(target);
			if (dest == null){
				Utils.FAILS++;
            //cambio mio
            //send(msg, successorList[0]);
      }
			else{
				send(msg, dest);
			}
		}
	}


   //onFinal es un mensaje de respuesta del nodo encargado, el codigo de aca
   //lo realiza el nodo que envió la peticion
	public void onFinal(ChordMessage msg){
      Utils.receivedMessages.add(msg);
      Utils.SUCCESS++;

      /*Mensaje mm = (Mensaje)msg.getExtra();
      if(mm!=null){
         System.out.println("Llego al final del chord.");
         Mensaje aux = (Mensaje)mm;
         if(aux.getLabel()=="RQST_IMAGE") {
            //le pongo delay 0 ya que solo estoy colocando el evento
            //en el protocolo de Acuerdo (buscar un mejor nombre)
            int delay = 0;
            EDSimulator.add(delay, aux, node, Utils.MID);
         }
      }*/

	}

	public void onSuccessorFound(ChordMessage msg){
		String label = msg.getLabel();
		ChordProtocol succ = (ChordProtocol)msg.getContent();
		if(label.contains("successor")) //predecessor
		{
			BigInteger pred = succ.predecessor;
			if(label.contains("first") || pred.equals(chordId) || !Utils.isUp(pred)){
				successorList[0] = succ.chordId;
				if(label.contains("first")) predecessor = pred;
				System.arraycopy(succ.successorList,0,successorList,1,successorList.length-1);
			}
			else if(label.contains("stabilize")){
				if (inAB(pred, chordId, succ.chordId)){
					successorList[0] = pred;
					successorList[1] = succ.chordId;
					System.arraycopy(succ.successorList,0,successorList,2,successorList.length-2);
				}
				notify(successorList[0]);
			}
		}
		else if(label.contains("finger")){
			int index = Integer.parseInt(label.split(" ")[1]);
			fingerTable[index] = succ.chordId;
		}
	}

	public void onNotify(ChordMessage msg){
		BigInteger nodeId = (BigInteger) msg.getContent();
		if (predecessor == null ||
			(inAB(nodeId, predecessor, this.chordId)
			&& !nodeId.equals(chordId)))
			predecessor = nodeId;
	}

	public void join(Node myNode) {
		try{
			Runtime.getRuntime().exec("clear");
		}
		catch(IOException ioe){

		}
		//System.out.println("Nodo "+myNode.getID()+" uniendose.");
		node = myNode;
		// search a bootstrap node to join
		Node n;
		do {
			n = Network.get(CommonState.r.nextInt(Network.size()));
		} while (n == null || !n.isUp() || n.getID()==Utils.SERVER_ID);

		chordId = Utils.generateNewID();
		Utils.NODES.put(chordId, this);
		ChordProtocol cpRemote = Utils.getChordFromNode(n);
		successorList = new BigInteger[Utils.SUCC_SIZE];
		fingerTable = new BigInteger[Utils.M];

		findSuccessor(cpRemote.chordId, chordId, "successor first");
		for (int i = 0; i < fingerTable.length; i++) {
			long a = (long) (chordId.longValue() + Math.pow(2, i)) %(long)Math.pow(2, Utils.M);
			BigInteger id = new BigInteger(a+"");
			findSuccessor(cpRemote.chordId, id, "finger " + i);
		}
		System.out.println("Node " + chordId + " is in da house");
	}

	public void findSuccessor(BigInteger nodeToAsk, BigInteger id, String label){
		ChordMessage predmsg = new ChordMessage(ChordMessage.SUCCESSOR, id);
		predmsg.setLabel(label);
		predmsg.setSender(chordId);
		send(predmsg, nodeToAsk);
	}


	private BigInteger closestPrecedingNode(BigInteger id) {

		ArrayList<BigInteger> fullTable = getFullTable();
		BigInteger found = null;
		for (int i = fullTable.size()-1; i >= 0; i--) {
			BigInteger entry = fullTable.get(i);
			if (entry != null && Utils.isUp(entry) && inAB(entry, this.chordId, id) ) {
            //System.out.println(id+" esta entre "+entry+" y "+this.chordId);
				found = entry;
				break;
			}
         /*else{
            System.out.println(id+" NO esta entre "+entry+" y "+this.chordId);
         }*/
		}

		return found;
	}

	private ArrayList<BigInteger> getFullTable(){
		ArrayList<BigInteger> fullTable = new ArrayList<BigInteger>();
		HashSet<BigInteger> hs = new HashSet<BigInteger>();
		hs.addAll(Arrays.asList(fingerTable));
		hs.addAll(Arrays.asList(successorList));
		fullTable.addAll(hs);
		fullTable.sort(new Comparator<BigInteger>() {
			@Override
			public int compare(BigInteger arg0, BigInteger arg1) {
				int dist1 = Utils.distance(chordId, arg0);
				int dist2 = Utils.distance(chordId, arg1);
				return dist1 -dist2;
			}
		});

		fullTable.add(predecessor);
		return fullTable;
	}

	public void notify(BigInteger nodeId){
		ChordMessage notifyMsg = new ChordMessage(ChordMessage.NOTIFY, chordId);
		notifyMsg.setSender(chordId);
		send(notifyMsg, nodeId);
	}

	public void stabilize() {
		for(BigInteger succ: successorList){
			if(succ != null && Utils.isUp(succ)){
				successorList[0] = succ;
				findSuccessor(succ, succ, "successor stabilize");
				return;
			}
		}
		System.err.println("All successors of node " + this.chordId + " are down!");
		System.exit(1); //something went totally wrong

	}

	public void fixFingers(){
		if(fingerToFix >= fingerTable.length)
			fingerToFix = 0;
		long a = (long) (chordId.longValue() + Math.pow(2, fingerToFix)) %(long)Math.pow(2, Utils.M);
		BigInteger id = new BigInteger(a+"");
		findSuccessor(chordId, id, "finger " + fingerToFix);

	}


	private boolean inAB(BigInteger bid, BigInteger ba, BigInteger bb){
		long id = bid.longValue();
		long a = ba.longValue();
		long b = bb.longValue();
		if (id == a || id == b) return true;

		if(id > a && id < b)
			return true;
		if(id < a && a > b && id < b)
			return true;

		if(id > b && a > b && id > a)
			return true;


		return false;
	}



	@Override
	public String toString(){
		return this.chordId.toString();
	}


	public boolean equals(Object arg0){
		if(arg0 == null || !arg0.getClass().equals(this.getClass()))
			return false;
		return this.compareTo((ChordProtocol)arg0) == 0;

	}

	@Override
	public int compareTo(ChordProtocol arg0) {
		if (arg0 == null) return 100;
		return this.chordId.compareTo(arg0.chordId);
	}

	public Object clone() {
		ChordProtocol inp = null;
        try {
            inp = (ChordProtocol) super.clone();

        } catch (CloneNotSupportedException e) {
        } // never happens
        return inp;
    }


	public boolean isUp(){
		return node.isUp();
	}
}
