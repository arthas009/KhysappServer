
package com.company;
import java.sql.*;
import java.sql.SQLException;
import java.sql.PreparedStatement;
public class SQLfuncts 
{
    public ResultSet SearchForUser(Connection conn,String username) throws SQLException // SEARCHS USERS
    {
        PreparedStatement stmt;
        ResultSet rs;
        stmt = conn.prepareStatement("SELECT * FROM users WHERE Username = ?");   
        stmt.setString(1, username);
        rs = stmt.executeQuery();  
        return rs;
    }
    public void PrintUserValues(ResultSet rs) throws SQLException //JUST FOR INFORMATION FOR SERVER. PRINTS VALUES OF USER
    {
          /*   System.out.println("Your name: "+rs.getString("Name").trim());
             System.out.println("Your surname: "+rs.getString("Surname").trim());
             System.out.println("Your phone number: "+rs.getString("PhoneNumber").trim());
             System.out.println("Your position at company:"+ rs.getString("Position").trim());*/
    }
    public void InsertNewUser(Connection conn,String name,String surname,
            String phonenumber,String username,String position,
            String password,String datetime) throws SQLException // INSERTS A NEW USER
    {
        
        PreparedStatement stmt;
        stmt = conn.prepareStatement("INSERT into users (Name,Surname,PhoneNumber,Username,Position,Password,RegisterDate)" +" values (?,?,?,?,?,?,?)");   
        stmt.setString(1, name);
        stmt.setString(2, surname);
        stmt.setString(3, phonenumber);
        stmt.setString(4, username);
        stmt.setString(5, position);
        stmt.setString(6, password);
        stmt.setString(7,datetime);
        stmt.execute();   
        
    }
    public String getOnlineUsers(Connection conn,String username) // WHEN USER REGISTERED, OPEN A NEW TABLE TO HIM/HER FOR CHAT INFO
    {
        try{
        String returnValue="";
        PreparedStatement stmt;
        ResultSet rs;
        stmt = conn.prepareStatement("SELECT Username FROM users WHERE NOT Username = ?");  
        stmt.setString(1, username);
        rs = stmt.executeQuery();   
        while(rs.next())
        {
            returnValue += rs.getString("Username").trim()+"///";
        }
      //  System.out.println("now at getonlineusers");
        return returnValue;
    }
        catch(SQLException e)
    {
        //System.out.println("problem at getonlineusers");
        return "";
    }
    }
    public int[] getIdOfTwouser(Connection conn,String[] Str) throws SQLException
    {
        int[] IDs = new int[2];
        PreparedStatement stmt;
        ResultSet rs;
        stmt = conn.prepareStatement("SELECT * FROM users WHERE Username = ?"); 
        stmt.setString(1, Str[0]);
        rs = stmt.executeQuery();
        while(rs.next())
        {
            IDs[0] = rs.getInt("ID");
           // System.out.println(IDs[0]+"");      
        }
        stmt = conn.prepareStatement("SELECT * FROM users WHERE Username = ?"); 
        stmt.setString(1, Str[1]);
        rs = stmt.executeQuery();  
        while(rs.next())
        {
            IDs[1] = rs.getInt("ID");
           // System.out.println(IDs[1]+"");      
        }
        return IDs;
    }
    public void setUserToOnline(Connection conn,String username)throws SQLException
    {
        PreparedStatement stmt;
        stmt = conn.prepareStatement("UPDATE users SET IsLogged='true' WHERE Username = ?"); 
        stmt.setString(1, username);
        stmt.execute();
    }
    public void setUserToOffline(Connection conn,String username)throws SQLException
    {
        PreparedStatement stmt;
        stmt = conn.prepareStatement("UPDATE users SET IsLogged='false' WHERE Username = ?"); 
        stmt.setString(1, username);
        stmt.execute();
    }
    
}

