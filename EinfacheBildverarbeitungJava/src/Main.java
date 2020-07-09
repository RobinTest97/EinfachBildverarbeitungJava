import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import javax.imageio.ImageIO;

public class Main {
	
	private Scanner stdin = new Scanner(System.in);
	private BufferedImage img = null;
	private GrayscaleImage grayImg = null;
	
	/*Aufgabe bezogene Werte*/
	private int pixelCount = 0;
	/** Möglich nur 0-255*/
	private int minGrayValue = -1;
	private int maxGrayValue = -1;
	private int mediumGrayValue = -1;
	private double varianz = -1;
	private double standardDevation = -1;
	private double entropie = 0;
	

	/**
	 * 
	 * @param args
	 * hauptprozedur
	 */
	public static void main(String[] args) {
		Main instance = new Main();
		instance.consoleMenu();
	}
	
	public void consoleMenu() {
		String input="";
		while (!input.equals("2")) {
			System.out.println("------------------------------------------------------------------");
			System.out.println("		HAUTMENU");
			System.out.println("------------------------------------------------------------------");
			System.out.println("	1: Bilddatei einlesen");
			System.out.println("	2: Programm beenden\n");
			System.out.print("Eingabe: ");
		  		  
			input = stdin.next();
			switch (input) {
				case "1": {
					System.out.println();
					readFileOverPath();
					grayImg = new GrayscaleImage(img.getWidth(),img.getHeight(), img);
					saveGrayscaledImage();
					gatherGrayscaleInformations();
					gatherPixelInformation();
					gatherMediumGrayValueInformation();
					gatherVarianzInformation();
					gatherStandardDevationInformation();
					gatherEntropieInformation();
					printOutput();
					break;
				}
				case "2":{
					stdin.close();
					System.out.println("-------------------- Ende des Programms --------------------------");
					break;
				}
				default:{
					System.out.println("------------------------------------------------------------------");
					System.out.println("ACHTUNG: Bitte geben Sie 1 oder 2 ein!");
					System.out.println("------------------------------------------------------------------\n");
				}
			}           
		} 

	} 
	
	/**
	 * 
	 * @param grayImg
	 * speichert das ausgegrautes Bild
	 */
	/**'/Users/robinkopitz/Desktop/GrayTestBild.png**/
	private void saveGrayscaledImage() {
		String filePath = null;
		int returnCode = 1;
		do {
			System.out.print("Geben Sie bitte den Pfad zum speichern der neuen Datei an: ");
			filePath = stdin.next();
			returnCode = grayImg.saveImage(filePath);
		}while(returnCode != 0);

	}	
	
	/**
	 * 
	 * @param grayImg
	 * holt den minimalen Grauwert und den maximalen Grauwert
	 */
	private void gatherGrayscaleInformations(){
		minGrayValue = grayImg.getMinGrayValue();
		maxGrayValue = grayImg.getMaxGrayValue();
	}
	
	/**
	 * 
	 * @param grayImg
	 * holt die Anzahl der Pixelelemente
	 */
	private void gatherPixelInformation(){
		pixelCount = grayImg.getPixelCount();
	}
	
	/**
	 * 
	 * @param grayImg
	 * holt den Mittelwert aller Grauwerte
	 */
	private void gatherMediumGrayValueInformation(){
		mediumGrayValue = grayImg.getMediumGrayValue();
	}
	
	/**
	 * 
	 * @param grayImg
	 * holt die Varianz aller Grauwerte
	 */
	private void gatherVarianzInformation(){
		varianz = grayImg.getVarianz();
	}
	
	/**
	 * 
	 * @param grayImg
	 * holt die Varianz aller Grauwerte
	 */
	private void gatherEntropieInformation(){
		entropie = grayImg.getEntropie();
	}
	
	/**
	 * 
	 * @param grayImg
	 * holt die Standardabweichung aller Grauwerte
	 */
	private void gatherStandardDevationInformation(){
		standardDevation = grayImg.getStandardDevation();
	}
	
	/**
	 * @param filePath beinhaltet den Eingabe String
	 * @param file beinhaltet die Datei
	 * Einlesen des Pfad Strings
	 */
	private void readFileOverPath() {
		String filePath = null;
		do {
			System.out.print("Geben Sie bitte den Pfad zur Datei an: ");
			filePath = stdin.next();
			img = loadImage(filePath);			
		}while(img == null);
	}
	
	/**
	 * 
	 * @param filePath
	 * 
	 * Liest Bilddateien aus dem angegebenen Pfad
	 * 
	 */
	private BufferedImage loadImage(String filePath) {
		File file = null;
		BufferedImage img = null;
		try {
			file = new File(filePath);
			img = ImageIO.read(file);
		}catch(IOException e){
			System.out.println("Fehler: Es ist ein Fehler aufgetreten: "+ e);
		}		
		return img;
	}
	
	/**
	 * kümmert sich um die Ausgabe (nur gerundete Werte hier für eine schöne ausgabe)
	 * gerundet wird auf 20 nachkommastellen
	 */
	private void printOutput() {
		System.out.println("\n------------------------ Ergebnis -------------------------------");
		System.out.println("Max Gray Value: "+ maxGrayValue);
		System.out.println("Min Gray Value: "+ minGrayValue);
		System.out.println("Pixel Count: "+ pixelCount);
		System.out.println("Mittlerer Grauwert: "+ mediumGrayValue);
		/** String Format Rundet die Werte nur in der Ausgabe*/
		System.out.println("Varianz: "+ String.format("%.10g", varianz));
		System.out.println("Standardabweichung: "+ String.format("%.10g", standardDevation));
		System.out.println("Entropie: "+ String.format("%.10g", entropie));
		System.out.println("------------------------------------------------------------------");
		
		printHistogramm();
		
		System.out.println("Haben sie alle nötigen Werte erfasst dann drücken Sie Enter!");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		clearConsole();
	}
	
	/** Bereitet das Historamm vor und speichert es als csv (csv da es allgemeingültig ist) datei für das Erstellen eines Diagramms*/
	private void printHistogramm() {
		ArrayList<Integer> usedGrayscales = grayImg.getUsedGrayscales();
		Collections.sort(usedGrayscales);
		ArrayList<Integer> pValues = grayImg.getValuesGrayscales();
		ArrayList<Integer> histogrammData = new ArrayList<>();
		
		int index = 0;
		for(int i = 0; i < 256; i++) {
			if(index < usedGrayscales.size() && usedGrayscales.contains(i)){
				histogrammData.add(pValues.get(index));
				index++;
			}else {
				histogrammData.add(0);
			}
		}
		System.out.println(histogrammData.size());
		int returnCode = 1;
		System.out.println("--------------------------Histogramm------------------------------\n");
		do {
			System.out.print("Geben Sie den Pfad ein, wo Sie das Histogramm speichern wollen: ");
			String path = stdin.next();
			returnCode = FileWriter.writeToFile(path, histogrammData);			
		}while(returnCode != 0);
		
		System.out.println("\n------------------------------------------------------------------");
		
		
	}
	
	
	/** Bereinigt die Console vom ersten Ergebnis
	 *  dabei wird unterschieden zwischen Windows und Unix based Consolen
	 */
	private final static void clearConsole()
	{
	    try {
	        final String os = System.getProperty("os.name");
	        if (os.contains("Windows")) {
	            Runtime.getRuntime().exec("cls");
	        } else {
	            Runtime.getRuntime().exec("clear");
	        }
	      } catch (final Exception e) {
	          System.out.println("something went wrong : " + e);
	      }
	}
	
}
