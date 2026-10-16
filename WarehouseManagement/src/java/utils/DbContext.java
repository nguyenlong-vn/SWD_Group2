package utils;
import java.sql.Connection;
import java.sql.DriverManager;

public class DbContext {
    
    protected Connection connection;
    public DbContext() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            
            String user = "admin";
            String pass = "123456";
            String url = "jdbc:sqlserver://localhost:1433;"
           + "databaseName=WarehouseManagement;"
           + "encrypt=true;"
           + "trustServerCertificate=true";

            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException ex) {
            System.err.println("SQL Server JDBC Driver not found. Please add mssql-jdbc.jar to your classpath.");
            ex.printStackTrace();
            connection = null;
        } catch (Exception ex) {
            System.err.println("Connection Error: " + ex.getMessage());
            System.err.println("Please check:");
            System.err.println("1. SQL Server is running");
            System.err.println("2. Database 'ITServiceFlow' exists");
            System.err.println("3. Username and password are correct");
            System.err.println("4. SQL Server is listening on port 1433");
            ex.printStackTrace();
            connection = null;
        }
    }

    public boolean isConnected() {
        return connection != null;
    }
    
    public static void main(String[] args) {
        DbContext dbContext = new DbContext();
        try{
            if (dbContext.isConnected()) {
            System.out.println("Successfully connected");
        } else {
            System.out.println("Failed to connected");
        }
        }
        catch(Exception ex)
        {
            System.out.println(ex);
        }
    }
}
