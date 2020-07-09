
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class FileWriter {

	/** Schreibt eine Datei über den input String an den gewünschten Pfad*/
	public static int writeToFile(String path, ArrayList<Integer> data) {
	    try (PrintWriter writer = new PrintWriter(new File(path+".csv"))) {

	      StringBuffer sb = new StringBuffer();
	      for(int i = 0; i < 256; i++){
	    	  sb.append(String.valueOf(i));
	    	  sb.append(',');
	      }
	      sb.append('\n');
	      for(int i = 0; i < 256; i++){
	    	  sb.append(data.get(i));
	    	  sb.append(',');
	      }
	      writer.write(sb.toString());
	      writer.close();
	      return 0;

	    } catch (FileNotFoundException e) {
	      System.out.println(e.getMessage());
	    }
		return -1;
	}
}
