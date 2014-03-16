package lab2.event;

/**
 * Models an event for simulation
 * @author kolive
 *
 */
public class Event {

	private EventType type;
	private double time;
	private int sn;
	private boolean error;
	
	
	public Event(EventType type, double time){
		this.type = type;
		this.time = time;
	}
	
	public Event(EventType type, double time, int sn, boolean error){
		this.type = type;
		this.time = time;
		this.error = error;
		this.sn = sn;
	}
	
	public double getTime(){
		return time;
	}
	
	public EventType getType(){
		return type;
	}
	
	public boolean isError(){
		if(type == EventType.TO) return false;
		return error;
	}
	
	public int getSN(){
		if(type == EventType.TO) return -1;
		return sn;
	}
	
	
	
}
