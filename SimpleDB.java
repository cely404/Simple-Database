import java.util.*;
/**
 * SimpleDB class that takes input from standard input and 
 * prints output to standard output. 
 * USAGE: 
 * SET name value
 * GET name
 * UNSET name
 * NUMEQUALTO value
 * END
 * BEGIN
 * ROLLBACK
 * COMMIT 
 */
public class SimpleDB{
	//map will hold the name and values of inputs 
	HashMap<String, Integer> map; 
	//valueCounter will hold the value and the number of values equal to it
	HashMap<Integer, Integer> valueCounter;
	//the cache holds the values before any updates occured in a begin block
	ArrayList<ArrayList<Object[]>> cache;

	boolean transactionInProgress;

	public SimpleDB(){
		map = new HashMap<String, Integer>();
		valueCounter = new HashMap<Integer, Integer>();
		cache = new ArrayList<ArrayList<Object[]>>();
		transactionInProgress = false; 
	}
	public static void main(String[] args){

		Scanner scanner = new Scanner(System.in);
		SimpleDB db = new SimpleDB();
		while(scanner.hasNext()){
			String input = scanner.nextLine();
			input.trim();
			db.parse(input);
		}
		System.exit(0);
	}

	/**
	* Parse function takes in a string and delegates a call to the
	* appropriate function 
	*/
	private void parse(String input){
		String[] parsedString = input.split("\\s+");
		//ensure case is not an issue when comparing strings 
		parsedString[0].toUpperCase();

		if(parsedString[0].equals("ROLLBACK")){
			rollback();
			return;
		}

		try{
			switch(parsedString[0]){
				case "BEGIN":
				begin();
				break;
				case "COMMIT":
				commit();
				break;
				case "END":
				end();
				break;
				case "SET":
				//if inside a begin block, add to cache 
				if(transactionInProgress){
					addToCache(parsedString[1]);
				}
				set(parsedString[1], Integer.parseInt(parsedString[2]));
				break;
				case "GET":
				get(parsedString[1]);
				break;
				case "UNSET":
				//if inside a begin block, add to cache 
				if(transactionInProgress){
					addToCache(parsedString[1]);
				}
				unset(parsedString[1]);
				break;
				case "NUMEQUALTO":
				numEqualTo(Integer.parseInt(parsedString[1]));
				break;
				default:
				System.out.println("Entered something invalid");
			}
		}
		catch(Exception e){
			System.out.println("Invalid input format, please try again");
		}
	}

	/*
	* Sets the variable name to the value value. 
	* Updates the data structures map and valueCounter 
	* based off of new values  
	*/
	private void set(String name, int value){
		Integer previousMapValue = map.put(name, value); 
		if(previousMapValue != null && previousMapValue != value){
			Integer previousCounterValue = valueCounter.get(previousMapValue);
			valueCounter.put(previousMapValue, previousCounterValue-1);
		}
		Integer numEqualTo = valueCounter.get(value);
		if(numEqualTo != null){
			valueCounter.put(value,numEqualTo+1);
		}
		else{
			valueCounter.put(value, 1);
		}
	}

	/**
	* Print out the value of the variable name, 
	* or NULL if that variable is not set. Returns the value 
	*/
	private Integer get(String name){
		Integer value = map.get(name);
		String output = (value == null) ? "NULL" : String.valueOf(value);
		System.out.println(output);
		return value;
	}

    /**
    * Unset the variable name, making it just like that variable was never set.
    */
	private void unset(String name){
		Integer value = map.remove(name);
		if(value == null){
			return;
		}
		Integer numEqualTo = valueCounter.get(value);
		if(numEqualTo != null){
			valueCounter.put(value,numEqualTo-1);
		}
	}
	/**
	* Print out the number of variables that are currently set to value.
	* If no variables equal that value, print 0. Returns the number of variables
	*/
	private Integer numEqualTo(int value){
		Integer numEqualTo = valueCounter.get(value);
		int output = (numEqualTo == null) ? 0 : numEqualTo;
		System.out.println(output);
		return numEqualTo;
	}

	/**
	* Open a new transaction block 
	*/
	private void begin(){
		ArrayList<Object[]> newList = new ArrayList<Object[]>();
		cache.add(newList);
		transactionInProgress = true;
	}

	/**
	* Reverts variables in cache to state at the beginning of the begin block
	*/
	private void rollback(){
		if(cache.isEmpty()){
			System.out.println("NO TRANSACTION");
		}
		else{
			//grab the last begin block or arraylist 
			ArrayList<Object[]> beginBlock = cache.remove(cache.size()-1);
			//restore to previous values 
			for(Object[] rbItem: beginBlock){
				map.put((String)rbItem[0], (Integer)rbItem[1]);
				valueCounter.put((Integer)rbItem[1],(Integer)rbItem[2]);
			}
			//if that was the last beginBlock set boolean to false 
			if(cache.size() == 0){
				transactionInProgress = false;
			}
		}

	}

	/**
	* Helper function takes in the name of the variable to be kept track of
	* The cache will not contain duplicates and will only keep track of
	* the state of the variable at the beginning of each begin block 
	*/
	private void addToCache(String name){
		ArrayList<Object[]> lastBeginBlock = cache.get(cache.size()-1);
		for(Object[] array : lastBeginBlock){
			if(((String)array[0]).equals(name)){
				return;
			}
		}
		Integer value = map.get(name);
		Object[] newCachedItem = new Object[]{name,value, valueCounter.get(value)};
		lastBeginBlock.add(newCachedItem);
	}

	/**
	* Closes all open transaction blocks and commits all changes 
	*/
	private void commit(){
		transactionInProgress = false;
		if(cache.isEmpty()){
			return;
		}
		cache.clear();
	}
	/**
	* Exit the program, clean up 
	*/
	private void end(){
		map = null; 
		valueCounter=null;
		cache=null; 
		System.exit(0);
	}

}