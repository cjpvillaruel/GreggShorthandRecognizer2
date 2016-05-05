import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class NormalizeData {
	ArrayList<Shorthand> attributesList;
	ArrayList<Shorthand> trainingData;
	double[] maxAttributes, minAttributes;
	double[] mean, stdDev;
	Mat attributeData;
	int attributes;
	String filename;
	public NormalizeData(String filename, int max, int min, int attributes){
		attributesList= new ArrayList<Shorthand>();
		//read file
		this.readFile(filename);
	}
	public NormalizeData(ArrayList<Shorthand> trainingData, int attributes){
		attributeData= new Mat(trainingData.size(), attributes,  CvType.CV_32F);
		//put the features in one Matrix
		for(int i=0;i<trainingData.size();i++){
			Mat features= trainingData.get(i).getFeatures();
			//System.out.println(features.dump());
			for(int j=0; j< attributes; j++){
				double num= features.get(0, j)[0];
				attributeData.put(i, j, num);
			}
		}
		mean= new double[attributes];
		stdDev= new double[attributes];
		String m="";
		for(int i=0;i<attributes;i++){
			Mat data = attributeData.col(i);
			Double mean1= getMean(data);
			Double stdd= getStandardDev(data);
			m+=mean1+','+stdd+"\n";
			mean[i] = mean1;
			stdDev[i] = stdd;
		//	System.out.println("mean:"+getMean(data)+" stdev:"+ getStandardDev(data));
		}

		for(int i=0;i<trainingData.size();i++){
			Mat features= trainingData.get(i).getFeatures();
			//System.out.println(features.dump());
			for(int j=0; j< attributes; j++){
				double num= features.get(0, j)[0];
				double norm = (num-mean[j])/stdDev[j];
				features.put(0, j, norm);
			}
		}
		this.trainingData = trainingData;
		//save the mean and stddev in a file
		String str="";
		for(int i=0;i<attributes;i++){
			str+=mean[i]+","+stdDev[i]+"\n";
		}
		saveFile("scale", str);
	}
	public void saveFile(String filename, String text){
		try{
			File file = new File(filename+".txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(text);
			bw.close();
			System.out.print("File updated");
		}
		catch(Exception e){	
		}
	}
	public double getMean(Mat data){
		double sum=0;
		double rows= data.rows();
		for(int i=0; i<rows ;i++){
			sum+= data.get(i, 0)[0];
		}
		double mean = sum/rows;
		return mean;
	}
	public double getStandardDev(Mat data){
		double mean=getMean(data);
		double size= data.rows();
		double temp = 0;
		for(int i=0;i< size;i++){
			double a = data.get(i, 0)[0];
			 temp += (mean-a)*(mean-a);
		}
        double variance = temp/size;
        return Math.sqrt(variance);
	}
	public NormalizeData(String filename,  int attributes){
		attributesList= new ArrayList<Shorthand>();
		//read file
		this.readFile(filename);
		this.filename= filename;
		maxAttributes= new double[attributes];
		minAttributes= new double[attributes];
//		for(int i=0;i<attributes;i++){
//			maxAttributes[i]= 0;
//			minAttributes[i]=Double.MAX_VALUE;
//		}
//		getMaxMinAttributes();
		
		printToFile();
	}
	public NormalizeData(Mat data){
		//get maximum and minimum per column
		attributes= data.cols();
		maxAttributes= new double[attributes];
		minAttributes= new double[attributes];
		
		getMaxMinInMat(data);
		
		for(int i=0;i< data.cols();i++){
			for(int j=0;j< data.rows();j++){
				double num= data.get(j, i)[0];
				double norm= normalizeValue(num, maxAttributes[i], minAttributes[i]);
				data.put(j, i, norm);
			}
		}
	}
	public void getMaxMinInMat(Mat data){
		for(int i=0;i< attributes;i++){
			Mat col = data.col(i);
			Core.MinMaxLocResult mmr = Core.minMaxLoc(col);
			System.out.println("min of col 0 ="+mmr.maxVal);
			maxAttributes[i]=mmr.maxVal;
			minAttributes[i]=mmr.minVal;
		}
		
	}
	public void getMaxMinAttributes(){
		for(int i=0;i< attributesList.size();i++){
			Shorthand word=attributesList.get(i); 
			double width= word.width;
			double height= word.height;
			double x= word.x;
			double y=word.y;
			double area= word.area;

			if(width > maxAttributes[0])
				maxAttributes[0]= width;
			if(width < minAttributes[0])
				minAttributes[0]=width;
			
			if(height > maxAttributes[1])
				maxAttributes[1]= height;
			if(height < minAttributes[1])
				minAttributes[1]=height;
			
			if(x > maxAttributes[2])
				maxAttributes[2]= x;
			if(x < minAttributes[2])
				minAttributes[2]=x;
			
			if(y > maxAttributes[3])
				maxAttributes[3]= y;
			if(y < minAttributes[3])
				minAttributes[3]=y;
			
			if(area > maxAttributes[4])
				maxAttributes[4]= area;
			if(area < minAttributes[4])
				minAttributes[4]=area;
		}
		System.out.println("Max "+maxAttributes[0]+" "+maxAttributes[1]+" "+maxAttributes[4]);
	}
	public void readFile(String filename){
		//save it to arrayList
		String  line = null;
		try{
			// open input stream test.txt for reading purpose.
			FileReader fl = new FileReader(filename);
			BufferedReader br = new BufferedReader(fl);
			while(true){
				line = br.readLine();
				if(line== null)break;
				String[] data = line.split(",");
				System.out.println(data[4]+", "+data[1]);
				double width= Double.parseDouble(data[0]);
				double height= Double.parseDouble(data[1]);
				double x= Double.parseDouble(data[2]);
				double y= Double.parseDouble(data[3]);
				double area= Double.parseDouble(data[4]);
				int id= Integer.parseInt(data[5]);
				
				Shorthand word= new Shorthand(width, height, x, y, area, id);
				attributesList.add(word);
				
			}
			
			br.close();
	  }catch(Exception ex){
	     ex.printStackTrace();
	  }
		
	}
	
	public double normalizeValue( double val,double maxAttribute,double minAttribute){
		double upper=1, lower=0;
		double norm= lower+(lower+upper)*((val-minAttribute)/(maxAttribute-minAttribute));
		return norm;
	}
	public void printToFile(){
		String str="";
		for(int i=0;i< attributesList.size();i++){
			Shorthand word=attributesList.get(i); 
			double width= word.width;
			double height= word.height;
			double x= word.x;
			double y=word.y;
			double area= word.area;
			
			str+=normalizeValue(width,maxAttributes[0],minAttributes[0])+",";
			str+=normalizeValue(height,maxAttributes[1],minAttributes[1])+",";
			str+=normalizeValue(x,maxAttributes[2],minAttributes[2])+",";
			str+=normalizeValue(y,maxAttributes[3],minAttributes[3])+",";
			str+=normalizeValue(area,maxAttributes[4],minAttributes[4])+",";
			str+= word.id;
			for(int j=0;j<30;j++){
				if(word.id-1== j)
					str+="1";
				else str+="0";
				if(j<29)
					str+=",";
			}
			str+="\n";
			
		}
		//creating a file
		try{
			File file = new File("normalized.txt");
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(str);
			bw.close();
			System.out.print("File updated");
		}
		catch(Exception e){	
		}
	}
	public NormalizeData(String filename, int classes, int attributes){
		String  line = null;
		String printtofile="";
		try{
			// open input stream test.txt for reading purpose.
			FileReader fl = new FileReader(filename);
			BufferedReader br = new BufferedReader(fl);
			while(true){
				line = br.readLine();
				if(line== null)break;
				String[] data = line.split(",");
				//System.out.println(data[attributes]);
				double a= Double.parseDouble(data[attributes]);	
				for(int i=0;i< attributes;i++){
					printtofile+=data[i]+",";
				}
				for(int i=0;i<classes;i++){
					if((i+1) == Integer.parseInt(data[attributes])){
						printtofile+="1";
					}
					else{
						printtofile+="0";
					}
					if(i<classes-1){
						printtofile+=",";
					}
					else{
						printtofile+="\n";
					}
				}
			}
			
			br.close();
	  }catch(Exception ex){
	     ex.printStackTrace();
	  }
	//System.out.println(printtofile);
	//creating a file
	try{
		File file = new File("neurophtraining.txt");
		// if file doesnt exists, then create it
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(printtofile);
		bw.close();
		System.out.print("File updated");
	}
	catch(Exception e){	
	}
	}
	public static void main(String[] args){
		NormalizeData n= new NormalizeData("training_data.txt",60,13);
	}
}
