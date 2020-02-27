/****************************************************************/
/* Individual Project Application Main class (Section 5.6)               */
/* Needs grade1.java and grade2.java to be compiled             */
/* Chapter 5; Oracle Programming -- A Primer                    */
/*            by R. Sunderraman                                 */
/****************************************************************/

import java.io.*; 
import java.sql.*;

class Shows { 

  public static void main (String args []) 
      throws SQLException, IOException { 

    Shows show = new Shows();
    boolean done;
    char ch,ch1;
    byte num = 0;

    try {
      Class.forName ("oracle.jdbc.driver.OracleDriver");
    } catch (ClassNotFoundException e) {
        System.out.println ("Could not load the driver");
        return;
      }
    String user, pass;
    user = "jmulliga";
    pass = "jm805369";

    //  The following line was modified by Prof. Marling to work on prime

    Connection conn = DriverManager.getConnection
       ("jdbc:oracle:thin:@deuce.cs.ohio.edu:1521:class", user, pass);

    done = false;
    do {
      show.print_menu();
      System.out.print("Type in your option:");
      System.out.flush();
      ch = (char) System.in.read();
      ch1 = (char) System.in.read();
      switch (ch) {
        case '1' : show.display_table(conn);
                   break;
        case '2' : show.add_Show(conn);
                   break;
        case '3' : show.mod_shows(conn);
                   break;
        case '4' : show.delete_show(conn);
                   break;
        case '5' : show.report_1(conn);
                   break;
        case '6' : show.report_2(conn);
                   break;           
        case 'q' : done = true;
                   break;
        default  : System.out.println("Type in option again");
      }
    } while (!done);

    conn.close();
  }// end of main function
  
    void display_table(Connection conn)
        throws SQLException, IOException {
          Statement stmt = conn.createStatement();
          String query = "select * from Shows";
          try {
             int nrows = stmt.executeUpdate(query);
          } catch (SQLException e) {
            System.out.println("Error Displaying Table");
            while (e != null) {
              System.out.println("Message : " + e.getMessage());
              e = e.getNextException();
            }
          }
          ResultSet rset = stmt.executeQuery(query);
          System.out.println("ID - Num_E - S_name - genre - duration");
          while (rset.next()) {
            System.out.println(rset.getInt(1) + "   " + rset.getInt(2) + "   " + rset.getString(3) + "   " + rset.getString(4) + "   " + rset.getInt(5));
          }

    }
      

    void add_Show(Connection conn) 
        throws SQLException, IOException {
        Statement stmt = conn.createStatement(); 

    String sid   = readEntry("Show's ID: ");
    String num_e = readEntry("Number of Episodes: ");
    String s_name = readEntry("Show's Name: ");
    String genre = readEntry("Genre: ");
    String dur = readEntry("duration: ");
    String query = "insert into Shows values (" +
            "'" + sid + "','" + num_e + "','" + s_name + "','" + genre + "','" + dur + "')";
    try {
      int nrows = stmt.executeUpdate(query);
    } catch (SQLException e) {
        System.out.println("Error Adding Show Entry");
        while (e != null) {
          System.out.println("Message     : " + e.getMessage());
          e = e.getNextException();
        }
        return;
      }
    stmt.close();
    System.out.println("Added A New Show Entry");
    }

   void mod_shows(Connection conn)
    throws SQLException, IOException {
      String id = readEntry("Show ID :");
      String query1 = "select Num_E, S_name, genre, duration from Shows where ID = " + id;
      
      Statement stmt = conn.createStatement (); 
      ResultSet rset;
      try{
        rset = stmt.executeQuery(query1);
      } catch (SQLException e) {
        System.out.println("Error");
        while(e != null) {
          System.out.println("message	: " + e.getMessage());
          e = e.getNextException();
        }
        return;
      }
      System.out.println("");
      if ( rset.next ()  ) {
            System.out.println("Old Show ID = " + rset.getInt(1));
            String nid = readEntry("Enter New ID: ");
            String nep = readEntry("Enter Number of episodes: ");
            String nname = readEntry("Enter Show name: ");
            String ngenre = readEntry("Enter Genre: ");
            String ndur = readEntry("Enter duration: ");
            String query2 = "update Shows set ID = '" + nid + "'," + "Num_E = '" + nep + "'," + "S_name = '" + nname + "'," + "genre = '" + ngenre + "'," + "duration = '" + ndur + "'" + " where id = " + id; 
            /*String query3 = "update Shows set Num_E = " + nep + " where id = " + id;
            String query4 = "update Shows set S_name = " + nname + " where id = " + id;
            String query5 = "update Shows set genre = " + ngenre + " where id = " + id;
            String query6 = "update Shows set duration = " + ndur + " where id = " + id;*/
            try {
              stmt.executeUpdate(query2);
              /*stmt.executeUpdate(query3);
              stmt.executeUpdate(query4);
              stmt.executeUpdate(query5);
              stmt.executeUpdate(query6);*/
            } catch (SQLException e) {
                System.out.println("Could not modify Show");
                while (e != null) {
                  System.out.println("Message     : " + e.getMessage());
                  e = e.getNextException();
                }
                return;
            }
            System.out.println("Modified Show successfully");
          }
          else
            System.out.println("Show not found");
          stmt.close();

    }

