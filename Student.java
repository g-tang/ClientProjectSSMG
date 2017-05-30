package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
//CRSTITLE,DUR,FIRST,ID,LAST,PD,TCHF,TCHL
public class Student {
	private String fName;
	private String lName;
	private String id;
	ArrayList<Class> schedule1=new ArrayList<Class>();
	ArrayList<Class> schedule2=new ArrayList<Class>();
	String[] quals;
	
	public Student(String[] s){
		fName=s[2];
		lName=s[4];
		id=s[3];
	}
	
	public void addClass(String[] s){

		if(s[1].equals("S1")){
			schedule1.add(new Class(s[0], s[1], Integer.parseInt(s[5]), s[6], s[7]));
		}
		if(s[1].equals("S2")){
			schedule2.add(new Class(s[0], s[1], Integer.parseInt(s[5]), s[6], s[7]));
		}
	}
	
	public String toString(){
		System.out.println( fName+" "+lName+", #"+id);
		for(Class c:schedule1){
			System.out.println(c);
		}
		for(Class d:schedule2){
			System.out.println(d);
		}
		return "";
	}
	
	public void sortClass(){
		Collections.sort(schedule1);
		Collections.sort(schedule2);
		
	}
	
	public String getFullName(){
		return fName+ " "+lName;
	}
	public static ArrayList<String[]> parseBellSchedule(){
		ArrayList<String[]> bellSchedule=new ArrayList<String[]>();
		Scanner s=null;
		try {
			s = new Scanner(new File("BellSchedule"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while(s.hasNextLine()){
			bellSchedule.add(s.nextLine().split(":"));
		}
		return bellSchedule;
	}
	public static Teacher getTheoreticalTeacher(ArrayList<Class> c){
		ArrayList<String[]> bellSchedule=parseBellSchedule();
		Date d=new Date();
		for(int i=0;i<bellSchedule.size();i++){
			try{
				if(d.getHours()<=Integer.parseInt(bellSchedule.get(i)[0])){
					return(searchClass(c, i).getTeacher());
				}else if(d.getHours()==Integer.parseInt(bellSchedule.get(i)[0])){
					if(d.getMinutes()<=Integer.parseInt(bellSchedule.get(i)[1])){
						return(searchClass(c,i).getTeacher());
					}
				}
			}catch(Exception e){}
		}
		return null;
	}
	private static Class searchClass(ArrayList<Class> classes, int pd){
		for(Class c:classes){
			if(c.getPd()==pd){
				return c;
			}
		}
		return null;
	}
	public String getFirstName(){
		return fName;
	}
	
	public String getID(){
		return id;
	}
	
	public ArrayList<Class> getSchedule(int sem){
		if(sem==1){
			return schedule1;
		}
		return schedule2;
	}
}
