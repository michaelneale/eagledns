package se.unlogic.standardutils.dao.script;
/**
 * This class represents a region (a sequence) between two symbols (sequences) in a sequence 
 * @author sikstromj
 *
 */
public class Region {

	private final Symbol start;
	private final Symbol end;

	public Region(Symbol start, Symbol end) {
		this.start = start;
		this.end = end;
	}

	public Integer getStart() {
		return this.start.getOffsets().getEnd();
	}

	public Integer getEnd() {
		return this.end.getOffsets().getStart();
	}
	
	
}