   void delete_show(Connection conn)
    throws SQLException, IOException {
      String id = readEntry("Show ID to drop : ");
      String query2 = "delete Shows where ID = "	+ id;
      
      conn.setAutoCommit(false);
      Statement stmt = conn.createStatement();
      int nrows;
      try {
        //nrows = stmt.executeUpdate(query1);
        nrows = stmt.executeUpdate(query2);
      } catch (SQLException e) {
        System.out.println("Could not drop Show");
        while (e != null) {
          System.out.println("Message     : " + e.getMessage());
            e = e.getNextException();
        }
        conn.rollback();
        return;
      }
      System.out.println("Dropped Show");
      conn.commit();
      conn.setAutoCommit(true);
      stmt.close();

    }
    //what are the highest rated shows and who watched them?
    void report_1(Connection conn) 
      throws SQLException, IOException {
        System.out.println("\n");
      String query = "select S_name, FName from((Watch join Shows on Watch.ID = Shows.ID) join Users on Watch.user_N = Users.user_N) where Shows.ID = Watch.ID and rate in (select max(rate) from Watch)";
      
      Statement stmt = conn.createStatement (); 
      ResultSet rset;
      try{
        rset = stmt.executeQuery(query);
      } catch (SQLException e) {
        System.out.println("Error");
        while(e != null) {
          System.out.println("message	: " + e.getMessage());
          e = e.getNextException();
        }
        return;
      }
      System.out.println("Show's Name --- User's first names");
      while(rset.next()){
        System.out.println(rset.getString(1) + "      " + rset.getString(2));
      }
      System.out.println("\n");
      stmt.close();

    }
    //for any given user show their plan to watch list
    void report_2(Connection conn) 
      throws SQLException, IOException {
      String user = readEntry("Enter username: ");
      String query = "select S_name from ((Plan join Shows on Plan.ID = Shows.ID) join Users on Plan.user_N = Users.user_N) where Shows.ID = Plan.ID and Plan.user_N = '" + user + "'";
        System.out.println("\n");
      Statement stmt = conn.createStatement (); 
      ResultSet rset;
      try{
        rset = stmt.executeQuery(query);
      } catch (SQLException e) {
        System.out.println("Error");
        while(e != null) {
          System.out.println("message	: " + e.getMessage());
          e = e.getNextException();
        }
        return;
      }
      System.out.println("Shows on user's plan to watch list");
      while(rset.next()){
        System.out.println(rset.getString(1));
      }
      System.out.println("\n");
      stmt.close();

    }

    void print_menu() {
      System.out.println("      SHOWS PROGRAM\n");
      System.out.println("(1) Display Shows table");
      System.out.println("(2) Add a show");
      System.out.println("(3) Modify a show");
      System.out.println("(4) Delete a show");
      System.out.println("(5) Display Report 1");
      System.out.println("(6) Display Report 2");
      System.out.println("(q) Quit\n");
  }
  

  //readEntry function -- to read input string
  static String readEntry(String prompt) {
     try {
       StringBuffer buffer = new StringBuffer();
       System.out.print(prompt);
       System.out.flush();
       int c = System.in.read();
       while(c != '\n' && c != -1) {
         buffer.append((char)c);
         c = System.in.read();
       }
       return buffer.toString().trim();
     } catch (IOException e) {
       return "";
       }
   }
} 