package chord;

import java.util.ArrayList;
import peersim.core.Control;
import peersim.core.Network;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import peersim.config.Configuration;



public class ResultObserver implements Control {
	private static final String PAR_VERSION = "version";

	public String VERSION;

	public ResultObserver(String prefix) {
		VERSION = Configuration.getString(prefix + "." + PAR_VERSION);

	}


	public boolean execute() {
		/*ArrayList<Integer> hopCounters = new ArrayList<Integer>();
		int max = 0;
		int min = Integer.MAX_VALUE;
		for (int j = 0; j < Utils.receivedMessages.size(); j++){
			@SuppressWarnings("unchecked")
			int hops = ((ArrayList<String>)Utils.receivedMessages.get(j).getContent()).size()-1;
			if (hops > max)
				max = hops;
			if (hops< min)
				min = hops;
			hopCounters.add(hops);
		}
		double mean = meanCalculator(hopCounters);
		//System.out.println("Mean:  " + mean + " Max Value: " + max+ " Min Value: " + min);
		*/
		QueueObserver.printResult(Utils.printerColas);
		Utils.cerrarPrinters();
		PrintWriter resultados = null;
		try{
			resultados = new PrintWriter(new FileWriter("resultados"+VERSION+".txt"));
		}
		catch(IOException ioe){
			System.exit(1);
		}
		//resultados.println("Failures: " + Utils.FAILS+ " Success: " + Utils.SUCCESS);
		resultados.println("TOTAL TAREAS:"+Utils.TASKS);
		resultados.println("Final system size: " + Network.size());
		if(Utils.CACHE_PETICIONES!=0){
			resultados.println("Cache hits: " + Utils.CACHE_HITS+"/"+Utils.CACHE_PETICIONES+" = "+(float)Utils.CACHE_HITS/Utils.CACHE_PETICIONES);
		}
		resultados.println("Latencia Servidor-Red: " + Utils.getLatenciaServidor());
		resultados.println("Latencia Red: " + Utils.getLatenciaRed());
		resultados.println("Mensajes Locales: "+Utils.MENSAJES_RED_P2P);
		resultados.println("Mensajes Servidor: "+Utils.MENSAJES_SERVIDOR);

		resultados.println("TTL_RESEND: "+Utils.TTL_RESEND);
		resultados.println("TTL_RESEND_FAIL: "+Utils.TTL_RESEND_FAIL);
		resultados.println("TTL_RESEND_OK: "+Utils.TTL_RESEND_OK);

		resultados.println("Throghput: "+Utils.C_RESPUESTAS+"/"+Utils.SIM_TIME +" = "+ Utils.getThroughput());

		resultados.println("Mensajes enviados:");
		resultados.println("INIT: "+ Utils.INIT);
		resultados.println("RE_INIT: "+ Utils.INIT);
		resultados.println("ISP_RQST_TASKS: "+ Utils.ISP_RQST_TASKS);
		resultados.println("COM_RQST_TASKS: "+ Utils.COM_RQST_TASKS);
		resultados.println("RQST_TASKS: "+ Utils.RQST_TASKS);
		resultados.println("RSPNS_TASKS: "+ Utils.RSPNS_TASKS);
		resultados.println();
		resultados.println("PROCESS_DEQUEUE: "+ Utils.PROCESS_DEQUEUE);
		resultados.println("ISP_DEQUEUE: "+ Utils.ISP_DEQUEUE);
		resultados.println("COM_SERVER_DEQUEUE: "+ Utils.COM_SERVER_DEQUEUE);
		resultados.println("RQST_IMAGE_DEQUEUE: "+ Utils.RQST_IMAGE_DEQUEUE);
		resultados.println("RESEND_TASKS: "+ Utils.RESEND_TASKS);
		resultados.println();

		resultados.println("RQST_IMAGE: "+ Utils.RQST_IMAGE);
		resultados.println("COM_RQST_IMAGE: "+ Utils.COM_RQST_IMAGE);
		resultados.println("RSPNS_IMAGE: "+ Utils.RSPNS_IMAGE);
		resultados.println("ISP_RQST_SERVER_IMAGE: "+ Utils.ISP_RQST_SERVER_IMAGE);
		resultados.println("COM_RQST_SERVER_IMAGE: "+ Utils.COM_RQST_SERVER_IMAGE);
		resultados.println("RQST_SERVER_IMAGE: "+ Utils.RQST_SERVER_IMAGE);
		resultados.println("RSPNS_SERVER_IMAGE: "+ Utils.RSPNS_SERVER_IMAGE);

		resultados.println();


		resultados.println("RESEND_ANSWERS: "+ Utils.RESEND_ANSWERS);
		resultados.println("ISP_SEND_LABELS: "+ Utils.ISP_SEND_LABELS);
		resultados.println("COM_SEND_LABELS: "+ Utils.COM_SEND_LABELS);
		resultados.println("SEND_LABELS: "+ Utils.SEND_LABELS);
		resultados.println("MORE_TASKS: "+ Utils.MORE_TASKS);


		resultados.println("CHORD_LOOK_UP: "+Utils.CHORD_LOOK_UP);
		resultados.println("CHORD_SUCCESSOR: "+Utils.CHORD_SUCCESSOR);
		resultados.println("CHORD_SUCCESSOR_FOUND: "+Utils.CHORD_SUCCESSOR_FOUND);
		resultados.println("CHORD_FINAL: "+Utils.CHORD_FINAL);
		resultados.println("CHORD_NOTIFY: "+Utils.CHORD_NOTIFY);

		resultados.close();


		/*System.out.println("Utilizacion por Peer:");
		for (int i = 0; i < Utils.UTILIZACION_PEERS.size() ; i++) {
			if(i!=Utils.UTILIZACION_PEERS.size()-1){
				System.out.println("  ["+i+"] -> "+Utils.UTILIZACION_PEERS.get(i));
			}
			else{
				System.out.println("  [S] -> "+Utils.UTILIZACION_PEERS.get(i));
			}
		}*/

		return false;
	}

	private double meanCalculator(ArrayList<Integer> list) {
		int lenght = list.size();
		if (lenght == 0)
			return 0;
		int sum = 0;
		for (int i = 0; i < lenght; i++) {
			sum = sum + ((Integer) list.get(i)).intValue();
		}
		double mean = (double) sum / lenght;
		return mean;
	}

}
