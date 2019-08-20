package chord;

import peersim.core.Control;
import peersim.core.Network;

public class ChordMaintainer implements Control{

	public ChordMaintainer(String prefix){

	}

	@Override
	public boolean execute() {
		for(int i = 0; i < Network.size(); i++){
         if(Network.get(i).getID()!=Utils.SERVER_ID){
            ChordProtocol cp = Utils.getChordFromNode(Network.get(i));
            cp.stabilize();
            cp.fixFingers();            
         }
		}
		return false;
	}

}
