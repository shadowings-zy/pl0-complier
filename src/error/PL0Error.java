package error;

public class PL0Error {
    public static void log(int errorType) {
        String[] error = new String[] {
                "[0 undefined error]",
                "[1 constant definition error] undefined const ident",
                "[2 constant definition error] const syntax error",
                "[3 constant definition error] undefined var ident",
                "[4 constant definition error] var syntax error",
                "[5 token error] unknown token",
                "[6 procedure error] procedure layer more than 3",
                "[7 procedure error] undefined procedure ident",
                "[8 ident error] ident syntax error",
                "[9 ident error] undefined ident",
                "[10 ident parse error] assign ident error",
                "[11 if parse error] if syntax error",
                "[12 boolean parse error] boolean expression syntax error",
                "[13 while parse error] while syntax error",
                "[14 begin end parse error] missing . or ;",
                "[15 call parse error] missing call ident",
                "[16 call parse error] missing ;",
        };
        if (errorType < error.length) {
            System.out.println(error[errorType]);
        } else {
            System.out.println(error[0]);
        }
    }
}
