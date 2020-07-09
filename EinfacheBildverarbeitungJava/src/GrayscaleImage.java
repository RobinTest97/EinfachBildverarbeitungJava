import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class GrayscaleImage extends BufferedImage {
	
	private int minGrayValue = 300;
	private int maxGrayValue = -1;
	private int pixelCount = -1;
	private int mediumGrayValue = -1;
	private double varianz = -1;
	private double standardDevation = -1;
	private int[] grayValues;
	private double entropie = 1;

	public GrayscaleImage(int width, int height, BufferedImage orgImg) {
		super(width, height, TYPE_INT_ARGB);
		countPixels();
		initializeGrayValues();
		createGrayscaleImg(orgImg);
		checkForMinMaxValue();
		calculateMediumGrayValue();
		calculateVarianz();
		calculateStandardDevation();
		prepare_p_value();
		calculateEntropie();
	}

	
	/**-----------------------------------------Getter------------------------------------------------*/
	
	public int getMaxGrayValue() {
		return maxGrayValue;
	}
	
	public int getMinGrayValue() {
		return minGrayValue;
	}
	
	public int getPixelCount() {
		return pixelCount;
	}
	
	public int getMediumGrayValue() {
		return mediumGrayValue;
	}
	
	public double getVarianz() {
		return varianz;
	}
	
	public double getStandardDevation() {
		return standardDevation;
	}
	
	public double getEntropie() {
		return entropie;
	}
	
	public ArrayList<Integer> getUsedGrayscales() {
		return usedValues;
	}
	
	public ArrayList<Integer> getValuesGrayscales() {
		return pValues;
	}
	
	/**-----------------------------------------Ende Getter--------------------------------------------*/	
	
	
	private void createGrayscaleImg(BufferedImage orgImg) {
		for(int height = 0; height <  orgImg.getHeight(); height++) {
			for(int width = 0; width < orgImg.getWidth(); width++) {
				
				Color color = new Color(orgImg.getRGB(width, height));
				int red = color.getRed();
				int green = color.getGreen();
				int blue = color.getBlue();
				int alpha = color.getAlpha();
				
				//Einfaches Grayscaling
				int grayscaling = (red + blue + green) / 3;
				addToGrayValues(grayscaling);
				
				Color grayColor = new Color(grayscaling, grayscaling, grayscaling, alpha);
				this.setRGB(width, height, grayColor.getRGB());
				
			}
		}		
	}
	
	/**
	 *
	 * @param orgImg
	 * Berechnet die Anzahl der vorhandenen Pixel im 
	 * orginal Bild (unterscheidet sich nicht vom Graywert Bild)
	 */
	private void countPixels() {
		int pixelWidth = this.getWidth();
		int pixelHeigth= this.getHeight();
		
		pixelCount = pixelWidth * pixelHeigth;
		
	}
	
	/**
	 * Initialisiert das Grauwert Array auf Basis der Pixel Anzahl, 
	 * da jeder Pixel ein Grauwert aufweist
	 */
	private void initializeGrayValues() {
		grayValues = new int[pixelCount];
		
	}
	
	/**Halter Liste für P-Werte*/
	ArrayList<Integer>pValues = new ArrayList<>();
	/** Used Values für das Histogramm zwischen Speichern */
	ArrayList<Integer> usedValues = new ArrayList<>();
	
	private void calculateEntropie() {
		for(int value : pValues) {
			double probability = (double) value / pixelCount;
			/**-1 * sum(p(g)*log2(p(g)))*/
			entropie -= (probability*log2(probability));
		}
		//entropie *= -1;
	}
	

	/** Zählt die Häufigkeit eines Grauwerts*/
	private void prepare_p_value() {
		for(int i = 0; i < grayValues.length; i++) {
			int counter = 1;
			/*Cast zu double für die die Division*/
			if(!usedValues.contains(grayValues[i])) {
				usedValues.add(grayValues[i]);
				for(int j = i+1; j < grayValues.length; j++) {
					if(grayValues[i] == grayValues[j]) {
						counter++;
					}
				}
				pValues.add(counter);
			}
		}
	}
	
	//log2 = ln a / ln b
	private double log2( double wert) {
		  return Math.log( wert) / Math.log( 2.0);
	}
	
	/**
	 * 
	 * @param path
	 * Speichert das umgewandelte Bild, nur Sinnvoll bei einer Farbbild eingabe
	 */
	public int saveImage(String path) {
		try {
			ImageIO.write(this, "png", new File(path));
			return 0;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Das Bild konnte nicht gespeichert werden!");
		}
		return -1;
	}
	
	private void checkForMinMaxValue() {
		checkForMaxGrayValue();
		checkForMinGrayValue();
	}
	
	/**
	 * 
	 * @param grayscaling
	 * Aktualisiert solange bis der geringste Grauwert gefunden wurde.
	 */
	private void checkForMinGrayValue() {
		for(int i = 0; i < grayValues.length; i++) {
			if(minGrayValue > grayValues[i])
				minGrayValue = grayValues[i];
		}
	}
	
	/**
	 * 
	 * @param grayscaling
	 * Aktualisiert solange bis der größte Grauwert gefunden wurde.
	 */
	private void checkForMaxGrayValue() {
		for(int i = 0; i < grayValues.length; i++) {
			if(maxGrayValue < grayValues[i])
				maxGrayValue = grayValues[i];
		}
	}
	
	/**
	 * Kalkuliert den Mittelwert aller Grauwerte.
	 */
	private void calculateMediumGrayValue() {
		for(int value : grayValues) {
			mediumGrayValue += value;
		}
		mediumGrayValue /= pixelCount;
	}
	
	/**
	 * Kalkuliert die Varianz des Bildes σ2 = (x - xquer)^2 / n-1
	 */
	private void calculateVarianz() {
		for(int value : grayValues) {
			varianz += ((value - mediumGrayValue) * (value - mediumGrayValue));
			varianz /= (pixelCount-1);
		}
	}
	
	private void calculateStandardDevation() {
		standardDevation = Math.sqrt(varianz);
	}
	
	/** Fortlaufender Index für die Grauwerte*/
	private int index = 0;

	/**
	 * @param grayscaling --> Einzelner Grauwert
	 * Fügt die generierten Grauwerte einem Array hinzu, damit die Grauwerte nicht jedesmal geholt werden müssen
	 */
	private void addToGrayValues(int grayscaling) {
		grayValues[index] = grayscaling;
		index++;
	}
}
