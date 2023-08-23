package wyvern.target.corewyvernIL.support;

public class BreakException extends Exception{
    private static long exID;

    public static long GetNextID() { return ++exID; }

    public final long ID;

    public Object retVal;

    public BreakException(long id, Object retVal) {
        this.ID = id;
        this.retVal = retVal;
    }
}
