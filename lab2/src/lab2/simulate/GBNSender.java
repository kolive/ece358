package lab2.simulate;
import lab2.event.*;
import lab2.simulate.Buffer;

public class GBNSender {
		
	private double timeoutInS;	
	private double packetSizeInBits;
	private double headerSizeInBits;
	private double propDelayInS;	
	private double bitErrorRate;
	private double frameTransmitInS;
	private double headerTransmitInS;
	private int bufferSize;
	private int reqNumber;
	
	public GBNSender(
			double timeoutInS, double packetSizeInBits, double headerSizeInBits, double linkRateInBPS, double propDelayInS, double bitErrorRate, int bufferSize){
		this.timeoutInS = timeoutInS;
		this.packetSizeInBits = packetSizeInBits;
		this.headerSizeInBits = headerSizeInBits;
		this.propDelayInS = propDelayInS;
		frameTransmitInS = (headerSizeInBits + packetSizeInBits)/linkRateInBPS;
		headerTransmitInS = headerSizeInBits/linkRateInBPS;
		this.bitErrorRate = bitErrorRate;
		this.bufferSize = bufferSize;
		
	}
	
	public Event send(int seqNumber, double currentTimeInS){
		//first stretch of the channel begins after processing the FRAME must take into account time to transmit FRAME
		Channel.simulate(bitErrorRate, propDelayInS, currentTimeInS + frameTransmitInS, packetSizeInBits + headerSizeInBits);
		boolean forwardDropped = Channel.isPacketDropped();
		boolean forwardError = Channel.isPacketError();

		if(!forwardDropped && !forwardError && seqNumber == reqNumber) 
			reqNumber = (reqNumber+1)%(bufferSize+1);
		
		//second stretch of the channel begins after getting the FRAME, must also take into account time to transmit ACK
		Channel.simulate(bitErrorRate, propDelayInS, Channel.getLastTime() + headerTransmitInS, headerSizeInBits);
		boolean reverseDropped = Channel.isPacketDropped();
		boolean reverseError = Channel.isPacketError();
		
		if(forwardDropped || reverseDropped){
			return null;
		}
		
		//sender gets the ack
		return new Event(EventType.ACK, Channel.getLastTime(), reqNumber, forwardError || reverseError );
	}
	
	public double simulate(int successfulArrivals, boolean nackEnabled){
		Event e;
		int count = 0;
		int seqNumber = 0;
		reqNumber = 0;
		double currentTimeInS = 0;
		EventScheduler eventScheduler = new EventScheduler();
		Buffer buffer = new Buffer(bufferSize, seqNumber);
		
		while( count < successfulArrivals ){
			//process next event
			e = eventScheduler.dequeue();
			if(e != null){
				currentTimeInS = e.getTime();
				if(e.getType() == EventType.TO){	
					//set all packets in the buffer to "unsent" state
					buffer.setAllUnsent();
					//try to send again
					eventScheduler.purgeTimeouts();
					eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
					boolean escape = false;
					do{	
						if(buffer.nextUnsent != -1){
							eventScheduler.queue(send(buffer.sendNextFrame(currentTimeInS + frameTransmitInS), currentTimeInS));
							currentTimeInS = currentTimeInS + frameTransmitInS;	
							//if there's an ACK that happens after this guy but before I can start the next guy, process the ack
							if(currentTimeInS >= eventScheduler.getNextTime()){
								escape = true;
							}
						}
						
					}while(!escape && buffer.nextUnsent != -1);		
					
		
				}else if(!e.isError()){
				
					//this is an ack
					//slide the window until we've counted all the ack'd packets
					//prepare new packets
					//shift until the next unacked packet is at the front
					while(buffer.getFront().sn != -1 && buffer.getFront().time != -2 && buffer.getFront().sn != e.getSN()){						
						count++;
						buffer.shift();
						buffer.prepareFrame(packetSizeInBits+headerSizeInBits, seqNumber);
						seqNumber = (seqNumber+1)%(bufferSize+1);
					}

					//set next timeout
					if(buffer.getFront().sn != -1 && buffer.getFront().time != -2){
						eventScheduler.purgeTimeouts();
						eventScheduler.queue(new Event(EventType.TO, buffer.getFront().time+timeoutInS));	
					}else{
						eventScheduler.purgeTimeouts();
						eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
					}
					
					//keep trying to send any unsent packets
					boolean escape = false;
					do{	
						if(buffer.nextUnsent != -1){
							eventScheduler.queue(send(buffer.sendNextFrame(currentTimeInS + frameTransmitInS), currentTimeInS));
							currentTimeInS = currentTimeInS + frameTransmitInS;	
							//if there's an ACK that happens after this guy but before I can start the next guy, process the ack
							if(currentTimeInS >= eventScheduler.getNextTime()){
								escape = true;
							}
						}
						
					}while(!escape && buffer.nextUnsent != -1);				
					
				}else if(e.isError()){
					//if the event is actually in error, pretend we didn't get it and keep trying to send the buffer
					boolean escape = false;
					do{	
						if(buffer.nextUnsent != -1){
							eventScheduler.queue(send(buffer.sendNextFrame(currentTimeInS + frameTransmitInS), currentTimeInS));
							currentTimeInS = currentTimeInS + frameTransmitInS;	
							//if there's an ACK that happens after this guy but before I can start the next guy, process the ack
							if(currentTimeInS >= eventScheduler.getNextTime()){
								escape = true;
							}
						}
						
					}while(!escape && buffer.nextUnsent != -1);				
					
				}
			}else{
				
				//prepare a buffer of n packets
				for(int i = 0; i < bufferSize; i++){
					buffer.prepareFrame(packetSizeInBits+headerSizeInBits, seqNumber);
					seqNumber = (seqNumber+1)%(bufferSize+1);
				}
				
				eventScheduler.purgeTimeouts();
				eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
				boolean escape = false;
				do{	
					if(buffer.nextUnsent != -1){
						eventScheduler.queue(send(buffer.sendNextFrame(currentTimeInS + frameTransmitInS), currentTimeInS));
						currentTimeInS = currentTimeInS + frameTransmitInS;	
						//if there's an ACK that happens after this guy but before I can start the next guy, process the ack
						if(currentTimeInS >= eventScheduler.getNextTime()){
							escape = true;
						}
					}
					
				}while(!escape && buffer.nextUnsent != -1);					
				
				
				//if there's an event that occurs during processing break and let handler get it
			}
			
		}
		return (count*packetSizeInBits)/(currentTimeInS); //returns throughput in bps
		
	}

}
