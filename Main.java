package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

import javax.mail.MessagingException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;


public class Main extends Application {
	static ArrayList<Student> students=new ArrayList<Student>();
	static ArrayList<Teacher> teachers=new ArrayList<Teacher>();
	static ArrayList<String> reasons=new ArrayList<String>();
	static String courseFile="";
	static String teacherFile="";
	static String reasonFile="";
	String ID="";
	Student focus;
	static String password="";
	static String fromEmail="";
	static String fromEmailPassword="";
	static int semester=0;
	Teacher notify=new Teacher("","","");
	static Teacher administrator=new Teacher("","","");
	Teacher theoretical=new Teacher("","","");
	String reason="";
	Font oxygen30=new Font("Oxygen",30);
	Font oxygen50=new Font("Oxygen", 50);
	boolean toClose=false;
	static boolean validInit=true;
	static Scene settings;
	static PrintWriter backUp=null;
	public static void setConfig(){
		try {
			Scanner s=new Scanner(new File("config"));
			password=s.nextLine();
			administrator=new Teacher("","",s.nextLine());
			fromEmail=s.nextLine();
			fromEmailPassword=s.nextLine();
			EmailUtil.setFrom(fromEmail, fromEmailPassword);
			semester=Integer.parseInt(s.nextLine());
			courseFile=s.nextLine();
			parseData(courseFile);
			teacherFile=s.nextLine();
			teachers=Teacher.getTeacherInfo(new File(teacherFile));
			reasonFile=s.nextLine();
			Scanner q=new Scanner(new File(reasonFile));
			reasons=new ArrayList<String>();
			while(q.hasNextLine()){
				reasons.add(q.nextLine());
			}
		} catch (Exception e) {
			e.printStackTrace();
			// show settings screen
			validInit=false;
		}
		System.out.println("complete");
	}
	public void start(Stage s) {
		try {
			s.setMaximized(true);
			s.setTitle("Media Center Sign In");
			//s.setAlwaysOnTop(true);
			s.setFullScreen(true);
			s.setResizable(false);
			s.initStyle(StageStyle.UNDECORATED);
			s.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
			Platform.setImplicitExit(false);
			s.show();
			File backUpFile=new File(new Date().getMonth()+"_"+new Date().getDate()+"_MCLog");
			backUp=new PrintWriter(backUpFile);
			System.out.println(s.getWidth());
			System.out.println(s.getHeight());
			Date q=new Date();

			//Settings
			Pane settingsGrid=new Pane();
			settings=new Scene(settingsGrid, s.getWidth(), s.getHeight());

			Text promptChangePassword=new Text("Change Password:");
			promptChangePassword.setFont(oxygen30);
			promptChangePassword.setX(200);promptChangePassword.setY(200);
			settingsGrid.getChildren().add(promptChangePassword);

			Text promptNewPWord1=new Text("Enter new password below:");
			promptNewPWord1.setFont(oxygen30);
			promptNewPWord1.setX(200);promptNewPWord1.setY(250);
			settingsGrid.getChildren().add(promptNewPWord1);

			PasswordField newPassword1=new PasswordField();
			newPassword1.setFont(oxygen30);
			newPassword1.setPromptText("Enter your new password");
			newPassword1.setMinSize(300, 50);
			newPassword1.setLayoutX(200);newPassword1.setLayoutY(300);
			settingsGrid.getChildren().add(newPassword1);

			PasswordField newPassword2=new PasswordField();
			newPassword2.setFont(oxygen30);
			newPassword2.setPromptText("Re-enter your password");
			newPassword2.setMinSize(300, 50);
			newPassword2.setLayoutX(200);newPassword2.setLayoutY(400);
			settingsGrid.getChildren().add(newPassword2);

			Button goToBellSchedule=new Button("Edit Bell Schedule");
			goToBellSchedule.setFont(oxygen30);
			goToBellSchedule.setLayoutX(200);goToBellSchedule.setLayoutY(500);
			settingsGrid.getChildren().add(goToBellSchedule);

			Text promptAdminEmail=new Text("Enter your administrator's email address:");
			promptAdminEmail.setFont(oxygen30);
			promptAdminEmail.setX(200);promptAdminEmail.setY(620);
			settingsGrid.getChildren().add(promptAdminEmail);

			TextField enterAdminEmail=new TextField(administrator.getEmail());
			enterAdminEmail.setPromptText("Administrator Email");
			enterAdminEmail.setFont(oxygen30);
			enterAdminEmail.setMinSize(300, 50);
			enterAdminEmail.setLayoutX(200);enterAdminEmail.setLayoutY(650);
			settingsGrid.getChildren().add(enterAdminEmail);
			
			Text promptSem=new Text("Select current semester:");
			promptSem.setFont(oxygen30);
			promptSem.setLayoutX(200); promptSem.setLayoutY(800);
			settingsGrid.getChildren().add(promptSem);

			ObservableList<String> semesters=FXCollections.observableArrayList("Semester 1", "Semester 2");
			ComboBox<String> pickSemester=new ComboBox<String>(semesters);
			if(semester==1){
				pickSemester.setValue(semesters.get(0));
			}else{pickSemester.setValue(semesters.get(1));}
			pickSemester.setLayoutX(200);pickSemester.setLayoutY(830);
			settingsGrid.getChildren().add(pickSemester);

			Text pWordError=new Text("Passwords are mismatched!");
			pWordError.setFont(oxygen30);
			pWordError.setX(200);pWordError.setY(100);
			pWordError.setVisible(false);
			settingsGrid.getChildren().add(pWordError);

			Text adEmailError=new Text("Invalid admin email!");
			adEmailError.setFont(oxygen30);
			adEmailError.setX(200);adEmailError.setY(850);
			adEmailError.setVisible(false);
			settingsGrid.getChildren().add(adEmailError);

			Button goToEmailTemps=new Button("Edit Email Templates");
			goToEmailTemps.setFont(oxygen30);
			goToEmailTemps.setLayoutX(900);goToEmailTemps.setLayoutY(100);
			settingsGrid.getChildren().add(goToEmailTemps);

			File myFiles=new File(".");
			ObservableList<File> files=FXCollections.observableArrayList(myFiles.listFiles());


			Text promptCourse=new Text("Select the student schedule file:");
			promptCourse.setFont(oxygen30);
			promptCourse.setX(900);promptCourse.setY(225);
			settingsGrid.getChildren().add(promptCourse);

			ComboBox<File> pickClassSource=new ComboBox<File>(files);
			try{pickClassSource.setValue(files.get(files.indexOf(new File(courseFile))));}catch(Exception e){e.printStackTrace();}
			pickClassSource.setLayoutX(900);pickClassSource.setLayoutY(250);
			settingsGrid.getChildren().add(pickClassSource);

			Text promptTeacher=new Text("Select the teacher email file:");
			promptTeacher.setFont(oxygen30);
			promptTeacher.setX(900);promptTeacher.setY(375);
			settingsGrid.getChildren().add(promptTeacher);


			ComboBox<File> pickTeacherSource=new ComboBox<File>(files);
			try{pickTeacherSource.setValue(files.get(files.indexOf(new File(teacherFile))));}catch(Exception e){e.printStackTrace();}
			pickTeacherSource.setLayoutX(900);pickTeacherSource.setLayoutY(400);
			settingsGrid.getChildren().add(pickTeacherSource);

			Text promptReason=new Text("Select the visit reasons file:");
			promptReason.setFont(oxygen30);
			promptReason.setX(900);promptReason.setY(525);
			settingsGrid.getChildren().add(promptReason);

			ComboBox<File> pickReasonSource=new ComboBox<File>(files);
			try{pickReasonSource.setValue(files.get(files.indexOf(new File(reasonFile))));}catch(Exception e){e.printStackTrace();}
			pickReasonSource.setLayoutX(900);pickReasonSource.setLayoutY(550);
			settingsGrid.getChildren().add(pickReasonSource);

			Text promptBaseEmail=new Text("Enter the address and password of \n the notification email account:");
			promptBaseEmail.setFont(oxygen30);
			promptBaseEmail.setX(900);promptBaseEmail.setY(700);
			settingsGrid.getChildren().add(promptBaseEmail);

			TextField enterBaseEmail=new TextField(fromEmail);
			enterBaseEmail.setPromptText("Address");
			enterBaseEmail.setFont(oxygen30);
			enterBaseEmail.setMinSize(300, 50);
			enterBaseEmail.setLayoutX(900);enterBaseEmail.setLayoutY(750);
			settingsGrid.getChildren().add(enterBaseEmail);

			PasswordField enterBaseEmailPWord=new PasswordField();
			enterBaseEmailPWord.setPromptText("Password");
			enterBaseEmailPWord.setFont(oxygen30);
			enterBaseEmailPWord.setMinSize(300, 50);
			enterBaseEmailPWord.setLayoutX(900);enterBaseEmailPWord.setLayoutY(850);
			settingsGrid.getChildren().add(enterBaseEmailPWord);

			Text baseError=new Text("Invalid notification address/password");
			baseError.setFont(oxygen30);
			baseError.setX(900);baseError.setY(850);
			baseError.setVisible(false);
			settingsGrid.getChildren().add(baseError);

			Text fileError=new Text("Invalid source file!");
			fileError.setFont(oxygen30);
			fileError.setX(1400);fileError.setY(200);
			fileError.setVisible(false);
			settingsGrid.getChildren().add(fileError);

			Button settingsToEnterID=new Button("Back");
			settingsToEnterID.setFont(oxygen50);
			settingsToEnterID.setLayoutX(1400);settingsToEnterID.setLayoutY(400);
			settingsGrid.getChildren().add(settingsToEnterID);

			Button submitSettings=new Button("Save");
			submitSettings.setFont(oxygen50);
			submitSettings.setLayoutX(1400);submitSettings.setLayoutY(600);
			settingsGrid.getChildren().add(submitSettings);

			settingsGrid.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());

			//Bell Schedule Editor
			Pane bellScheduleGrid=new Pane();
			Scene bellSchedule=new Scene(bellScheduleGrid,s.getWidth(),s.getHeight());

			ArrayList<TextField> hours=new ArrayList<TextField>();
			ArrayList<TextField> minutes=new ArrayList<TextField>();
			ArrayList<Text> colon=new ArrayList<Text>();
			ArrayList<String[]> bellSched=Student.parseBellSchedule();
			for(int i=0;i<8;i++){
				try{
					hours.add(new TextField(bellSched.get(i)[0]));
					minutes.add(new TextField(bellSched.get(i)[1]));
					colon.add(new Text(":"));
				}catch(Exception e){
					e.printStackTrace();
					hours.add(new TextField());
					minutes.add(new TextField());
					colon.add(new Text(":"));
				}
				hours.get(i).setFont(oxygen30);
				hours.get(i).setMinSize(100, 50);hours.get(i).setMaxSize(100, 50);
				hours.get(i).setLayoutX(800);hours.get(i).setLayoutY(200+(i*75));
				colon.get(i).setFont(oxygen30);
				colon.get(i).setLayoutX(950);colon.get(i).setLayoutY(225+(i*75));
				minutes.get(i).setFont(oxygen30);
				minutes.get(i).setMinSize(100, 50);minutes.get(i).setMaxSize(100, 50);
				minutes.get(i).setLayoutX(1000);minutes.get(i).setLayoutY(200+(i*75));
			}
			bellScheduleGrid.getChildren().addAll(colon);
			bellScheduleGrid.getChildren().addAll(hours);
			bellScheduleGrid.getChildren().addAll(minutes);

			Button saveBellSchedule=new Button("Save");
			saveBellSchedule.setFont(oxygen50);
			saveBellSchedule.setLayoutX(880);saveBellSchedule.setLayoutY(900);
			bellScheduleGrid.getChildren().add(saveBellSchedule);
			
			Text bellError=new Text("Please enter an integer from 0-24 or 0-60.");
			bellError.setFont(oxygen30);
			bellError.setX(600); bellError.setY(900);
			bellScheduleGrid.getChildren().add(bellError);
			bellError.setVisible(false);
			
			Text bellInst=new Text("Please enter the times at which each period ends in 24-hour format.\nIf there are not 8 periods, leave extra spaces at the end blank.");
			bellInst.setFont(oxygen30);
			bellInst.setX(600);bellInst.setY(50);
			bellScheduleGrid.getChildren().add(bellInst);
			
			bellScheduleGrid.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());


