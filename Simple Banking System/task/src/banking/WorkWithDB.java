package banking;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class WorkWithDB {
    WorkWithDB() {
    }

    String url;


    public void createNewDataBase(String fileName) {
        url = "jdbc:sqlite:" + fileName;

        try (Connection conn = DriverManager.getConnection(url)) {
            if (conn != null) {

                DatabaseMetaData meta = conn.getMetaData();

                //System.out.println("The Driver name is " + meta.getDriverName());
               // System.out.println("A new database has been created");

            }
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS card(" +
                        "ID INTEGER PRIMARY KEY," +
                        "number TEXT NOT NULL," +
                        "pin TEXT NOT NULL," +
                        "balance INTEGER DEFAULT 0)");
            } catch (SQLException e) {
                e.printStackTrace();
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
   /* private Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);

        } catch (SQLException e) {
            System.out.println(e.getMessage());

        }
        return conn;
    }*/

    public void insertData(String number, String pin, Integer balance) {
        // String sql2 = "INSERT INTO CARD ( number , pin, balance) VALUES (?,?,?)";
        String sql = "INSERT INTO CARD ( number, pin, balance) VALUES (?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            //pstmt.setInt(1,id);
            pstmt.setString(1, number);
            pstmt.setString(2, pin);
            pstmt.setInt(3, balance);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String pinCheck(String enteredNumber, String enteredPin) {
        String sql =    "SELECT" +
                " CASE" +
                " WHEN pin = ? THEN 1" +
                " ELSE 0 END " +
                " FROM card" +
                " WHERE number == ? ";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(2,enteredNumber);
            pstmt.setString(1,enteredPin);
            ResultSet rs =pstmt.executeQuery();

            while (rs.next()) {
                return rs.getString(1);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return "0";
    }
    public void addIncome(int amountOfMoney, String cardNumber) {
        String sql = "UPDATE CARD SET balance = balance + ? " +
                " WHERE number = ? ";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, amountOfMoney);
            pstmt.setString(2, cardNumber);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public int showBalance(String cardNumber) {

        String sql = " SELECT balance FROM CARD WHERE number = ? ";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            ResultSet rs = pstmt.executeQuery();
            int balance = rs.getInt(1);
            return balance;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return 0;
    }
    public void moneyTransfer(String cardNumber,String cardHolder,int moneyToTransfer) {
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl(url);

        String sqlTakeMoney = "UPDATE CARD SET balance = balance - ? WHERE number LIKE ?";
        String sqlReceive =   "UPDATE CARD SET balance = balance + ? WHERE number LIKE ?";
        try (Connection con = dataSource.getConnection()) {

            //Disable auto commit mode
           // con.setAutoCommit(false);
            try (PreparedStatement psTake = con.prepareStatement(sqlTakeMoney);
                 PreparedStatement psReceive = con.prepareStatement(sqlReceive)) {

                psTake.setInt(1,moneyToTransfer);
                psTake.setString(2,cardHolder);
                psTake.executeUpdate();
                psReceive.setInt(1,moneyToTransfer);
                psReceive.setString(2,cardNumber);
                psReceive.executeUpdate();

             //   con.commit();



            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }



    }
    public void deleteAccount (String cardNumber) {
        String sql = "DELETE FROM CARD WHERE number = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public String baseChecking (String cardNumber) {
        String sql = " SELECT CASE WHEN number = ? THEN TRUE ELSE FALSE END FROM card ";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cardNumber);


            ResultSet rs = pstmt.executeQuery();
            String somecrap;

            while (rs.next()) {
                somecrap = rs.getString(1);

                if (somecrap.equals("1")){
                    return "1";}
            }

        } catch (SQLException e) {

        }
        return "0";
    }
    public boolean baseChecking2 (String cardNumber) {
        String sql = "SELECT CASE WHEN number LIKE ? THEN 1 ELSE " +
                     " 0 END FROM CARD ";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,cardNumber);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String somecrap = rs.getString(1);
                System.out.println(somecrap);
                if (somecrap.equals("1")) {
                    return true;
                }

            }

        } catch (SQLException e) {
            //System.out.println(e.getMessage());
            //return false;
        }
        return false;
    }
}