package com.whitecache;

import java.io.*;
import java.util.*;

public class Main {
    private static class ColumnDescription {
        String fieldName;
        Number startingPosition;
        Number size;
        String type;
    }

    public static final String T = "Table";
    public static final String S = "Split";
    public static final String C = "Column";

    public static void main(String[] args) throws IOException {
        List<ColumnDescription> columnDescriptions = new ArrayList<ColumnDescription>();

        BufferedReader fieldsFile = new BufferedReader(new FileReader("s:\\fields.csv"));

        String line;

        int index = 0;

        System.out.println();
        System.out.println(line = fieldsFile.readLine()); // Display the header line
        System.out.println();

        while (null != (line = fieldsFile.readLine())) {
            ArrayList<String> columnDescriptionElements = new ArrayList<>(Arrays.asList(line.split(",")));
            ColumnDescription columnDescription = new ColumnDescription();

            columnDescription.fieldName = columnDescriptionElements.get(0);
            columnDescription.startingPosition = Integer.parseInt(columnDescriptionElements.get(1));
            columnDescription.size = Integer.parseInt(columnDescriptionElements.get(2));
            columnDescription.type = columnDescriptionElements.get(3).equals("N") ? "type number" : "type text";

            columnDescriptions.add(columnDescription);

            System.out.println("index: " + ++index);
            System.out.println("field name: " + columnDescription.fieldName);
            System.out.println("starts: " + columnDescription.startingPosition);
            System.out.println("size: " + columnDescription.size);
            System.out.println("type: " + columnDescription.type);
            System.out.println();
        }

        fieldsFile.close();

        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();

        // https://docs.microsoft.com/en-us/powerquery-m/lines-frombinary
        // Lines.FromBinary(binary as binary, optional quoteStyle as nullable number, optional includeLineSeparators as nullable logical, optional encoding as nullable number) as list

        // https://docs.microsoft.com/en-us/powerquery-m/table-fromcolumns
        // Table.FromColumns(lists as list, optional columns as any) as table

        // Table.SplitColumn(table as table, sourceColumn as text, splitter as function, optional columnNamesOrNumber as any, optional default as any, optional extraColumns as any) as table
        // https://docs.microsoft.com/en-us/powerquery-m/table-splitcolumn

        // Table.TransformColumnTypes(table as table, typeTransformations as list, optional culture as nullable text) as table
        // https://docs.microsoft.com/en-us/powerquery-m/table-transformcolumntypes

        // Source = Table.FromColumns({Lines.FromBinary(File.Contents("C:\Users\John Hart\Desktop\SLS_September_ReturnsExport.txt"), null, null, 1252)}),
        // #"Split Column by Position" = Table.SplitColumn(Source, "Column1", Splitter.SplitTextByPositions({0, 60}, false), {"Column1.1", "Column1.2"}),
        // #"Changed Type" = Table.TransformColumnTypes(#"Split Column by Position",{{"Column1.1", type text}, {"Column1.2", type text}}),
        // #"Split Column by Position1" = Table.SplitColumn(#"Changed Type", "Column1.2", Splitter.SplitTextByPositions({0, 60}, false), {"Column1.2.1", "Column1.2.2"}),
        // #"Changed Type1" = Table.TransformColumnTypes(#"Split Column by Position1",{{"Column1.2.1", type text}, {"Column1.2.2", type text}}),

        // becomes

        // #"Table1" = Table.FromColumns({Lines.FromBinary(File.Contents("C:\Users\John Hart\Desktop\SLS_September_ReturnsExport.txt"), null, null, 1252)}),
        // #"Split1" = Table.SplitColumn(#"Table1", "Column1", Splitter.SplitTextByPositions({0, columnDescription.size}, false), {columnDescription.fieldName, "Column2"}),
        // #"Table2" = Table.TransformColumnTypes(#"Split1", {{columnDescription.fieldName, columnDescription.type}, {"Column2", type text}}),
        // #"Split2" = Table.SplitColumn(#"Table2", "Column2", Splitter.SplitTextByPositions({0, columnDescription.size}, false), {columnDescription.fieldName, "Column3"}),
        // #"Table3" = Table.TransformColumnTypes(#"Split2", {{columnDescription.fieldName, columnDescription.type}, {"Column3", type text}}),

        {
            ColumnDescription columnDescription;
            String currentTableName;
            String nextTableName;
            String splitterName;
            String currentColumnName;
            String nextColumnName;

            String templateLine;

            currentTableName = T + "1";
            nextTableName = "";

            // Create a Power Query M Language file for Excel to use to parse the O Series Returns Export (Tax 2000) file

            BufferedWriter templateFile = new BufferedWriter(new FileWriter("tax2000.pq"));

            templateLine = "let";
            System.out.println(templateLine);
            templateFile.write(templateLine);
            templateFile.newLine();

            templateLine = "    #\"" + currentTableName + "\" = Table.FromColumns({Lines.FromBinary(File.Contents(\"data.txt\"), null, null, 1252)}),";
            System.out.println(templateLine);
            templateFile.write(templateLine);
            templateFile.newLine();

            for (int columnIndex = 1; columnIndex < columnDescriptions.size(); ++columnIndex) {
                columnDescription = columnDescriptions.get(columnIndex - 1);
                currentTableName = T + columnIndex;
                nextTableName = T + (columnIndex + 1);
                splitterName = S + columnIndex;
                currentColumnName = C + (columnIndex);
                nextColumnName = C + (columnIndex + 1);

                templateLine = "    #\"" + splitterName + "\" = Table.SplitColumn(#\"" + currentTableName + "\", \"" + currentColumnName + "\", Splitter.SplitTextByPositions({0, " + columnDescription.size + "}, false), {\"" + columnDescription.fieldName + "\", \"" + nextColumnName + "\"}),";
                System.out.println(templateLine);
                templateFile.write(templateLine);
                templateFile.newLine();

                templateLine = "    #\"" + nextTableName + "\" = Table.TransformColumnTypes(#\"" + splitterName + "\", {{\"" + columnDescription.fieldName + "\", " + columnDescription.type + "}, {\"" + nextColumnName + "\", type text}})" + ((columnIndex + 1) < columnDescriptions.size() ? "," : "");
                System.out.println(templateLine);
                templateFile.write(templateLine);
                templateFile.newLine();
            }

            templateLine = "in";
            System.out.println(templateLine);
            templateFile.write(templateLine);
            templateFile.newLine();

            templateLine = "    #\"" + nextTableName + "\"";
            System.out.println(templateLine);
            templateFile.write(templateLine);
            templateFile.newLine();

            System.out.println();
            templateFile.newLine();

            templateFile.close();
        }
    }
}
