package jp.aoyama.it.movesenseloggerapp.activity02;

import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lipponep on 23.11.2017.
 */

public class MdsLogbookEntriesResponse {

    @SerializedName("Content")
    public LogContent logContent;

    public MdsLogbookEntriesResponse(LogContent logContent) {
        this.logContent = logContent;
    }

    public class LogContent{
        @SerializedName("elements")
        public List<LogEntry> logEntries;

        public LogContent(List<LogEntry> logEntries){
            this.logEntries = logEntries;
        }
    }

    public static class LogEntry {
        @SerializedName("Id")
        public final int id;

        @SerializedName("ModificationTimestamp")
        public final long modificationTimestamp;

        @SerializedName("Size")
        public final Long size;

        LogEntry(int id, int modificationTimestamp, Long size) {
            this.id = id;
            this.modificationTimestamp = modificationTimestamp;
            this.size = size;
        }

        static SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");

        public String getDateStr() {
            return dateFormat.format(new Date(modificationTimestamp*1000));
        }
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(id);
            sb.append(" : ");
            // pretty print date time
            sb.append(getDateStr());
            sb.append(" : ");
            sb.append(size);

            return sb.toString();
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (LogEntry le : logContent.logEntries) {
            if (sb.length() > 0)
                sb.append("\n");

            sb.append(le.id);
            sb.append(" : ");
            sb.append(le.modificationTimestamp);
            sb.append(" : ");
            sb.append(le.size);
        }
        return sb.toString();
    }

}
