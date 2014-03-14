package lab2;

import lab2.simulate.ABPSender;
import lab2.simulate.ABPSimulator;
import lab2.simulate.GBNSender;
import lab2.simulate.GBNSimulator;

public class Lab2 {

	public static void main(String[] args) {
		if(args.length == 0){
			System.out.println ("Execution error, please see readme.");
		}
		
		if(args[0].equals( "1" )){
			questionOne();
		}else if(args[0].equals( "2" )){
			questionTwo();
		}else if(args[0].equals( "3" )){
			questionThree();
		}else{
			GBNvABP();
			/*System.out.println("ABP RESULTS");
			questionOne();
			System.out.println("ABP_NACK RESULTS");
			questionTwo();
			System.out.println("GBN RESULTS");
			questionThree();*/
		}
	}

	private static void GBNvABP() {
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000;
		double C = 655360.0; //c in b/s
		double h = 54.0;
		double p = 1500.0;
		int N = 4;
		double[] timeoutOne = {(2.5 * tau), (5 * tau), (10 * tau), 7.5 * tau,  (12.5 * tau) };
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 7.5 * tau2, 10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		double result; 
		String resultstr;
		
		
		System.out.println("\u0394 / \u03c4 , 2\u03c4 = 10ms,,, 2\u03c4 = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		GBNSender gbn;
		ABPSender abp;
		for(int i = 0; i < 1; i++){
			resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 1; n++){
				gbn = new GBNSender(timeoutOne[i], p, h, C, tau, ber[2], 4);
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[2]);
			//	System.out.println("ABP result: " + 8*50000*p/abp.simulate(50000,false));
				
				//result = 8*50000*p/gbn.simulate(50000,false);
				System.out.println("GBN1 result: " + 8*50000*p/gbn.simulate(50000,false));
				
				
				
			}

		}
		
	}

	public static void questionOne(){
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000;
		double C = 655360.0; //c in b/s
		double h = 54.0;
		double p = 1500.0;
		double[] timeoutOne = {(2.5 * tau), (5 * tau), 7.5 * tau, (10 * tau), (12.5 * tau) };
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 7.5 * tau2,  10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		double result; 
		String resultstr;
		
		
		System.out.println("\u0394 / \u03c4 , 2\u03c4 = 10ms,,, 2\u03c4 = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		ABPSender abp;
		ABPSimulator[] sims = new ABPSimulator[3];

		boolean stillExecuting = true;
		for(int i = 0; i < timeoutOne.length; i++){
			resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 3; n++){
				
				sims[n] = new ABPSimulator(timeoutOne[i], p, h, C, tau, ber[n], 50000, false);
				sims[n].start();
				
				
			/*	abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = 8*12500*p/abp.simulate(12500,false);
				resultstr +=  "," + result;
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = 8*25000*p/abp.simulate(25000, false);
			*/	
				
			}
			while(stillExecuting){
				stillExecuting = !sims[0].isComplete() || !sims[1].isComplete() || !sims[2].isComplete();
			}
			stillExecuting = true;
			resultstr += sims[0].getResult() + sims[1].getResult() + sims[2].getResult();
			
			for(int n = 0; n < 3; n++){
				
				sims[n] = new ABPSimulator(timeoutTwo[i], p, h, C, tau2, ber[n], 50000, false);
				sims[n].start();
				
				/*
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = 8*12500*p/abp.simulate(12500,false);
				resultstr +=  ","+ result;
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = 8*25000*p/abp.simulate(25000, false);
				*/
				
			}
			
			while(stillExecuting){
				stillExecuting = !sims[0].isComplete() || !sims[1].isComplete() || !sims[2].isComplete();
			}
			stillExecuting = true;
			resultstr += sims[0].getResult() + sims[1].getResult() + sims[2].getResult();
			System.out.println(resultstr);
		}
		
	}
	
	public static void questionTwo(){
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000;
		double C = 655360.0; //c in b/s
		double h = 54.0;
		double p = 1500.0;
		double[] timeoutOne = {(2.5 * tau), (5 * tau), 7.5 * tau,  (10 * tau), (12.5 * tau) };
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 7.5 * tau2,  10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		String resultstr;
		double result;
		System.out.println("\u0394 / \u03c4 , 2\u03c4 = 10ms,,, 2\u03c4 = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		ABPSender abp;
		for(int i = 0; i < timeoutOne.length; i++){
			resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = 8*12500*p/abp.simulate(12500,true);
				resultstr +=  "," + result;
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = 8*25000*p/abp.simulate(25000, true);
				
				
			}
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = 8*12500*p/abp.simulate(12500,true);
				resultstr +=  "," + result;
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = 8*25000*p/abp.simulate(25000, true);
				
				
			}
			System.out.println(resultstr);
		}
	}
	
	public static void questionThree(){
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000;
		double C = 655360.0; //c in b/s
		double h = 54.0;
		double p = 1500.0;
		int N = 4;
		double[] timeoutOne = {(2.5 * tau), (5 * tau), 7.5 * tau, (10 * tau),  (12.5 * tau) };
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 7.5 * tau2, 10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		double result; 
		String resultstr;
		
		
		System.out.println("\u0394 / \u03c4 , 2\u03c4 = 10ms,,, 2\u03c4 = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		GBNSender gbn;
		GBNSimulator[] sims = new GBNSimulator[3];
		boolean stillExecuting = true;
		for(int i = 0; i < timeoutOne.length; i++){
			resultstr = Double.toString(timeoutOne[i]/tau);
			sims = new GBNSimulator[3];
			for(int n = 0; n < 3; n++){
				sims[n] = new GBNSimulator(timeoutOne[i], p, h, C, tau, ber[n], N, 50000);
				sims[n].start();
				
				/*resultstr +=  "," + result;
				gbn = new GBNSender(timeoutOne[i], p, h, C, tau, ber[n], N);
				result = 8*100000*p/gbn.simulate(100000, false);
				*/
				
			}
			
			while(stillExecuting){
				stillExecuting = !sims[0].isComplete() || !sims[1].isComplete() || !sims[2].isComplete();
			}
			stillExecuting = true;
			resultstr += sims[0].getResult() + sims[1].getResult() + sims[2].getResult();
			sims = new GBNSimulator[3];
			for(int n = 0; n < 3; n++){
				sims[n] = new GBNSimulator(timeoutTwo[i], p, h, C, tau2, ber[n], N, 50000);
				sims[n].start();
				
				/*gbn = new GBNSender(timeoutTwo[i], p, h, C, tau2, ber[n], N);
				result = 8*100000*p/gbn.simulate(100000, false);*/
				
				
			}
			while(stillExecuting){
				stillExecuting = !sims[0].isComplete() || !sims[1].isComplete() || !sims[2].isComplete();
			}
			stillExecuting = true;
			resultstr += sims[0].getResult() + sims[1].getResult() + sims[2].getResult();
			System.out.println(resultstr);
		}
		
		
		
		
	}

}

