
public class Bus {
	public int id;
	public String location;
	public int passengers;
	public int line;
	public boolean makeStop;
	public int highTemp;
	public int stall;
	public String nextStop;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}
	
	public void setLine(int line) {
		this.line = line;
	}
	
	public void setMakeStop(boolean makeStop) {
		this.makeStop = makeStop;
	}
	
	public void setHighTemp(int highTemp) {
		this.highTemp = highTemp;
	}
	
	public void setStall(int stall) {
		this.stall = stall;
	}
	
	public void setNextStop(String nextStop) {
		this.nextStop = nextStop;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public int getPassengers() {
		return this.passengers;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public boolean getMakeStop() {
		return this.makeStop;
	}
	
	public int getHighTemp() {
		return this.highTemp;
	}
	
	public int getStall() {
		return this.stall;
	}
	
	public String getNextStop() {
		return this.nextStop;
	}
	
	public void passengersLeaving(int crowd) {
		if(stall > 0) {
			passengers = passengers - crowd; 
			stall = stall - 1;
		}
		if(passengers < 0 )
			passengers = 0;
	}
	
	public int pplEntering(int crowd) {
		int pplEntered = 0;
		int canEnter = 100 - passengers;
		if(crowd <= canEnter) {
			passengers = passengers + crowd;
			pplEntered = crowd;
		}
		else {
			passengers = 100;
			pplEntered = canEnter;
		}
		if(stall>0)
			stall = stall - 1;
		return pplEntered;
	}
	
	
}
