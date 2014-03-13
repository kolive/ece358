package lab2.simulate;
import lab2.event.*;
import lab2.simulate.Buffer;

public class GBNSender {
	
	private EventScheduler es;
	
	private int sender_sn;
	private int reciever_rn;
	private double timeoutValue;
	private double currentTime;
	
	private double packetSize;
	private double headerSize;
	private double propDelay;
	
	private double ber;

	private double processPTime;

	private double processHTime;

	private int n;
	private Buffer buffer;
	
	public GBNSender(
			double timeout, double ps, double hs, double lr, double pd, double ber, int n){
		sender_sn = 0;
		reciever_rn = 1;
		timeoutValue = timeout;
		currentTime = 0;
		packetSize = ps;
		headerSize = hs;
		propDelay = pd;
		processPTime = (hs + ps)/lr;
		processHTime = hs/lr;
		es = new EventScheduler();
		this.ber = ber;
		this.n = n;
		buffer = new Buffer(n, sender_sn);
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
		
		//generate ack event
		return new Event(EventType.ACK, Channel.getLastTime(), reciever_rn, forwardError || reverseError );
	}
	
	public double simulate(int successfulArrivals, boolean nackEnabled){
		currentTime = 0;
		Event e;
		int count = 0;
		while( count < successfulArrivals ){
			//process next event
			e = es.dequeue();
			if(e != null){
				if(e.getType() == EventType.TO){
					//process a timeout event
					currentTime = e.getTime(); //is there a time to process to?
					
					//timeout occurs, resend all packets in buffer
					es.queue(new Event(EventType.TO, currentTime+timeoutValue+processPTime));	
					for(int i = 0; i < n; i++){
						int tmpsn = buffer.getN(i).sn;
						if(tmpsn != -1) es.queue(send(tmpsn)); //if the buffer spot isn't empty resend that packet
					}					
					
				}else if(!e.isError()){
					//process an error free ack 
					currentTime = e.getTime() + processHTime; //time to process the return header
					//if the ack is good, then shift the buffer until we get to the element with the same sn as the ack
					if(e.getType() == EventType.ACK){
						do{
							count++;
							buffer.shift();
						} while (buffer.getFront().sn != -1 && buffer.getFront().sn != e.getSN());
						
						//we can also now send a bunch more packets to fill the buffer
						int x = 0;
						while(buffer.nextNull != -1){
							es.queue(send(buffer.addPacket(packetSize+headerSize, currentTime + x*processPTime)));
							x++;
							// TODO: you aren't updating current time properly. when you send a bunch of packets it fucks up the simulation
						}
						
						//we also have to reset the timeout based on when the next unacked packet
						es.purgeTimeouts();
						es.queue(new Event(EventType.TO, buffer.getFront().time+timeoutValue+processPTime));
					}
				}else if(e.isError()){
					currentTime = e.getTime(); //is there a time to process to?
				}
			}else{
				es.queue(new Event(EventType.TO, currentTime+timeoutValue+processPTime));	
				//we can also now send a bunch more packets to fill the buffer
				//not sure if this should be "scheduled sends" or if that's equivalent to sending it now
				//they'll get pre-empted if it's something happens before the send, wont they?
					//no probably not. need to add an event for "scheduling" a send. if something happens before that
					//all "scheduled" events probably need to be thrown out
				int x = 0;
				while(buffer.nextNull != -1){
					es.queue(send(buffer.addPacket(packetSize+headerSize, currentTime + x*processPTime)));
					x++;
					// TODO: you aren't updating current time properly. when you send a bunch of packets it fucks up the simulation
				}
			}
			
		}
		return currentTime;
		
	}

}
