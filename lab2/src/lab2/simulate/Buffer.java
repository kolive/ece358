package lab2.simulate;

/**
 * Simple ds to represent the buffer
 * This linked list should always have size elements, but all of them could be "null" packets
 * @author Kyle
 *
 */
public class Buffer {
	
	class Packet{
		
		public int sn;
		public double size;
		public double time;
		
		Packet(int sn, double size, double time){
			this.sn = sn;
			this.size = size;
			this.time = time;
		}
		
		Packet(){
			sn = -1;
			size = -1;
			time = -1;
		}
		
	}

	Packet[] buffer;
	int head;
	int nextSN;
	int nextNull;
	double maxtime;
	
	public Buffer(int size, int startSN){
		
		if(size > 0){
			buffer = new Packet[size];
		}
		
		for(int i = 0; i < size; i++){
			buffer[i] = new Packet();
		}
		
		nextNull = 0;
		nextSN = startSN;
		head = 0;
	}
	
	void setNextNull(){
		if(nextNull == -1) nextNull = 0;
		for(int i = nextNull, n = 0; n < buffer.length; i = (i + 1)%buffer.length, n++){
			if(buffer[i].sn == -1){
				nextNull = i;
				return;
			}
		}
		nextNull = -1;
	}
	
	public Packet getFront(){
		return buffer[head];
	}
	
	public Packet getN(int n){
		return buffer[(head + n)%buffer.length];
	}
	
	public int addPacket(double size, double time, int sn){
		if(nextNull == -1) return -1;
		buffer[nextNull] = new Packet(sn, size, time);
		maxtime = time;
		setNextNull();
		return sn;
	}
	
	public void shift(){
		buffer[head] = new Packet();
		head = (head + 1)%buffer.length;
		setNextNull();
	}

	public double getMaxTime() {
		return maxtime;
	}
	

}
