package chord;
import peersim.core.CommonState;
import peersim.core.Network;
import peersim.dynamics.DynamicNetwork;
import peersim.core.Node;

public class ChordDynamicNetwork extends DynamicNetwork {

	public ChordDynamicNetwork(String prefix) {
		super(prefix);
	}

	protected void remove(int n){
		for(int i=0; i < n; i++){
         Node aux;
         int index;
         do{
            index = CommonState.r.nextInt(Network.size());
            aux = Network.get(index);
         }while(aux.getID()==Utils.SERVER_ID);

			ChordProtocol cp = Utils.getChordFromNode(aux);
			System.out.println("Node " + cp.chordId + " died");
			Utils.NODES.remove(cp.chordId);
			Network.remove(index);
//			Network.get(inx.get(i)).setFailState(Fallible.DOWN);
		}
	}
}
