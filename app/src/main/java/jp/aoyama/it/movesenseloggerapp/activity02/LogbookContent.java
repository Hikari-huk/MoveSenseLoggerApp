package jp.aoyama.it.movesenseloggerapp.activity02;

public class LogbookContent {

    private LogContent logContent;

    public LogContent getLogContent() {
        return logContent;
    }

    public void setLogContent(LogContent logContent) {
        this.logContent = logContent;
    }

    public class LogContent{
        private Element[] elements;

        public Element[] getElements() {
            return elements;
        }

        public void setElements(Element[] elements) {
            this.elements = elements;
        }
    }
    public class Element{
        private int id;
        private long modificationTimestamp;
        private Long size;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getModificationTimestamp() {
            return modificationTimestamp;
        }

        public void setModificationTimestamp(long modificationTimestamp) {
            this.modificationTimestamp = modificationTimestamp;
        }
    }
}
