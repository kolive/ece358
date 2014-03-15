package lab2.simulate;

/**
 * Simple ds to represent the buffer
 * This linked list should always have size elements, but all of them could be "null" Frames
 * @author Kyle
 *
 */
public class Buffer {
	
	class Frame{
		
		public int sn;
		public double size;
		public double time;
		
		Frame(int sn, double size, double time){
			this.sn = sn;
			this.size = size;
			this.time = time;
		}
		
		Frame(){
			sn = -1;
			size = -1;
			time = -1;
		}
		
	}

	Frame[] buffer;
	int head;
	int nextNull;
	int nextUnsent;
	double maxtime;
	
	public Buffer(int size, int startSN){
		
		if(size > 0){
			buffer = new Frame[size];
		}
		
		for(int i = 0; i < size; i++){
			buffer[i] = new Frame();
		}
		
		nextNull = 0;
		nextUnsent = -1;
		head = 0;
	}
	
	void setNextNull(){
		if(nextNull == -1) nextNull = head;
		for(int i = nextNull, n = 0; n < buffer.length; i = (i + 1)%buffer.length, n++){
			if(buffer[i].sn == -1){
				nextNull = i;
				return;
			}
		}
		nextNull = -1;
		
	}
	
	void setNextUnsent(){
		if(nextUnsent == -1) nextUnsent = head;
		for(int i = nextUnsent, n = 0; n < buffer.length; i = (i + 1)%buffer.length, n++){
			if(buffer[i].time == -2){
				nextUnsent = i;
				return;
			}
		}
		nextUnsent = -1;
	}
	
	void setAllUnsent(){
		for(int i = head, n = 0; i != nextNull && n < buffer.length; i=(i+1)%buffer.length, n++){
			if(buffer[i].sn != -1)
				buffer[i].time = -2;
		}
		nextUnsent = head;
	}
	
	public Frame getFront(){
		return buffer[head];
	}
	
	public Frame getN(int n){
		return buffer[(head + n)%buffer.length];
	}
	
	public int prepareFrame(double size, int seqNumber){
		if(nextNull == -1) return -1;
		buffer[nextNull] = new Frame(seqNumber, size, -2); //time is -1 because it hasn't been sent
		if(nextUnsent == -1) nextUnsent = nextNull;
		setNextNull();
		return seqNumber;
	}
	
	public int sendNextFrame(double transmissionCompleteTime){
		if(nextUnsent == -1) return -1;
		buffer[nextUnsent].time = transmissionCompleteTime;
		maxtime = transmissionCompleteTime;
		int sn = buffer[nextUnsent].sn;
		setNextUnsent();
		return sn;
	}
	
	public void shift(){
		buffer[head] = new Frame();
		head = (head + 1)%buffer.length;
		setNextNull();
		setNextUnsent();
	}
	
	public void print(){
		String output = "";
		for(int n = 0; n < buffer.length; n++ ){
			output += "[" + buffer[n].sn + ", " + buffer[n].time + "],";
		}
		output += " HEAD: " + head + ", ";
		output += " Next Unsent: " + nextUnsent + ", ";
		output += " Next Null: " + nextNull;
		
		System.out.println(output);
	}

	

}
