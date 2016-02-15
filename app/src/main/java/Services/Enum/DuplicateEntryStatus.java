package Services.Enum;

/**
 * Created by akhil on 14-02-2016.
 */
public enum DuplicateEntryStatus {

    NotVerified(0),
    True(1),
    False(2),
    Error(3);

    private final int id;
    DuplicateEntryStatus(int id)
    {
        this.id = id;
    }
    public int getValue() { return id; }

    public static DuplicateEntryStatus getEnum(int id)
    {
        switch(id) {
            case 0:
                return NotVerified;
            case 1:
                return True;
            case 2:
                return False;
            case 3:
                return Error;
            default:
                return  Error;
        }
    }

}

