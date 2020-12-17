# bruno-solutions-flat-file-to-excel-power-query-m-script-generator
Given a column description create a Power Query / M script to parse a flat file into Excel columns

Presently this command line Java application was created to meet a "quick-and-dirty" need. The poor design of this application is to the point that you should adjust the following crudities for your utilization:

   - The field description input file must be named "fields.csv" and must reside at the root of the "s:" drive.
   - The Power Query M script file that is output will be created in the current directory and will be named "tax2000.pq".
   - The Power Query M script will expect the input file to be parsed to be named "data.txt".
   - When run by Excel, Excel complains that the Power Query M script input file is not a fully qualified path.
   
## To use the Power Query M script that is output by the generator application in Excel 365:

	- Open Excel
	- Create a `Blank workbook`
	- Select the `Data` from the Excel ribbon menu
	- Click `From Text/CSV`
	- Select the fixed width file to parse
	- Click `Load` (I know this seems strange, but it's Microsoft's design not mine)
	- Under `Queries & Connections` click the query that just attempted to load your flat file
		- The file should be imported as a single (possibly very wide) column
	- From the `Power Query Editor` ribbon menu select `Advanced Editor`
	- Using an outside editor copy your generated Power Query M script (e.g. "tax2000.pq")
	- In the `Power Query Advanced Editor` paste your generated Power Query M script
	- Copy the file reference within the `File.Contents` function
	- Paste the file reference into the `File.Contents` function of your script
	- Remove the Excel default (single column parse) Power Query M script such that the `let` command of your script is the first line
		- You should see `No syntax errors have been detected` under the `Advanced Editor` text box
	- Click `Done`
		- The input flat file will parse into columns
		- Review the results and if there are problems fix the generated Power Query M script
			- Either directly through the `Power Query Advanced Editor`
			- Or by altering the field description file provided to the Java application
			- Or by altering the Java application
	- Click `Close & Load` from the `Power Query Editor` ribbon menu
		- Your data should render within an Excel worksheet

## To do items:

	- Fix the last column so it is named properly instead of `Column180` (or numbered according to the number of columns in your field description file)
