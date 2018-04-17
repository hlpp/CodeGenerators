package hlpp.github.io.generator.db;

public class Column {
    private String name;
    private String comment;
    private String key;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    
    @Override
    public String toString() {
        return "Column [name=" + name + ", comment=" + comment + "]";
    }

}