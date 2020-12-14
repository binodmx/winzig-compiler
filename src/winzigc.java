public class winzigc {
    public static void main(String[] args) {
        String flag = args[0];
        switch (flag) {
            case "-ast":
                System.out.println("called -ast");
                break;
            case "-help":
                System.out.println("called -help");
                break;
            default:
                System.out.println("Invalid command. Run with flag -help for help");
        }
    }
}
