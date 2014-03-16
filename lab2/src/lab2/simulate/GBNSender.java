package lab2.simulate;
import lab2.event.*;

/**
 * GBPSender is a simulator for a one way transmission using GBN retransmission scheme.
 * Simulation should be started by instantiating with correct params and then calling simulate()
 * 
 * @author kolive
 *
 */
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
		
		//setup simulation constants
		this.timeoutInS = timeoutInS;
		this.packetSizeInBits = packetSizeInBits;
		this.headerSizeInBits = headerSizeInBits;
		this.propDelayInS = propDelayInS;
		frameTransmitInS = (headerSizeInBits + packetSizeInBits)/linkRateInBPS;
		headerTransmitInS = headerSizeInBits/linkRateInBPS;
		this.bitErrorRate = bitErrorRate;
		this.bufferSize = bufferSize;
		
	}
	
	/**
	 * Simulates the channel and receiver as one blackbox.
	 * Channel is a static class which wraps the error and propagation calculations 
	 *
	 * Also keeps track of reqNumber. 
	 *
	 * @param seqNumber
	 * @param currentTimeInS
	 * @return a null event if the frame was simulated to be dropped, otherwise an ACK
	 */
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
	
	/**
	 * Simulates the transmission of identical packets using GBN retransmission scheme.
	 * Returns the throughput of the scheme in bps 
	 * 
	 * @param successfulArrivals is the count of packets that must be received and ack'd before the simulation is complete
	 * @return a double, representing the throughput of the scheme in bits per second
	 */
	public double simulate(int successfulArrivals){
		
		//setup simulation state
		Event e;
		int count = 0;
		int seqNumber = 0;
		reqNumber = 0;
		double currentTimeInS = 0;
		EventScheduler eventScheduler = new EventScheduler();
		Buffer buffer = new Buffer(bufferSize, seqNumber);
		
		//main processing loop
		while( count < successfulArrivals ){
			//process next event
			e = eventScheduler.dequeue();
			if(e != null){
				//set the current time. if the current time is less than the buffer's max time, then update it (to avoid concurrent transmissions)
				currentTimeInS = e.getTime();
				if(buffer.maxtime > currentTimeInS) currentTimeInS = buffer.maxtime;
				
				if(e.getType() == EventType.TO){	
					//set all packets in the buffer to "unsent" state
					buffer.setAllUnsent();
					//try to send again
					eventScheduler.purgeTimeouts();
					eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
					boolean escape = false;
					do{	
						if(buffer.existsUnsent()){
							eventScheduler.queue(send(buffer.sendNextFrame(currentTimeInS + frameTransmitInS), currentTimeInS));
							currentTimeInS = currentTimeInS + frameTransmitInS;	
							//stop transmitting if there's an event to process
							if(currentTimeInS >= eventScheduler.getNextTime()){
								escape = true;
							}
						}
						
					}while(!escape && buffer.existsUnsent());		
					
		
				}else if(!e.isError()){
				
					//this is an ack
					//shift until the next unacked packet is at the front
					//prepare new packets in buffer
					while(!buffer.isFrontNull() && !buffer.isFrontUnsent() && !buffer.compareFrontSN(e.getSN())){						
						count++;
						buffer.shift();
						buffer.prepareFrame(packetSizeInBits+headerSizeInBits, seqNumber);
						seqNumber = (seqNumber+1)%(bufferSize+1);
					}

					//set next timeout
					if(!buffer.isFrontNull() && !buffer.isFrontUnsent()){
						//if there's already a sent packet at the head
						eventScheduler.purgeTimeouts();
						eventScheduler.queue(new Event(EventType.TO, buffer.getFront().time+timeoutInS));	
					}else{
						//otherwise, that packet needs to be sent
						eventScheduler.purgeTimeouts();
						eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
					}
					
					//keep trying to send any unsent packets
					boolean escape = false;
					do{	
						if(buffer.existsUnsent()){
							eventScheduler.queue(send(buffer.sendNextFrame(currentTimeInS + frameTransmitInS), currentTimeInS));
							currentTimeInS = currentTimeInS + frameTransmitInS;	
							//stop transmitting if there's an event to process
							if(currentTimeInS >= eventScheduler.getNextTime()){
								escape = true;
							}
						}
						
					}while(!escape && buffer.existsUnsent());				
					
				}else if(e.isError()){
					//if the event is actually in error, pretend we didn't get it and keep trying to send the buffer
					boolean escape = false;
					do{	
						if(buffer.existsUnsent()){
							eventScheduler.queue(send(buffer.sendNextFrame(currentTimeInS + frameTransmitInS), currentTimeInS));
							currentTimeInS = currentTimeInS + frameTransmitInS;	
							//stop transmitting if there's an event to process
							if(currentTimeInS >= eventScheduler.getNextTime()){
								escape = true;
							}
						}
						
					}while(!escape && buffer.existsUnsent());				
					
				}
			}else{
				
				//prepare a buffer of n packets
				for(int i = 0; i < bufferSize; i++){
					buffer.prepareFrame(packetSizeInBits+headerSizeInBits, seqNumber);
					seqNumber = (seqNumber+1)%(bufferSize+1);
				}
				
				//set new timeout and start trying to send them
				eventScheduler.purgeTimeouts();
				eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
				boolean escape = false;
				do{	
					if(buffer.existsUnsent()){
						eventScheduler.queue(send(buffer.sendNextFrame(currentTimeInS + frameTransmitInS), currentTimeInS));
						currentTimeInS = currentTimeInS + frameTransmitInS;	
						//stop transmitting if there's an event to process
						if(currentTimeInS >= eventScheduler.getNextTime()){
							escape = true;
						}
					}
					
				}while(!escape && buffer.existsUnsent());					
			}
			
		}
		//returns throughput in bps
		return (count*packetSizeInBits)/(currentTimeInS); 
		
	}

}
