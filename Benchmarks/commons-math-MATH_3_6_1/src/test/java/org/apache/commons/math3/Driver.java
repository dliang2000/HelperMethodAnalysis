package org.apache.commons.math3;

public class Driver {
	public static void main(String[] argv) {
		org.apache.commons.math3.filter.KalmanFilterTest class1 = new org.apache.commons.math3.filter.KalmanFilterTest();
		class1.testTransitionMeasurementMatrixMismatch();
		class1.testTransitionControlMatrixMismatch();
		class1.testConstant();
		class1.testConstantAcceleration();
		class1.testCannonball();
		org.apache.commons.math3.fitting.WeightedObservedPointsTest class2 = new org.apache.commons.math3.fitting.WeightedObservedPointsTest();
		class2.testAdd1();
		class2.testAdd2();
		class2.testAdd3();
		class2.testClear();
		class2.testToListCopy();
		org.apache.commons.math3.fitting.PolynomialCurveFitterTest class3 = new org.apache.commons.math3.fitting.PolynomialCurveFitterTest();
		class3.testFit();
		class3.testNoError();
		class3.testSmallError();
		class3.testRedundantSolvable();
		class3.testLargeSample();
		org.apache.commons.math3.fitting.HarmonicFitterTest class4 = new org.apache.commons.math3.fitting.HarmonicFitterTest();
		class4.testPreconditions1();
		class4.testNoError();
		class4.test1PercentError();
		class4.testTinyVariationsData();
		class4.testInitialGuess();
		class4.testUnsorted();
		class4.testMath844();
		org.apache.commons.math3.fitting.PolynomialFitterTest class5 = new org.apache.commons.math3.fitting.PolynomialFitterTest();
		class5.testFit();
		class5.testNoError();
		class5.testSmallError();
		class5.testMath798();
		class5.testMath798WithToleranceTooLow();
		class5.testMath798WithToleranceTooLowButNoException();
		class5.testRedundantSolvable();
		class5.testRedundantUnsolvable();
		class5.testLargeSample();
		org.apache.commons.math3.fitting.GaussianFitterTest class6 = new org.apache.commons.math3.fitting.GaussianFitterTest();
		class6.testFit01();
		class6.testFit02();
		class6.testFit03();
		class6.testFit04();
		class6.testFit05();
		class6.testFit06();
		class6.testFit07();
		class6.testMath519();
		class6.testMath798();
		org.apache.commons.math3.fitting.HarmonicCurveFitterTest class7 = new org.apache.commons.math3.fitting.HarmonicCurveFitterTest();
		class7.testPreconditions1();
		class7.testNoError();
		class7.test1PercentError();
		class7.testTinyVariationsData();
		class7.testInitialGuess();
		class7.testUnsorted();
		class7.testMath844();
		org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizerWithLUTest class8 = new org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizerWithLUTest();
		class8.testMoreEstimatedParametersSimple();
		class8.testMoreEstimatedParametersUnsorted();
		try {
			class8.testMaxEvaluations();
		}
		catch (java.lang.Exception err1) {
			err1.printStackTrace();
		}
		class8.testCircleFittingBadInit();
		try {
			class8.testHahn1();
		}
		catch (java.io.IOException err2) {
			err2.printStackTrace();
		}
		org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizerTest class9 = new org.apache.commons.math3.fitting.leastsquares.LevenbergMarquardtOptimizerTest();
		class9.testNonInvertible();
		class9.testControlParameters();
		class9.testBevington();
		class9.testCircleFitting2();
		class9.testParameterValidator();
		class9.testEvaluationCount();
		org.apache.commons.math3.fitting.leastsquares.EvaluationTest class10 = new org.apache.commons.math3.fitting.leastsquares.EvaluationTest();
		class10.testComputeResiduals();
		try {
			class10.testComputeCovariance();
		}
		catch (java.io.IOException err3) {
			err3.printStackTrace();
		}
		class10.testComputeValueAndJacobian();
		try {
			class10.testComputeCost();
		}
		catch (java.io.IOException err4) {
			err4.printStackTrace();
		}
		try {
			class10.testComputeRMS();
		}
		catch (java.io.IOException err5) {
			err5.printStackTrace();
		}
		try {
			class10.testComputeSigma();
		}
		catch (java.io.IOException err6) {
			err6.printStackTrace();
		}
		try {
			class10.testEvaluateCopiesPoint();
		}
		catch (java.io.IOException err7) {
			err7.printStackTrace();
		}
		class10.testLazyEvaluation();
		class10.testLazyEvaluationPrecondition();
		class10.testDirectEvaluation();
		org.apache.commons.math3.fitting.leastsquares.MinpackTest class11 = new org.apache.commons.math3.fitting.leastsquares.MinpackTest();
		class11.testMinpackLinearFullRank();
		class11.testMinpackLinearRank1();
		class11.testMinpackLinearRank1ZeroColsAndRows();
		class11.testMinpackRosenbrok();
		class11.testMinpackHelicalValley();
		class11.testMinpackPowellSingular();
		class11.testMinpackFreudensteinRoth();
		class11.testMinpackBard();
		class11.testMinpackKowalikOsborne();
		class11.testMinpackMeyer();
		class11.testMinpackWatson();
		class11.testMinpackBox3Dimensional();
		class11.testMinpackJennrichSampson();
		class11.testMinpackBrownDennis();
		class11.testMinpackChebyquad();
		class11.testMinpackBrownAlmostLinear();
		class11.testMinpackOsborne1();
		class11.testMinpackOsborne2();
		org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizerWithSVDTest class12 = new org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizerWithSVDTest();
		try {
			class12.testMaxEvaluations();
		}
		catch (java.lang.Exception err8) {
			err8.printStackTrace();
		}
		class12.testCircleFittingBadInit();
		try {
			class12.testHahn1();
		}
		catch (java.io.IOException err9) {
			err9.printStackTrace();
		}
		class12.testGetIterations();
		try {
			class12.testNonInvertible();
		}
		catch (java.lang.Exception err10) {
			err10.printStackTrace();
		}
		org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizerWithQRTest class13 = new org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizerWithQRTest();
		class13.testMoreEstimatedParametersUnsorted();
		try {
			class13.testMaxEvaluations();
		}
		catch (java.lang.Exception err11) {
			err11.printStackTrace();
		}
		class13.testCircleFittingBadInit();
		try {
			class13.testHahn1();
		}
		catch (java.io.IOException err12) {
			err12.printStackTrace();
		}
		org.apache.commons.math3.fitting.leastsquares.EvaluationTestValidation class14 = new org.apache.commons.math3.fitting.leastsquares.EvaluationTestValidation();
		class14.testParametersErrorMonteCarloObservations();
		class14.testParametersErrorMonteCarloParameters();
		org.apache.commons.math3.fitting.leastsquares.EvaluationRmsCheckerTest class15 = new org.apache.commons.math3.fitting.leastsquares.EvaluationRmsCheckerTest();
		class15.testConverged();
		org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizerWithCholeskyTest class16 = new org.apache.commons.math3.fitting.leastsquares.GaussNewtonOptimizerWithCholeskyTest();
		class16.testMoreEstimatedParametersSimple();
		class16.testMoreEstimatedParametersUnsorted();
		try {
			class16.testMaxEvaluations();
		}
		catch (java.lang.Exception err13) {
			err13.printStackTrace();
		}
		class16.testCircleFittingBadInit();
		try {
			class16.testHahn1();
		}
		catch (java.io.IOException err14) {
			err14.printStackTrace();
		}
		org.apache.commons.math3.fitting.GaussianCurveFitterTest class17 = new org.apache.commons.math3.fitting.GaussianCurveFitterTest();
		class17.testFit01();
		class17.testWithMaxIterations1();
		class17.testWithMaxIterations2();
		class17.testWithStartPoint();
		class17.testFit02();
		class17.testFit03();
		class17.testFit04();
		class17.testFit05();
		class17.testFit06();
		class17.testFit07();
		class17.testMath519();
		class17.testMath798();
		org.apache.commons.math3.fitting.SimpleCurveFitterTest class18 = new org.apache.commons.math3.fitting.SimpleCurveFitterTest();
		class18.testPolynomialFit();
		org.apache.commons.math3.fitting.CurveFitterTest class19 = new org.apache.commons.math3.fitting.CurveFitterTest();
		class19.testMath303();
		class19.testMath304();
		class19.testMath372();
	}
}