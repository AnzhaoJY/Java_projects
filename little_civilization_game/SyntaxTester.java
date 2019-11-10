import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;

public class SyntaxTester {
    private static void testFnNames(String className, ArrayList<String> correctMethods, ArrayList<String> correctConstructors) {
        if (classExists(className)) // Proceed only if the class exists
        {
            Class theClass = null;
            try {
                theClass = Class.forName(className); // get the class e.g. Tile or Worker

            }catch (Exception e)
            {
                System.out.println("Not sure what's going on!"); // We shouldn't be here
                return;
            }

            Method[] studentMethods = theClass.getDeclaredMethods(); // get the public methods
            Constructor[] studentConstr = theClass.getConstructors(); // get the constructor methods

            ArrayList<String> s_studentMethods = new ArrayList< String >();
            ArrayList< String > s_studentConstr = new ArrayList< String >();

            for( Method method : studentMethods ) s_studentMethods.add ( method.toString() );
            for( Constructor constr : studentConstr ) s_studentConstr.add ( constr.toString() );
            
            removeStaticKeyword( s_studentMethods );

            evaluate(correctConstructors, s_studentConstr, true, className);
            evaluate(correctMethods, s_studentMethods, false, className);

        }
        else
            System.out.println("Class " + className + " does not exist.");
    }
    
    private static void testEquals() 
    {
    	String []classList = {"Settler", "Worker", "Archer", "Warrior"};
    	
    	// Check if all required classes exist
    	boolean allRequiredClassesExist = true;
    	for (String s : classList) allRequiredClassesExist &= classExists(s);
    	allRequiredClassesExist &= classExists("Tile");
    	allRequiredClassesExist &= classExists("Unit");
    	
    	if (!allRequiredClassesExist) 
    	{
    		System.out.println("Equals checking cannot proceed. Please implement all the classes first.");
    		return;
    	}
    	
    	Class _Tile = null;
    	Class []_Units = new Class[4]; // Class Settler, Worker, Archer, Warrior
    	
    	try {
            _Tile = Class.forName("Tile"); // get the classes
            for (int i = 0; i < 4; i++)
            	_Units[i] = Class.forName(classList[i]);
           
        }catch (Exception e)
        {
            System.out.println("Not sure what's going on!"); // We shouldn't be here
            return;
        }
    	
    	Constructor tileConstr = null; // Declared constructor for Tile
    	Constructor []unitConstrs = new Constructor[4]; // Declared constructor for Settler, Worker, Archer, Warrior
    	
    	try {
    		tileConstr = _Tile.getDeclaredConstructor(int.class, int.class);
    		for (int i = 0; i < 4; i++)
    			unitConstrs[i] = _Units[i].getDeclaredConstructor(_Tile, double.class, String.class);
    	} catch(Exception e) {
    		System.out.println("Contructor not found. Ensure you pass all the previous tests.");
    		return;
    	}
    	
    	Object t = null; // Instance of Tile object
    	Object []u = new Object[4]; // Instance of Settler, Worker, Archer, Warrior
    	String faction = "Gentle Gorilla";
    	try {
    		t = tileConstr.newInstance(1,2);
    		for (int i = 0; i < 4; i++)
    			u[i] = unitConstrs[i].newInstance(t, 2.0, faction);
    	}
    	catch (InvocationTargetException e) {
    		System.out.println("Equals checking cannot proceed. One of your constructors (Tile, Settler, Archer, Worker, Warrior or their base classes) have thrown an exception.");
    		return;
    	}
    	catch (Exception e) {
    		System.out.println("Failed to create required objects.");
    		return;
    	}
    	
    	for (int i = 0; i < 4; i++)
    		for (int j = 0; j < 4; j++) {
    			boolean correctAnswer = (i == j);
    			String errString = "Check equals() for " + classList[i] + ". Getting " + classList[i] + " is " + (!correctAnswer ? "" : "not ") + classList[j];
    			try {
    				if (u[i].equals(u[j]) != correctAnswer) {
    					System.out.println(errString);
    					return;
    				}
    			} catch (ClassCastException e) {
    				System.out.println ("Check equals() for " + classList[i] + ". Getting exception :" + e.getMessage());
    				return;
    			}
    		}
    	
    	System.out.println("Equals - instanceOf test passed.");

//    	CHECK FOR STRING EQUALS
        Object []u1 = new Object[4]; // Instance of Settler, Worker, Archer, Warrior
        Object []u2 = new Object[4];
        String factionValue1 = new String("TenderDragon1");
        String factionValue2 = new String("TenderDragon1");
        try {
            t = tileConstr.newInstance(1,2);
            for (int i = 0; i < 4; i++) {
                u1[i] = unitConstrs[i].newInstance(t, 2.0, factionValue1);
                u2[i] = unitConstrs[i].newInstance(t, 2.0, factionValue2);
            }
        }
        catch (InvocationTargetException e) {
    		System.out.println("Equals checking cannot proceed. One of your constructors (Tile, Settler, Archer, Worker, Warrior or their base classes) have thrown an exception.");
    		return;
    	}
        catch (Exception e) {
            System.out.println("Failed to create required objects.");
            return;
        }

        for (int i = 0; i < 4; i++) {
            try {
                if (u1[i].equals(u2[i]) != true) {
                    System.out.println("Wrong implementation for checking string equality in class " + classList[i]);
                    return;
                }
            } catch (ClassCastException e) {
                System.out.println ("Check equals() for " + classList[i] + ". Getting exception :" + e.getMessage());
                return;
            }
        }

        System.out.println("Equals - String equality test passed.");
        
        //CHECK FOR ARROW AND JOB EQUALS
        Object worker1 = u1[1];
        Object worker2 = u2[1];
        Object archer1 = u1[2];
        Object archer2 = u2[2];
        
        Field worker2jobs = null;
        Field archer2arrows = null;

        Field []workerf = worker2.getClass().getDeclaredFields();
        Field []archerf = archer2.getClass().getDeclaredFields();
        	
        if (workerf == null || archerf == null || workerf.length != 1 || archerf.length != 1) {
        	System.out.println("Equals checking cannot proceed. Class Worker or Archer not as per specification. Incorrect fields.");
        	return;
        }
        	
        if (workerf[0].getType().equals(int.class) && archerf[0].getType().equals(int.class)) {
        	worker2jobs = workerf[0];
        	archer2arrows = archerf[0];
        }
        else {
        	System.out.println("Equals checking cannot proceed. Class Worker or Archer not as per specification. Incorrect fields.");
        	return;
        }
                
        worker2jobs.setAccessible(true);
        archer2arrows.setAccessible(true);
        try {
        	worker2jobs.set(worker2, 100);
        	archer2arrows.set(archer2, 100);
        	
        } catch(IllegalAccessException e) {
        	System.out.println("Equals checking cannot proceed. Cannot access field jobs or arrows.");
        	return;
        }
        
        if (worker1.equals(worker2)) {
        	System.out.println("Wrong implementation for checking equality in class Worker.");
        	return;
        }
        
        if (archer1.equals(archer2)) {
        	System.out.println("Wrong implementation for checking equality in class Archer.");
        	return;
        }
        
        System.out.println("Equals - All equality test passed.");
    }


