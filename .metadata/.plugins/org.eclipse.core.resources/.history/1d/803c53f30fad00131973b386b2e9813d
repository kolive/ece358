package lab2.simulate;
import lab2.event.*;

public class ABPSender {
	
	
	private double timeoutInS;	
	private double packetSizeInBits;
	private double headerSizeInBits;
	private double propDelayInS;	
	private double bitErrorRate;
	private double frameTransmitInS;
	private double headerTransmitInS;
	
	public ABPSender(
			double timeoutInS, double packetSizeInBits, double headerSizeInBits, double linkRateInBPS, double propDelayInS, double bitErrorRate){
		this.timeoutInS = timeoutInS;
		this.packetSizeInBits = packetSizeInBits;
		this.headerSizeInBits = headerSizeInBits;
		this.propDelayInS = propDelayInS;
		frameTransmitInS = (headerSizeInBits + packetSizeInBits)/linkRateInBPS;
		headerTransmitInS = headerSizeInBits/linkRateInBPS;
		this.bitErrorRate = bitErrorRate;
		
	}
	
	public Event send(int seqNumber, double currentTimeInS){
		//first stretch of the channel begins after processing the FRAME must take into account time to transmit FRAME
		Channel.simulate(bitErrorRate, propDelayInS, currentTimeInS + frameTransmitInS, packetSizeInBits + headerSizeInBits);
		boolean forwardDropped = Channel.isPacketDropped();
		boolean forwardError = Channel.isPacketError();
		
		//second stretch of the channel begins after getting the FRAME, must also take into account time to transmit ACK
		Channel.simulate(bitErrorRate, propDelayInS, Channel.getLastTime() + headerTransmitInS, headerSizeInBits);
		boolean reverseDropped = Channel.isPacketDropped();
		boolean reverseError = Channel.isPacketError();
		
		if(forwardDropped || reverseDropped){
			return null;
		}
		
		//sender gets the ack
		return new Event(EventType.ACK, Channel.getLastTime(), (seqNumber+1)%2, forwardError || reverseError );
	}
	
	//returns throughput
	public double simulate(int successfulArrivals, boolean nackEnabled){
		
		Event e;
		int count = 0;
		int seqNumber = 0;
		double currentTimeInS = 0;
		EventScheduler eventScheduler = new EventScheduler();
		
		while( count < successfulArrivals ){
			//process next event
			e = eventScheduler.dequeue();
			if(e != null){
				currentTimeInS = e.getTime(); 
				if(e.getType() == EventType.TO){
					currentTimeInS = e.getTime();
					
					//if there's a timeout, send the new packet and then register the new timeout
					eventScheduler.queue(send(seqNumber, currentTimeInS));					
					seqNumber = (seqNumber+1) % 2;
					eventScheduler.purgeTimeouts();
					eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
					
				}else if(!e.isError()){
					//if the rn == sn then packet recieved success
					if(e.getSN() == seqNumber){
						
						count++;
						//send a new packet, purge the timeouts
						eventScheduler.queue(send(seqNumber, currentTimeInS));					
						seqNumber = (seqNumber+1) % 2;
						eventScheduler.purgeTimeouts();
						eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
						
					}else if(nackEnabled){
						//if recieved a nack, then don't wait for timeout to resend
						seqNumber = e.getSN(); //sn = rn
						eventScheduler.queue(send(seqNumber, currentTimeInS));		
						seqNumber = (seqNumber+1) % 2;
						eventScheduler.purgeTimeouts();
						eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
					}
				}else if(nackEnabled && e.isError()){
					//if recieved a nack, then don't wait for timeout to resend
					seqNumber = e.getSN(); //sn = rn
					eventScheduler.queue(send(seqNumber, currentTimeInS));		
					seqNumber = (seqNumber+1) % 2;
					eventScheduler.purgeTimeouts();
					eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
				}
			}else{
				//send a new packet, purge the timeouts
				eventScheduler.queue(send(seqNumber, currentTimeInS));					
				seqNumber = (seqNumber+1) % 2;
				eventScheduler.purgeTimeouts();
				eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
			}
			
		}
		return (count*packetSizeInBits)/(currentTimeInS); //returns throughput in bps
		
	}

}
