package hlpp.github.io.generator.data;

import java.util.ArrayList;
import java.util.List;

public class Table {
    private List<TableHeadDataOption> headDataOptions = new ArrayList<TableHeadDataOption>();

    public List<TableHeadDataOption> getHeadDataOptions() {
        return headDataOptions;
    }

    public void setHeadDataOptions(List<TableHeadDataOption> headDataOptions) {
        this.headDataOptions = headDataOptions;
    }
}
