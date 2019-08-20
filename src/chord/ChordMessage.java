/**
 *
 */
package chord;

import java.math.BigInteger;
//import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;




public class ChordMessage {

	public static final int LOOK_UP=0;//buscar nodo encargado de este mensaje
	public static final int FINAL=1;//encontre el nodo encargado del mensaje
	public static final int SUCCESSOR=2;//?
	public static final int SUCCESSOR_FOUND=3;//?
	public static final int NOTIFY=4;//?

   public static String[] nombres = {"LOOK_UP","FINAL","SUCCESSOR","SUCCESSOR_FOUND","NOTIFY"};



   private String label;
	private int type;
	private BigInteger sender;
	private Object content;
	private ArrayList<String> path = new ArrayList<String>();
	private Object extra;

	int size;

   public ChordMessage(int type, Object content) {
		this.type = type;
		this.content = content;
    this.extra = null;
		this.size = 0;
	}

	public void setExtra(Object extra, long s) {
		this.extra = extra;
		this.size += s;
	}


	public int getSize(){
		return this.size;
	}
	public Object getExtra() {
		return this.extra;
	}

   public int getType(){
		return this.type;
	}

	public BigInteger getSender() {
		return sender;
	}

	public void setSender(BigInteger sender){
		this.size += (int)Utils.M/8;//M bits usados para el anillo
		this.sender = sender;
		addToPath(sender);
	}

	public Object getContent() {
		return content;
	}

   public String getName(int type){
      return nombres[type];
   }


	public void addToPath(BigInteger chordId){
		if(path.size() > 0){
			String last = path.get(path.size()-1);
			if(last != null && last.equals(chordId.toString()))
				return;
		}
		path.add(chordId.toString());
	}
	public ArrayList<String> getPath(){
		return path;
	}


	public String getLabel() {
		return label;
	}


	public void setLabel(String label) {

		this.size += (label!=null?label.length():0) ;
		this.label = label;
	}

	public boolean isType(int type){
		return this.type == type;
	}


}
