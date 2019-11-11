package parsing;

/**
 * 声明的元素类，用于记录程序中声明的不同种类的变量/常量/函数
 * 用于语法分析
 *
 * @author ZY
 */
public class Declaration {
    private String name = "";
    private String kind = "";
    private String val = "";
    private String level = "";
    private String adr = "";
    private int start = -1;
    private int end = -1;
    private int codeStartIndex = -1;

    public Declaration(String name, String kind, String val, String level, String adr, int start, int end) {
        this.name = name;
        this.kind = kind;
        this.val = val;
        this.level = level;
        this.adr = adr;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getAdr() {
        return adr;
    }

    public void setAdr(String adr) {
        this.adr = adr;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getCodeStartIndex() {
        return codeStartIndex;
    }

    public void setCodeStartIndex(int codeStartIndex) {
        this.codeStartIndex = codeStartIndex;
    }

    @Override
    public String toString() {
        switch (kind) {
            case "CONSTANT":
                return "Declaration{" +
                        "name='" + name + '\'' +
                        ", kind='" + kind + '\'' +
                        ", val='" + val + '\'' +
                        '}';
            case "VARIABLE":
                return "Declaration{" +
                        "name='" + name + '\'' +
                        ", kind='" + kind + '\'' +
                        ", val='" + val + '\'' +
                        ", level='" + level + '\'' +
                        ", adr='" + adr + '\'' +
                        '}';
            case "PROCEDURE":
                return "Declaration{" +
                        "name='" + name + '\'' +
                        ", kind='" + kind + '\'' +
                        ", val='" + val + '\'' +
                        ", level='" + level + '\'' +
                        ", start=" + start +
                        ", end=" + end +
                        '}';
            default:
                return "Declaration{" +
                        "name='" + name + '\'' +
                        ", kind='" + kind + '\'' +
                        ", val='" + val + '\'' +
                        ", level='" + level + '\'' +
                        ", adr='" + adr + '\'' +
                        ", start=" + start +
                        ", end=" + end +
                        '}';
        }
    }
}
