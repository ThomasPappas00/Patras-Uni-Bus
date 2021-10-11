
public class BusStop {
	public int id;
	public String name;
	public String location;
	public int pplWaiting;
	public int line;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setPplWaiting(int pplWaiting) {
		this.pplWaiting = pplWaiting;
	}
	
	public void setLine(int line) {
		this.line = line;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getLocation() {
		return this.location;
	}
	
	public int getPplWaiting() {
		return this.pplWaiting;
	}
	
	public int getLine() {
		return this.line;
	}
}
