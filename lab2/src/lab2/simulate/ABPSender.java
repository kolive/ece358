package lab2.simulate;
import lab2.event.*;

public class ABPSender {
	
	private EventScheduler es;
	
	private int sn;
	private int nextSN;
	private double timeoutValue;
	private double currentTime;
	
	private double packetSize;
	private double headerSize;
	private double propDelay;
	
	private double ber;

	private double processPTime;

	private double processHTime;
	
	public ABPSender(
			double timeout, double ps, double hs, double lr, double pd, double ber){
		sn = 0;
		nextSN = 1;
		timeoutValue = timeout;
		currentTime = 0;
		packetSize = ps;
		headerSize = hs;
		propDelay = pd;
		processPTime = (hs + ps)/lr;
		processHTime = hs/lr;
		es = new EventScheduler();
		this.ber = ber;
		
	}
	
	public Event send(int sn){
		//TODO: Make sure I'm calculating process and delay properly for both sides
		
		//first stretch of the channel begins after processing the FRAME (processTime)
		Channel.simulate(ber, propDelay, currentTime + processPTime, packetSize + headerSize);
		boolean forwardDropped = Channel.isPacketDropped();
		boolean forwardError = Channel.isPacketError();
		
		//second stretch of the channel begins after processing the ACK (headerSize / linkRate)
		Channel.simulate(ber, propDelay, Channel.getLastTime()+ processHTime, headerSize);
		boolean reverseDropped = Channel.isPacketDropped();
		boolean reverseError = Channel.isPacketError();
		
		if(forwardDropped || reverseDropped){
			return null;
		}
		
		return new Event(EventType.ACK, Channel.getLastTime(), nextSN, forwardError || reverseError );
	}
	
	public double simulate(int successfulArrivals, boolean nackEnabled){
		currentTime = 0;
		Event e;
		int count = 0;
		sn = 0;
		nextSN = 1;
		es = new EventScheduler();
		
		while( count < successfulArrivals ){
			//process next event
			e = es.dequeue();
			if(e != null){
				if(e.getType() == EventType.TO){
					currentTime = e.getTime(); //is there a time to process to?
					es.queue(new Event(EventType.TO, currentTime+timeoutValue+processPTime));	
					es.queue(send(sn));
				}else if(!e.isError()){
					currentTime = e.getTime() + processHTime; //time to process the return header
					if(e.getSN() == nextSN){
						count++;
						es.purgeTimeouts();
						sn = (sn+1)%2;
						nextSN = (nextSN+1)%2;
					/*	System.out.println("abpReceived packet with SN: " + e.getSN() + " next SN to send: " + nextSN);
						System.out.println("abpcurrent count: " + count);
						System.out.println("abpcurrent time: " + currentTime);
						System.out.println("Sending: " + sn);
						System.out.println("events: " + ecount);*/
						es.queue(new Event(EventType.TO, currentTime+timeoutValue+processPTime));	
						es.queue(send(sn));
					}else if(nackEnabled){
						//should i be purging timeouts here? I think so
						es.purgeTimeouts();
						es.queue(new Event(EventType.TO, currentTime+timeoutValue+processPTime));	
						es.queue(send(sn));
					}
				}else if(nackEnabled && e.isError()){
					currentTime = e.getTime(); //is there a time to process to?
					//should i be purging timeouts here? I think so
					// Why am I purging timeouts and resending when nackEnabled = true and the event has an error? Shouldn't this happen if the event is a NACK?
					es.purgeTimeouts();
					es.queue(new Event(EventType.TO, currentTime+timeoutValue+processPTime));	
					es.queue(send(sn));
				}
			}else{
				es.queue(new Event(EventType.TO, currentTime + processPTime+timeoutValue));	
				es.queue(send(sn));
			}
			
		}
		return currentTime;
		
	}

}
