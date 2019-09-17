##Example Java scripts to use Algorand with the ISDA CDM


#Take a file with allocation instructions and convert it into a CDM file
mvn -s settings.xml exec:java -Dexec.mainClass="com.algorand.demo.DerivhackDemo" \
 -Dexec.args="./Files/input_allocations.json"  -l "./Files/output_allocations.json" -q  | grep -v WARNING


#Compute the hash of a CDM JSON file and commit that hash to the blockchain
 mvn -s settings.xml exec:java -Dexec.mainClass="com.algorand.demo.HashTransaction"\
 -Dexec.args="./Files/output_allocations.json" -e -l  "./Files/transaction_id.txt" -q | grep -v WARNING

#Verify that the hash of a CDM JSON file
mvn -s settings.xml exec:java -Dexec.mainClass="com.algorand.demo.VerifyTransaction"\
 -Dexec.args="./Files/output_allocations.json ./Files/transaction_id.txt" -e  -q | grep -v WARNING