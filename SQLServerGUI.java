/*
 Author:      Mike O'Malley
 Source File: SQLServerGUI.java
 Description: Basic GUI to demonstrate database connectivity.

REF:
* "SQLServer - with Connect, Query, and basic GUI app - Mike O's notes for students.txt"

Ammendment History
Ver   Date        Author    Details
----- ----------- --------  ----------------------------------------------------
0.1   03-Jan-2019 Mike O    Created.


*/

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class SQLServerGUI extends JFrame
{
   JTextArea textArea      = new JTextArea ();
   JButton   query1Button  = new JButton ("Query 1");
   JButton   query2Button  = new JButton ("Query 2");

   public SQLServerGUI ()
   {
      //this.setLayout (new BorderLayout ());
      JPanel buttonPanel = new JPanel (new FlowLayout (FlowLayout.CENTER));

      buttonPanel.add (query1Button);
      buttonPanel.add (query2Button);

      add (textArea,     BorderLayout.CENTER);
      add (buttonPanel,  BorderLayout.SOUTH);

      query1Button.addActionListener (event -> query1() );
      query2Button.addActionListener (event -> query2() );

      SQLServerBasics01.loadJARFileLibrary ("sqljdbc42.jar");
   }


   private void query1()
   {
      // Get a single value from the database and display in textarea.
      String resultStr = SQLServerBasics01.getStringValueFromSQLDatabase ("SELECT COUNT(*) FROM CPatient");
      System.out.println ("-> Number of Patients: " + resultStr);

      textArea.setText ("-> Number of Patients: " + resultStr);
   }

   private void query2()
   {
      // Display all patients from the database and display in textarea.

      textArea.setText ("");

      System.out.println ("\n\nReading Patient data ...");
      Object[][] arr2D = SQLServerBasics01.getObjectArray2DFromSQLDatabase ("SELECT rtrim(CPatientID) AS [ID], rtrim(FirstName) + ' ' + rtrim(LastName) AS [PatientName], rtrim(Suburb) AS [Suburb] FROM CPatient");

      System.out.println ();
      for (int r = 0; r < arr2D.length; r++)
      {
         for (int c = 0; c < arr2D[r].length; c++)
         {
            System.out.print (("" + arr2D [r][c]).trim() + "\t");
            textArea.append  (("" + arr2D [r][c]).trim() + "\t");
         }

         System.out.println ();
         textArea.append ("\n");
      }

   }

   public static void main(String[] args)
   {
      SQLServerGUI app = new SQLServerGUI ();

      app.setSize (600, 400);
      app.setVisible (true);
   }
}