import weka.core.AbstractInstance;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.functions.neural.*;
import weka.classifiers.functions.supportVector.*;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.classifiers.functions.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;
 
public class CSV2Arff {
  /**
   * takes 2 arguments:
   * - CSV input file
   * - ARFF output file
   */
public static void main(String[] args) throws Exception {
	// Declare two numeric attributes
	
	BufferedReader reader = new BufferedReader(new FileReader("training_data.arff"));
	Instances trainingdata = new Instances(reader);
	reader.close();
	
	BufferedReader reader2 = new BufferedReader(new FileReader("testing_data.arff"));
	Instances testdata = new Instances(reader2);
	reader.close();
	// setting class attribute
	testdata.setClassIndex(13);
	trainingdata.setClassIndex(13);
	// train NaiveBayes
	 long starttime = System.currentTimeMillis(); 
	 NaiveBayes nb = new NaiveBayes();
	 nb.buildClassifier(trainingdata);
	 weka.core.SerializationHelper.write("naivebayesmodel.model", nb);
	 //Classifier nbayes = (Classifier) weka.core.SerializationHelper.read("naivebayesmodel.model");
	 long stoptime = System.currentTimeMillis(); 
	 long elapsedtime = stoptime - starttime; 
	 System.out.println("build time: " + elapsedtime/60);
	
	 
	 MultilayerPerceptron mlp = new MultilayerPerceptron();
	 mlp.setOptions(Utils.splitOptions("-L 0.3 -M 0.2 -N 500 -V 0 -S 0 -E 20 -H a")); 
//	 mlp.buildClassifier(trainingdata);
	 
	 
	 Evaluation eval = new Evaluation(trainingdata);
	 eval.evaluateModel(nb, testdata);
//	 eval.crossValidateModel(nb, trainingdata, 10, new Random(1));
	 System.out.println(eval.toSummaryString("\nResults for MLP\n======\n", false));
	 System.out.println(eval.toClassDetailsString());
	 System.out.println(eval.toMatrixString());
  }
}