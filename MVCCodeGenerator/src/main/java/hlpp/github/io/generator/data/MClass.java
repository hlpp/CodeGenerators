package hlpp.github.io.generator.data;

public class MClass {
    private String simpleName;
    private MPackage pkg;
    
    public String getName() {
        return pkg != null ? (pkg.getName() + "." + simpleName) : simpleName;
    }
    public String getSimpleName() {
        return simpleName;
    }
    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }
    public void setPackage(MPackage pkg) {
        this.pkg = pkg;
    }
    public MPackage getPackage() {
        return pkg;
    }
}