    private static void evaluate(ArrayList<String> correct, ArrayList<String> student, boolean constr, String className) {
        boolean match = true; //if everything passes

        for (String correctString : correct) //we only care about testable methods
        {
            if (!student.contains(correctString)) //signature cannot be found
            {
                if (constr) {
                    System.out.println("Error with constructor: " + correctString + " in class: " + className);
                    System.out.println("Constructor cannot be found or has wrong signature.\n");
                } else {
                    System.out.println("Error with method: " + correctString + " in class: " + className);
                    System.out.println("Method cannot be found or has wrong signature.\n");
                }

                match = false;
            }
        }
        if (match) {
            if (constr) System.out.println("Constructor has been found in class " + className + "!");
            else System.out.println("All methods have been found in class " + className + "!\n");
        }
    }
    private static boolean classExists(String className) {
        try {
            Class.forName( className );
            return true;
        }
//        catch(ClassNotFoundException e) { return false; }
//        catch( NoClassDefFoundError e ){ return false; }
        catch( Exception e ){ return false; }
    }

    private static void removeStaticKeyword( ArrayList< String > arr )
    {
        for( int i = 0; i < arr.size(); i++ )
        {
            String[] tokens = arr.get( i ).split( " " ); //tokenize based on space delimiter

            String answer = ""; //build the answer from empty string

            for( int j = 0; j < tokens.length; j++ )
            {
                if( !tokens[ j ].equals( "static" ) ) answer += ( tokens[ j ] + ( j == tokens.length - 1 ? "" : " " ) ); //also add spaces
            }
            arr.remove( i ); //replace the string in place
            arr.add( i,answer );
            //System.out.println( Arrays.toString( tokens ) );
        }
    }

