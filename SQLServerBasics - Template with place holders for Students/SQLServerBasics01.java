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
{
   //private static final String DATABASE_URL       = "jdbc:sqlserver://MooseServer.azure.com;databaseName=DB_Patients";
   private static final String DATABASE_URL       = "jdbc:sqlserver://localhost;databaseName=DB_Patients";
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

      // TODO
      // TODO
      // TODO

      return dataStr; // A single piece of data.
   }


   // Get the number of rows in the results set -
   // this excludes column headings.
   // i.e. it is rows of data.
   private static int getNumberOfRowsInResultSet (ResultSet resultSet)
   {
      int numberOfRows = 0;

      // TODO
      // TODO
      // TODO

      return numberOfRows; // Rows of data - excludes column headings !
   }


   // Returns a 2D Array of data with it's data type intact: int, double, String, Date, etc.
   // Row 0 is the column headings.
   public static Object[][] getObjectArray2DFromSQLDatabase (String selectQueryStr)
   {
      Object[][] objectArray2D = null;
      int numberOfRows = 0;

      // TODO
      // TODO
      // TODO

      return objectArray2D;
   }


   // MAIN
   public static void main (String[] args)
   {
      loadJARFileLibrary ("sqljdbc42.jar");

      // Get a single value from the database:
      System.out.println ("\n\nCounting Patients in 'CPatient' table ...");
      String resultStr = getStringValueFromSQLDatabase ("SELECT COUNT(*) FROM CPatient");
      System.out.println ("-> Number of Patients: " + resultStr);


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

      //int[][] ints = { {1, 2, 3}, {4, 5}  }
   }
}