			//Edit Email Files
			Pane editTemplateGrid=new Pane();
			Scene editTemplate=new Scene(editTemplateGrid, s.getWidth(),s.getHeight());
			Text editTemplateInstructions=new Text("Key: <THEORETICAL>-name of predicted teacher   <TIME>-date and time\n<SIGNEDPASS>-name of selected teacher    <STUDENTNAME>-name of student");
			editTemplateInstructions.setFont(oxygen30);
			editTemplateInstructions.setX(0);editTemplateInstructions.setY(100);
			editTemplateGrid.getChildren().add(editTemplateInstructions);

			Text promptPredicted=new Text("To the predicted teacher if the student does not select him/her:");
			promptPredicted.setFont(oxygen30);
			promptPredicted.setX(0);promptPredicted.setY(200);
			editTemplateGrid.getChildren().add(promptPredicted);

			TextArea predicted=new TextArea(Teacher.fileToString(new File("TheoreticalEmail")));
			predicted.setFont(oxygen30);
			predicted.setMaxSize(800, 300);predicted.setMinSize(800, 300);
			predicted.setWrapText(true);
			predicted.setLayoutX(0);
			predicted.setLayoutY(250);
			editTemplateGrid.getChildren().add(predicted);

			Text promptNormal=new Text("To the predicted teacher if the student selects him/her:");
			promptNormal.setFont(oxygen30);
			promptNormal.setX(0);promptNormal.setY(600);
			editTemplateGrid.getChildren().add(promptNormal);

