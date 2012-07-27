package mx.uabc.lcc.teikoku.error;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import de.irf.it.rmg.core.teikoku.job.Job;

public class ErrorParser {
    // instance variables - replace the example below with your ow
    File archivo;
    Set<Error> errorSet;
    int contErr;

    /**
     * Constructor for objects of class Parser
     */
    public ErrorParser()
    {
        // initialise instance variables
      String path="C:/Users/Aritz/Documents/Escuela/Tesis/TGSF/errorloads/test1.txt";
//        String path="C:/Users/Aritz/Documents/Escuela/Tesis/TGSF/errorloads/test2.txt";
//      String path="C:/Users/Aritz/Documents/Escuela/Tesis/TGSF/errorloads/noErrors.txt";
//        String path="C:/Users/Aritz/Documents/Escuela/Tesis/TGSF/errorloads/falloTotal.txt";
//        String path="C:/Users/Aritz/Documents/Escuela/Tesis/TGSF/errorloads/falloTotal2.txt";
        contErr=0;
        archivo= new File(path);
        errorSet=this.makeErrorSet();
    }
    public ErrorParser(String path)
    {
        // initialise instance variables
        contErr=0;
        archivo= new File(path);
        errorSet=this.makeErrorSet();
    }
    public Set<Error> getErrorSet()
    {
    	return errorSet;
    }
    private Set<Error> makeErrorSet()
    {

    	StringBuilder contents = new StringBuilder();
    	Set<Error> eSet = new HashSet();
    try {
    		BufferedReader input =  new BufferedReader(new FileReader(archivo));
    		try {
    			String line = null;
    			while (( line = input.readLine()) != null){
    				Error e = this.makeErrorFromLine(line);
    				eSet.add(e);
    			}
    			System.out.println("Error Set Done");
    		}
    		finally {
    			input.close();
    		}
    	}
    catch (IOException ex){
    	ex.printStackTrace();
    }
    return eSet;
    }
    
    private Error makeErrorFromLine(String line)
    {
        //
        String token="";
        int contador=0;
        Integer type=null;
        Long time=null;
        String name=null;
        for (int c=0;c<line.length();c++)
        {
            String actual=((Character)line.charAt(c)).toString();
            if(actual.matches(" ")||actual.matches("\n")||actual.matches("\t")||actual.matches("\r")||actual.matches(""))
            {
                //
                //System.out.println("El token es = "+token);
                if(!token.matches(""))
                {
                	switch (contador)
                	{
                		case 0:
                			type = new Integer(token);
                			contador++;
                			break;
                		case 1:
                			time = new Long(token);
                			contador++;
                			break;
                		case 2:
                			name=token;
                			break;
                		default:
                			System.out.println("Error on the ErrorLoad");
                			break;	
                	}
                }
                token="";
            }
            else
            {
                //System.out.println("Actual = "+actual);
                token=token.concat(actual);
            }
        }
        name=token;
        return new Error(type,time,name);
    }    
}
