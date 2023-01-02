package information;

import java.util.List;

public class ProductHrefByTitle {
    private List<String> hrefs;
    private String title;

    public ProductHrefByTitle(List<String> hrefs, String title) {
        this.hrefs = hrefs;
        this.title = title;
    }

    public String get (int index){
        return hrefs.get(index);
    }

    public String getTitle() {
        return title;
    }

    public int size () {
        return hrefs.size();
    }

}
