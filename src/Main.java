import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardOpenOption.CREATE;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Main
{
    private static final ArrayList<String> myArrList = new ArrayList<>();

    private static final Scanner in = new Scanner(System.in);

    private static boolean needsToBeSaved = false;

    private static Path filePath = null;

    public static void main(String[]args) throws FileNotFoundException
    {
        String option;

        do{
            display();
            option = SafeInput.getRegExString(in,"Choose an option(A,D,O,S,C,V,Q, or a,d,o,s,c,v,q)", "[ADOSCVQadoscvq]");
            if(option.equalsIgnoreCase("A"))
                addItem();
            else if(option.equalsIgnoreCase("D"))
                deleteItem();
            if (option.equalsIgnoreCase("O"))
                filePath = openFile();
            else if(option.equalsIgnoreCase("S"))
                saveFile();
            else if(option.equalsIgnoreCase("C"))
                clearList();
            else if(option.equalsIgnoreCase("V"))
                viewList();
            else if(option.equalsIgnoreCase("Q"))
            {
                if(needsToBeSaved)
                {
                    boolean saveYN = SafeInput.getYNConfirm(in, "Changes are pending. Do you want to save the changes?");
                    if(saveYN)
                        saveFile();
                }
                break;
            }

        }while(true);
    }

    private static void display()
    {
        System.out.println("A - Add an item to the list");
        System.out.println("D - Delete an item to the list");
        System.out.println("O - Open a list file from disk");
        System.out.println("S - Save the current list file to disk");
        System.out.println("C - Clear elements from current list");
        System.out.println("V - View the list");
        System.out.println("Q - Quit the program");
    }

    private static Path openFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setAcceptAllFileFilterUsed( false );
        chooser.setFileFilter( new FileNameExtensionFilter( "Text Files", "txt" ) );
        File selectedFile;
        String lineStr;

        if (needsToBeSaved) {
            boolean saveYN = SafeInput.getYNConfirm( in, filePath + "is already open. Do you want to save the file again?" );
            if (saveYN)
                saveFile();
            needsToBeSaved = false;
        }

        try {
            File dir = new File( System.getProperty( "user.dir" ) );
            chooser.setCurrentDirectory( dir );
            if (chooser.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION) {
                selectedFile = chooser.getSelectedFile();
                filePath = selectedFile.toPath();
                InputStream in = new BufferedInputStream( Files.newInputStream( filePath, CREATE ) );
                BufferedReader reader = new BufferedReader( new InputStreamReader( in ) );

                int totalLines = 0;
                while (reader.ready()) {
                    lineStr = reader.readLine();
                    myArrList.add( lineStr );
                    needsToBeSaved = true;
                    totalLines++;
                }

                System.out.println( "File Selected: " + selectedFile.getName() );
                System.out.println( "File Path: " + filePath );
                System.out.println( "Total lines in file: " + totalLines );
                reader.close();
            }
        }catch(FileNotFoundException e){
            System.out.println( "File not found!!!" );
            e.printStackTrace();
        }catch(IOException e)
        {
            e.printStackTrace();
        }

        return filePath;
    }

    private static void saveFile ()
    {
        if (filePath == null)
            System.out.println( "Cannot save as there was no file opened before" );
        else if (needsToBeSaved) {
            try {
                List<String> lines = myArrList;
                Files.write( filePath, lines );
                needsToBeSaved = false;
            } catch (IOException e) {
                System.out.println( e );
            }
        }
    }

    private static void addItem ()
    {
        String item;
        boolean done = false;
        item = SafeInput.getNonZeroLenString(in, "Add item to the list: ");
        for (int row = 0; row < myArrList.size(); row++)
        {
            if (myArrList.get(row).equals("")) {
                myArrList.set(row, item);
                done = true;
                break;
            }
        }
        if (!done)
            myArrList.add(item);
        needsToBeSaved = true;
    }

    private static void deleteItem ()
    {
        int row;
        viewList();
        row = SafeInput.getRangedInt( in, "Delete item: ", 1, myArrList.size() ) - 1;
        myArrList.set( row, "" );
        viewList();
        needsToBeSaved = true;
    }

    private static void clearList ()
    {
        myArrList.clear();
        needsToBeSaved = true;
    }

    private static void viewList ()
    {
        for (int row = 0; row < myArrList.size(); row++)
            System.out.println( row + 1 + ":" + myArrList.get(row) );
    }
}
