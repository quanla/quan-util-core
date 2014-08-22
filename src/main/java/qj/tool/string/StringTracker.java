package qj.tool.string;

import java.util.List;


/**
 * Created by QuanLA
 * Date: Mar 13, 2006
 * Time: 2:20:02 PM
 */
public class StringTracker {
	public static StringTracker makeSpacesCharTracker() {
		return new StringTracker(new String[]{" ", "\r", "\n", "\t", "\f"});
	}
	
    private String[] targets;
    private char[] buffer;
    private byte rollIndex = 0;
    private int meetAt = -1;

    /**
     * Constructor
     * @param targets
     */
    public StringTracker(String[] targets) {
        this.targets = targets;
        buffer = new char[maxLength(targets)];
    }
    /**
     * Constructor
     * @param targets
     */
    public StringTracker(List targets) {
        this.targets = (String[]) targets.toArray(new String[]{});
        buffer = new char[maxLength(this.targets)];
    }

	private static int maxLength(String[] strings) {
		int maxLength = 0;
		for (int i = 0; i < strings.length; i++) {
			if (strings[i] != null 
					&& strings[i].length()>maxLength)
				maxLength = strings[i].length();
		}
		return maxLength;
	}

    /**
     * Constructor
     * @param strTarget
     */
    public StringTracker(String strTarget) {
        this(new String[]{strTarget});
    }

    /**
     * 
     * @param chr
     * @return true if match with any given strings.
     */
    public boolean track(char chr) {
        // Append vao buffer
        // Check equals ? return true: false
        if (rollIndex == buffer.length)
            rollIndex = 0;
        buffer[rollIndex++] = chr;
        
        // Check against all targets
        for (meetAt = 0; meetAt < targets.length; meetAt++) {
        	if (checkAgainst(targets[meetAt]))
        		return true;
		}
        
        meetAt = -1;
        return false;
    }

    /**
     * Check the current buffer if match this String.
     * @param target
     * @return true if match
     */
	public boolean checkAgainst(String target) {
		if (target == null) {
			return false;
		}
		
		int length = target.length();
		for (int i = 1; i <= length ; i++) {
            if (buffer[(rollIndex - i < 0 ? rollIndex - i + buffer.length : rollIndex - i)] != target.charAt(length - i))
                return false;
        }
        return true;
	}
	
	public char getCharAt(int at) {
		return buffer[(rollIndex - at - 1 < 0 ? rollIndex - at - 1 + buffer.length : rollIndex - at - 1)];
	}

	private static void emptyBuffer(char[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = 0;
		}
	}
	
    /**
     * Make the char tracker ready for new tracking work.
     */
    public void refresh() {
    	emptyBuffer(buffer);
    }
    
    /**
     * Return the currently matched string.
     * @return
     */
    public String getMatchString() {
    	if (meetAt==-1)
    		return null;
    	else
    		return targets[meetAt];
    }
    
    
    ////////////////////	UTILITY METHODS		//////////////////////
    
    public int indexOf(String content) {
    	int length = content.length();
		for (int i = 0; i < length; i++) {
			if (this.track(content.charAt(i)))
				return i;
		}
		return -1;
    }
    
    public int indexNonHappensOf(String content) {
    	int length = content.length();
		for (int i = 0; i < length; i++) {
			if (!this.track(content.charAt(i)))
				return i;
		}
		return -1;
    }
    
    public int lastIndexOf(String content) {
    	int length = content.length();
		for (int i = length - 1; i > -1; i--) {
			if (this.track(content.charAt(i)))
				return i;
		}
		return -1;
    }
    
    public int lastIndexNonHappensOf(String content) {
    	int length = content.length();
		for (int i = length - 1; i > -1; i--) {
			if (!this.track(content.charAt(i)))
				return i;
		}
		return -1;
    }
    
    /**
     * 
     * @param content
     * @param pos
     * @return
     */
    public int lastIndexNonHappensOf(String content, int pos) {
		for (int i = pos; i > -1; i--) {
			if (!this.track(content.charAt(i)))
				return i;
		}
		return -1;
    }
    public static void main(String[] args) {
		String searchedString = "2008";
		StringTracker st = new StringTracker(searchedString);
		
		// Loop read (i)
		{
			int i = 213; // Reading 213th char
			byte b = 1;// read from file
			if (st.track((char) b))
				System.out.println("Found at " + (i - searchedString.length() + 1));
		}
	}
//    public static void main(String[] args) {
//    	StringTracker s = new StringTracker(new String[] {"//", "/*", "@"});
//    	System.out.println(s.track('/'));
//    	System.out.println(s.getMatchString());
//    	System.out.println(s.track('*'));
//    	
//    	System.out.println(s.getMatchString());
//    }
}
