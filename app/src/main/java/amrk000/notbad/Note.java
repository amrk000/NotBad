package amrk000.notbad;

public class Note {

    private String title;
    private String content;
    private String time;

    public Note(){
        title="";
        content="";
        time="";
    }

    public Note setTitle(String x){
        title=x;
        return this;
    }

    public Note setContent(String x){
        content=x;
        return this;
    }

    public Note setTime(String x) {
        this.time = x;
        return this;
    }

    public String getTitle(){
        return title;
    }

    public String getContent(){
        return content;
    }

    public String getTime() {
        return time;
    }
}