			TextArea normal=new TextArea(Teacher.fileToString(new File("NormalEmail")));
			normal.setFont(oxygen30);
			normal.setMaxSize(800, 300);normal.setMinSize(800, 300);
			normal.setWrapText(true);
			normal.setLayoutX(0);normal.setLayoutY(650);
			editTemplateGrid.getChildren().add(normal);

			Text promptSignedPass=new Text("To the selected teacher if the student does not select the predicted teacher:");
			promptSignedPass.setFont(oxygen30);
			promptSignedPass.setX(900);promptSignedPass.setY(200);
			editTemplateGrid.getChildren().add(promptSignedPass);

			TextArea signedPass=new TextArea(Teacher.fileToString(new File("SignedPassEmail")));
			signedPass.setFont(oxygen30);
			signedPass.setMaxSize(800, 300);signedPass.setMinSize(800, 300);
			signedPass.setWrapText(true);
			signedPass.setLayoutX(900);signedPass.setLayoutY(250);
			editTemplateGrid.getChildren().add(signedPass);

			Text promptAdmin=new Text("To the administrator:");
			promptAdmin.setFont(oxygen30);
			promptAdmin.setX(900);promptAdmin.setY(600);
			editTemplateGrid.getChildren().add(promptAdmin);

			TextArea admin=new TextArea(Teacher.fileToString(new File("AdminEmail")));
			admin.setFont(oxygen30);
			admin.setMaxSize(800, 300);admin.setMinSize(800, 300);
			admin.setWrapText(true);
			admin.setLayoutX(900);admin.setLayoutY(650);
			editTemplateGrid.getChildren().add(admin);

