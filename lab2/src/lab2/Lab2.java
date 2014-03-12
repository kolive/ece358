package lab2;

import lab2.simulate.ABPSender;
import lab2.simulate.GBNSender;

public class Lab2 {

	public static void main(String[] args) {
		if(args.length == 0){
			System.out.println ("Execution error, please see readme.");
		}
		
		if(args[0] == "1"){
			questionOne();
		}else if(args[0] == "2"){
			questionTwo();
		}else if(args[0] == "3"){
			questionThree();
		}
	}

	public static void questionOne(){
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000;
		double C = 655360.0; //c in b/s
		double h = 54.0;
		double p = 1500.0;
		double[] timeoutOne = {(2.5 * tau), (5 * tau), (10 * tau), (12.5 * tau) };
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		double result; 
		String resultstr;
		
		
		System.out.println("\u0394 / \u03c4 , 2\u03c4 = 10ms,,, 2\u03c4 = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		ABPSender abp;
		for(int i = 0; i < 4; i++){
			resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = 12500*p/abp.simulate(12500,false);
				resultstr +=  ",(" +ber[n] + ")" + result;
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = 25000*p/abp.simulate(25000, false);
				
				
			}
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = 12500*p/abp.simulate(12500,false);
				resultstr +=  ",(" +ber[n] + ")" + result;
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = 25000*p/abp.simulate(25000, false);
				
				
			}
			System.out.println(resultstr);
		}
		
	}
	
	public static void questionTwo(){
		double tau = 5.0/1000; //tau in s
		double tau2 = 250.0/1000;
		double C = 655360.0; //c in b/s
		double h = 54.0;
		double p = 1500.0;
		double[] timeoutOne = {(2.5 * tau), (5 * tau), (10 * tau), (12.5 * tau) };
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		String resultstr;
		double result;
		System.out.println("\u0394 / \u03c4 , 2\u03c4 = 10ms,,, 2\u03c4 = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		ABPSender abp;
		for(int i = 0; i < 4; i++){
			resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = 12500*p/abp.simulate(12500,true);
				resultstr +=  ",(" +ber[n] + ")" + result;
				abp = new ABPSender(timeoutOne[i], p, h, C, tau, ber[n]);
				result = 25000*p/abp.simulate(25000, true);
				
				
			}
			for(int n = 0; n < 3; n++){
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = 12500*p/abp.simulate(12500,true);
				resultstr +=  ",(" +ber[n] + ")" + result;
				abp = new ABPSender(timeoutTwo[i], p, h, C, tau2, ber[n]);
				result = 25000*p/abp.simulate(25000, true);
				
				
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
		double[] timeoutOne = {(2.5 * tau), (5 * tau), (10 * tau), (12.5 * tau) };
		double[] timeoutTwo = {2.5 * tau2, 5 * tau2, 10 * tau2, 12.5 * tau2 };
		double[] ber = {0, 0.00001, 0.0001};
		double result; 
		String resultstr;
		
		
		System.out.println("\u0394 / \u03c4 , 2\u03c4 = 10ms,,, 2\u03c4 = 500ms,,,");
		System.out.println(",BER=0.0, BER=1e-5, BER=1e-4, BER=0.0, BER=1e-5, BER=1e-4");
	
		GBNSender gbn;
		for(int i = 0; i < 4; i++){
			resultstr = Double.toString(timeoutOne[i]/tau);
			for(int n = 0; n < 3; n++){
				gbn = new GBNSender(timeoutOne[i], p, h, C, tau, ber[n], N);
				result = 50000*p/gbn.simulate(50000,false);
				resultstr +=  ",(" +ber[n] + ")" + result;
				gbn = new GBNSender(timeoutOne[i], p, h, C, tau, ber[n], N);
				result = 100000*p/gbn.simulate(100000, false);
				
				
			}
			for(int n = 0; n < 3; n++){
				gbn = new GBNSender(timeoutOne[i], p, h, C, tau, ber[n], N);
				result = 50000*p/gbn.simulate(50000,false);
				resultstr +=  ",(" +ber[n] + ")" + result;
				gbn = new GBNSender(timeoutTwo[i], p, h, C, tau2, ber[n], N);
				result = 100000*p/gbn.simulate(100000, false);
				
				
			}
			System.out.println(resultstr);
		}
		
		
		
		
	}

}

