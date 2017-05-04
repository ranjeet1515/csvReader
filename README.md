# CsvReader
I have developed the csv reader program using producer consumer desigen pattern.

Please find the project structure details as below.

csv_reader has to main packages  
1. Main
2. test
 
 Main : In Main package contains business logic 
  There is two class in this package
  1. CSVMain
  2. CSVReaderFactory
  
  CSVMain : This classs is use for start the execution of program.
  CSVReaderFactory: This class is Single ton class In this ,
   I have created thread which use as producer and consumer.
   For more details please check class.
   

 test : test package conatins JUnit test cases.
  CSVReaderFactoryUnitTest: This calss is use for test CSVReaderFactory calsss.
  
  
# How to Execute Program.
you can download Jar file and execute on terminal

run below command in current directory 

java -jar csv_reader.jar "Enter csv directory path"
  
   
   
# To stop the program
 Press ctrl +c to cancel this program.
 
