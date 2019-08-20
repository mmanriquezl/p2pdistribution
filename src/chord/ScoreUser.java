package chord;
import java.util.ArrayList;

public class ScoreUser{



  ArrayList<Double> S2UDelays;
  ArrayList<Double> U2SDelays;
  ArrayList<Double> TaskDelays;

  int enviadas;
  int respondidas;
  int correctas;
  public ScoreUser(){
    S2UDelays = new ArrayList<Double>();
    U2SDelays = new ArrayList<Double>();
    TaskDelays = new ArrayList<Double>();
    enviadas = 0;
    respondidas = 0;
    correctas = 0;
  }

  public ScoreUser addS2UDelay(double delay){
    S2UDelays.add(delay);
    return this;
  }
  public ScoreUser addU2SDelay(double delay){
    U2SDelays.add(delay);
    return this;
  }

  public double promS2U(){
    return promedio(S2UDelays);
  }
  public double promU2S(){
    return promedio(U2SDelays);
  }
  public double promTasks(){
    return promedio(TaskDelays);
  }
  public double promedio(ArrayList<Double> lista){
    double prom = 0.0;
    if(lista.size()>0){
      for (int i = 0; i < lista.size() ; i++ ) {
        prom += lista.get(i);
      }
      prom /= lista.size();
    }
    return prom;
  }

  public ScoreUser addEnviadas(){
    enviadas++;
    return this;
  }
  public ScoreUser addEnviadas(int cant){
    enviadas+= cant;
    return this;
  }

  public void addRespondidas(){
    respondidas++;
  }
  public ScoreUser addRespondidas(int cant){
    respondidas+= cant;
    return this;
  }

  public ScoreUser addCorrectas(){
    correctas++;
    return this;
  }
  public ScoreUser addCorrectas(int cant){
    correctas+=cant;
    return this;
  }
  public String toString(){
    return Constantes.ALPHA+"*(1/"+promS2U()+" + 1/"+promU2S()+") + "+Constantes.BETA+"*"+promTasks()+" + "+Constantes.GAMMA+"*"+respondidas+"/"+enviadas+" + "+Constantes.DELTA+"*"+correctas+"/"+enviadas;
  }

  public double score(){
    double s2u = promS2U();
    double u2s = promU2S();
    if(s2u>0 && u2s>0 && enviadas>0){
      return Constantes.ALPHA*(1/s2u + 1/u2s) + Constantes.BETA*promTasks() + Constantes.GAMMA*respondidas/enviadas + Constantes.DELTA*correctas/enviadas;
    }
    return 0.0;
  }

}