			Button editTemplateToSettings=new Button("Ok");
			editTemplateToSettings.setFont(oxygen30);
			editTemplateToSettings.setLayoutX(900);editTemplateToSettings.setLayoutY(1000);
			editTemplateGrid.getChildren().add(editTemplateToSettings);
			
			editTemplateGrid.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());

			//Enter Your Student ID Interface Elements
			Pane enterIDGrid=new Pane();
			Scene enterID=new Scene(enterIDGrid, s.getWidth(),s.getHeight());

			Text promptID=new Text("Enter your student ID number below \n or scan your card. Then click \"Next\"");
			promptID.setFont(oxygen50);
			promptID.setX(500);promptID.setY(300);
			enterIDGrid.getChildren().add(promptID);
			promptID.setTextAlignment(TextAlignment.CENTER);

			TextField inID=new TextField();
			inID.setFont(oxygen30);
			inID.setPromptText("Enter your student ID #");
			inID.setLayoutX(500);inID.setLayoutY(600);
			inID.setMinSize(800, 20);
			enterIDGrid.getChildren().add(inID);

			Button enterIDToPickClass=new Button("Next");
			enterIDToPickClass.setFont(oxygen50);
			enterIDToPickClass.setLayoutX(800);enterIDToPickClass.setLayoutY(750);
			enterIDGrid.getChildren().add(enterIDToPickClass);

			Text idError=new Text("Please enter a valid Student ID");
			idError.setFont(oxygen30);
			idError.setVisible(false);
			idError.setX(500);idError.setY(700);
			enterIDGrid.getChildren().add(idError);

			Button terminate=new Button("Close");
			terminate.setLayoutX(1750);terminate.setLayoutY(50);
			terminate.setFont(oxygen30);
			enterIDGrid.getChildren().add(terminate);

			Button enterIDToSettings=new Button("Settings");
			enterIDToSettings.setLayoutX(1550);enterIDToSettings.setLayoutY(50);
			enterIDToSettings.setFont(oxygen30);
			enterIDGrid.getChildren().add(enterIDToSettings);

			enterIDGrid.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());

			
			//Pick your class Elements
			Pane pickClassGrid=new Pane();
			Scene pickClass=new Scene(pickClassGrid, s.getWidth(), s.getHeight());

			Text promptPickClass=new Text();
			promptPickClass.setFont(oxygen50);
			promptPickClass.setX(300);promptPickClass.setY(200);

			Button pickClassToOtherTeacher=new Button("Other");
			pickClassToOtherTeacher.setFont(oxygen50);
			pickClassToOtherTeacher.setLayoutX(1500);pickClassToOtherTeacher.setLayoutY(500);
			pickClassGrid.getChildren().add(pickClassToOtherTeacher);

			Button pickClassToEnterID=new Button("Back");
			pickClassToEnterID.setFont(oxygen50);
			pickClassToEnterID.setLayoutX(100);pickClassToEnterID.setLayoutY(500);
			pickClassGrid.getChildren().add(pickClassToEnterID);

			ObservableList<Class> classes=FXCollections.observableArrayList();
			ListView<Class> classesList=new ListView<Class>(classes);
			classesList.setMaxSize(700, 500);classesList.setMinSize(700, 500);
			classesList.setLayoutX(500);classesList.setLayoutY(300);
			pickClassGrid.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());

			//Other Teacher Elements
			Pane otherTeacherGrid=new Pane();
			Scene otherTeacher=new Scene(otherTeacherGrid, s.getWidth(), s.getHeight());

			Text promptPickTeacher=new Text("Select the teacher you are with:");
			promptPickTeacher.setFont(oxygen50);
			promptPickTeacher.setX(150);promptPickTeacher.setY(100);
			otherTeacherGrid.getChildren().add(promptPickTeacher);

			Button otherTeacherToPickClass=new Button("Back");
			otherTeacherToPickClass.setFont(oxygen50);
			otherTeacherToPickClass.setLayoutX(1100);otherTeacherToPickClass.setLayoutY(500);
			otherTeacherGrid.getChildren().add(otherTeacherToPickClass);

			ObservableList<Teacher> data=FXCollections.observableArrayList();
			ListView<Teacher> listTeacher=new ListView<Teacher>(data);
			listTeacher.setMaxSize(500, 700);listTeacher.setMinSize(500, 700);
			listTeacher.setLayoutX(250);listTeacher.setLayoutY(200);

			data.addAll(teachers);//move to main after parse
			otherTeacherGrid.getChildren().add(listTeacher);

			otherTeacher.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());


			//Reasons Elements
			Pane pickReasonsGrid=new Pane();
			Scene pickReasons=new Scene(pickReasonsGrid, s.getWidth(), s.getHeight());

			Text promptReasons=new Text("Why have you visited?");
			promptReasons.setFont(oxygen50);
			promptReasons.setX(250);promptReasons.setY(100);
			pickReasonsGrid.getChildren().add(promptReasons);

			ObservableList<String> reasonsData=FXCollections.observableArrayList();
			ListView<String> listReasons=new ListView<String>(reasonsData);
			listReasons.setMaxSize(500, 700);listReasons.setMinSize(500, 700);
			reasonsData.addAll(reasons);
			listReasons.setLayoutX(250);listReasons.setLayoutY(200);
			pickReasonsGrid.getChildren().add(listReasons);

			pickReasons.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());

			Button reasonsToOtherTeacher=new Button("Back");
			reasonsToOtherTeacher.setFont(oxygen50);
			reasonsToOtherTeacher.setLayoutX(1125);reasonsToOtherTeacher.setLayoutY(300);
			pickReasonsGrid.getChildren().add(reasonsToOtherTeacher);

			Button submit=new Button("Submit");
			submit.setFont(oxygen50);
			submit.setLayoutX(1100);submit.setLayoutY(500);
			pickReasonsGrid.getChildren().add(submit);
			submit.setDisable(true);

			//Request Admin Password to Close


			Pane adminToCloseGrid=new Pane();
			Scene adminToClose=new Scene(adminToCloseGrid, s.getWidth(), s.getHeight());

			Text promptPassword=new Text("Please enter the password");
			promptPassword.setFont(oxygen50);
			promptPassword.setX(700);promptPassword.setY(200);
			adminToCloseGrid.getChildren().add(promptPassword);

			PasswordField enterPWordToClose=new PasswordField();
			enterPWordToClose.setFont(oxygen30);
			enterPWordToClose.setPromptText("Enter password here");
			enterPWordToClose.setLayoutX(500);enterPWordToClose.setLayoutY(400);
			enterPWordToClose.setMinSize(1000, 100);
			adminToCloseGrid.getChildren().add(enterPWordToClose);

			Button close=new Button("Ok");
			close.setFont(oxygen50);
			close.setLayoutX(1000);close.setLayoutY(700);
			adminToCloseGrid.getChildren().add(close);

			Button cancelClose=new Button("Cancel");
			cancelClose.setFont(oxygen50);
			cancelClose.setLayoutX(700);cancelClose.setLayoutY(700);
			adminToCloseGrid.getChildren().add(cancelClose);

			Text wrongPassword=new Text("Wrong password!");
			wrongPassword.setFont(oxygen30);
			wrongPassword.setX(500);wrongPassword.setY(700);
			adminToCloseGrid.getChildren().add(wrongPassword);
			wrongPassword.setVisible(false);
			
			adminToCloseGrid.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());


			//Thank you!
			Pane thankYouGrid=new Pane();
			Scene thankYou=new Scene(thankYouGrid,s.getWidth(),s.getHeight());
			Text tyTxt=new Text("Thank you!");
			tyTxt.setFont(oxygen50);
			tyTxt.setX(600);tyTxt.setY(500);
			thankYouGrid.getChildren().add(tyTxt);
			
			thankYouGrid.getStylesheets().add(this.getClass().getResource("application.css").toExternalForm());


			//Listeners

			//Enter ID
			enterIDToPickClass.setOnAction(e->{
				try{
					ID=inID.getText();
					System.out.println(ID);
					focus=searchStudent(ID);
					promptPickClass.setText("Hello, "+focus.getFirstName()+". Please pick the class you are in right now.");
					pickClassGrid.getChildren().remove(promptPickClass);
					pickClassGrid.getChildren().add(promptPickClass);
					classes.clear();
					if(semester==1){
						classes.addAll(focus.schedule1);
					}else if(semester==2){classes.addAll(focus.schedule2);}
					pickClassGrid.getChildren().remove(classesList);
					pickClassGrid.getChildren().add(classesList);
					s.setScene(pickClass);
					idError.setVisible(false);
				}catch(Exception d){
					d.printStackTrace();
					idError.setVisible(true);
				}
			});
			inID.setOnAction(e->{
				enterIDToPickClass.fire();
			});
			enterIDToSettings.setOnAction(e->{
				toClose=false;
				s.setScene(adminToClose);
			});
			//Pick Class
			pickClassToOtherTeacher.setOnAction(e->{
				try{listTeacher.getSelectionModel().clearSelection();}catch(Exception d){d.printStackTrace();}
				s.setScene(otherTeacher);
			});
			pickClassToEnterID.setOnAction(e->{
				inID.clear();
				ID="";
				inID.requestFocus();
				idError.setVisible(false);
				s.setScene(enterID);	
			});
			classesList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Class>(){
				public void changed(ObservableValue<? extends Class> arg0, Class old, Class newClass) {
					try{
						notify=newClass.getTeacher();
					}catch(Exception e){e.printStackTrace();}
					s.setScene(pickReasons);
					try{listReasons.getSelectionModel().clearSelection();}catch(Exception e){e.printStackTrace();}
					submit.setDisable(true);
				}
			});

			//Pick Other Teacher
			otherTeacherToPickClass.setOnAction(e->{
				enterIDToPickClass.fire();
			});
			listTeacher.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Teacher>(){
				public void changed(ObservableValue<? extends Teacher> arg0, Teacher old, Teacher newTeach) {
					notify=newTeach;
					s.setScene(pickReasons);
					submit.setDisable(true);
					try{listReasons.getSelectionModel().clearSelection();}catch(Exception e){e.printStackTrace();}
				}
			});

			//Pick Reasons
			reasonsToOtherTeacher.setOnAction(e ->{
				enterIDToPickClass.fire();
			});
			listReasons.setOnKeyPressed(e->{e.consume();});
			listReasons.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>(){
				public void changed(ObservableValue<? extends String> arg0, String old, String newReason) {
					reason=newReason;
					//try{listReasons.getSelectionModel().clearSelection();}catch(Exception e){e.printStackTrace();}
					submit.setDisable(false);
				}
			});
			submit.setOnAction(e->{
				try{
					s.setScene(thankYou);
					theoretical=Student.getTheoreticalTeacher(focus.getSchedule(1));
					System.out.println("Notify: "+notify);
					System.out.println("Theoretical: "+theoretical);
					theoretical=theoretical.findTeacher(teachers);
					notify=notify.findTeacher(teachers);
				}catch(Exception d){d.printStackTrace();}
				if(theoretical!=null){
					if(theoretical.toString().equals(notify.toString())){
						System.out.println("Send normal email to "+notify);
						//					notify.sendEmail(new File("NormalEmail"), theoretical, notify, focus);
					}else{
						System.out.println("Send mismatch case emails to "+notify+" and "+theoretical);
						//					notify.sendEmail(new File("SignedPassEmail"), theoretical, notify, focus);
						//					theoretical.sendEmail(new File("TheoreticalEmail"), theoretical, notify, focus);
					}
				}else{
					System.out.println("Send Normal email to "+notify);
					//		notify.sendEmail(new File("NormalEmail"), theoretical, notify, focus);
				}
				System.out.println("Send admin email");
				
				backUp.println(focus.getFullName()+"\t"+focus.getID()+"\t"+Teacher.dateToString(false)+"\t"+reason);
				//		administrator.sendEmail(new File("AdminEmail"), theoretical, notify, focus);
				inID.requestFocus();
				s.setScene(enterID);
				idError.setVisible(false);
				inID.clear();
			});
			//Close Program
			enterPWordToClose.setOnAction(e->{
				close.fire();
			});
			s.setOnCloseRequest(e->{
				e.consume();
				toClose=true;
				s.setScene(adminToClose);
				enterPWordToClose.clear();
				wrongPassword.setVisible(false);
			});
			close.setOnAction(e->{
				wrongPassword.setVisible(false);
				if(enterPWordToClose.getText().equals(password)){
					enterPWordToClose.clear();
					if(toClose){
						backUp.close();
						try{
							writeToDrive(backUpFile);
							backUpFile.delete();
							Platform.exit();
						}catch(Exception d){d.printStackTrace();}
					}else{s.setScene(settings);}
				}else{
					enterPWordToClose.clear();
					wrongPassword.setVisible(true);
				}
			});
			cancelClose.setOnAction(e->{
				inID.clear();
				enterPWordToClose.clear();
				wrongPassword.setVisible(false);
				s.setScene(enterID);
			});
			terminate.setOnAction(e->{
				wrongPassword.setVisible(false);
				enterPWordToClose.clear();
				toClose=true;
				s.setScene(adminToClose);
			});

			//Settings
			settingsToEnterID.setOnAction(e->{
				newPassword1.clear();
				newPassword2.clear();
				enterAdminEmail.setText(administrator.getEmail());
				pickSemester.getSelectionModel().select(semester-1);
				try{pickClassSource.setValue(files.get(files.indexOf(new File(courseFile))));}catch(Exception d){d.printStackTrace();}
				try{pickTeacherSource.setValue(files.get(files.indexOf(new File(teacherFile))));}catch(Exception d){d.printStackTrace();}
				try{pickReasonSource.setValue(files.get(files.indexOf(new File(reasonFile))));}catch(Exception d){d.printStackTrace();}
				enterBaseEmail.setText(fromEmail);
				enterBaseEmailPWord.clear();
				baseError.setVisible(false);
				fileError.setVisible(false);
				adEmailError.setVisible(false);
				pWordError.setVisible(false);
				s.setScene(enterID);
			});

			submitSettings.setOnAction(e->{
				boolean validSettings=true;
				try {
					PrintWriter configWrite=new PrintWriter("config");
					if(newPassword1.getText().equals("")&&newPassword2.getText().equals("")){
						configWrite.println(password);
					}else{
						if(newPassword1.getText().equals(newPassword2.getText())){
							configWrite.println(newPassword1.getText());
						}else{
							pWordError.setVisible(true);
							validSettings=false;
						}
					}
					if(enterAdminEmail.getText().equals(administrator.getEmail())){
						configWrite.println(administrator.getEmail());
					}else{
						try{
							EmailUtil.sendEmail(enterAdminEmail.getText(), "Test", "This is a test", null);
							administrator=new Teacher("","",enterAdminEmail.getText());
							configWrite.println(enterAdminEmail.getText());
						}catch(Exception d){
							d.printStackTrace();
							adEmailError.setVisible(true);
							validSettings=false;
						}
					}
					if(enterBaseEmail.getText().equals(fromEmail)&&enterBaseEmailPWord.getText().equals("")){
						configWrite.println(fromEmail);
						configWrite.println(fromEmailPassword);
					}else{
						try{
							EmailUtil.setFrom(enterBaseEmail.getText(), enterBaseEmailPWord.getText());
							EmailUtil.sendEmail(enterAdminEmail.getText(), "Test", "This is a test",null);
							configWrite.println(enterBaseEmail.getText());
							configWrite.println(enterBaseEmailPWord.getText());
						}catch(Exception d){
							d.printStackTrace();
							baseError.setVisible(true);
							validSettings=false;
						}
					}
					try{
						configWrite.println(pickSemester.getSelectionModel().getSelectedIndex()+1);
						configWrite.println(courseFile=pickClassSource.getSelectionModel().getSelectedItem().toString());
						configWrite.println(teacherFile=pickTeacherSource.getSelectionModel().getSelectedItem().toString());
						configWrite.println(reasonFile=pickReasonSource.getSelectionModel().getSelectedItem().toString());
					}catch(Exception d){
						d.printStackTrace();
						fileError.setVisible(true);
					}
					if(validSettings){
						baseError.setVisible(false);
						fileError.setVisible(false);
						adEmailError.setVisible(false);
						pWordError.setVisible(false);
						configWrite.close();
						setConfig();
						newPassword1.clear();
						newPassword2.clear();
						pickClassToEnterID.fire();
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
			});
			goToBellSchedule.setOnAction(e->{
				s.setScene(bellSchedule);
			});
			goToEmailTemps.setOnAction(e->{
				s.setScene(editTemplate);
			});

			//Bell Schedule	
			saveBellSchedule.setOnAction(e->{
				bellError.setVisible(false);
				boolean validSchedule=true;
				PrintWriter pwBell=null;
				try {
					pwBell=new PrintWriter(new File("BellSchedule"));
				} catch (FileNotFoundException e1) {e1.printStackTrace();}
				for(int i=0;i<8;i++){
					if(hours.get(i).getText().equals("")&&minutes.get(i).getText().equals("")){
						pwBell.println();
					}else{
						try{
							if(!(Integer.parseInt(hours.get(i).getText())<=24&&Integer.parseInt(hours.get(i).getText())>=0)&&Integer.parseInt(minutes.get(i).getText())<=60&&Integer.parseInt(minutes.get(i).getText())>=0){throw new NumberFormatException();}
							pwBell.println(hours.get(i).getText()+":"+minutes.get(i).getText());
						}catch(Exception d){
							bellError.setVisible(true);
							d.printStackTrace();
							validSchedule=false;
						}
					}
				}
				if(validSchedule){
					pwBell.close();
					s.setScene(settings);
				}
			});

			//Email Templates
			editTemplateToSettings.setOnAction(e->{
				PrintWriter printTemps=null;
				try {
					printTemps=new PrintWriter(new File("NormalEmail"));
					printTemps.println(normal.getText());
					printTemps.close();
					printTemps=new PrintWriter(new File("AdminEmail"));
					printTemps.println(admin.getText());
					printTemps.close();
					printTemps=new PrintWriter(new File("SignedPassEmail"));
					printTemps.println(signedPass.getText());
					printTemps.close();
					printTemps=new PrintWriter(new File("TheoreticalEmail"));
					printTemps.println(predicted.getText());
					printTemps.close();
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				}
				s.setScene(settings);
			});


			if(validInit){
				s.setScene(enterID);
			}else{
				s.setScene(settings);
			}

		}catch(Exception e) {
			e.printStackTrace();
			s.setScene(settings);
		}
	}
	public static Teacher getAdmin(){
		return administrator;
	}
	public static void parseData(String file){
		students=new ArrayList<Student>();
		Scanner s = null;
		String id="";
		String [] quals;
		String in="";
		try {
			s=new Scanner(new File(file));
			s.nextLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		in=s.nextLine();
		quals=in.substring(1,in.length()-1).split("\",\"");
		while(s.hasNextLine()){
			students.add(new Student(quals));
			id=quals[3];
			students.get(students.size()-1).addClass(quals);
			in=s.nextLine();
			quals=in.substring(1,in.length()-1).split("\",\"");
			do{
				students.get(students.size()-1).addClass(quals);
				in=s.nextLine();
				quals=in.substring(1,in.length()-1).split("\",\"");
			}while(id.equals(quals[3])&&s.hasNextLine());
			students.get(students.size()-1).sortClass();
		}
	}
	public Student searchStudent(String findThisID){
		for(Student s: students){
			if(s.getID().equals(findThisID)){
				return s;
			}
		}
		return null;
	}
	public static void writeToDrive(File f) throws IOException, MessagingException {
		EmailUtil.sendEmail(fromEmail, "MediaCenterLogData", "",f);
		
		URL url = new URL("https://script.google.com/macros/s/AKfycby4tgnSC6H6HE6QN9u-tcX7w9bn9-SB4gjmp460RBYOOzCGcM4/exec");
		HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
		httpCon.setDoOutput(true);
		httpCon.setRequestMethod("GET");
		OutputStreamWriter out = new OutputStreamWriter(
				httpCon.getOutputStream());
		System.out.println(httpCon.getResponseCode());
		System.out.println(httpCon.getResponseMessage());
		out.close();
	}
	public static void main(String[] args) {
		setConfig();
		launch(args);
	}
}