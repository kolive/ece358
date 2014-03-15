package lab2;

import lab2.simulate.ABPSender;
import lab2.simulate.GBNSender;

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
			System.out.println("ABP RESULTS");
			questionOne();
			System.out.println("ABP_NACK RESULTS");
			questionTwo();
			System.out.println("GBN RESULTS");
			questionThree();
		}
	}



	public static void questionOne(){
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000; //tau2 in s
		double C = 5242880.0; //c in b/s
		double h = 8*54.0; //h in b
		double p = 8*1500.0; //p in b
		double[] timeoutOne = {(2.5 * tau), (5 * tau), 7.5 * tau, (10 * tau), (12.5 * tau) }; //timeouts in s
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 7.5 * tau2,  10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		double result; 
		String resultstr = "";
		
		
		System.out.println("Delta / Tau , 2Tau = 10ms,,, 2Tau = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		ABPSender abp;
		for(int i = 0; i < timeoutOne.length; i++){
			//resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = abp.simulate(12500,false);
				resultstr +=  "," + result;
				
				//for stability check, commented out for test runs
				/*
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = abp.simulate(25000, false);
				*/
				
			}
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = abp.simulate(12500,false);
				resultstr +=  ","+ result;
				
				//for stability check, commented out for test runs
				/*
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = abp.simulate(25000, false);
				*/
				
			}
			System.out.println(resultstr);
		}
		
	}
	
	public static void questionTwo(){
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000; //tau2 in s
		double C = 5242880.0; //c in b/s
		double h = 8*54.0; //h in b
		double p = 8*1500.0; //p in b
		double[] timeoutOne = {(2.5 * tau), (5 * tau), 7.5 * tau, (10 * tau), (12.5 * tau) }; //timeouts in s
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 7.5 * tau2,  10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		double result; 
		String resultstr = "";
		
		
		System.out.println("Delta / Tau , 2Tau = 10ms,,, 2Tau = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		ABPSender abp;
		for(int i = 0; i < timeoutOne.length; i++){
			//resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = abp.simulate(12500,true);
				resultstr +=  "," + result;
				
				//for stability check, commented out for test runs
				/*
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = abp.simulate(25000, true);
				*/
				
			}
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = abp.simulate(12500,true);
				resultstr +=  ","+ result;
				
				//for stability check, commented out for test runs
				/*
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = abp.simulate(25000, true);
				*/
				
			}
			System.out.println(resultstr);
		}
		
	}
	
	public static void questionThree(){
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000; //tau2 in s
		double C = 5242880.0; //c in b/s
		double h = 8*54.0; //h in b
		double p = 8*1500.0; //p in b
		double[] timeoutOne = {(2.5 * tau), (5 * tau), 7.5 * tau, (10 * tau), (12.5 * tau) }; //timeouts in s
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 7.5 * tau2,  10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		double result; 
		String resultstr = "";
		
		
		//System.out.println("Delta / Tau , 2Tau = 10ms,,, 2Tau = 500ms,,,");
		//System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		GBNSender gbn;
		for(int i = 0; i < timeoutOne.length; i++){
			//resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 3; n++){
				gbn = new GBNSender(timeoutOne[i], p, h, C, tau, ber[n], 4);
				result = gbn.simulate(25000,false);
				resultstr +=  "," + result;
				
				//for stability check, commented out for test runs
				/*
				gbn = new GBNSender(timeoutOne[i], p, h, C, tau, ber[n], 4);
				result = gbn.simulate(100000, false);
				*/		
			}
			for(int n = 0; n < 3; n++){
				gbn = new GBNSender(timeoutTwo[i], p, h, C, tau2, ber[n], 4);
				result = gbn.simulate(25000,false);
				resultstr +=  "," + result;
				
				//for stability check, commented out for test runs
				/* 
				gbn = new GBNSender(timeoutTwo[i], p, h, C, tau2, ber[n], 4);
				result = gbn.simulate(50000, false);
				*/
				
				
			}
			System.out.println(resultstr);
		}
		
		
		
		
	}

}

