package se.unlogic.standardutils.dao.script;
/**
 * This class represents a start offset and an end offset within a sequence
 * @author sikstromj
 *
 */
public class Offsets {

	private final Integer start;
	private final Integer end;

	public Offsets(Integer start, Integer end) {
		super();
		this.start = start;
		this.end = end;
	}

	public Integer getStart() {
		return start;
	}

	public Integer getEnd() {
		return end;
	}
}
