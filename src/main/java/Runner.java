import de.uni_bonn.cs.tnn.gui.DataPlotter;
import de.uni_bonn.cs.tnn.io.Stimulus;
import de.uni_bonn.cs.tnn.mlp.MLPNetwork;
import de.uni_bonn.cs.tnn.core.TransferFuncType;
import de.uni_bonn.cs.tnn.gui.ErrorPlotter;
import de.uni_bonn.cs.tnn.io.Pattern;
import de.uni_bonn.cs.tnn.io.PatternLoader;
import de.uni_bonn.cs.tnn.rbf.RBFNetwork;
import de.uni_bonn.cs.tnn.som.MultiNeuralGas;
import de.uni_bonn.cs.tnn.util.IdentityFunction;
import de.uni_bonn.cs.tnn.util.PatternGenerator;
import de.uni_bonn.cs.tnn.util.QuadraticFunction;
import de.uni_bonn.cs.tnn.util.SinusFunction;

import java.io.File;
import java.util.*;

public class Runner {

    public static void main(String[] args){

        final String ERROR_MSG = "First argument should be either MLP or RBF!";

        if (args.length != 1){
            System.out.println(ERROR_MSG);
        }
        else{
            switch(args[0]) {
                case "MLP":
                    System.out.println("Running MLP demo");
                    runMLP();
                    break;
                case "SOM":
                    System.out.println("Running M-GAS demo");
                    runSOM();
                    break;
                case "RBF":
                    System.out.println("Running RBF demo");
                    runRBF();
                    break;
            }
        }
    }

    public static void runMLP(){
        // define shape
        int[] shape = {1, 10, 10, 1};
        TransferFuncType[] functions = {TransferFuncType.IDENTITY, TransferFuncType.TANH, TransferFuncType.TANH, TransferFuncType.TANH};
        MLPNetwork testMLP = new MLPNetwork(shape, functions);

        // load Patterns
        PatternLoader loader = new PatternLoader();

        // shuffle
        List<Pattern> patterns = loader.loadPatterns(new File("src/main/resources/training2.dat"));
        patterns = patterns.subList(0,150);
        patterns.addAll(patterns);
        patterns.addAll(patterns);

        List<Pattern> shuffled = loader.getShuffledPatternList(patterns);
        // train
        testMLP.train(shuffled);

        // visualize
        ErrorPlotter plotter = new ErrorPlotter(testMLP.errorValues);
        plotter.showErrorPlot();
    }

    public static void runRBF(){
        int[] shape2 = {2, 2, 1};
        double[][] centers = {{0.0, 1.0}, {1.0, 0.0}};
        RBFNetwork testRBF = new RBFNetwork(shape2, centers);
        double[] testPoint = {0.0, 0.0};
        System.out.println("Testing XOR for "+Arrays.toString(testPoint)+": "+Arrays.toString(testRBF.calculateOutputs(testPoint)));
        double[] testPoint2 = {1.0, 0.0};
        System.out.println("Testing XOR for "+Arrays.toString(testPoint2)+": "+Arrays.toString(testRBF.calculateOutputs(testPoint2)));
    }

    public static void runSOM(){
        // initialize 2 gases of 5 neurons
        int[] shape = {5, 5};
        MultiNeuralGas mGas = new MultiNeuralGas(shape, 2);

        // save data for visualization
        List<Stimulus> data = new ArrayList<>();

        // e.g. clustering of 2 squares
        Random r = new Random();
        for (int i = 0; i < 100; i++) { //100 stimuli
            double[] stimPos;
            if(r.nextBoolean()){ //50-50 chance to get into the unit square's bottom left quarter
                stimPos = new double[]{r.nextDouble(), r.nextDouble()};
            }
            else{ //50-50 chance to get into the unit square's top right quarter
                stimPos = new double[]{-r.nextDouble(), -r.nextDouble()};
            }
            data.add(new Stimulus(stimPos));
            mGas.applyStimulus(stimPos);
        }
        System.out.println("applied 100 stimuli to the "+ Arrays.toString(shape) +" M-GAS");

        //visualize
        DataPlotter plotter = new DataPlotter();
        plotter.addData(data, "Data Points");
        plotter.addData(mGas, "Neurons");
        plotter.plot();
    }
}
