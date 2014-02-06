package simulate;
import lab2.event.*;

public class ABPSender {
	
	private EventScheduler es;
	
	private int sn;
	private int nextSN;
	private double timeoutValue;
	private double currentTime;
	
	private double packetSize;
	private double headerSize;
	private double linkRate;
	private double processTime;
	
	public ABPSender(
			double timeout, double ps, double hs, double lr,
			EventScheduler es){
		sn = 0;
		nextSN = 1;
		timeoutValue = timeout;
		currentTime = 0;
		packetSize = ps;
		headerSize = hs;
		linkRate = lr;
		this.es = es;
		processTime = (hs + ps)/lr;
	}
	
	public Event send(int sn){
		
		return null;
	}
	
	public void simulate(){
		while(true){
			double transferTime = currentTime + processTime;
			es.queue(new Event(EventType.TO, transferTime+timeoutValue));	
			send(sn);
		}
	}

}
