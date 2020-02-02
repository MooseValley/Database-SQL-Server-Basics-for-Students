/*

REF:
* "SQLServer - with Connect, Query, and basic GUI app - Mike O's notes for students.txt"

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
   }

   private void query2()
   {
      // Display all patients from the database and display in textarea.
   }

   public static void main(String[] args)
   {
      SQLServerGUI app = new SQLServerGUI ();

      app.setSize (600, 400);
      app.setVisible (true);
   }
}