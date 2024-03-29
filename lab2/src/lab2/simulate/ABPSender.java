package lab2.simulate;
import lab2.event.*;

/**
 * ABPSender is a simulator for a one way transmission using ABP retransmission scheme.
 * Simulation should be started by instantiating with correct params and then calling simulate()
 * 
 * @author kolive
 *
 */
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
		
		//setup simulation constants
		this.timeoutInS = timeoutInS;
		this.packetSizeInBits = packetSizeInBits;
		this.headerSizeInBits = headerSizeInBits;
		this.propDelayInS = propDelayInS;
		frameTransmitInS = (headerSizeInBits + packetSizeInBits)/linkRateInBPS;
		headerTransmitInS = headerSizeInBits/linkRateInBPS;
		this.bitErrorRate = bitErrorRate;
		
	}
	
	/**
	 * Simulates the channel and receiver as one blackbox.
	 * Channel is a static class which wraps the error and propagation calculations 
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
	
	/**
	 * Simulates the transmission of identical packets using ABP retransmission scheme.
	 * Returns the throughput of the scheme in bps 
	 * 
	 * @param successfulArrivals is the count of packets that must be received and ack'd before the simulation is complete
	 * @param nackEnabled flag to enable or disable NACK functionality
	 * @return a double, representing the throughput of the scheme in bits per second
	 */
	public double simulate(int successfulArrivals, boolean nackEnabled){
		
		//init simulation constants
		Event e;
		int count = 0;
		int seqNumber = 0;
		double currentTimeInS = 0;
		EventScheduler eventScheduler = new EventScheduler();
		
		//main simulation loop
		while( count < successfulArrivals ){
			//process next event
			e = eventScheduler.dequeue();
			if(e != null){
				currentTimeInS = e.getTime(); 
				if(e.getType() == EventType.TO){
					currentTimeInS = e.getTime();
					
					//if there's a timeout, send the new packet and then register the new timeout
                    eventScheduler.queue(send((seqNumber-1)%2, currentTimeInS));  
                    eventScheduler.purgeTimeouts();

					eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
					
				}else if(!e.isError()){
					//if the rn == sn then packet received success
					if(e.getSN() == seqNumber){
						
						count++;
						//send a new packet, purge the timeouts
						eventScheduler.queue(send(seqNumber, currentTimeInS));					
						seqNumber = (seqNumber+1) % 2;
						eventScheduler.purgeTimeouts();
						eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
						
					}else if(nackEnabled){
						//if received a nack, then don't wait for timeout to resend
						seqNumber = e.getSN(); //sn = rn
						eventScheduler.queue(send(seqNumber, currentTimeInS));		
						seqNumber = (seqNumber+1) % 2;
						eventScheduler.purgeTimeouts();
						eventScheduler.queue(new Event(EventType.TO, currentTimeInS+timeoutInS+frameTransmitInS));	
					}
				}else if(nackEnabled && e.isError()){
					//if received a nack, then don't wait for timeout to resend
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
		//returns throughput in bps
		return (count*packetSizeInBits)/(currentTimeInS); 
		
	}

}
