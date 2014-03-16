package lab2.event;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Wrapper for queues to handle the timeout and ack events.
 * 
 * @author kolive
 *
 */
public class EventScheduler {

	Queue<Event> eventSet; 
	Queue<Event> timeoutSet;
	
	
	public EventScheduler(){
		eventSet = new LinkedList<Event>();
		timeoutSet = new LinkedList<Event>();
	}
	
	public void queue(Event event) {
		if(event == null)
			return;

		if(event.getType() == EventType.TO){
			timeoutSet.add(event);
		}else{	
			eventSet.add(event);
		}
		
	}

	/**
	 * Returns the next event in time
	 * @return
	 */
	public Event dequeue() {
		if(timeoutSet.peek() == null && eventSet.peek() == null){
			return null;
		}else if(timeoutSet.peek() == null && eventSet.peek() != null){
			return eventSet.poll();
		}else if(eventSet.peek() == null && timeoutSet.peek() != null){
			return timeoutSet.poll();
		}else if(eventSet.peek().getTime() < timeoutSet.peek().getTime()){
			return eventSet.poll();
		}else if(eventSet.peek().getTime() >= timeoutSet.peek().getTime()){
			return timeoutSet.poll();
		}
		
		return null;
	}
	
	/**
	 * Returns the time of the next event in time
	 * @return
	 */
	public double getNextTime(){
		if(timeoutSet.peek() == null && eventSet.peek() == null) return Double.MAX_VALUE;
		else if(eventSet.peek() != null && timeoutSet.peek() != null) return Math.min(eventSet.peek().getTime(), timeoutSet.peek().getTime());
		else if(eventSet.peek() == null && timeoutSet.peek() != null) return timeoutSet.peek().getTime();
		return eventSet.peek().getTime();
	}

	/**
	 * Throws away old timeouts
	 */
	public void purgeTimeouts() {
		timeoutSet = new LinkedList<Event>();
	}
	
	public int size(){
		return timeoutSet.size() + eventSet.size();
	}


}
