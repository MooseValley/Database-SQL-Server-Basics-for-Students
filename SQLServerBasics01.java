/*
Command line app that does a simple query.

Requires: Java 8 (v1.8) or prior (v1.7, v1.6, ....)
          Will NOT work with Java 9 (v1.9) or later.
          I need to fix the loadJARFileLibrary () method to work with Java 9, 10, ...

REF:
* "SQLServer - with Connect, Query, and basic GUI app - Mike O's notes for students.txt"

*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.net.URL;
import java.net.URLClassLoader;
import java.lang.reflect.Method;
import java.io.File;
import javax.swing.JOptionPane;     // For Swing Dialogs


public class SQLServerBasics01
{                                                   //"jdbc:sqlserver://SQL42.mydomain.private;databaseName=CC_Extras;"
   //private static final String DATABASE_URL       = "jdbc:sqlserver://MooseServer.azure.com;databaseName=DB_Patients";
   //private static final String DATABASE_URL       = "jdbc:sqlserver://localhost;databaseName=DB_Patients";
   private static final String DATABASE_URL       = "jdbc:sqlserver://CCQL043;databaseName=CC_Extras";

   private static final String DATABASE_USERNAME  = "???";
   private static final String DATABASE_PASSWORD  = "???";

   public static boolean loadJARFileLibrary (String jarName)
   {
      // Load sqljdbc42.jar file - this does what Netbeans'
      // "Add Library" command does.
      // Now I don't have to use EFFING Netbeans == WoooHoo !!!!!
      //
      // How should I load Jars dynamically at runtime?
      // REF: https://stackoverflow.com/questions/60764/how-should-i-load-jars-dynamically-at-runtime
      /*
      Java 9 ->
      This does NOT work in Java 9 onwards.
      Possible solutions:
      * How should I load Jars dynamically at runtime?
        https://stackoverflow.com/questions/60764/how-should-i-load-jars-dynamically-at-runtime#60775
      * Java 9 - add jar dynamically at runtime
        https://stackoverflow.com/questions/48322201/java-9-add-jar-dynamically-at-runtime
      */

      boolean result = false;
      File    file   = null;
      URL     url    = null;
      String  urlStr = "";

      try
      {
          file = new File (jarName);
          url   = file.toURI().toURL();

          if (file.exists() == false)
          {
             throw new Exception ();
          }

          URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
          Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
          method.setAccessible(true);
          method.invoke(classLoader, url);

          //classLoader.close();

          result = true; // All OK.
      }
      catch (Exception err) // NoSuchMethodException | SecurityException | IllegalArgumentException | InvocationTargetException | MalformedURLException | IllegalAccessException
      {
         err.printStackTrace();

         urlStr = "" + url;

         JOptionPane.showMessageDialog
               (null, "Error: could not load: '" + jarName + "'." + "\n" +
               "Please ensure the required file is in the required location:" + "\n\n" +
               file + "\n\n" +
               urlStr.replaceAll ("%20", " ") + "\n\n" +
               "And make sure you are using Java 8 (v1.8) or earlier.",
               "ERROR:",  JOptionPane.ERROR_MESSAGE);
         System.exit (-1); // Exit / close the program.
      }
      return result;
   }


   // Run a query that returns a single value from the database.
   // Read this value in as a String.
   public static String getStringValueFromSQLDatabase (String selectQueryStr)
   {
      int    numRows = 0;
      String dataStr = "";

      try
      {
         // Connect to database:
         Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

         // Execute query:
         Statement statement   = connection.createStatement();
         ResultSet resultSet   = statement.executeQuery(selectQueryStr);

         // Get ResultSet's meta data:
         ResultSetMetaData metaData = resultSet.getMetaData();
         int numberOfColumns        = metaData.getColumnCount();

         // Get the query results:
         while (resultSet.next())
         {
            numRows++;

            dataStr = String.format ("%s", resultSet.getObject(1));
         }

         // We were only expecting a single piece of data,
         // so make sure that is what we got:
         if ((numberOfColumns != 1) || (numRows != 1))
         {
            dataStr = "Error: expecting 1 piece of data: result contained " +
                      numberOfColumns + " columns and " + numRows + " rows.";
         }

         // Disconnect from database.
         statement.close();
         connection.close();
      }
      catch (Exception err) //(SQLException sqlException)
      {
         //err.printStackTrace();
         dataStr = "ERROR: " + err.toString();
      }

      return dataStr; // A single piece of data.
   }


   // Get the number of rows in the results set -
   // this excludes column headings.
   // i.e. it is rows of data.
   private static int getNumberOfRowsInResultSet (ResultSet resultSet)
   {
      int numberOfRows = 0;

      try
      {
         resultSet.last();                  // Move to the last row.
         numberOfRows = resultSet.getRow(); // Get the row number of last row.
         resultSet.beforeFirst();           // Move back to start of data.
      }
      catch(Exception ex)
      {
         numberOfRows = 0;
      }

      return numberOfRows; // Rows of data - excludes column headings !
   }


   // Returns a 2D Array of data with it's data type intact: int, double, String, Date, etc.
   // Row 0 is the column headings.
   public static Object[][] getObjectArray2DFromSQLDatabase (String selectQueryStr)
   {
      Object[][] objectArray2D = null;
      int numberOfRows = 0;

      try
      {
         // Connect to database:
         Connection connection = DriverManager.getConnection(DATABASE_URL, DATABASE_USERNAME, DATABASE_PASSWORD);

         // Execute query:
         Statement statement   = connection.createStatement (ResultSet.TYPE_SCROLL_INSENSITIVE,
                                                             ResultSet.CONCUR_READ_ONLY);
         ResultSet resultSet   = statement.executeQuery(selectQueryStr);

         // Get the number of rows of data (excluding column headings).
         numberOfRows = getNumberOfRowsInResultSet (resultSet);

         numberOfRows++; // Make room for column headings.

         if (numberOfRows == 0)
            return null; // ERROR - no rows - not even headings !!!

         // Get ResultSet's meta data:
         ResultSetMetaData metaData = resultSet.getMetaData();
         int numberOfColumns        = metaData.getColumnCount();

         if (numberOfColumns == 0)
            return null; // ERROR - no columns !!!

         // Declare our array to be big enough to hold all of the
         // data and the column headings.
         objectArray2D = new Object [numberOfRows][numberOfColumns];

         // Get the column headings into Row 0 of the array.
         int row = 0;
         for (int i = 1; i <= numberOfColumns; i++)
         {
            // Column headings are Strings !
            objectArray2D [row][i-1] = new String (String.format ("%s", metaData.getColumnName(i)).trim());
         }

         // Get the query results into the array.
         row++; // Headings are in Row 0, load data from Row 1 onwards.
         while (resultSet.next())
         {
            for (int i = 1; i <= numberOfColumns; i++)
            {
               objectArray2D [row][i-1] = resultSet.getObject(i);
            }
            row++;
         }

         // Disconnect from database.
         statement.close();
         connection.close();
      }
      catch (Exception err) //(SQLException sqlException)
      {
         //err.printStackTrace();
         System.out.println ("ERROR: " + err.toString());
      }
      //}

      // -1 to remove headings.
      if (numberOfRows > 0)
         numberOfRows--;

      System.out.println (" -> " + numberOfRows + " records read from database.");

      return objectArray2D;
   }


   // MAIN
   public static void main (String[] args)
   {
      loadJARFileLibrary ("sqljdbc42.jar");

      /*
      // Get a single value from the database:
      System.out.println ("\n\nCounting Patients in 'CPatient' table ...");
      String resultStr = getStringValueFromSQLDatabase ("SELECT COUNT(*) FROM CPatient");
      System.out.println ("-> Number of Patients: " + resultStr);
      */

      // Get a single value from the database:
      System.out.println ("\n\nCounting queries in 'QyeryList' table ...");
      String resultStr = getStringValueFromSQLDatabase ("SELECT COUNT(*) FROM QyeryList");
      System.out.println ("-> Number of queries: " + resultStr);

      /*
      // Get a 2D table of data from the database:
      System.out.println ("\n\nReading Patient data ...");
      Object[][] arr2D = getObjectArray2DFromSQLDatabase ("SELECT rtrim(CPatientID) AS [ID], rtrim(FirstName) + ' ' + rtrim(LastName) AS [PatientName], rtrim(Suburb) AS [Suburb] FROM CPatient");

      if (arr2D != null)
      {
         System.out.println ();
         for (int r = 0; r < arr2D.length; r++)
         {
            for (int c = 0; c < arr2D[r].length; c++)
            {
               System.out.print (("" + arr2D [r][c]).trim() + "\t");
            }
            System.out.println ();
         }
      }
      */


      //int[][] ints = { {1, 2, 3}, {4, 5}  }
   }
}