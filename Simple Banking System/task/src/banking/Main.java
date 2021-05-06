package banking;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class Main {


    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String AccountName = new String();
        String AccountPass = new String();
        int i = 0;
        CurrentCard curCar = new CurrentCard();

        WorkWithDB Base = new WorkWithDB();
        String fileName = args[1];
        Base.createNewDataBase(fileName);
        // WorkWithDB.createNewDataBase("db.s3db");
        boolean loggingIn = false;
        boolean exit = false;


        while (!exit) {
            if (!loggingIn) {
                startMenu();
            } else if (loggingIn) {
                logMenu();
            }
            String action = scanner.next();
            if (!loggingIn) {
                switch (action) {
                    case "1":
                        System.out.println("Your card has been created");
                        do {
                            AccountName = String.valueOf(nameGen());

                        } while (!algorithmLuhn(AccountName));
                        AccountPass = String.valueOf(passGen());

                        System.out.println("Your card number: \n"
                                + AccountName
                                + "\nYour card PIN: \n"
                                + AccountPass + "\n");
                        i++;

                        Base.insertData(AccountName, AccountPass,0);
                        break;
                    case "2":

                        System.out.println("Enter your card number:");
                        String login = scanner.next();
                        System.out.println("Enter your PIN:");
                        String password = scanner.next();
                        String loggIn = Base.pinCheck(login,password);

                        if (loggIn.equals("1")) {
                            curCar.currentCardNumber = login;
                            System.out.println("You have successfully logged in!");
                            loggingIn = true;
                        } else {
                            System.out.println("Wrong card number or PIN!");}
                        break;
                    case "0":
                        System.out.println("Bye!");
                        exit = true;
                }
            } else {
                switch (action) {
                    case "1":
                        Integer balance = Base.showBalance(curCar.currentCardNumber);
                        System.out.println("Balance: " + balance);
                        break;
                    case "2":
                        System.out.println("Enter income");
                        int Money = scanner.nextInt();
                        Base.addIncome(Money, curCar.currentCardNumber);
                        System.out.println("Income was added!\n");
                        System.out.println(" ");
                        break;
                    case "3":
                        System.out.println("Enter card number:");
                        String moneyReceiver = scanner.next();
                        if (algorithmLuhn(moneyReceiver)) {
                            boolean isItIn = Base.baseChecking2(moneyReceiver);
                            if (!isItIn) {
                                System.out.println("Such a card does not exist.");
                                break;
                            } else {

                                System.out.println("Enter how much money" +
                                        " you want to transfer:");
                                int moneyToTransfer = scanner.nextInt();
                                if (moneyToTransfer > Base.showBalance(curCar.currentCardNumber)) {
                                    System.out.println("Not enough money!");
                                    break;
                                }
                                Base.moneyTransfer(moneyReceiver,
                                        curCar.currentCardNumber, moneyToTransfer);
                                System.out.println(Base.showBalance(curCar.currentCardNumber));
                                System.out.println(Base.showBalance(moneyReceiver));
                                break;
                            }
                        } else {
                            System.out.println("Probably you made a mistake" +
                                    " in the card number. Please try again!");
                        }


                        break;
                    case "4":
                        Base.deleteAccount(curCar.currentCardNumber);
                        System.out.println("The account has been closed!");
                    case "5":
                        loggingIn = false;
                        break;
                    case "0":
                        exit = true;
                }
            }
        }

    }

    static long nameGen() {
        Random random = new Random();
        return 400000_0000000000L + random.nextInt(999999999);
    }

    static int passGen() {
        Random random = new Random();
        return random.nextInt(8999) + 1000;
    }

    public static void startMenu() {
        System.out.println("1. Create an account");
        System.out.println("2. Log into account \n0. Exit");
    }

    public static void logMenu() {
        System.out.println("1. Balance\n2. Add income\n3. Do transfer\n" +
                "4. Close account\n5. Log out\n0. Exit ");
    }

    static boolean algorithmLuhn(String number) {
        boolean numberStatus = false;
        String cardNumber = number;
        int sum2;
        int sum1 = sum2 = 0;
        for (int i = cardNumber.length(); i > 0; i--) {
            int digit = Character.getNumericValue(cardNumber.charAt(i - 1));
            int z = digit;
            int y = digit;
            if (i % 2 != 0) {
                z *= 2;
                if (z > 9) {
                    z -= 9;
                }
                sum1 += z;
            } else sum2 += y;
        }
        int sum = sum1 + sum2;
        if (cardNumber.length() != 16) sum = 1;
        if (sum % 10 == 0) {
            return true;
        } else return false;

    }
}


