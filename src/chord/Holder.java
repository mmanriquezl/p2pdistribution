package chord;

public class Holder{
   int hold;
   public Holder(){
      hold = 0;
   }

   public void hold(int millisegundos){
      hold+=millisegundos;
   }
   public int getHold(){
      return hold;
   }

   public void reset(){
      hold = 0;
   }
}
