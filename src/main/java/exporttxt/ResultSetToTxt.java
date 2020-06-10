package exporttxt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ResultSetToTxt {
    private String pathFile;

    public ResultSetToTxt(String strPathFile) {
        this.pathFile = strPathFile;
    }

    public void toFileTxtExport(TableRecordsAll tbl) {
        try
        {
            FileWriter fileWriter = new FileWriter( new File(this.pathFile));
            for(Records row: tbl.getListRec()) {
                for(int i = 0, collCount = row.cellCount(); i < collCount; i++) {
                    if(collCount > 0) {
                        fileWriter.write(row.getCell(i) + ";");
                    }
                    else
                    {
                        fileWriter.write(row.getCell(i) + "");
                    }
                }
                fileWriter.write("\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPathFile() {
        return pathFile;
    }
}
