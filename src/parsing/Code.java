package parsing;

public class Code {
    private String function;
    private String levelDifference;
    private String addressOffset;

    public Code(String function, String levelDifference, String addressOffset) {
        this.function = function;
        this.levelDifference = levelDifference;
        this.addressOffset = addressOffset;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }

    public String getLevelDifference() {
        return levelDifference;
    }

    public void setLevelDifference(String levelDifference) {
        this.levelDifference = levelDifference;
    }

    public String getAddressOffset() {
        return addressOffset;
    }

    public void setAddressOffset(String addressOffset) {
        this.addressOffset = addressOffset;
    }

    @Override
    public String toString() {
        return "\t" + function + "\t\t" + levelDifference + "\t" + addressOffset;
    }
}