    public static void main(String[] args)
    {
    	// UNIT
        ArrayList<String> correctFnNamesUnit = new ArrayList<String>( Arrays.asList(
                "public final Tile Unit.getPosition()",
                "public final double Unit.getHP()",
                "public final java.lang.String Unit.getFaction()",
                "public boolean Unit.moveTo(Tile)",
                "public void Unit.receiveDamage(double)",
                "public abstract void Unit.takeAction(Tile)",
                "public boolean Unit.equals(java.lang.Object)"
                ) );
        ArrayList<String> correctConstNamesUnit = new ArrayList<String>(Arrays.asList( "public Unit(Tile,double,int,java.lang.String)" ));
        testFnNames("Unit", correctFnNamesUnit, correctConstNamesUnit);
        
      //SETTLER
        ArrayList<String> correctFnNamesSettler = new ArrayList<String>( Arrays.asList(
                "public void Settler.takeAction(Tile)",
                "public boolean Settler.equals(java.lang.Object)") );
        ArrayList<String> correctConstNamesSettler = new ArrayList<String>(Arrays.asList( "public Settler(Tile,double,java.lang.String)" ));
        testFnNames("Settler", correctFnNamesSettler, correctConstNamesSettler);
        
        // WORKER
        ArrayList<String> correctFnNamesWorker = new ArrayList<String>( Arrays.asList(
                "public void Worker.takeAction(Tile)",
                "public boolean Worker.equals(java.lang.Object)") );
        ArrayList<String> correctConstNamesWorker = new ArrayList<String>(Arrays.asList( "public Worker(Tile,double,java.lang.String)" ));
        testFnNames("Worker", correctFnNamesWorker, correctConstNamesWorker);
        
        // MILITARYUNIT
        ArrayList<String> correctFnNamesMilitaryUnit = new ArrayList<String>( Arrays.asList(
                "public void MilitaryUnit.takeAction(Tile)",
                "public void MilitaryUnit.receiveDamage(double)") ); //might need to change all recieveDamage to doubles now? Unsure atm
        ArrayList<String> correctConstNamesMilitaryUnit = new ArrayList<String>(Arrays.asList( "public MilitaryUnit(Tile,double,int,java.lang.String,double,int,int)" ));
        testFnNames("MilitaryUnit", correctFnNamesMilitaryUnit, correctConstNamesMilitaryUnit);

        //WARRIOR
        ArrayList<String> correctFnNamesWarrior = new ArrayList<String>( Arrays.asList(
                "public boolean Warrior.equals(java.lang.Object)") );
        ArrayList<String> correctConstNamesWarrior = new ArrayList<String>(Arrays.asList( "public Warrior(Tile,double,java.lang.String)" ));
        testFnNames("Warrior", correctFnNamesWarrior, correctConstNamesWarrior);
        
        // ARCHER
        ArrayList<String> correctFnNamesArcher = new ArrayList<String>( Arrays.asList(
                "public void Archer.takeAction(Tile)",
                "public boolean Archer.equals(java.lang.Object)") );
        ArrayList<String> correctConstNamesArcher = new ArrayList<String>(Arrays.asList( "public Archer(Tile,double,java.lang.String)" ));
        testFnNames("Archer", correctFnNamesArcher, correctConstNamesArcher);
        
        // LISTOFUNITS
        ArrayList<String> correctFnNamesLoU = new ArrayList<String>( Arrays.asList(
        		"public void ListOfUnits.add(Unit)",
                "public Unit ListOfUnits.get(int)",
                "public boolean ListOfUnits.remove(Unit)",
                "public int ListOfUnits.indexOf(Unit)",
                "public int ListOfUnits.size()",
                "public Unit[] ListOfUnits.getUnits()",
                "public MilitaryUnit[] ListOfUnits.getArmy()") );
        ArrayList<String> correctConstNamesLoU = new ArrayList<String>(Arrays.asList( "public ListOfUnits()" ));
        testFnNames("ListOfUnits", correctFnNamesLoU, correctConstNamesLoU);
        
        // TILE
        ArrayList<String> correctFnNamesTile = new ArrayList<String>( Arrays.asList(
        		"public boolean Tile.isImproved()",
                "public double Tile.getDistance(Tile,Tile)",
                "public int Tile.getY()",
                "public int Tile.getX()",
                "public void Tile.foundCity()",
//                "public Unit[] Tile.getUnit()", //not in the assignment?
                "public Unit Tile.selectWeakEnemy(java.lang.String)",
                "public boolean Tile.removeUnit(Unit)",
                "public boolean Tile.addUnit(Unit)",
                "public boolean Tile.isCity()",
                "public void Tile.buildImprovement()") );
        ArrayList<String> correctConstNamesTile = new ArrayList<String>(Arrays.asList( "public Tile(int,int)" ));
        testFnNames("Tile", correctFnNamesTile, correctConstNamesTile);
                
        testEquals();
    }
}