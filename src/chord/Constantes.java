package chord;

import peersim.config.Configuration;
import peersim.core.Control;

import peersim.config.Configuration;

public class Constantes implements Control{

  private static final String PAR_TASKS = "tasks";
  private static final String PAR_THRESHOLD_R = "threshold_r";
  private static final String PAR_THRESHOLD_C = "threshold_c";
  private static final String PAR_CONTINUAR = "continuar";
  private static final String PAR_INIT_DELAY = "init_delay";
  private static final String PAR_PESO = "peso";
  private static final String PAR_PROB_EXPERTO = "prob_experto";
  private static final String PAR_MIN_TASK_DELAY = "min_task_delay";
  private static final String PAR_MAX_TASK_DELAY = "max_task_delay";
  private static final String PAR_MIN_COM_DELAY = "min_com_delay";
  private static final String PAR_MAX_COM_DELAY = "max_com_delay";
  private static final String PAR_MIN_SERVER_DELAY = "min_server_delay";
  private static final String PAR_MAX_SERVER_DELAY = "max_server_delay";
  private static final String PAR_COM_SIZE = "com_size";
  private static final String PAR_CONSENSO_SOLVERS = "consenso_solvers";
  private static final String PAR_EXPERTO_CORRECTA = "prob_experto_correcta";
  private static final String PAR_NORMAL_CORRECTA = "prob_normal_correcta";


  private static final String PAR_ALPHA = "alpha";
  private static final String PAR_BETA = "beta";
  private static final String PAR_GAMMA = "gamma";
  private static final String PAR_DELTA = "delta";

  private static final String PAR_TTL_RESEND = "ttl_resend";
  private static final String PAR_MAX_RESPUESTAS = "max_respuestas";



  public static int TASKS;
  public static int THRESHOLD_RESPUESTAS;
	public static double THRESHOLD_CONSENSO;
  public static double P_CONTINUAR;
  public static double INIT_DELAY;
  public static double PROB_EXPERTO;
	public static double PESO_EXPERTO;
	public static int MIN_TASK_DELAY;
	public static int MAX_TASK_DELAY;
	public static int MIN_COM_DELAY;
	public static int MAX_COM_DELAY;
	public static int MIN_SERVER_DELAY;
	public static int MAX_SERVER_DELAY;
	public static int COM_SIZE;
  public static int CONSENSO_SOLVERS;
  public static double EXPERTO_CORRECTA;
  public static double NORMAL_CORRECTA;

	public static double ALPHA;
	public static double BETA;
	public static double GAMMA;
	public static double DELTA;

  public static int TTL_RESEND;
  public static int MAX_RESPUESTAS;

  public Constantes(String prefix){

    this.TASKS = Configuration.getInt(prefix + "." + PAR_TASKS);
    this.THRESHOLD_RESPUESTAS = Configuration.getInt(prefix + "." + PAR_THRESHOLD_R);
    this.THRESHOLD_CONSENSO = Configuration.getDouble(prefix + "." + PAR_THRESHOLD_C);
    this.P_CONTINUAR = Configuration.getDouble(prefix + "." + PAR_CONTINUAR);
    this.INIT_DELAY = Configuration.getDouble(prefix + "." + PAR_INIT_DELAY);
    this.PROB_EXPERTO = Configuration.getDouble(prefix + "." + PAR_PROB_EXPERTO);
  	this.PESO_EXPERTO = Configuration.getDouble(prefix + "." + PAR_PESO);
  	this.MIN_TASK_DELAY = Configuration.getInt(prefix + "." + PAR_MIN_TASK_DELAY);
  	this.MAX_TASK_DELAY = Configuration.getInt(prefix + "." + PAR_MAX_TASK_DELAY);
  	this.MIN_COM_DELAY = Configuration.getInt(prefix + "." + PAR_MIN_COM_DELAY);
  	this.MAX_COM_DELAY = Configuration.getInt(prefix + "." + PAR_MAX_COM_DELAY);
  	this.MIN_SERVER_DELAY = Configuration.getInt(prefix + "." + PAR_MIN_SERVER_DELAY);
  	this.MAX_SERVER_DELAY = Configuration.getInt(prefix + "." + PAR_MAX_SERVER_DELAY);
  	this.COM_SIZE = Configuration.getInt(prefix + "." + PAR_COM_SIZE);
    this.CONSENSO_SOLVERS = Configuration.getInt(prefix + "." + PAR_CONSENSO_SOLVERS);
    this.EXPERTO_CORRECTA = Configuration.getDouble(prefix + "." + PAR_EXPERTO_CORRECTA);
    this.NORMAL_CORRECTA = Configuration.getDouble(prefix + "." + PAR_NORMAL_CORRECTA);


  	this.ALPHA = Configuration.getDouble(prefix + "." + PAR_ALPHA);
  	this.BETA = Configuration.getDouble(prefix + "." + PAR_BETA);
  	this.GAMMA = Configuration.getDouble(prefix + "." + PAR_GAMMA);
  	this.DELTA = Configuration.getDouble(prefix + "." + PAR_DELTA);

    this.TTL_RESEND = Configuration.getInt(prefix + "." + PAR_TTL_RESEND);

    this.MAX_RESPUESTAS = Configuration.getInt(prefix + "." + PAR_MAX_RESPUESTAS);
  }

  public boolean execute() {
    return false;
  }
}
