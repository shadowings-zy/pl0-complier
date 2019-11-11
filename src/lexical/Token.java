package lexical;

/**
 * 符号类，用于词法分析
 *
 * @author ZY
 */
public class Token {
    private String sym = "";
    private String id = "";
    private String num = "";

    public Token(String sym, String id, String num) {
        super();
        this.sym = sym;
        this.id = id;
        this.num = num;
    }

    public String getSym() {
        return sym;
    }

    public void setSym(String sym) {
        this.sym = sym;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return "Token{" +
                "sym='" + sym + '\'' +
                ", id='" + id + '\'' +
                ", num='" + num + '\'' +
                '}';
    }
}
